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
import org.joda.time.DateTime;
/**
 * Copied node information
 */
@Schema(description = "Copied node information")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-07-25T23:34:01.480829+02:00[Europe/Paris]")
public class CopyNode {
  @JsonProperty("id")
  private Long id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("timestampCreation")
  private DateTime timestampCreation = null;

  @JsonProperty("timestampModification")
  private DateTime timestampModification = null;

  public CopyNode id(Long id) {
    this.id = id;
    return this;
  }

   /**
   * Source node ID
   * @return id
  **/
  @Schema(required = true, description = "Source node ID")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public CopyNode name(String name) {
    this.name = name;
    return this;
  }

   /**
   * New node name
   * @return name
  **/
  @Schema(description = "New node name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CopyNode timestampCreation(DateTime timestampCreation) {
    this.timestampCreation = timestampCreation;
    return this;
  }

   /**
   * &amp;#128640; Since v4.22.0  Time the node was created on external file system  (default: current server datetime in UTC format)
   * @return timestampCreation
  **/
  @Schema(description = "&#128640; Since v4.22.0  Time the node was created on external file system  (default: current server datetime in UTC format)")
  public DateTime getTimestampCreation() {
    return timestampCreation;
  }

  public void setTimestampCreation(DateTime timestampCreation) {
    this.timestampCreation = timestampCreation;
  }

  public CopyNode timestampModification(DateTime timestampModification) {
    this.timestampModification = timestampModification;
    return this;
  }

   /**
   * &amp;#128640; Since v4.22.0  Time the content of a node was last modified on external file system  (default: current server datetime in UTC format)
   * @return timestampModification
  **/
  @Schema(description = "&#128640; Since v4.22.0  Time the content of a node was last modified on external file system  (default: current server datetime in UTC format)")
  public DateTime getTimestampModification() {
    return timestampModification;
  }

  public void setTimestampModification(DateTime timestampModification) {
    this.timestampModification = timestampModification;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CopyNode copyNode = (CopyNode) o;
    return Objects.equals(this.id, copyNode.id) &&
        Objects.equals(this.name, copyNode.name) &&
        Objects.equals(this.timestampCreation, copyNode.timestampCreation) &&
        Objects.equals(this.timestampModification, copyNode.timestampModification);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, timestampCreation, timestampModification);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CopyNode {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    timestampCreation: ").append(toIndentedString(timestampCreation)).append("\n");
    sb.append("    timestampModification: ").append(toIndentedString(timestampModification)).append("\n");
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
