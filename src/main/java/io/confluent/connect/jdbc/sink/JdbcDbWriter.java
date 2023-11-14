/*
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.confluent.connect.jdbc.sink;

import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import io.confluent.connect.jdbc.dialect.DatabaseDialect;
import io.confluent.connect.jdbc.util.CachedConnectionProvider;
import io.confluent.connect.jdbc.util.TableId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcDbWriter {
  private static final Logger log = LoggerFactory.getLogger(JdbcDbWriter.class);

  private final JdbcSinkConfig config;
  private final DatabaseDialect dbDialect;
  private final DbStructure dbStructure;
  final CachedConnectionProvider cachedConnectionProvider;

  JdbcDbWriter(final JdbcSinkConfig config, DatabaseDialect dbDialect, DbStructure dbStructure) {
    this.config = config;
    this.dbDialect = dbDialect;
    this.dbStructure = dbStructure;

    this.cachedConnectionProvider = connectionProvider(
        config.connectionAttempts,
        config.connectionBackoffMs
    );
  }

  protected CachedConnectionProvider connectionProvider(int maxConnAttempts, long retryBackoff) {
    return new CachedConnectionProvider(this.dbDialect, maxConnAttempts, retryBackoff) {
      @Override
      protected void onConnect(final Connection connection) throws SQLException {
        log.info("JdbcDbWriter Connected");
        connection.setAutoCommit(false);
      }
    };
  }

  final Map<TableId, BufferedRecords> bufferByTable = new HashMap<>();
  void write(final Collection<SinkRecord> records)
      throws SQLException, TableAlterOrCreateException {
    final Connection connection = cachedConnectionProvider.getConnection();
    try {

      for (SinkRecord record : records) {
        final TableId tableId = destinationTable(record.topic());
        BufferedRecords buffer = bufferByTable.get(tableId);
        if (buffer == null) {
          buffer = config.batchInsertMode == JdbcSinkConfig.BatchInsertMode.GPLOAD ? new GPBufferedRecords(config, tableId, dbDialect, dbStructure, connection) : new BufferedRecords(config, tableId, dbDialect, dbStructure, connection);
          buffer.setLastFlushTime(System.currentTimeMillis());
          bufferByTable.put(tableId, buffer);
        }
        buffer.add(record);
      }
      // to remove enteries
      commitPendingRecords();
      connection.commit();
    } catch (SQLException | TableAlterOrCreateException e) {
      try {
        connection.rollback();
      } catch (SQLException sqle) {
        e.addSuppressed(sqle);
      } finally {
        throw e;
      }
    }
  }

  void closeQuietly() {
    cachedConnectionProvider.close();
  }

  TableId destinationTable(String topic) {
    final String tableName = config.tableNameFormat.replace("${topic}", topic);
    if (tableName.isEmpty()) {
      throw new ConnectException(String.format(
          "Destination table name for topic '%s' is empty using the format string '%s'",
          topic,
          config.tableNameFormat
      ));
    }
    return dbDialect.parseTableIdentifier(tableName);
  }

  public void commitPendingRecords() {
    if (bufferByTable == null || bufferByTable.isEmpty()) {
      return;
    }
    List<TableId> toRemoveEntries = new ArrayList<>();

    for (Map.Entry<TableId, BufferedRecords> entry : bufferByTable.entrySet()) {
      TableId tableId = entry.getKey();
      BufferedRecords buffer = entry.getValue();
      if(System.currentTimeMillis() - buffer.getLastFlushTime() < config.maxBatchWaitTime) {
        continue;
      }
      toRemoveEntries.add(tableId);
      log.debug("Flushing records in JDBC Writer for table ID: {}", tableId);
      try {
        buffer.flush();
        buffer.close();
      } catch (SQLException e) {
        log.error("Error while flushing records in JDBC Writer for table ID: {}", tableId);
      }

    }
    for(TableId tableId : toRemoveEntries) {
      bufferByTable.remove(tableId);
    }
  }
}
