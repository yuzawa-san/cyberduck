/*
 * ReSTFS
 * ReSTFS Open API 3.0 Spec
 *
 * OpenAPI spec version: 1.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ch.cyberduck.core.gmxcloud.io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import ch.cyberduck.core.gmxcloud.io.swagger.client.model.ResourceresourceIdUpdateUifs;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
/**
 * ResourceresourceIdUpdate
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-10-12T21:58:48.478680+02:00[Europe/Zurich]")
public class ResourceresourceIdUpdate {
  @JsonProperty("ui:fs")
  private ResourceresourceIdUpdateUifs uifs = null;

  public ResourceresourceIdUpdate uifs(ResourceresourceIdUpdateUifs uifs) {
    this.uifs = uifs;
    return this;
  }

   /**
   * Get uifs
   * @return uifs
  **/
  @Schema(description = "")
  public ResourceresourceIdUpdateUifs getUifs() {
    return uifs;
  }

  public void setUifs(ResourceresourceIdUpdateUifs uifs) {
    this.uifs = uifs;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResourceresourceIdUpdate resourceresourceIdUpdate = (ResourceresourceIdUpdate) o;
    return Objects.equals(this.uifs, resourceresourceIdUpdate.uifs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uifs);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResourceresourceIdUpdate {\n");
    
    sb.append("    uifs: ").append(toIndentedString(uifs)).append("\n");
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
