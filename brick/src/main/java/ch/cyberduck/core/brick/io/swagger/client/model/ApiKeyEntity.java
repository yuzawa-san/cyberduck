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
import org.joda.time.DateTime;
/**
 * List Api Keys
 */
@Schema(description = "List Api Keys")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-07-23T20:47:00.742522+02:00[Europe/Paris]")
public class ApiKeyEntity {
  @JsonProperty("id")
  private Integer id = null;

  @JsonProperty("descriptive_label")
  private String descriptiveLabel = null;

  @JsonProperty("created_at")
  private DateTime createdAt = null;

  @JsonProperty("expires_at")
  private DateTime expiresAt = null;

  @JsonProperty("key")
  private String key = null;

  @JsonProperty("last_use_at")
  private DateTime lastUseAt = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("path")
  private String path = null;

  /**
   * Permissions for this API Key.  Keys with the &#x60;desktop_app&#x60; permission set only have the ability to do the functions provided in our Desktop App (File and Share Link operations).  Additional permission sets may become available in the future, such as for a Site Admin to give a key with no administrator privileges.  If you have ideas for permission sets, please let us know.
   */
  public enum PermissionSetEnum {
    NONE("none"),
    FULL("full"),
    DESKTOP_APP("desktop_app"),
    SYNC_APP("sync_app"),
    OFFICE_INTEGRATION("office_integration");

    private String value;

    PermissionSetEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    @JsonCreator
    public static PermissionSetEnum fromValue(String text) {
      for (PermissionSetEnum b : PermissionSetEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("permission_set")
  private PermissionSetEnum permissionSet = null;

  @JsonProperty("platform")
  private String platform = null;

  @JsonProperty("user_id")
  private Integer userId = null;

  public ApiKeyEntity id(Integer id) {
    this.id = id;
    return this;
  }

   /**
   * API Key ID
   * @return id
  **/
  @Schema(example = "1", description = "API Key ID")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public ApiKeyEntity descriptiveLabel(String descriptiveLabel) {
    this.descriptiveLabel = descriptiveLabel;
    return this;
  }

   /**
   * Unique label that describes this API key.  Useful for external systems where you may have API keys from multiple accounts and want a human-readable label for each key.
   * @return descriptiveLabel
  **/
  @Schema(example = "Site-wide API key for https://site.files.com/ (key ID #1)", description = "Unique label that describes this API key.  Useful for external systems where you may have API keys from multiple accounts and want a human-readable label for each key.")
  public String getDescriptiveLabel() {
    return descriptiveLabel;
  }

  public void setDescriptiveLabel(String descriptiveLabel) {
    this.descriptiveLabel = descriptiveLabel;
  }

  public ApiKeyEntity createdAt(DateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

   /**
   * Time which API Key was created
   * @return createdAt
  **/
  @Schema(description = "Time which API Key was created")
  public DateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(DateTime createdAt) {
    this.createdAt = createdAt;
  }

  public ApiKeyEntity expiresAt(DateTime expiresAt) {
    this.expiresAt = expiresAt;
    return this;
  }

   /**
   * API Key expiration date
   * @return expiresAt
  **/
  @Schema(description = "API Key expiration date")
  public DateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(DateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public ApiKeyEntity key(String key) {
    this.key = key;
    return this;
  }

   /**
   * API Key actual key string
   * @return key
  **/
  @Schema(example = "[key]", description = "API Key actual key string")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public ApiKeyEntity lastUseAt(DateTime lastUseAt) {
    this.lastUseAt = lastUseAt;
    return this;
  }

   /**
   * API Key last used - note this value is only updated once per 3 hour period, so the &#x27;actual&#x27; time of last use may be up to 3 hours later than this timestamp.
   * @return lastUseAt
  **/
  @Schema(description = "API Key last used - note this value is only updated once per 3 hour period, so the 'actual' time of last use may be up to 3 hours later than this timestamp.")
  public DateTime getLastUseAt() {
    return lastUseAt;
  }

  public void setLastUseAt(DateTime lastUseAt) {
    this.lastUseAt = lastUseAt;
  }

  public ApiKeyEntity name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Internal name for the API Key.  For your use.
   * @return name
  **/
  @Schema(example = "My Main API Key", description = "Internal name for the API Key.  For your use.")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ApiKeyEntity path(String path) {
    this.path = path;
    return this;
  }

   /**
   * Folder path restriction for this api key.
   * @return path
  **/
  @Schema(example = "shared/docs", description = "Folder path restriction for this api key.")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public ApiKeyEntity permissionSet(PermissionSetEnum permissionSet) {
    this.permissionSet = permissionSet;
    return this;
  }

   /**
   * Permissions for this API Key.  Keys with the &#x60;desktop_app&#x60; permission set only have the ability to do the functions provided in our Desktop App (File and Share Link operations).  Additional permission sets may become available in the future, such as for a Site Admin to give a key with no administrator privileges.  If you have ideas for permission sets, please let us know.
   * @return permissionSet
  **/
  @Schema(example = "full", description = "Permissions for this API Key.  Keys with the `desktop_app` permission set only have the ability to do the functions provided in our Desktop App (File and Share Link operations).  Additional permission sets may become available in the future, such as for a Site Admin to give a key with no administrator privileges.  If you have ideas for permission sets, please let us know.")
  public PermissionSetEnum getPermissionSet() {
    return permissionSet;
  }

  public void setPermissionSet(PermissionSetEnum permissionSet) {
    this.permissionSet = permissionSet;
  }

  public ApiKeyEntity platform(String platform) {
    this.platform = platform;
    return this;
  }

   /**
   * If this API key represents a Desktop app, what platform was it created on?
   * @return platform
  **/
  @Schema(example = "win32", description = "If this API key represents a Desktop app, what platform was it created on?")
  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public ApiKeyEntity userId(Integer userId) {
    this.userId = userId;
    return this;
  }

   /**
   * User ID for the owner of this API Key.  May be blank for Site-wide API Keys.
   * @return userId
  **/
  @Schema(example = "1", description = "User ID for the owner of this API Key.  May be blank for Site-wide API Keys.")
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
    ApiKeyEntity apiKeyEntity = (ApiKeyEntity) o;
    return Objects.equals(this.id, apiKeyEntity.id) &&
        Objects.equals(this.descriptiveLabel, apiKeyEntity.descriptiveLabel) &&
        Objects.equals(this.createdAt, apiKeyEntity.createdAt) &&
        Objects.equals(this.expiresAt, apiKeyEntity.expiresAt) &&
        Objects.equals(this.key, apiKeyEntity.key) &&
        Objects.equals(this.lastUseAt, apiKeyEntity.lastUseAt) &&
        Objects.equals(this.name, apiKeyEntity.name) &&
        Objects.equals(this.path, apiKeyEntity.path) &&
        Objects.equals(this.permissionSet, apiKeyEntity.permissionSet) &&
        Objects.equals(this.platform, apiKeyEntity.platform) &&
        Objects.equals(this.userId, apiKeyEntity.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, descriptiveLabel, createdAt, expiresAt, key, lastUseAt, name, path, permissionSet, platform, userId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiKeyEntity {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    descriptiveLabel: ").append(toIndentedString(descriptiveLabel)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    expiresAt: ").append(toIndentedString(expiresAt)).append("\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    lastUseAt: ").append(toIndentedString(lastUseAt)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    permissionSet: ").append(toIndentedString(permissionSet)).append("\n");
    sb.append("    platform: ").append(toIndentedString(platform)).append("\n");
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
