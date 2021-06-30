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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * List Groups
 */
@ApiModel(description = "List Groups")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2021-06-30T21:29:25.490+02:00")
public class GroupEntity {
  @JsonProperty("id")
  private Integer id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("admin_ids")
  private String adminIds = null;

  @JsonProperty("notes")
  private String notes = null;

  @JsonProperty("user_ids")
  private List<Integer> userIds = null;

  @JsonProperty("usernames")
  private List<String> usernames = null;

  public GroupEntity id(Integer id) {
    this.id = id;
    return this;
  }

   /**
   * Group ID
   * @return id
  **/
  @ApiModelProperty(example = "1", value = "Group ID")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public GroupEntity name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Group name
   * @return name
  **/
  @ApiModelProperty(example = "owners", value = "Group name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public GroupEntity adminIds(String adminIds) {
    this.adminIds = adminIds;
    return this;
  }

   /**
   * List of user IDs who are group administrators (separated by commas)
   * @return adminIds
  **/
  @ApiModelProperty(example = "", value = "List of user IDs who are group administrators (separated by commas)")
  public String getAdminIds() {
    return adminIds;
  }

  public void setAdminIds(String adminIds) {
    this.adminIds = adminIds;
  }

  public GroupEntity notes(String notes) {
    this.notes = notes;
    return this;
  }

   /**
   * Notes about this group
   * @return notes
  **/
  @ApiModelProperty(example = "", value = "Notes about this group")
  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public GroupEntity userIds(List<Integer> userIds) {
    this.userIds = userIds;
    return this;
  }

  public GroupEntity addUserIdsItem(Integer userIdsItem) {
    if (this.userIds == null) {
      this.userIds = new ArrayList<>();
    }
    this.userIds.add(userIdsItem);
    return this;
  }

   /**
   * List of user IDs who belong to this group (separated by commas)
   * @return userIds
  **/
  @ApiModelProperty(example = "[1]", value = "List of user IDs who belong to this group (separated by commas)")
  public List<Integer> getUserIds() {
    return userIds;
  }

  public void setUserIds(List<Integer> userIds) {
    this.userIds = userIds;
  }

  public GroupEntity usernames(List<String> usernames) {
    this.usernames = usernames;
    return this;
  }

  public GroupEntity addUsernamesItem(String usernamesItem) {
    if (this.usernames == null) {
      this.usernames = new ArrayList<>();
    }
    this.usernames.add(usernamesItem);
    return this;
  }

   /**
   * List of usernames who belong to this group (separated by commas)
   * @return usernames
  **/
  @ApiModelProperty(example = "[\"user\"]", value = "List of usernames who belong to this group (separated by commas)")
  public List<String> getUsernames() {
    return usernames;
  }

  public void setUsernames(List<String> usernames) {
    this.usernames = usernames;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GroupEntity groupEntity = (GroupEntity) o;
    return Objects.equals(this.id, groupEntity.id) &&
        Objects.equals(this.name, groupEntity.name) &&
        Objects.equals(this.adminIds, groupEntity.adminIds) &&
        Objects.equals(this.notes, groupEntity.notes) &&
        Objects.equals(this.userIds, groupEntity.userIds) &&
        Objects.equals(this.usernames, groupEntity.usernames);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, adminIds, notes, userIds, usernames);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GroupEntity {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    adminIds: ").append(toIndentedString(adminIds)).append("\n");
    sb.append("    notes: ").append(toIndentedString(notes)).append("\n");
    sb.append("    userIds: ").append(toIndentedString(userIds)).append("\n");
    sb.append("    usernames: ").append(toIndentedString(usernames)).append("\n");
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

