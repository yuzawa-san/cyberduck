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
import ch.cyberduck.core.sds.io.swagger.client.model.FileKey;
import ch.cyberduck.core.sds.io.swagger.client.model.UserFileKeyList;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
/**
 * Request model for completing an upload
 */
@Schema(description = "Request model for completing an upload")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-08-16T11:28:10.116221+02:00[Europe/Zurich]")
public class CompleteUploadRequest {
  /**
   * Node conflict resolution strategy:  * &#x60;autorename&#x60;  * &#x60;overwrite&#x60;  * &#x60;fail&#x60;
   */
  public enum ResolutionStrategyEnum {
    AUTORENAME("autorename"),
    OVERWRITE("overwrite"),
    FAIL("fail");

    private String value;

    ResolutionStrategyEnum(String value) {
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
    public static ResolutionStrategyEnum fromValue(String text) {
      for (ResolutionStrategyEnum b : ResolutionStrategyEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("resolutionStrategy")
  private ResolutionStrategyEnum resolutionStrategy = ResolutionStrategyEnum.AUTORENAME;

  @JsonProperty("keepShareLinks")
  private Boolean keepShareLinks = false;

  @JsonProperty("fileName")
  private String fileName = null;

  @JsonProperty("fileKey")
  private FileKey fileKey = null;

  @JsonProperty("userFileKeyList")
  private UserFileKeyList userFileKeyList = null;

  public CompleteUploadRequest resolutionStrategy(ResolutionStrategyEnum resolutionStrategy) {
    this.resolutionStrategy = resolutionStrategy;
    return this;
  }

   /**
   * Node conflict resolution strategy:  * &#x60;autorename&#x60;  * &#x60;overwrite&#x60;  * &#x60;fail&#x60;
   * @return resolutionStrategy
  **/
  @Schema(description = "Node conflict resolution strategy:  * `autorename`  * `overwrite`  * `fail`")
  public ResolutionStrategyEnum getResolutionStrategy() {
    return resolutionStrategy;
  }

  public void setResolutionStrategy(ResolutionStrategyEnum resolutionStrategy) {
    this.resolutionStrategy = resolutionStrategy;
  }

  public CompleteUploadRequest keepShareLinks(Boolean keepShareLinks) {
    this.keepShareLinks = keepShareLinks;
    return this;
  }

   /**
   * Preserve Download Share Links and point them to the new node.
   * @return keepShareLinks
  **/
  @Schema(description = "Preserve Download Share Links and point them to the new node.")
  public Boolean isKeepShareLinks() {
    return keepShareLinks;
  }

  public void setKeepShareLinks(Boolean keepShareLinks) {
    this.keepShareLinks = keepShareLinks;
  }

  public CompleteUploadRequest fileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

   /**
   * New file name to store with
   * @return fileName
  **/
  @Schema(description = "New file name to store with")
  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public CompleteUploadRequest fileKey(FileKey fileKey) {
    this.fileKey = fileKey;
    return this;
  }

   /**
   * Get fileKey
   * @return fileKey
  **/
  @Schema(description = "")
  public FileKey getFileKey() {
    return fileKey;
  }

  public void setFileKey(FileKey fileKey) {
    this.fileKey = fileKey;
  }

  public CompleteUploadRequest userFileKeyList(UserFileKeyList userFileKeyList) {
    this.userFileKeyList = userFileKeyList;
    return this;
  }

   /**
   * Get userFileKeyList
   * @return userFileKeyList
  **/
  @Schema(description = "")
  public UserFileKeyList getUserFileKeyList() {
    return userFileKeyList;
  }

  public void setUserFileKeyList(UserFileKeyList userFileKeyList) {
    this.userFileKeyList = userFileKeyList;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompleteUploadRequest completeUploadRequest = (CompleteUploadRequest) o;
    return Objects.equals(this.resolutionStrategy, completeUploadRequest.resolutionStrategy) &&
        Objects.equals(this.keepShareLinks, completeUploadRequest.keepShareLinks) &&
        Objects.equals(this.fileName, completeUploadRequest.fileName) &&
        Objects.equals(this.fileKey, completeUploadRequest.fileKey) &&
        Objects.equals(this.userFileKeyList, completeUploadRequest.userFileKeyList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resolutionStrategy, keepShareLinks, fileName, fileKey, userFileKeyList);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompleteUploadRequest {\n");
    
    sb.append("    resolutionStrategy: ").append(toIndentedString(resolutionStrategy)).append("\n");
    sb.append("    keepShareLinks: ").append(toIndentedString(keepShareLinks)).append("\n");
    sb.append("    fileName: ").append(toIndentedString(fileName)).append("\n");
    sb.append("    fileKey: ").append(toIndentedString(fileKey)).append("\n");
    sb.append("    userFileKeyList: ").append(toIndentedString(userFileKeyList)).append("\n");
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
