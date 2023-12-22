// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: gpss.proto

package io.confluent.connect.jdbc.gp.gpss.api;

public interface WriteRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:api.WriteRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.api.Session Session = 1;</code>
   */
  boolean hasSession();
  /**
   * <code>.api.Session Session = 1;</code>
   */
  Session getSession();
  /**
   * <code>.api.Session Session = 1;</code>
   */
  SessionOrBuilder getSessionOrBuilder();

  /**
   * <pre>
   * The data to load into the target table
   * </pre>
   *
   * <code>repeated .api.RowData Rows = 2;</code>
   */
  java.util.List<RowData> 
      getRowsList();
  /**
   * <pre>
   * The data to load into the target table
   * </pre>
   *
   * <code>repeated .api.RowData Rows = 2;</code>
   */
  RowData getRows(int index);
  /**
   * <pre>
   * The data to load into the target table
   * </pre>
   *
   * <code>repeated .api.RowData Rows = 2;</code>
   */
  int getRowsCount();
  /**
   * <pre>
   * The data to load into the target table
   * </pre>
   *
   * <code>repeated .api.RowData Rows = 2;</code>
   */
  java.util.List<? extends RowDataOrBuilder> 
      getRowsOrBuilderList();
  /**
   * <pre>
   * The data to load into the target table
   * </pre>
   *
   * <code>repeated .api.RowData Rows = 2;</code>
   */
  RowDataOrBuilder getRowsOrBuilder(
      int index);
}
