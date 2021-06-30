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
import org.joda.time.DateTime;

/**
 * List Settings Changes
 */
@ApiModel(description = "List Settings Changes")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2021-06-30T21:29:25.490+02:00")
public class SettingsChangeEntity {
  @JsonProperty("change_details")
  private Object changeDetails = null;

  @JsonProperty("created_at")
  private DateTime createdAt = null;

  @JsonProperty("user_id")
  private Integer userId = null;

  public SettingsChangeEntity changeDetails(Object changeDetails) {
    this.changeDetails = changeDetails;
    return this;
  }

   /**
   * Specifics on what changed.
   * @return changeDetails
  **/
  @ApiModelProperty(example = "\"{ domain: [\\\"olddomain.com', \\\"newdomain.com\\\"] }\"", value = "Specifics on what changed.")
  public Object getChangeDetails() {
    return changeDetails;
  }

  public void setChangeDetails(Object changeDetails) {
    this.changeDetails = changeDetails;
  }

  public SettingsChangeEntity createdAt(DateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

   /**
   * The time this change was made
   * @return createdAt
  **/
  @ApiModelProperty(example = "2000-01-01T01:00:00Z", value = "The time this change was made")
  public DateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(DateTime createdAt) {
    this.createdAt = createdAt;
  }

  public SettingsChangeEntity userId(Integer userId) {
    this.userId = userId;
    return this;
  }

   /**
   * The user id responsible for this change
   * @return userId
  **/
  @ApiModelProperty(example = "1", value = "The user id responsible for this change")
  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SettingsChangeEntity settingsChangeEntity = (SettingsChangeEntity) o;
    return Objects.equals(this.changeDetails, settingsChangeEntity.changeDetails) &&
        Objects.equals(this.createdAt, settingsChangeEntity.createdAt) &&
        Objects.equals(this.userId, settingsChangeEntity.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(changeDetails, createdAt, userId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SettingsChangeEntity {\n");
    
    sb.append("    changeDetails: ").append(toIndentedString(changeDetails)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
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

