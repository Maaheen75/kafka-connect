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

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import io.confluent.connect.jdbc.gp.gpfdist.framweork.support.SegmentRejectType;
import io.confluent.connect.jdbc.source.JdbcSourceConnectorConfig;

import io.confluent.connect.jdbc.util.*;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.config.types.Password;

public class JdbcSinkConfig extends AbstractConfig {

    // config for timestamp.auto.convert boolean default true
    public static final String TIMESTAMP_AUTO_CONVERT = "timestamp.auto.convert";
    private static final boolean TIMESTAMP_AUTO_CONVERT_DEFAULT = true;
    private static final String TIMESTAMP_AUTO_CONVERT_DOC = "Whether to convert time-based fields to Timestamp, Date, and Time objects.";
    public static final String TIMESTAMP_AUTO_CONVERT_DISPLAY = "Timestamp Auto Convert";

    // config for date.from.timezone & date.to.timezone
    public static final String DATE_FROM_TIMEZONE = "date.from.timezone";
    private static final String DATE_FROM_TIMEZONE_DEFAULT = null;
    private static final String DATE_FROM_TIMEZONE_DOC = "The timezone to use for the date in the source record.";
    public static final String DATE_FROM_TIMEZONE_DISPLAY = "Date From Timezone";

    public static final String DATE_TO_TIMEZONE = "date.to.timezone";
    private static final String DATE_TO_TIMEZONE_DEFAULT = null;
    private static final String DATE_TO_TIMEZONE_DOC = "The timezone to use for the date in the sink record.";
    public static final String DATE_TO_TIMEZONE_DISPLAY = "Date To Timezone";




    public static final String COLUMN_ALTERNATIVE = "column.alternative";
    private static final String COLUMN_ALTERNATIVE_DEFAULT = null;
    private static final String COLUMN_ALTERNATIVE_DOC = "The column alternative, i.e. use value of other column if it is missed in the source.";
    public static final String COLUMN_ALTERNATIVE_DISPLAY = "Column Alternative";

    // configs for timestamp.from.format and timestamp.to.format, date.from.format and date.to.format, time.from.format and time.to.format and timezone
    public static final String TIMESTAMP_FROM_FORMAT = "timestamp.from.format";
    private static final String TIMESTAMP_FROM_FORMAT_DEFAULT = "EEE MMM dd HH:mm:ss zzz yyyy";
    private static final String TIMESTAMP_FROM_FORMAT_DOC = "The format of the timestamp in the source record.";
    public static final String TIMESTAMP_FROM_FORMAT_DISPLAY = "Timestamp From Format";

    public static final String TIMESTAMP_TO_FORMAT = "timestamp.to.format";
    private static final String TIMESTAMP_TO_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    private static final String TIMESTAMP_TO_FORMAT_DOC = "The format of the timestamp in the sink record.";
    public static final String TIMESTAMP_TO_FORMAT_DISPLAY = "Timestamp To Format";

    public static final String DATE_FROM_FORMAT = "date.from.format";
    private static final String DATE_FROM_FORMAT_DEFAULT = "EEE MMM dd HH:mm:ss zzz yyyy";
    private static final String DATE_FROM_FORMAT_DOC = "The format of the date in the source record.";
    public static final String DATE_FROM_FORMAT_DISPLAY = "Date From Format";

    public static final String DATE_TO_FORMAT = "date.to.format";
    private static final String DATE_TO_FORMAT_DEFAULT = "yyyy-MM-dd";
    private static final String DATE_TO_FORMAT_DOC = "The format of the date in the sink record.";
    public static final String DATE_TO_FORMAT_DISPLAY = "Date To Format";

    public static final String TIME_FROM_FORMAT = "time.from.format";
    private static final String TIME_FROM_FORMAT_DEFAULT = "EEE MMM dd HH:mm:ss zzz yyyy";
    private static final String TIME_FROM_FORMAT_DOC = "The format of the time in the source record.";
    public static final String TIME_FROM_FORMAT_DISPLAY = "Time From Format";

    public static final String TIME_TO_FORMAT = "time.to.format";
    private static final String TIME_TO_FORMAT_DEFAULT = "HH:mm:ss";
    private static final String TIME_TO_FORMAT_DOC = "The format of the time in the sink record.";
    public static final String TIME_TO_FORMAT_DISPLAY = "Time To Format";


    public static final String COLUMN_SELECTION_STRATEGY = "column.selection.strategy";
    private static final String COLUMN_SELECTION_STRATEGY_DEFAULT = ColumnSelectionStrategy.DEFAULT.name();
    private static final String COLUMN_SELECTION_STRATEGY_DOCS = "The column selection strategy to use (for gpss only). Supported strategies are:\n"
            + "``DEFAULT``\n"
            + "    Use all columns received from source table.\n"
            + "``SINK_PREFERRED``\n"
            + "    Prefer columns in the sink table if there is a difference between the source and sink tables.";
    public static final String COLUMN_SELECTION_STRATEGY_DISPLAY = "Column Selection Strategy";

    public enum ColumnSelectionStrategy {
        DEFAULT,
        SINK_PREFERRED,

    }


    public int gpssTimeout = 3000;

    public enum UpdateMode {
        DEFAULT,
        FIRST_ROW_ONLY,
        LAST_ROW_ONLY,
    }

    public static final String UPDATE_MODE = "update.mode";

    public static final String UPDATE_MODE_DEFAULT = UpdateMode.DEFAULT.name();

    public static final String UPDATE_MODE_DOC = "The update mode to use for updates:" +
            "`DEFAULT`: Do nothing, use default behavior." +
            "`FIRST_ROW_ONLY`: Choose first row only from a batch of updates." +
            "`LAST_ROW_ONLY`: Choose last row only from a batch of updates.";

    public static final String DEBUG_LOG = "debug.logs";
    private static final boolean DEBUG_LOG_DEFAULT = false;
    private static final String DEBUG_LOG_DOC = "Whether to log debug logs.";

    public static final String UPDATE_MODE_DISPLAY = "Update Mode";
    public static final String GPSS_HOST = "gpss.host";

    public static final String GP_MAX_LINE_LENGTH = "gp.max.line.length";
    private static final int GP_MAX_LINE_LENGTH_DEFAULT = 65535;
    private static final String GP_MAX_LINE_LENGTH_DOC = "The maximum length of a line for gpfdist.";
    private static final String GPSS_HOST_DEFAULT = "localhost";
    private static final String GPSS_HOST_DOC = "The gpss host for gpss mode.";

    public static final String GPSS_USE_STICKY_SESSION = "gpss.use.sticky.session";
    private static final boolean GPSS_USE_STICKY_SESSION_DEFAULT = false;
    private static final String GPSS_USE_STICKY_SESSION_DOC = "Whether to use sticky session for gpss mode.";

    public static final String GPSS_PORT = "gpss.port";
    private static final String GPSS_PORT_DEFAULT = "5000";
    private static final String GPSS_PORT_DOC = "The gpss port for gpss mode.";


    public static final String DB_SCHEMA = "db.schema";
    private static final String DB_SCHEMA_DEFAULT = null;
    private static final String DB_SCHEMA_DOC = "The schema to use for the connector's tables.";
    public static final String GPFDIST_HOST = "gpfdist.host";
    public static final String GPFDIST_HOST_DEFAULT = null;
    public static final String GPFDIST_HOST_DOC = "The gpfdist host for gpfdist and gpload modes. Gpfdist server will fallback to current machine's ip or hostname if not specified.";
    public static final String KEEP_GP_FILES_CONFIG = "keep.gp.files";
    private static final boolean KEEP_GP_FILES_DEFAULT = false; // Set your default value
    private static final String KEEP_GP_FILES_DOC = "Whether to keep Greenplum files for debugging.";


    public static final String GREENPLUM_HOME_CONFIG = "greenplum.home";
    private static final String GREENPLUM_HOME_DEFAULT = null; // Set your default value
    private static final String GREENPLUM_HOME_DOC = "The path to the Greenplum installation directory.";


    // max time to wait for batch
    public static final String DATA_DELIMITER = "data.delimiter";
    private static final String DATA_DELIMITER_DEFAULT = ",";

    public static final String GP_ERRORS_LIMIT = "gp.error.limit";
    private static final int GP_ERROR_LIMIT_DEFAULT = 0;

    public static final String GP_ERRORS_PERCENTAGE_LIMIT = "gp.error.percentage.limit";
    private static final int GP_ERRORS_PERCENTAGE_LIMIT_DEFAULT = 0;
    private static final String GP_ERRORS_PERCENTAGE_LIMIT_DOC = "The maximum percentage of errors allowed in a batch before the batch is considered failed.";


    public static final String CSV_HEADER = "csv.header";
    private static final boolean CSV_HEADER_DEFAULT = true;

    public static final String NULL_STRING = "null.string";
    private static final String NULL_STRING_DEFAULT = null;
    public static final String NULL_STRING_DOC = "The string to use for null values in the CSV file/gpss stream.";
    public static final String CSV_QUOTE = "csv.quote";
    private static final String CSV_QUOTE_DEFAULT = "\"";

    public static final String CSV_ENCODING = "csv.encoding";
    private static final String CSV_ENCODING_DEFAULT = "UTF-8";

    public static final String GP_LOG_ERRORS = "gp.log.errors";
    private static final boolean GP_LOG_ERRORS_DEFAULT = true;


    public static final String MAX_BATCH_WAIT_TIME = "max.batch.wait.time";
    private static final long MAX_BATCH_WAIT_TIME_DEFAULT = 60000;
    private static final String MAX_BATCH_WAIT_TIME_DOC = "The maximum time to wait for a batch to be completed.";
    private static final String MAX_BATCH_WAIT_TIME_DISPLAY = "Maximum Batch Wait Time";


    public static final String PORT_RANGE = "port.range";
    private static final List<String> PORT_RANGE_DEFAULT = Arrays.asList("8000", "9000"); // can be passed one value to specify a single port
    private static final String PORT_RANGE_DOC = "The range of ports to use for gpfdist.";
    private static final String PORT_RANGE_DISPLAY = "Port Range";
    public String dataLineSeparator = "\n";

    public int getGpfdistPort() {
        return portRange.size() > 0 ? portRange.get(0) : 5120;
    }

    public String getGpfdistHost() {
        return gpfdistHost != null ? gpfdistHost : CommonUtils.getLocalIpOrHost();
    }

    public Character getDelimiter() {
        return delimiter == null || delimiter.isEmpty() ? ',' : delimiter.charAt(0);
    }


    public enum BatchInsertMode {
        NONE,
        GPLOAD,
        GPSS,

        GPFDIST,
//        GPKAFKA, // https://github.com/yanivbhemo/greenplum-gpss
//        DSH

    }


    public enum InsertMode {
        INSERT,
        UPSERT,
        UPDATE,
        MERGE

    }

    public enum PrimaryKeyMode {
        NONE,
        KAFKA,
        RECORD_KEY,
        RECORD_VALUE;
    }

    public static final List<String> DEFAULT_KAFKA_PK_NAMES = Collections.unmodifiableList(
            Arrays.asList(
                    "__connect_topic",
                    "__connect_partition",
                    "__connect_offset"
            )
    );

    public static final String CONNECTION_URL = JdbcSourceConnectorConfig.CONNECTION_URL_CONFIG;
    private static final String CONNECTION_URL_DOC =
            "JDBC connection URL.\n"
                    + "For example: ``jdbc:oracle:thin:@localhost:1521:orclpdb1``, "
                    + "``jdbc:mysql://localhost/db_name``, "
                    + "``jdbc:sqlserver://localhost;instance=SQLEXPRESS;"
                    + "databaseName=db_name``";
    private static final String CONNECTION_URL_DISPLAY = "JDBC URL";

    public static final String CONNECTION_USER = JdbcSourceConnectorConfig.CONNECTION_USER_CONFIG;
    private static final String CONNECTION_USER_DOC = "JDBC connection user.";
    private static final String CONNECTION_USER_DISPLAY = "JDBC User";

    public static final String CONNECTION_PASSWORD =
            JdbcSourceConnectorConfig.CONNECTION_PASSWORD_CONFIG;
    private static final String CONNECTION_PASSWORD_DOC = "JDBC connection password.";
    private static final String CONNECTION_PASSWORD_DISPLAY = "JDBC Password";

    public static final String CONNECTION_ATTEMPTS =
            JdbcSourceConnectorConfig.CONNECTION_ATTEMPTS_CONFIG;
    private static final String CONNECTION_ATTEMPTS_DOC =
            JdbcSourceConnectorConfig.CONNECTION_ATTEMPTS_DOC;
    private static final String CONNECTION_ATTEMPTS_DISPLAY =
            JdbcSourceConnectorConfig.CONNECTION_ATTEMPTS_DISPLAY;
    public static final int CONNECTION_ATTEMPTS_DEFAULT =
            JdbcSourceConnectorConfig.CONNECTION_ATTEMPTS_DEFAULT;

    public static final String CONNECTION_BACKOFF =
            JdbcSourceConnectorConfig.CONNECTION_BACKOFF_CONFIG;
    private static final String CONNECTION_BACKOFF_DOC =
            JdbcSourceConnectorConfig.CONNECTION_BACKOFF_DOC;
    private static final String CONNECTION_BACKOFF_DISPLAY =
            JdbcSourceConnectorConfig.CONNECTION_BACKOFF_DISPLAY;
    public static final long CONNECTION_BACKOFF_DEFAULT =
            JdbcSourceConnectorConfig.CONNECTION_BACKOFF_DEFAULT;

    public static final String TABLE_NAME_FORMAT = "table.name.format";
    private static final String TABLE_NAME_FORMAT_DEFAULT = "${topic}";
    private static final String TABLE_NAME_FORMAT_DOC =
            "A format string for the destination table name, which may contain '${topic}' as a "
                    + "placeholder for the originating topic name.\n"
                    + "For example, ``kafka_${topic}`` for the topic 'orders' will map to the table name "
                    + "'kafka_orders'.";
    private static final String TABLE_NAME_FORMAT_DISPLAY = "Table Name Format";

    public static final String MAX_RETRIES = "max.retries";
    private static final int MAX_RETRIES_DEFAULT = 10;
    private static final String MAX_RETRIES_DOC =
            "The maximum number of times to retry on errors before failing the task.";
    private static final String MAX_RETRIES_DISPLAY = "Maximum Retries";

    public static final String RETRY_BACKOFF_MS = "retry.backoff.ms";
    private static final int RETRY_BACKOFF_MS_DEFAULT = 3000;
    private static final String RETRY_BACKOFF_MS_DOC =
            "The time in milliseconds to wait following an error before a retry attempt is made.";
    private static final String RETRY_BACKOFF_MS_DISPLAY = "Retry Backoff (millis)";

    public static final String BATCH_SIZE = "batch.size";
    private static final int BATCH_SIZE_DEFAULT = 3000;
    private static final String BATCH_SIZE_DOC =
            "Specifies how many records to attempt to batch together for insertion into the destination"
                    + " table, when possible.";
    private static final String BATCH_SIZE_DISPLAY = "Batch Size";

    public static final String DELETE_ENABLED = "delete.enabled";
    private static final String DELETE_ENABLED_DEFAULT = "false";
    private static final String DELETE_ENABLED_DOC =
            "Whether to treat ``null`` record values as deletes. Requires ``pk.mode`` "
                    + "to be ``record_key``.";
    private static final String DELETE_ENABLED_DISPLAY = "Enable deletes";

    public static final String AUTO_CREATE = "auto.create";
    private static final String AUTO_CREATE_DEFAULT = "false";
    private static final String AUTO_CREATE_DOC =
            "Whether to automatically create the destination table based on record schema if it is "
                    + "found to be missing by issuing ``CREATE``.";
    private static final String AUTO_CREATE_DISPLAY = "Auto-Create";

    public static final String AUTO_EVOLVE = "auto.evolve";
    private static final String AUTO_EVOLVE_DEFAULT = "false";
    private static final String AUTO_EVOLVE_DOC =
            "Whether to automatically add columns in the table schema when found to be missing relative "
                    + "to the record schema by issuing ``ALTER``.";
    private static final String AUTO_EVOLVE_DISPLAY = "Auto-Evolve";
    public static final String BATCH_INSERT_MODE = "batch.insert.mode";
    public static final String BATCH_INSERT_MODE_DEFAULT = "none";
    private static final String BATCH_INSERT_MODE_DOC =
            "The batch insertion mode to use. Supported modes are:\n"
                    + "``default``\n"
                    + "    Use standard SQL ``INSERT`` statements.\n"
                    + "``gpload``\n"
                    + "    Use gpload utility to load data by creating a yml file at runtime (For greenplum only). Make sure that the gpload is in your path \n"
                    + "``GPSS``\n"
                    + "    Use greenplum streaming server (For greenplum only) https://docs.vmware.com/en/VMware-Greenplum-Streaming-Server/1.10/greenplum-streaming-server/ref-gpss.html\n" +
                    "``GPFDIST`` Use gpfdist in memory server\n";
    private static final String BATCH_INSERT_MODE_DISPLAY = "Batch Insert Mode";
    public static final String INSERT_MODE = "insert.mode";
    private static final String INSERT_MODE_DEFAULT = "insert";
    private static final String INSERT_MODE_DOC =
            "The insertion mode to use. Supported modes are:\n"
                    + "``insert``\n"
                    + "    Use standard SQL ``INSERT`` statements.\n"
                    + "``upsert``\n"
                    + "    Use the appropriate upsert semantics for the target database if it is supported by "
                    + "the connector, e.g. ``INSERT OR IGNORE``.\n"
                    + "``update``\n"
                    + "    Use the appropriate update semantics for the target database if it is supported by "
                    + "the connector, e.g. ``UPDATE``.";
    private static final String INSERT_MODE_DISPLAY = "Insert Mode";

    public static final String PK_FIELDS = "pk.fields";
    private static final String PK_FIELDS_DEFAULT = "";
    private static final String PK_FIELDS_DOC =
            "List of comma-separated primary key field names. The runtime interpretation of this config"
                    + " depends on the ``pk.mode``:\n"
                    + "``none``\n"
                    + "    Ignored as no fields are used as primary key in this mode.\n"
                    + "``kafka``\n"
                    + "    Must be a trio representing the Kafka coordinates, defaults to ``"
                    + StringUtils.join(DEFAULT_KAFKA_PK_NAMES, ",") + "`` if empty.\n"
                    + "``record_key``\n"
                    + "    If empty, all fields from the key struct will be used, otherwise used to extract the"
                    + " desired fields - for primitive key only a single field name must be configured.\n"
                    + "``record_value``\n"
                    + "    If empty, all fields from the value struct will be used, otherwise used to extract "
                    + "the desired fields.";
    private static final String PK_FIELDS_DISPLAY = "Primary Key Fields";

    public static final String PK_MODE = "pk.mode";
    private static final String PK_MODE_DEFAULT = "none";
    private static final String PK_MODE_DOC =
            "The primary key mode, also refer to ``" + PK_FIELDS + "`` documentation for interplay. "
                    + "Supported modes are:\n"
                    + "``none``\n"
                    + "    No keys utilized.\n"
                    + "``kafka``\n"
                    + "    Kafka coordinates are used as the PK.\n"
                    + "``record_key``\n"
                    + "    Field(s) from the record key are used, which may be a primitive or a struct.\n"
                    + "``record_value``\n"
                    + "    Field(s) from the record value are used, which must be a struct.";
    private static final String PK_MODE_DISPLAY = "Primary Key Mode";

    public static final String FIELDS_WHITELIST = "fields.whitelist";
    private static final String FIELDS_WHITELIST_DEFAULT = "";
    private static final String FIELDS_WHITELIST_DOC =
            "List of comma-separated record value field names. If empty, all fields from the record "
                    + "value are utilized, otherwise used to filter to the desired fields.\n"
                    + "Note that ``" + PK_FIELDS + "`` is applied independently in the context of which field"
                    + "(s) form the primary key columns in the destination database,"
                    + " while this configuration is applicable for the other columns.";
    private static final String FIELDS_WHITELIST_DISPLAY = "Fields Whitelist";

    private static final ConfigDef.Range NON_NEGATIVE_INT_VALIDATOR = ConfigDef.Range.atLeast(0);

    private static final String CONNECTION_GROUP = "Connection";
    private static final String WRITES_GROUP = "Writes";
    private static final String DATAMAPPING_GROUP = "Data Mapping";
    private static final String DDL_GROUP = "DDL Support";
    private static final String DML_GROUP = "DML Support";
    private static final String RETRIES_GROUP = "Retries";
    public static final String DIALECT_NAME_CONFIG = "dialect.name";
    private static final String DIALECT_NAME_DISPLAY = "Database Dialect";
    public static final String DIALECT_NAME_DEFAULT = "";
    private static final String DIALECT_NAME_DOC =
            "The name of the database dialect that should be used for this connector. By default this "
                    + "is empty, and the connector automatically determines the dialect based upon the "
                    + "JDBC connection URL. Use this if you want to override that behavior and use a "
                    + "specific dialect. All properly-packaged dialects in the JDBC connector plugin "
                    + "can be used.";

    public static final String DB_TIMEZONE_CONFIG = "db.timezone";
    public static final String DB_TIMEZONE_DEFAULT = "UTC";
    private static final String DB_TIMEZONE_CONFIG_DOC =
            "Name of the JDBC timezone that should be used in the connector when "
                    + "inserting time-based values. Defaults to UTC.";
    private static final String DB_TIMEZONE_CONFIG_DISPLAY = "DB Time Zone";

    public static final String QUOTE_SQL_IDENTIFIERS_CONFIG =
            JdbcSourceConnectorConfig.QUOTE_SQL_IDENTIFIERS_CONFIG;
    public static final String QUOTE_SQL_IDENTIFIERS_DEFAULT =
            JdbcSourceConnectorConfig.QUOTE_SQL_IDENTIFIERS_DEFAULT;
    public static final String QUOTE_SQL_IDENTIFIERS_DOC =
            JdbcSourceConnectorConfig.QUOTE_SQL_IDENTIFIERS_DOC;
    private static final String QUOTE_SQL_IDENTIFIERS_DISPLAY =
            JdbcSourceConnectorConfig.QUOTE_SQL_IDENTIFIERS_DISPLAY;

    public static final String TABLE_TYPES_CONFIG = "table.types";
    private static final String TABLE_TYPES_DISPLAY = "Table Types";
    public static final String TABLE_TYPES_DEFAULT = TableType.TABLE.toString();
    private static final String TABLE_TYPES_DOC =
            "The comma-separated types of database tables to which the sink connector can write. "
                    + "By default this is ``" + TableType.TABLE + "``, but any combination of ``"
                    + TableType.TABLE + "``, ``" + TableType.PARTITIONED_TABLE + "`` and ``"
                    + TableType.VIEW + "`` is allowed. Not all databases support writing to views, "
                    + "and when they do the sink connector will fail if the "
                    + "view definition does not match the records' schemas (regardless of ``"
                    + AUTO_EVOLVE + "``).";

    public static final String TRIM_SENSITIVE_LOG_ENABLED = "trim.sensitive.log";
    private static final String TRIM_SENSITIVE_LOG_ENABLED_DEFAULT = "false";
    private static final EnumRecommender QUOTE_METHOD_RECOMMENDER =
            EnumRecommender.in(QuoteMethod.values());

    private static final EnumRecommender TABLE_TYPES_RECOMMENDER =
            EnumRecommender.in(TableType.values());
    public static final String MSSQL_USE_MERGE_HOLDLOCK = "mssql.use.merge.holdlock";
    private static final String MSSQL_USE_MERGE_HOLDLOCK_DEFAULT = "true";
    private static final String MSSQL_USE_MERGE_HOLDLOCK_DOC =
            "Whether to use HOLDLOCK when performing a MERGE INTO upsert statement. "
                    + "Note that it is only applicable to SQL Server.";
    private static final String MSSQL_USE_MERGE_HOLDLOCK_DISPLAY =
            "SQL Server - Use HOLDLOCK in MERGE";

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
            // Connection
            .define(
                    CONNECTION_URL,
                    ConfigDef.Type.STRING,
                    ConfigDef.NO_DEFAULT_VALUE,
                    ConfigDef.Importance.HIGH,
                    CONNECTION_URL_DOC,
                    CONNECTION_GROUP,
                    1,
                    ConfigDef.Width.LONG,
                    CONNECTION_URL_DISPLAY
            )
            .define(
                    CONNECTION_USER,
                    ConfigDef.Type.STRING,
                    null,
                    ConfigDef.Importance.HIGH,
                    CONNECTION_USER_DOC,
                    CONNECTION_GROUP,
                    2,
                    ConfigDef.Width.MEDIUM,
                    CONNECTION_USER_DISPLAY
            )
            .define(
                    CONNECTION_PASSWORD,
                    ConfigDef.Type.PASSWORD,
                    null,
                    ConfigDef.Importance.HIGH,
                    CONNECTION_PASSWORD_DOC,
                    CONNECTION_GROUP,
                    3,
                    ConfigDef.Width.MEDIUM,
                    CONNECTION_PASSWORD_DISPLAY
            )
            .define(
                    DIALECT_NAME_CONFIG,
                    ConfigDef.Type.STRING,
                    DIALECT_NAME_DEFAULT,
                    DatabaseDialectRecommender.INSTANCE,
                    ConfigDef.Importance.LOW,
                    DIALECT_NAME_DOC,
                    CONNECTION_GROUP,
                    4,
                    ConfigDef.Width.LONG,
                    DIALECT_NAME_DISPLAY,
                    DatabaseDialectRecommender.INSTANCE
            )
            .define(
                    CONNECTION_ATTEMPTS,
                    ConfigDef.Type.INT,
                    CONNECTION_ATTEMPTS_DEFAULT,
                    ConfigDef.Range.atLeast(1),
                    ConfigDef.Importance.LOW,
                    CONNECTION_ATTEMPTS_DOC,
                    CONNECTION_GROUP,
                    5,
                    ConfigDef.Width.SHORT,
                    CONNECTION_ATTEMPTS_DISPLAY
            ).define(
                    CONNECTION_BACKOFF,
                    ConfigDef.Type.LONG,
                    CONNECTION_BACKOFF_DEFAULT,
                    ConfigDef.Importance.LOW,
                    CONNECTION_BACKOFF_DOC,
                    CONNECTION_GROUP,
                    6,
                    ConfigDef.Width.SHORT,
                    CONNECTION_BACKOFF_DISPLAY
            ).define(
                    BATCH_INSERT_MODE,
                    ConfigDef.Type.STRING,
                    BATCH_INSERT_MODE_DEFAULT,
                    EnumValidator.in(BatchInsertMode.values()),
                    ConfigDef.Importance.HIGH,
                    BATCH_INSERT_MODE_DOC,
                    WRITES_GROUP,
                    1,
                    ConfigDef.Width.MEDIUM,
                    BATCH_INSERT_MODE_DISPLAY
            )
            // Writes
            .define(
                    INSERT_MODE,
                    ConfigDef.Type.STRING,
                    INSERT_MODE_DEFAULT,
                    EnumValidator.in(InsertMode.values()),
                    ConfigDef.Importance.HIGH,
                    INSERT_MODE_DOC,
                    WRITES_GROUP,
                    1,
                    ConfigDef.Width.MEDIUM,
                    INSERT_MODE_DISPLAY
            )
            .define(
                    BATCH_SIZE,
                    ConfigDef.Type.INT,
                    BATCH_SIZE_DEFAULT,
                    NON_NEGATIVE_INT_VALIDATOR,
                    ConfigDef.Importance.MEDIUM,
                    BATCH_SIZE_DOC, WRITES_GROUP,
                    2,
                    ConfigDef.Width.SHORT,
                    BATCH_SIZE_DISPLAY
            )
            .define(
                    DELETE_ENABLED,
                    ConfigDef.Type.BOOLEAN,
                    DELETE_ENABLED_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    DELETE_ENABLED_DOC, WRITES_GROUP,
                    3,
                    ConfigDef.Width.SHORT,
                    DELETE_ENABLED_DISPLAY,
                    DeleteEnabledRecommender.INSTANCE
            )
            .define(
                    TABLE_TYPES_CONFIG,
                    ConfigDef.Type.LIST,
                    TABLE_TYPES_DEFAULT,
                    TABLE_TYPES_RECOMMENDER,
                    ConfigDef.Importance.LOW,
                    TABLE_TYPES_DOC,
                    WRITES_GROUP,
                    4,
                    ConfigDef.Width.MEDIUM,
                    TABLE_TYPES_DISPLAY
            )
            // Data Mapping
            .define(
                    TABLE_NAME_FORMAT,
                    ConfigDef.Type.STRING,
                    TABLE_NAME_FORMAT_DEFAULT,
                    new ConfigDef.NonEmptyString(),
                    ConfigDef.Importance.MEDIUM,
                    TABLE_NAME_FORMAT_DOC,
                    DATAMAPPING_GROUP,
                    1,
                    ConfigDef.Width.LONG,
                    TABLE_NAME_FORMAT_DISPLAY
            )
            .define(
                    PK_MODE,
                    ConfigDef.Type.STRING,
                    PK_MODE_DEFAULT,
                    EnumValidator.in(PrimaryKeyMode.values()),
                    ConfigDef.Importance.HIGH,
                    PK_MODE_DOC,
                    DATAMAPPING_GROUP,
                    2,
                    ConfigDef.Width.MEDIUM,
                    PK_MODE_DISPLAY,
                    PrimaryKeyModeRecommender.INSTANCE
            )
            .define(
                    PK_FIELDS,
                    ConfigDef.Type.LIST,
                    PK_FIELDS_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    PK_FIELDS_DOC,
                    DATAMAPPING_GROUP,
                    3,
                    ConfigDef.Width.LONG, PK_FIELDS_DISPLAY
            )
            .define(
                    FIELDS_WHITELIST,
                    ConfigDef.Type.LIST,
                    FIELDS_WHITELIST_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    FIELDS_WHITELIST_DOC,
                    DATAMAPPING_GROUP,
                    4,
                    ConfigDef.Width.LONG,
                    FIELDS_WHITELIST_DISPLAY
            ).define(
                    DB_TIMEZONE_CONFIG,
                    ConfigDef.Type.STRING,
                    DB_TIMEZONE_DEFAULT,
                    TimeZoneValidator.INSTANCE,
                    ConfigDef.Importance.MEDIUM,
                    DB_TIMEZONE_CONFIG_DOC,
                    DATAMAPPING_GROUP,
                    5,
                    ConfigDef.Width.MEDIUM,
                    DB_TIMEZONE_CONFIG_DISPLAY
            )
            // DDL
            .define(
                    AUTO_CREATE,
                    ConfigDef.Type.BOOLEAN,
                    AUTO_CREATE_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    AUTO_CREATE_DOC, DDL_GROUP,
                    1,
                    ConfigDef.Width.SHORT,
                    AUTO_CREATE_DISPLAY
            )
            .define(
                    AUTO_EVOLVE,
                    ConfigDef.Type.BOOLEAN,
                    AUTO_EVOLVE_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    AUTO_EVOLVE_DOC, DDL_GROUP,
                    2,
                    ConfigDef.Width.SHORT,
                    AUTO_EVOLVE_DISPLAY
            ).define(
                    QUOTE_SQL_IDENTIFIERS_CONFIG,
                    ConfigDef.Type.STRING,
                    QUOTE_SQL_IDENTIFIERS_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    QUOTE_SQL_IDENTIFIERS_DOC,
                    DDL_GROUP,
                    3,
                    ConfigDef.Width.MEDIUM,
                    QUOTE_SQL_IDENTIFIERS_DISPLAY,
                    QUOTE_METHOD_RECOMMENDER
            )
            // DML
            .define(
                    MSSQL_USE_MERGE_HOLDLOCK,
                    ConfigDef.Type.BOOLEAN,
                    MSSQL_USE_MERGE_HOLDLOCK_DEFAULT,
                    ConfigDef.Importance.LOW,
                    MSSQL_USE_MERGE_HOLDLOCK_DOC,
                    DML_GROUP,
                    1,
                    ConfigDef.Width.MEDIUM,
                    MSSQL_USE_MERGE_HOLDLOCK_DISPLAY
            )
            // Retries
            .define(
                    MAX_RETRIES,
                    ConfigDef.Type.INT,
                    MAX_RETRIES_DEFAULT,
                    NON_NEGATIVE_INT_VALIDATOR,
                    ConfigDef.Importance.MEDIUM,
                    MAX_RETRIES_DOC,
                    RETRIES_GROUP,
                    1,
                    ConfigDef.Width.SHORT,
                    MAX_RETRIES_DISPLAY
            ).define(
                    MAX_BATCH_WAIT_TIME,
                    ConfigDef.Type.LONG, // Use LONG for representing time in milliseconds
                    MAX_BATCH_WAIT_TIME_DEFAULT, // Set your desired default value
                    NON_NEGATIVE_INT_VALIDATOR, // Ensure the value is non-negative
                    ConfigDef.Importance.MEDIUM,
                    MAX_BATCH_WAIT_TIME_DOC,
                    WRITES_GROUP, // Change this category as needed
                    1, // Order within the category
                    ConfigDef.Width.SHORT,
                    MAX_BATCH_WAIT_TIME_DISPLAY // Display name
            )
            .define(
                    RETRY_BACKOFF_MS,
                    ConfigDef.Type.INT,
                    RETRY_BACKOFF_MS_DEFAULT,
                    NON_NEGATIVE_INT_VALIDATOR,
                    ConfigDef.Importance.MEDIUM,
                    RETRY_BACKOFF_MS_DOC,
                    RETRIES_GROUP,
                    2,
                    ConfigDef.Width.SHORT,
                    RETRY_BACKOFF_MS_DISPLAY
            ).define(
                    // Other configuration entries...

                    PORT_RANGE,
                    ConfigDef.Type.LIST,
                    PORT_RANGE_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    PORT_RANGE_DOC,
                    WRITES_GROUP, // Change this category as needed
                    3, // Order within the category
                    ConfigDef.Width.LONG,
                    PORT_RANGE_DISPLAY
            )

            .defineInternal(
                    TRIM_SENSITIVE_LOG_ENABLED,
                    ConfigDef.Type.BOOLEAN,
                    TRIM_SENSITIVE_LOG_ENABLED_DEFAULT,
                    ConfigDef.Importance.LOW
            ).define(
                    DATA_DELIMITER,
                    ConfigDef.Type.STRING,
                    DATA_DELIMITER_DEFAULT, // Default delimiter
                    ConfigDef.Importance.MEDIUM,
                    "Data line delimiter character."
            )
            .define(
                    GP_ERRORS_LIMIT,
                    ConfigDef.Type.INT,
                    GP_ERROR_LIMIT_DEFAULT, // Default error limit
                    ConfigDef.Importance.MEDIUM,
                    "Error limit for gpload processing."
            )
            .define(
                    CSV_HEADER,
                    ConfigDef.Type.BOOLEAN,
                    CSV_HEADER_DEFAULT, // Default include header
                    ConfigDef.Importance.MEDIUM,
                    "Whether CSV file for gpload includes headers."
            )
            .define(
                    CSV_QUOTE,
                    ConfigDef.Type.STRING,
                    CSV_QUOTE_DEFAULT, // Default quote character
                    ConfigDef.Importance.MEDIUM,
                    "CSV quote character."
            )
            .define(
                    CSV_ENCODING,
                    ConfigDef.Type.STRING,
                    CSV_ENCODING_DEFAULT, // Default encoding
                    ConfigDef.Importance.MEDIUM,
                    "CSV encoding."
            )
            .define(
                    GP_LOG_ERRORS,
                    ConfigDef.Type.BOOLEAN,
                    GP_LOG_ERRORS_DEFAULT, // Default log errors
                    ConfigDef.Importance.MEDIUM,
                    "Whether to log CSV errors."
            ).define(
                    GREENPLUM_HOME_CONFIG,
                    ConfigDef.Type.STRING,
                    GREENPLUM_HOME_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    GREENPLUM_HOME_DOC
            ).define(
                    GPFDIST_HOST,
                    ConfigDef.Type.STRING,
                    GPFDIST_HOST_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    GPFDIST_HOST_DOC
            ).define(
                    KEEP_GP_FILES_CONFIG,
                    ConfigDef.Type.BOOLEAN,
                    KEEP_GP_FILES_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    KEEP_GP_FILES_DOC
            ).define(
                    DB_SCHEMA,
                    ConfigDef.Type.STRING,
                    DB_SCHEMA_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    DB_SCHEMA_DOC
            ).define(
                    GPSS_HOST,
                    ConfigDef.Type.STRING,
                    GPSS_HOST_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    GPSS_HOST_DOC
            ).define(
                    GPSS_PORT,
                    ConfigDef.Type.STRING,
                    GPSS_PORT_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    GPSS_PORT_DOC
            ).define(
                    GP_ERRORS_PERCENTAGE_LIMIT,
                    ConfigDef.Type.INT,
                    GP_ERRORS_PERCENTAGE_LIMIT_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    GP_ERRORS_PERCENTAGE_LIMIT_DOC

            ).define(
                    GPSS_USE_STICKY_SESSION,
                    ConfigDef.Type.BOOLEAN,
                    GPSS_USE_STICKY_SESSION_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    GPSS_USE_STICKY_SESSION_DOC

            ).define(
                    GP_MAX_LINE_LENGTH,
                    ConfigDef.Type.LONG,
                    GP_MAX_LINE_LENGTH_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    GP_MAX_LINE_LENGTH_DOC
            ).define(
                    UPDATE_MODE,
                    ConfigDef.Type.STRING,
                    UPDATE_MODE_DEFAULT,
                    EnumValidator.in(UpdateMode.values()),
                    ConfigDef.Importance.MEDIUM,
                    UPDATE_MODE_DOC,
                    WRITES_GROUP,
                    1,
                    ConfigDef.Width.MEDIUM,
                    UPDATE_MODE_DISPLAY
            ).define(
                    NULL_STRING,
                    ConfigDef.Type.STRING,
                    NULL_STRING_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    NULL_STRING_DOC
            ).define(DEBUG_LOG, ConfigDef.Type.BOOLEAN, DEBUG_LOG_DEFAULT, ConfigDef.Importance.MEDIUM, DEBUG_LOG_DOC)
            .define(
                    COLUMN_SELECTION_STRATEGY,
                    ConfigDef.Type.STRING,
                    COLUMN_SELECTION_STRATEGY_DEFAULT,
                    EnumValidator.in(ColumnSelectionStrategy.values()),
                    ConfigDef.Importance.MEDIUM,
                    COLUMN_SELECTION_STRATEGY_DOCS,
                    WRITES_GROUP,
                    1,
                    ConfigDef.Width.MEDIUM,
                    COLUMN_SELECTION_STRATEGY_DISPLAY
            ).define(
                    COLUMN_ALTERNATIVE,
                    ConfigDef.Type.STRING,
                    COLUMN_ALTERNATIVE_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    COLUMN_ALTERNATIVE_DOC,
                    WRITES_GROUP,
                    1,
                    ConfigDef.Width.LONG,
                    COLUMN_ALTERNATIVE_DISPLAY
                    // timestamp, time and date formats and timezone
            ).define(
            TIME_FROM_FORMAT,
            ConfigDef.Type.STRING,
            TIME_FROM_FORMAT_DEFAULT,
            ConfigDef.Importance.MEDIUM,
            TIME_FROM_FORMAT_DOC,
            WRITES_GROUP,
                    1,
            ConfigDef.Width.LONG,
            TIME_FROM_FORMAT_DISPLAY
            // timestamp, time and date formats and timezone
    ).define(
            TIME_TO_FORMAT,
            ConfigDef.Type.STRING,
            TIME_TO_FORMAT_DEFAULT,
            ConfigDef.Importance.MEDIUM,
            TIME_TO_FORMAT_DOC,
            WRITES_GROUP,
                    1,
            ConfigDef.Width.LONG,
            TIME_TO_FORMAT_DISPLAY
    ).define(
            TIMESTAMP_FROM_FORMAT,
            ConfigDef.Type.STRING,
            TIMESTAMP_FROM_FORMAT_DEFAULT,
            ConfigDef.Importance.MEDIUM,
            TIMESTAMP_FROM_FORMAT_DOC,
            WRITES_GROUP,
                    1,
            ConfigDef.Width.LONG,
            TIMESTAMP_FROM_FORMAT_DISPLAY
    ).define(
            TIMESTAMP_TO_FORMAT,
            ConfigDef.Type.STRING,
            TIMESTAMP_TO_FORMAT_DEFAULT,
            ConfigDef.Importance.MEDIUM,
            TIMESTAMP_TO_FORMAT_DOC,
            WRITES_GROUP,
                    1,
            ConfigDef.Width.LONG,
            TIMESTAMP_TO_FORMAT_DISPLAY
    ).define(
            DATE_FROM_FORMAT,
            ConfigDef.Type.STRING,
            DATE_FROM_FORMAT_DEFAULT,
            ConfigDef.Importance.MEDIUM,
            DATE_FROM_FORMAT_DOC,
            WRITES_GROUP,
                    1,
            ConfigDef.Width.LONG,
            DATE_FROM_FORMAT_DISPLAY
    ).define(
            DATE_TO_FORMAT,
            ConfigDef.Type.STRING,
            DATE_TO_FORMAT_DEFAULT,
            ConfigDef.Importance.MEDIUM,
            DATE_TO_FORMAT_DOC,
            WRITES_GROUP,
                    1,
            ConfigDef.Width.LONG,
            DATE_TO_FORMAT_DISPLAY
    ).define(
                    TIMESTAMP_AUTO_CONVERT,
                    ConfigDef.Type.BOOLEAN,
                    TIMESTAMP_AUTO_CONVERT_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    TIMESTAMP_AUTO_CONVERT_DOC,
                    WRITES_GROUP,
                    1,
                    ConfigDef.Width.SHORT,
                    TIMESTAMP_AUTO_CONVERT_DISPLAY)
            .define(
                    DATE_FROM_TIMEZONE,
                    ConfigDef.Type.STRING,
                    DATE_FROM_TIMEZONE_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    DATE_FROM_TIMEZONE_DOC,
                    WRITES_GROUP,
                    1,
                    ConfigDef.Width.MEDIUM,
                    DATE_FROM_TIMEZONE_DISPLAY
            ).define(
                    DATE_TO_TIMEZONE,
                    ConfigDef.Type.STRING,
                    DATE_TO_TIMEZONE_DEFAULT,
                    ConfigDef.Importance.MEDIUM,
                    DATE_TO_TIMEZONE_DOC,
                    WRITES_GROUP,
                    1,
                    ConfigDef.Width.MEDIUM,
                    DATE_TO_TIMEZONE_DISPLAY
            );
//


    public static void printConfigDefTable(ConfigDef configDef) {

        System.out.format("%-30s %-20s %-30s %-15s %-50s%n", "Name", "Type", "Default", "Importance", "Documentation");
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        for (ConfigDef.ConfigKey configKey : configDef.configKeys().values()) {
            System.out.format("%-30s %-20s %-30s %-15s %-50s%n",
                    configKey.name,
                    configKey.type,
                    configKey.defaultValue,
                    configKey.importance,
                    configKey.documentation);
        }
    }

    public final String connectorName;
    public final String connectionUrl;
    public final String connectionUser;
    public final String connectionPassword;
    public final int connectionAttempts;
    public final long connectionBackoffMs;
    public final String tableNameFormat;
    public final int batchSize;
    public final boolean deleteEnabled;
    public final int maxRetries;
    public final long maxBatchWaitTime;
    public final int retryBackoffMs;
    public final boolean autoCreate;
    public final boolean autoEvolve;
    public final BatchInsertMode batchInsertMode;
    public final InsertMode insertMode;
    public final PrimaryKeyMode pkMode;
    public final List<String> pkFields;
    public final Set<String> fieldsWhitelist;
    public final String dialectName;
    public final TimeZone timeZone;
    public final EnumSet<TableType> tableTypes;
    public final boolean useHoldlockInMerge;

    public final boolean trimSensitiveLogsEnabled;
    public final List<Integer> portRange;

    public final String delimiter;
    public final int gpErrorsLimit;
    public final boolean csvHeader;
    public final String csvQuote;
    public final String csvEncoding;
    public final boolean gpLogErrors;

    public final String greenplumHome;

    public final boolean keepGpFiles;

    public final String gpfdistHost;

    public final String dbSchema;
    public final String gpssHost;
    public final String gpssPort;
    public final Integer gpErrorsPercentageLimit;
    public final boolean gpssUseStickySession;

    public boolean printDebugLogs;

    //gpfdist

    /**
     * Error reject limit. (String, default: ``)
     */
    public String segmentRejectLimit = null;//"999999"; // TODO - move to config

    /**
     * Error reject type, either `rows` or `percent`. (String, default: `rows`)
     */
    public final SegmentRejectType segmentRejectType = SegmentRejectType.ROWS;

    /**
     * Null string definition. (String, default: `NULL`)
     */
    public final String nullString;

    public long gpMaxLineLength = 65535;

    public final UpdateMode updateMode;

    public final ColumnSelectionStrategy columnSelectionStrategy;

    public final HashMap<String,String> columnAlternative = new HashMap<>(); // columnName, alternative

    public final String timeFromFormat;
    public final String timeToFormat;
    public final String timestampFromFormat;
    public final String timestampToFormat;
    public final String dateFromFormat;
    public final String dateToFormat;

    public final boolean timestampAutoConvert;

    public TimeZone dateFromTimezone;
    public TimeZone dateToTimezone;




    public JdbcSinkConfig(Map<?, ?> props) {
        super(CONFIG_DEF, props);
        connectorName = ConfigUtils.connectorName(props);
        connectionUrl = getString(CONNECTION_URL);
        connectionUser = getString(CONNECTION_USER);
        connectionPassword = getPasswordValue(CONNECTION_PASSWORD);
        connectionAttempts = getInt(CONNECTION_ATTEMPTS);
        connectionBackoffMs = getLong(CONNECTION_BACKOFF);
        tableNameFormat = getString(TABLE_NAME_FORMAT).trim();
        batchSize = getInt(BATCH_SIZE);
        deleteEnabled = getBoolean(DELETE_ENABLED);
        maxRetries = getInt(MAX_RETRIES);
        maxBatchWaitTime = getLong(MAX_BATCH_WAIT_TIME);
        retryBackoffMs = getInt(RETRY_BACKOFF_MS);
        autoCreate = getBoolean(AUTO_CREATE);
        autoEvolve = getBoolean(AUTO_EVOLVE);
        batchInsertMode = BatchInsertMode.valueOf(getString(BATCH_INSERT_MODE).toUpperCase());
        insertMode = InsertMode.valueOf(getString(INSERT_MODE).toUpperCase());
        pkMode = PrimaryKeyMode.valueOf(getString(PK_MODE).toUpperCase());
        pkFields = getList(PK_FIELDS);
        dialectName = getString(DIALECT_NAME_CONFIG);
        fieldsWhitelist = new HashSet<>(getList(FIELDS_WHITELIST));
        String dbTimeZone = getString(DB_TIMEZONE_CONFIG);
        timeZone = TimeZone.getTimeZone(ZoneId.of(dbTimeZone));
        useHoldlockInMerge = getBoolean(MSSQL_USE_MERGE_HOLDLOCK);
        trimSensitiveLogsEnabled = getBoolean(TRIM_SENSITIVE_LOG_ENABLED);
        if (deleteEnabled && pkMode != PrimaryKeyMode.RECORD_KEY) {
            throw new ConfigException(
                    "Primary key mode must be 'record_key' when delete support is enabled");
        }
        tableTypes = TableType.parse(getList(TABLE_TYPES_CONFIG));
        portRange = getList(PORT_RANGE).stream().map(Integer::parseInt).collect(Collectors.toList());
        delimiter = getString(DATA_DELIMITER);
        gpErrorsLimit = getInt(GP_ERRORS_LIMIT);
        csvHeader = getBoolean(CSV_HEADER);
        csvQuote = getString(CSV_QUOTE);
        csvEncoding = getString(CSV_ENCODING);
        gpLogErrors = getBoolean(GP_LOG_ERRORS);
        greenplumHome = getString(GREENPLUM_HOME_CONFIG);
        keepGpFiles = getBoolean(KEEP_GP_FILES_CONFIG);

        gpfdistHost = getString(GPFDIST_HOST);
        dbSchema = getString(DB_SCHEMA);

        gpssHost = getString(GPSS_HOST);
        gpssPort = getString(GPSS_PORT);

        gpErrorsPercentageLimit = getInt(GP_ERRORS_PERCENTAGE_LIMIT);
        gpssUseStickySession = getBoolean(GPSS_USE_STICKY_SESSION);
        gpMaxLineLength = getLong(GP_MAX_LINE_LENGTH);

        updateMode = UpdateMode.valueOf(getString(UPDATE_MODE).toUpperCase());

        nullString = getString(NULL_STRING);
        printDebugLogs = getBoolean(DEBUG_LOG);
        columnSelectionStrategy = ColumnSelectionStrategy.valueOf(getString(COLUMN_SELECTION_STRATEGY).toUpperCase());
        String columnAlternatives = getString(COLUMN_ALTERNATIVE);
        if(columnAlternatives != null && !columnAlternatives.isEmpty()) {
            String[] alternatives = columnAlternatives.split(",");
            for(String alternative : alternatives) {
                String[] alternativeParts = alternative.split(":");
                if(alternativeParts.length == 2) {
                    columnAlternative.put(alternativeParts[0], alternativeParts[1]);
                }
            }
        }
        printConfigDefTable(CONFIG_DEF);
        timeFromFormat = getString(TIME_FROM_FORMAT);
        timeToFormat = getString(TIME_TO_FORMAT);
        timestampFromFormat = getString(TIMESTAMP_FROM_FORMAT);
        timestampToFormat = getString(TIMESTAMP_TO_FORMAT);
        dateFromFormat = getString(DATE_FROM_FORMAT);
        dateToFormat = getString(DATE_TO_FORMAT);
        timestampAutoConvert = getBoolean(TIMESTAMP_AUTO_CONVERT);
        String fromTimezone = getString(DATE_FROM_TIMEZONE);
        if (fromTimezone != null && !fromTimezone.isEmpty()){
            dateFromTimezone = TimeZone.getTimeZone(ZoneId.of(fromTimezone));
        }
         String toTimezone = getString(DATE_TO_TIMEZONE);
        if (toTimezone != null && !toTimezone.isEmpty()) {
            dateToTimezone = TimeZone.getTimeZone(ZoneId.of(toTimezone));
        }
    }

    private String getPasswordValue(String key) {
        Password password = getPassword(key);
        if (password != null) {
            return password.value();
        }
        return null;
    }

    public String connectorName() {
        return connectorName;
    }

    public EnumSet<TableType> tableTypes() {
        return tableTypes;
    }

    public Set<String> tableTypeNames() {
        return tableTypes().stream().map(TableType::toString).collect(Collectors.toSet());
    }

    private static class EnumValidator implements ConfigDef.Validator {
        private final List<String> canonicalValues;
        private final Set<String> validValues;

        private EnumValidator(List<String> canonicalValues, Set<String> validValues) {
            this.canonicalValues = canonicalValues;
            this.validValues = validValues;
        }

        public static <E> EnumValidator in(E[] enumerators) {
            final List<String> canonicalValues = new ArrayList<>(enumerators.length);
            final Set<String> validValues = new HashSet<>(enumerators.length * 2);
            for (E e : enumerators) {
                canonicalValues.add(e.toString().toLowerCase());
                validValues.add(e.toString().toUpperCase());
                validValues.add(e.toString().toLowerCase());
            }
            return new EnumValidator(canonicalValues, validValues);
        }

        @Override
        public void ensureValid(String key, Object value) {
            if (!validValues.contains(value)) {
                throw new ConfigException(key, value, "Invalid enumerator");
            }
        }

        @Override
        public String toString() {
            return canonicalValues.toString();
        }
    }

    public static void main(String... args) {
        System.out.println(CONFIG_DEF.toEnrichedRst());
    }

}
