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
import org.joda.time.DateTime;

/**
 * List Bundle Recipients
 */
@ApiModel(description = "List Bundle Recipients")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2021-06-30T21:29:25.490+02:00")
public class BundleRecipientEntity {
  @JsonProperty("company")
  private String company = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("note")
  private String note = null;

  @JsonProperty("recipient")
  private String recipient = null;

  @JsonProperty("sent_at")
  private DateTime sentAt = null;

  public BundleRecipientEntity company(String company) {
    this.company = company;
    return this;
  }

   /**
   * The recipient&#39;s company.
   * @return company
  **/
  @ApiModelProperty(example = "Acme Inc.", value = "The recipient's company.")
  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public BundleRecipientEntity name(String name) {
    this.name = name;
    return this;
  }

   /**
   * The recipient&#39;s name.
   * @return name
  **/
  @ApiModelProperty(example = "John Doe", value = "The recipient's name.")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BundleRecipientEntity note(String note) {
    this.note = note;
    return this;
  }

   /**
   * A note sent to the recipient with the bundle.
   * @return note
  **/
  @ApiModelProperty(example = "Some note.", value = "A note sent to the recipient with the bundle.")
  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public BundleRecipientEntity recipient(String recipient) {
    this.recipient = recipient;
    return this;
  }

   /**
   * The recipient&#39;s email address.
   * @return recipient
  **/
  @ApiModelProperty(example = "john.doe@example.com", value = "The recipient's email address.")
  public String getRecipient() {
    return recipient;
  }

  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  public BundleRecipientEntity sentAt(DateTime sentAt) {
    this.sentAt = sentAt;
    return this;
  }

   /**
   * When the Bundle was shared with this recipient.
   * @return sentAt
  **/
  @ApiModelProperty(example = "2000-01-01T01:00:00Z", value = "When the Bundle was shared with this recipient.")
  public DateTime getSentAt() {
    return sentAt;
  }

  public void setSentAt(DateTime sentAt) {
    this.sentAt = sentAt;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BundleRecipientEntity bundleRecipientEntity = (BundleRecipientEntity) o;
    return Objects.equals(this.company, bundleRecipientEntity.company) &&
        Objects.equals(this.name, bundleRecipientEntity.name) &&
        Objects.equals(this.note, bundleRecipientEntity.note) &&
        Objects.equals(this.recipient, bundleRecipientEntity.recipient) &&
        Objects.equals(this.sentAt, bundleRecipientEntity.sentAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(company, name, note, recipient, sentAt);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BundleRecipientEntity {\n");
    
    sb.append("    company: ").append(toIndentedString(company)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    note: ").append(toIndentedString(note)).append("\n");
    sb.append("    recipient: ").append(toIndentedString(recipient)).append("\n");
    sb.append("    sentAt: ").append(toIndentedString(sentAt)).append("\n");
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

