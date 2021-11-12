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

package ch.cyberduck.core.eue.io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import ch.cyberduck.core.eue.io.swagger.client.model.UiShareModel;
import ch.cyberduck.core.eue.io.swagger.client.model.UiWin32;
import ch.cyberduck.core.eue.io.swagger.client.model.Uifs;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
/**
 * Children
 */


public class Children {
  @JsonProperty("ui:fs")
  private Uifs uifs = null;

  @JsonProperty("ui:win32")
  private UiWin32 uiwin32 = null;

  @JsonProperty("ui:share")
  private UiShareModel uishare = null;

  public Children uifs(Uifs uifs) {
    this.uifs = uifs;
    return this;
  }

   /**
   * Get uifs
   * @return uifs
  **/
  @Schema(description = "")
  public Uifs getUifs() {
    return uifs;
  }

  public void setUifs(Uifs uifs) {
    this.uifs = uifs;
  }

  public Children uiwin32(UiWin32 uiwin32) {
    this.uiwin32 = uiwin32;
    return this;
  }

   /**
   * Get uiwin32
   * @return uiwin32
  **/
  @Schema(description = "")
  public UiWin32 getUiwin32() {
    return uiwin32;
  }

  public void setUiwin32(UiWin32 uiwin32) {
    this.uiwin32 = uiwin32;
  }

  public Children uishare(UiShareModel uishare) {
    this.uishare = uishare;
    return this;
  }

   /**
   * Get uishare
   * @return uishare
  **/
  @Schema(description = "")
  public UiShareModel getUishare() {
    return uishare;
  }

  public void setUishare(UiShareModel uishare) {
    this.uishare = uishare;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Children children = (Children) o;
    return Objects.equals(this.uifs, children.uifs) &&
        Objects.equals(this.uiwin32, children.uiwin32) &&
        Objects.equals(this.uishare, children.uishare);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uifs, uiwin32, uishare);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Children {\n");
    
    sb.append("    uifs: ").append(toIndentedString(uifs)).append("\n");
    sb.append("    uiwin32: ").append(toIndentedString(uiwin32)).append("\n");
    sb.append("    uishare: ").append(toIndentedString(uishare)).append("\n");
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
