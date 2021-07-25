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
 * Range information
 */
@Schema(description = "Range information")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-07-25T23:34:01.480829+02:00[Europe/Paris]")
public class Range {
  @JsonProperty("offset")
  private Long offset = null;

  @JsonProperty("limit")
  private Long limit = null;

  @JsonProperty("total")
  private Long total = null;

  public Range offset(Long offset) {
    this.offset = offset;
    return this;
  }

   /**
   * Range offset
   * @return offset
  **/
  @Schema(required = true, description = "Range offset")
  public Long getOffset() {
    return offset;
  }

  public void setOffset(Long offset) {
    this.offset = offset;
  }

  public Range limit(Long limit) {
    this.limit = limit;
    return this;
  }

   /**
   * Range limit. Maximum 500.
   * @return limit
  **/
  @Schema(required = true, description = "Range limit. Maximum 500.")
  public Long getLimit() {
    return limit;
  }

  public void setLimit(Long limit) {
    this.limit = limit;
  }

  public Range total(Long total) {
    this.total = total;
    return this;
  }

   /**
   * Total items available
   * @return total
  **/
  @Schema(required = true, description = "Total items available")
  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Range range = (Range) o;
    return Objects.equals(this.offset, range.offset) &&
        Objects.equals(this.limit, range.limit) &&
        Objects.equals(this.total, range.total);
  }

  @Override
  public int hashCode() {
    return Objects.hash(offset, limit, total);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Range {\n");
    
    sb.append("    offset: ").append(toIndentedString(offset)).append("\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
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
