// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: gpss.proto

package io.confluent.connect.jdbc.gp.gpss.api;

public interface ListTableRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:api.ListTableRequest)
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
   * 'public' is the default if no Schema is provided
   * </pre>
   *
   * <code>string Schema = 2;</code>
   */
  String getSchema();
  /**
   * <pre>
   * 'public' is the default if no Schema is provided
   * </pre>
   *
   * <code>string Schema = 2;</code>
   */
  com.google.protobuf.ByteString
      getSchemaBytes();
}