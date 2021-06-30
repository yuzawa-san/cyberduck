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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * List Inbox Registrations
 */
@ApiModel(description = "List Inbox Registrations")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2021-06-30T21:29:25.490+02:00")
public class InboxRegistrationEntity {
  @JsonProperty("code")
  private String code = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("company")
  private String company = null;

  @JsonProperty("email")
  private String email = null;

  @JsonProperty("clickwrap_body")
  private String clickwrapBody = null;

  @JsonProperty("form_field_set_id")
  private Integer formFieldSetId = null;

  @JsonProperty("form_field_data")
  private String formFieldData = null;

  public InboxRegistrationEntity code(String code) {
    this.code = code;
    return this;
  }

   /**
   * Registration cookie code
   * @return code
  **/
  @ApiModelProperty(example = "abc123", value = "Registration cookie code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public InboxRegistrationEntity name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Registrant name
   * @return name
  **/
  @ApiModelProperty(example = "account", value = "Registrant name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public InboxRegistrationEntity company(String company) {
    this.company = company;
    return this;
  }

   /**
   * Registrant company name
   * @return company
  **/
  @ApiModelProperty(example = "Action Verb", value = "Registrant company name")
  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public InboxRegistrationEntity email(String email) {
    this.email = email;
    return this;
  }

   /**
   * Registrant email address
   * @return email
  **/
  @ApiModelProperty(example = "john.doe@files.com", value = "Registrant email address")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public InboxRegistrationEntity clickwrapBody(String clickwrapBody) {
    this.clickwrapBody = clickwrapBody;
    return this;
  }

   /**
   * Clickwrap text that was shown to the registrant
   * @return clickwrapBody
  **/
  @ApiModelProperty(example = "", value = "Clickwrap text that was shown to the registrant")
  public String getClickwrapBody() {
    return clickwrapBody;
  }

  public void setClickwrapBody(String clickwrapBody) {
    this.clickwrapBody = clickwrapBody;
  }

  public InboxRegistrationEntity formFieldSetId(Integer formFieldSetId) {
    this.formFieldSetId = formFieldSetId;
    return this;
  }

   /**
   * Id of associated form field set
   * @return formFieldSetId
  **/
  @ApiModelProperty(example = "1", value = "Id of associated form field set")
  public Integer getFormFieldSetId() {
    return formFieldSetId;
  }

  public void setFormFieldSetId(Integer formFieldSetId) {
    this.formFieldSetId = formFieldSetId;
  }

  public InboxRegistrationEntity formFieldData(String formFieldData) {
    this.formFieldData = formFieldData;
    return this;
  }

   /**
   * Data for form field set with form field ids as keys and user data as values
   * @return formFieldData
  **/
  @ApiModelProperty(example = "", value = "Data for form field set with form field ids as keys and user data as values")
  public String getFormFieldData() {
    return formFieldData;
  }

  public void setFormFieldData(String formFieldData) {
    this.formFieldData = formFieldData;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InboxRegistrationEntity inboxRegistrationEntity = (InboxRegistrationEntity) o;
    return Objects.equals(this.code, inboxRegistrationEntity.code) &&
        Objects.equals(this.name, inboxRegistrationEntity.name) &&
        Objects.equals(this.company, inboxRegistrationEntity.company) &&
        Objects.equals(this.email, inboxRegistrationEntity.email) &&
        Objects.equals(this.clickwrapBody, inboxRegistrationEntity.clickwrapBody) &&
        Objects.equals(this.formFieldSetId, inboxRegistrationEntity.formFieldSetId) &&
        Objects.equals(this.formFieldData, inboxRegistrationEntity.formFieldData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, name, company, email, clickwrapBody, formFieldSetId, formFieldData);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InboxRegistrationEntity {\n");
    
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    company: ").append(toIndentedString(company)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    clickwrapBody: ").append(toIndentedString(clickwrapBody)).append("\n");
    sb.append("    formFieldSetId: ").append(toIndentedString(formFieldSetId)).append("\n");
    sb.append("    formFieldData: ").append(toIndentedString(formFieldData)).append("\n");
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

