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
 * List Behaviors
 */
@Schema(description = "List Behaviors")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-07-12T12:23:43.971535+02:00[Europe/Paris]")
public class BehaviorEntity {
  @JsonProperty("attachment_url")
  private String attachmentUrl = null;

  @JsonProperty("behavior")
  private String behavior = null;

  @JsonProperty("id")
  private Integer id = null;

  @JsonProperty("path")
  private String path = null;

  @JsonProperty("value")
  private Object value = null;

  public BehaviorEntity attachmentUrl(String attachmentUrl) {
    this.attachmentUrl = attachmentUrl;
    return this;
  }

   /**
   * URL for attached file
   * @return attachmentUrl
  **/
  @Schema(description = "URL for attached file")
  public String getAttachmentUrl() {
    return attachmentUrl;
  }

  public void setAttachmentUrl(String attachmentUrl) {
    this.attachmentUrl = attachmentUrl;
  }

  public BehaviorEntity behavior(String behavior) {
    this.behavior = behavior;
    return this;
  }

   /**
   * Behavior type.
   * @return behavior
  **/
  @Schema(example = "webhook", description = "Behavior type.")
  public String getBehavior() {
    return behavior;
  }

  public void setBehavior(String behavior) {
    this.behavior = behavior;
  }

  public BehaviorEntity id(Integer id) {
    this.id = id;
    return this;
  }

   /**
   * Folder behavior ID
   * @return id
  **/
  @Schema(example = "1", description = "Folder behavior ID")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public BehaviorEntity path(String path) {
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

  public BehaviorEntity value(Object value) {
    this.value = value;
    return this;
  }

   /**
   * Settings for this behavior.  See the section above for an example value to provide here.  Formatting is different for each Behavior type.  May be sent as nested JSON or a single JSON-encoded string.  If using XML encoding for the API call, this data must be sent as a JSON-encoded string.
   * @return value
  **/
  @Schema(example = "{ \"method\": \"GET\" }", description = "Settings for this behavior.  See the section above for an example value to provide here.  Formatting is different for each Behavior type.  May be sent as nested JSON or a single JSON-encoded string.  If using XML encoding for the API call, this data must be sent as a JSON-encoded string.")
  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BehaviorEntity behaviorEntity = (BehaviorEntity) o;
    return Objects.equals(this.attachmentUrl, behaviorEntity.attachmentUrl) &&
        Objects.equals(this.behavior, behaviorEntity.behavior) &&
        Objects.equals(this.id, behaviorEntity.id) &&
        Objects.equals(this.path, behaviorEntity.path) &&
        Objects.equals(this.value, behaviorEntity.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(attachmentUrl, behavior, id, path, value);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BehaviorEntity {\n");
    
    sb.append("    attachmentUrl: ").append(toIndentedString(attachmentUrl)).append("\n");
    sb.append("    behavior: ").append(toIndentedString(behavior)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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
