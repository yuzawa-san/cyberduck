/*
 * DRACOON API
 * REST Web Services for DRACOON<br>built at: 1970-01-01 00:00:00.000<br><br>This page provides an overview of all available and documented DRACOON APIs, which are grouped by tags.<br>Each tag provides a collection of APIs that are intended for a specific area of the DRACOON.<br><br><a title='Developer Information' href='https://developer.dracoon.com'>Developer Information</a>&emsp;&emsp;<a title='Get SDKs on GitHub' href='https://github.com/dracoon'>Get SDKs on GitHub</a><br><br><a title='Terms of service' href='https://www.dracoon.com/terms/general-terms-and-conditions/'>Terms of service</a>
 *
 * OpenAPI spec version: 4.28.3
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ch.cyberduck.core.sds.io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
/**
 * S3 configuration
 */
@Schema(description = "S3 configuration")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-07-25T23:34:01.480829+02:00[Europe/Paris]")
public class S3Config {
  @JsonProperty("bucketUrl")
  private String bucketUrl = null;

  @JsonProperty("accessKeyDefined")
  private Boolean accessKeyDefined = null;

  @JsonProperty("secretKeyDefined")
  private Boolean secretKeyDefined = null;

  @JsonProperty("region")
  private String region = null;

  @JsonProperty("endpointUrl")
  private String endpointUrl = null;

  @JsonProperty("bucketName")
  private String bucketName = null;

  public S3Config bucketUrl(String bucketUrl) {
    this.bucketUrl = bucketUrl;
    return this;
  }

   /**
   * S3 object storage bucket URL
   * @return bucketUrl
  **/
  @Schema(required = true, description = "S3 object storage bucket URL")
  public String getBucketUrl() {
    return bucketUrl;
  }

  public void setBucketUrl(String bucketUrl) {
    this.bucketUrl = bucketUrl;
  }

  public S3Config accessKeyDefined(Boolean accessKeyDefined) {
    this.accessKeyDefined = accessKeyDefined;
    return this;
  }

   /**
   * Determines whether Access Key ID is defined
   * @return accessKeyDefined
  **/
  @Schema(required = true, description = "Determines whether Access Key ID is defined")
  public Boolean isAccessKeyDefined() {
    return accessKeyDefined;
  }

  public void setAccessKeyDefined(Boolean accessKeyDefined) {
    this.accessKeyDefined = accessKeyDefined;
  }

  public S3Config secretKeyDefined(Boolean secretKeyDefined) {
    this.secretKeyDefined = secretKeyDefined;
    return this;
  }

   /**
   * Determines whether Access Secret Key is defined
   * @return secretKeyDefined
  **/
  @Schema(required = true, description = "Determines whether Access Secret Key is defined")
  public Boolean isSecretKeyDefined() {
    return secretKeyDefined;
  }

  public void setSecretKeyDefined(Boolean secretKeyDefined) {
    this.secretKeyDefined = secretKeyDefined;
  }

  public S3Config region(String region) {
    this.region = region;
    return this;
  }

   /**
   * S3 region
   * @return region
  **/
  @Schema(description = "S3 region")
  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public S3Config endpointUrl(String endpointUrl) {
    this.endpointUrl = endpointUrl;
    return this;
  }

   /**
   * &amp;#128679; Deprecated since v4.24.0  S3 object storage endpoint URL  use &#x60;bucketUrl&#x60; instead
   * @return endpointUrl
  **/
  @Schema(description = "&#128679; Deprecated since v4.24.0  S3 object storage endpoint URL  use `bucketUrl` instead")
  public String getEndpointUrl() {
    return endpointUrl;
  }

  public void setEndpointUrl(String endpointUrl) {
    this.endpointUrl = endpointUrl;
  }

  public S3Config bucketName(String bucketName) {
    this.bucketName = bucketName;
    return this;
  }

   /**
   * &amp;#128679; Deprecated since v4.24.0  S3 bucket name  use &#x60;bucketUrl&#x60; instead
   * @return bucketName
  **/
  @Schema(description = "&#128679; Deprecated since v4.24.0  S3 bucket name  use `bucketUrl` instead")
  public String getBucketName() {
    return bucketName;
  }

  public void setBucketName(String bucketName) {
    this.bucketName = bucketName;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    S3Config s3Config = (S3Config) o;
    return Objects.equals(this.bucketUrl, s3Config.bucketUrl) &&
        Objects.equals(this.accessKeyDefined, s3Config.accessKeyDefined) &&
        Objects.equals(this.secretKeyDefined, s3Config.secretKeyDefined) &&
        Objects.equals(this.region, s3Config.region) &&
        Objects.equals(this.endpointUrl, s3Config.endpointUrl) &&
        Objects.equals(this.bucketName, s3Config.bucketName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bucketUrl, accessKeyDefined, secretKeyDefined, region, endpointUrl, bucketName);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class S3Config {\n");
    
    sb.append("    bucketUrl: ").append(toIndentedString(bucketUrl)).append("\n");
    sb.append("    accessKeyDefined: ").append(toIndentedString(accessKeyDefined)).append("\n");
    sb.append("    secretKeyDefined: ").append(toIndentedString(secretKeyDefined)).append("\n");
    sb.append("    region: ").append(toIndentedString(region)).append("\n");
    sb.append("    endpointUrl: ").append(toIndentedString(endpointUrl)).append("\n");
    sb.append("    bucketName: ").append(toIndentedString(bucketName)).append("\n");
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
