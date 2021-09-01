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
 * PreviewEntity
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-08-17T09:08:22.070861+02:00[Europe/Zurich]")
public class PreviewEntity {
  @JsonProperty("id")
  private Integer id = null;

  @JsonProperty("status")
  private String status = null;

  @JsonProperty("download_uri")
  private String downloadUri = null;

  @JsonProperty("type")
  private String type = null;

  @JsonProperty("size")
  private Long size = null;

  public PreviewEntity id(Integer id) {
    this.id = id;
    return this;
  }

   /**
   * Preview ID
   * @return id
  **/
  @Schema(example = "1", description = "Preview ID")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public PreviewEntity status(String status) {
    this.status = status;
    return this;
  }

   /**
   * Preview status.  Can be invalid, not_generated, generating, complete, or file_too_large
   * @return status
  **/
  @Schema(example = "complete", description = "Preview status.  Can be invalid, not_generated, generating, complete, or file_too_large")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public PreviewEntity downloadUri(String downloadUri) {
    this.downloadUri = downloadUri;
    return this;
  }

   /**
   * Link to download preview
   * @return downloadUri
  **/
  @Schema(example = "https://mysite.files.com/...", description = "Link to download preview")
  public String getDownloadUri() {
    return downloadUri;
  }

  public void setDownloadUri(String downloadUri) {
    this.downloadUri = downloadUri;
  }

  public PreviewEntity type(String type) {
    this.type = type;
    return this;
  }

   /**
   * Preview status.  Can be invalid, not_generated, generating, complete, or file_too_large
   * @return type
  **/
  @Schema(example = "complete", description = "Preview status.  Can be invalid, not_generated, generating, complete, or file_too_large")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public PreviewEntity size(Long size) {
    this.size = size;
    return this;
  }

   /**
   * Preview size
   * @return size
  **/
  @Schema(example = "1024", description = "Preview size")
  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PreviewEntity previewEntity = (PreviewEntity) o;
    return Objects.equals(this.id, previewEntity.id) &&
        Objects.equals(this.status, previewEntity.status) &&
        Objects.equals(this.downloadUri, previewEntity.downloadUri) &&
        Objects.equals(this.type, previewEntity.type) &&
        Objects.equals(this.size, previewEntity.size);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, status, downloadUri, type, size);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PreviewEntity {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    downloadUri: ").append(toIndentedString(downloadUri)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
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
