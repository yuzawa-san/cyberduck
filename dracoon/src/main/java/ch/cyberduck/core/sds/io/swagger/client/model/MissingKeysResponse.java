/*
 * DRACOON API
 * REST Web Services for DRACOON<br><br>This page provides an overview of all available and documented DRACOON APIs, which are grouped by tags.<br>Each tag provides a collection of APIs that are intended for a specific area of the DRACOON.<br><br><a title='Developer Information' href='https://developer.dracoon.com'>Developer Information</a>&emsp;&emsp;<a title='Get SDKs on GitHub' href='https://github.com/dracoon'>Get SDKs on GitHub</a><br><br><a title='Terms of service' href='https://www.dracoon.com/terms/general-terms-and-conditions/'>Terms of service</a>
 *
 * OpenAPI spec version: 4.30.0-beta.4
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ch.cyberduck.core.sds.io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import ch.cyberduck.core.sds.io.swagger.client.model.FileFileKeys;
import ch.cyberduck.core.sds.io.swagger.client.model.Range;
import ch.cyberduck.core.sds.io.swagger.client.model.UserIdFileIdItem;
import ch.cyberduck.core.sds.io.swagger.client.model.UserUserPublicKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
/**
 * Missing keys information
 */
@Schema(description = "Missing keys information")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-08-16T11:28:10.116221+02:00[Europe/Zurich]")
public class MissingKeysResponse {
  @JsonProperty("range")
  private Range range = null;

  @JsonProperty("items")
  private List<UserIdFileIdItem> items = null;

  @JsonProperty("users")
  private List<UserUserPublicKey> users = null;

  @JsonProperty("files")
  private List<FileFileKeys> files = null;

  public MissingKeysResponse range(Range range) {
    this.range = range;
    return this;
  }

   /**
   * Get range
   * @return range
  **/
  @Schema(description = "")
  public Range getRange() {
    return range;
  }

  public void setRange(Range range) {
    this.range = range;
  }

  public MissingKeysResponse items(List<UserIdFileIdItem> items) {
    this.items = items;
    return this;
  }

  public MissingKeysResponse addItemsItem(UserIdFileIdItem itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

   /**
   * List of user ID and file ID mappings
   * @return items
  **/
  @Schema(description = "List of user ID and file ID mappings")
  public List<UserIdFileIdItem> getItems() {
    return items;
  }

  public void setItems(List<UserIdFileIdItem> items) {
    this.items = items;
  }

  public MissingKeysResponse users(List<UserUserPublicKey> users) {
    this.users = users;
    return this;
  }

  public MissingKeysResponse addUsersItem(UserUserPublicKey usersItem) {
    if (this.users == null) {
      this.users = new ArrayList<>();
    }
    this.users.add(usersItem);
    return this;
  }

   /**
   * List of user public keys
   * @return users
  **/
  @Schema(description = "List of user public keys")
  public List<UserUserPublicKey> getUsers() {
    return users;
  }

  public void setUsers(List<UserUserPublicKey> users) {
    this.users = users;
  }

  public MissingKeysResponse files(List<FileFileKeys> files) {
    this.files = files;
    return this;
  }

  public MissingKeysResponse addFilesItem(FileFileKeys filesItem) {
    if (this.files == null) {
      this.files = new ArrayList<>();
    }
    this.files.add(filesItem);
    return this;
  }

   /**
   * List of file keys
   * @return files
  **/
  @Schema(description = "List of file keys")
  public List<FileFileKeys> getFiles() {
    return files;
  }

  public void setFiles(List<FileFileKeys> files) {
    this.files = files;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MissingKeysResponse missingKeysResponse = (MissingKeysResponse) o;
    return Objects.equals(this.range, missingKeysResponse.range) &&
        Objects.equals(this.items, missingKeysResponse.items) &&
        Objects.equals(this.users, missingKeysResponse.users) &&
        Objects.equals(this.files, missingKeysResponse.files);
  }

  @Override
  public int hashCode() {
    return Objects.hash(range, items, users, files);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MissingKeysResponse {\n");
    
    sb.append("    range: ").append(toIndentedString(range)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    users: ").append(toIndentedString(users)).append("\n");
    sb.append("    files: ").append(toIndentedString(files)).append("\n");
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
