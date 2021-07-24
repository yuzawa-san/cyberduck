/*
 * Files.com API
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 0.0.1
 * Contact: support@files.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ch.cyberduck.core.brick.io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
/**
 * PermissionsBody
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-07-23T20:47:00.742522+02:00[Europe/Paris]")
public class PermissionsBody {
  @JsonProperty("group_id")
  private Integer groupId = null;

  @JsonProperty("path")
  private String path = null;

  @JsonProperty("permission")
  private String permission = null;

  @JsonProperty("recursive")
  private Boolean recursive = null;

  @JsonProperty("user_id")
  private Integer userId = null;

  @JsonProperty("username")
  private String username = null;

  public PermissionsBody groupId(Integer groupId) {
    this.groupId = groupId;
    return this;
  }

   /**
   * Group ID
   * @return groupId
  **/
  @Schema(description = "Group ID")
  public Integer getGroupId() {
    return groupId;
  }

  public void setGroupId(Integer groupId) {
    this.groupId = groupId;
  }

  public PermissionsBody path(String path) {
    this.path = path;
    return this;
  }

   /**
   * Folder path
   * @return path
  **/
  @Schema(description = "Folder path")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public PermissionsBody permission(String permission) {
    this.permission = permission;
    return this;
  }

   /**
   *  Permission type.  Can be &#x60;admin&#x60;, &#x60;full&#x60;, &#x60;readonly&#x60;, &#x60;writeonly&#x60;, &#x60;list&#x60;, or &#x60;history&#x60;
   * @return permission
  **/
  @Schema(description = " Permission type.  Can be `admin`, `full`, `readonly`, `writeonly`, `list`, or `history`")
  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public PermissionsBody recursive(Boolean recursive) {
    this.recursive = recursive;
    return this;
  }

   /**
   * Apply to subfolders recursively?
   * @return recursive
  **/
  @Schema(description = "Apply to subfolders recursively?")
  public Boolean isRecursive() {
    return recursive;
  }

  public void setRecursive(Boolean recursive) {
    this.recursive = recursive;
  }

  public PermissionsBody userId(Integer userId) {
    this.userId = userId;
    return this;
  }

   /**
   * User ID.  Provide &#x60;username&#x60; or &#x60;user_id&#x60;
   * @return userId
  **/
  @Schema(description = "User ID.  Provide `username` or `user_id`")
  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public PermissionsBody username(String username) {
    this.username = username;
    return this;
  }

   /**
   * User username.  Provide &#x60;username&#x60; or &#x60;user_id&#x60;
   * @return username
  **/
  @Schema(description = "User username.  Provide `username` or `user_id`")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PermissionsBody permissionsBody = (PermissionsBody) o;
    return Objects.equals(this.groupId, permissionsBody.groupId) &&
        Objects.equals(this.path, permissionsBody.path) &&
        Objects.equals(this.permission, permissionsBody.permission) &&
        Objects.equals(this.recursive, permissionsBody.recursive) &&
        Objects.equals(this.userId, permissionsBody.userId) &&
        Objects.equals(this.username, permissionsBody.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, path, permission, recursive, userId, username);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PermissionsBody {\n");
    
    sb.append("    groupId: ").append(toIndentedString(groupId)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    permission: ").append(toIndentedString(permission)).append("\n");
    sb.append("    recursive: ").append(toIndentedString(recursive)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
