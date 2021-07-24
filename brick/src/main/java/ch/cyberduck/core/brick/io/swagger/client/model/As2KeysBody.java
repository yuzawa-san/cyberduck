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
 * As2KeysBody
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-07-23T20:47:00.742522+02:00[Europe/Paris]")
public class As2KeysBody {
  @JsonProperty("user_id")
  private Integer userId = null;

  @JsonProperty("as2_partnership_name")
  private String as2PartnershipName = null;

  @JsonProperty("public_key")
  private String publicKey = null;

  public As2KeysBody userId(Integer userId) {
    this.userId = userId;
    return this;
  }

   /**
   * User ID.  Provide a value of &#x60;0&#x60; to operate the current session&#x27;s user.
   * @return userId
  **/
  @Schema(description = "User ID.  Provide a value of `0` to operate the current session's user.")
  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public As2KeysBody as2PartnershipName(String as2PartnershipName) {
    this.as2PartnershipName = as2PartnershipName;
    return this;
  }

   /**
   * AS2 Partnership Name
   * @return as2PartnershipName
  **/
  @Schema(example = "Test", required = true, description = "AS2 Partnership Name")
  public String getAs2PartnershipName() {
    return as2PartnershipName;
  }

  public void setAs2PartnershipName(String as2PartnershipName) {
    this.as2PartnershipName = as2PartnershipName;
  }

  public As2KeysBody publicKey(String publicKey) {
    this.publicKey = publicKey;
    return this;
  }

   /**
   * Actual contents of Public key.
   * @return publicKey
  **/
  @Schema(required = true, description = "Actual contents of Public key.")
  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    As2KeysBody as2KeysBody = (As2KeysBody) o;
    return Objects.equals(this.userId, as2KeysBody.userId) &&
        Objects.equals(this.as2PartnershipName, as2KeysBody.as2PartnershipName) &&
        Objects.equals(this.publicKey, as2KeysBody.publicKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, as2PartnershipName, publicKey);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class As2KeysBody {\n");
    
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    as2PartnershipName: ").append(toIndentedString(as2PartnershipName)).append("\n");
    sb.append("    publicKey: ").append(toIndentedString(publicKey)).append("\n");
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
