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
import ch.cyberduck.core.sds.io.swagger.client.model.CustomerAttributes;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import org.joda.time.DateTime;
/**
 * Customer information
 */
@Schema(description = "Customer information")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-08-16T11:28:10.116221+02:00[Europe/Zurich]")
public class Customer {
  @JsonProperty("id")
  private Long id = null;

  @JsonProperty("companyName")
  private String companyName = null;

  /**
   * Customer type
   */
  public enum CustomerContractTypeEnum {
    DEMO("demo"),
    FREE("free"),
    PAY("pay");

    private String value;

    CustomerContractTypeEnum(String value) {
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
    public static CustomerContractTypeEnum fromValue(String text) {
      for (CustomerContractTypeEnum b : CustomerContractTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("customerContractType")
  private CustomerContractTypeEnum customerContractType = null;

  @JsonProperty("quotaMax")
  private Long quotaMax = null;

  @JsonProperty("quotaUsed")
  private Long quotaUsed = null;

  @JsonProperty("userMax")
  private Integer userMax = null;

  @JsonProperty("userUsed")
  private Integer userUsed = null;

  @JsonProperty("createdAt")
  private DateTime createdAt = null;

  @JsonProperty("isLocked")
  private Boolean isLocked = false;

  @JsonProperty("trialDaysLeft")
  private Integer trialDaysLeft = null;

  @JsonProperty("updatedAt")
  private DateTime updatedAt = null;

  @JsonProperty("lastLoginAt")
  private DateTime lastLoginAt = null;

  @JsonProperty("customerAttributes")
  private CustomerAttributes customerAttributes = null;

  @JsonProperty("providerCustomerId")
  private String providerCustomerId = null;

  @JsonProperty("webhooksMax")
  private Long webhooksMax = null;

  @JsonProperty("activationCode")
  private String activationCode = null;

  @JsonProperty("lockStatus")
  private Boolean lockStatus = false;

  @JsonProperty("customerUuid")
  private String customerUuid = null;

  public Customer id(Long id) {
    this.id = id;
    return this;
  }

   /**
   * Unique identifier for the customer
   * @return id
  **/
  @Schema(required = true, description = "Unique identifier for the customer")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Customer companyName(String companyName) {
    this.companyName = companyName;
    return this;
  }

   /**
   * Company name
   * @return companyName
  **/
  @Schema(required = true, description = "Company name")
  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public Customer customerContractType(CustomerContractTypeEnum customerContractType) {
    this.customerContractType = customerContractType;
    return this;
  }

   /**
   * Customer type
   * @return customerContractType
  **/
  @Schema(required = true, description = "Customer type")
  public CustomerContractTypeEnum getCustomerContractType() {
    return customerContractType;
  }

  public void setCustomerContractType(CustomerContractTypeEnum customerContractType) {
    this.customerContractType = customerContractType;
  }

  public Customer quotaMax(Long quotaMax) {
    this.quotaMax = quotaMax;
    return this;
  }

   /**
   * Maximal disc space which can be allocated by customer in bytes. -1 for unlimited
   * @return quotaMax
  **/
  @Schema(required = true, description = "Maximal disc space which can be allocated by customer in bytes. -1 for unlimited")
  public Long getQuotaMax() {
    return quotaMax;
  }

  public void setQuotaMax(Long quotaMax) {
    this.quotaMax = quotaMax;
  }

  public Customer quotaUsed(Long quotaUsed) {
    this.quotaUsed = quotaUsed;
    return this;
  }

   /**
   * Used amount of disc space in bytes
   * @return quotaUsed
  **/
  @Schema(required = true, description = "Used amount of disc space in bytes")
  public Long getQuotaUsed() {
    return quotaUsed;
  }

  public void setQuotaUsed(Long quotaUsed) {
    this.quotaUsed = quotaUsed;
  }

  public Customer userMax(Integer userMax) {
    this.userMax = userMax;
    return this;
  }

   /**
   * Maximal number of users
   * @return userMax
  **/
  @Schema(required = true, description = "Maximal number of users")
  public Integer getUserMax() {
    return userMax;
  }

  public void setUserMax(Integer userMax) {
    this.userMax = userMax;
  }

  public Customer userUsed(Integer userUsed) {
    this.userUsed = userUsed;
    return this;
  }

   /**
   * Number of users which are already allocated.
   * @return userUsed
  **/
  @Schema(required = true, description = "Number of users which are already allocated.")
  public Integer getUserUsed() {
    return userUsed;
  }

  public void setUserUsed(Integer userUsed) {
    this.userUsed = userUsed;
  }

  public Customer createdAt(DateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

   /**
   * Creation date
   * @return createdAt
  **/
  @Schema(required = true, description = "Creation date")
  public DateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(DateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Customer isLocked(Boolean isLocked) {
    this.isLocked = isLocked;
    return this;
  }

   /**
   * Customer is locked:  * &#x60;false&#x60; - unlocked  * &#x60;true&#x60; - locked    All users of this customer will be blocked and can not login anymore.
   * @return isLocked
  **/
  @Schema(description = "Customer is locked:  * `false` - unlocked  * `true` - locked    All users of this customer will be blocked and can not login anymore.")
  public Boolean isIsLocked() {
    return isLocked;
  }

  public void setIsLocked(Boolean isLocked) {
    this.isLocked = isLocked;
  }

  public Customer trialDaysLeft(Integer trialDaysLeft) {
    this.trialDaysLeft = trialDaysLeft;
    return this;
  }

   /**
   * Number of days left for trial period (relevant only for type &#x60;demo&#x60;)  (not used)
   * @return trialDaysLeft
  **/
  @Schema(description = "Number of days left for trial period (relevant only for type `demo`)  (not used)")
  public Integer getTrialDaysLeft() {
    return trialDaysLeft;
  }

  public void setTrialDaysLeft(Integer trialDaysLeft) {
    this.trialDaysLeft = trialDaysLeft;
  }

  public Customer updatedAt(DateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

   /**
   * Modification date
   * @return updatedAt
  **/
  @Schema(description = "Modification date")
  public DateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(DateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Customer lastLoginAt(DateTime lastLoginAt) {
    this.lastLoginAt = lastLoginAt;
    return this;
  }

   /**
   * Date of last seen login for the customer
   * @return lastLoginAt
  **/
  @Schema(description = "Date of last seen login for the customer")
  public DateTime getLastLoginAt() {
    return lastLoginAt;
  }

  public void setLastLoginAt(DateTime lastLoginAt) {
    this.lastLoginAt = lastLoginAt;
  }

  public Customer customerAttributes(CustomerAttributes customerAttributes) {
    this.customerAttributes = customerAttributes;
    return this;
  }

   /**
   * Get customerAttributes
   * @return customerAttributes
  **/
  @Schema(description = "")
  public CustomerAttributes getCustomerAttributes() {
    return customerAttributes;
  }

  public void setCustomerAttributes(CustomerAttributes customerAttributes) {
    this.customerAttributes = customerAttributes;
  }

  public Customer providerCustomerId(String providerCustomerId) {
    this.providerCustomerId = providerCustomerId;
    return this;
  }

   /**
   * Provider customer ID
   * @return providerCustomerId
  **/
  @Schema(description = "Provider customer ID")
  public String getProviderCustomerId() {
    return providerCustomerId;
  }

  public void setProviderCustomerId(String providerCustomerId) {
    this.providerCustomerId = providerCustomerId;
  }

  public Customer webhooksMax(Long webhooksMax) {
    this.webhooksMax = webhooksMax;
    return this;
  }

   /**
   * &amp;#128640; Since v4.19.0  Maximal number of webhooks
   * @return webhooksMax
  **/
  @Schema(description = "&#128640; Since v4.19.0  Maximal number of webhooks")
  public Long getWebhooksMax() {
    return webhooksMax;
  }

  public void setWebhooksMax(Long webhooksMax) {
    this.webhooksMax = webhooksMax;
  }

  public Customer activationCode(String activationCode) {
    this.activationCode = activationCode;
    return this;
  }

   /**
   * &amp;#128679; Deprecated since v4.8.0  Customer activation code string:  * valid only for types &#x60;free&#x60; and &#x60;demo&#x60;  * for &#x60;pay&#x60; customers it is empty
   * @return activationCode
  **/
  @Schema(description = "&#128679; Deprecated since v4.8.0  Customer activation code string:  * valid only for types `free` and `demo`  * for `pay` customers it is empty")
  public String getActivationCode() {
    return activationCode;
  }

  public void setActivationCode(String activationCode) {
    this.activationCode = activationCode;
  }

  public Customer lockStatus(Boolean lockStatus) {
    this.lockStatus = lockStatus;
    return this;
  }

   /**
   * &amp;#128679; Deprecated since v4.7.0  Customer lock status:  * &#x60;false&#x60; - unlocked  * &#x60;true&#x60; - locked    Please use &#x60;isLocked&#x60; instead.  All users of this customer will be blocked and can not login anymore.
   * @return lockStatus
  **/
  @Schema(required = true, description = "&#128679; Deprecated since v4.7.0  Customer lock status:  * `false` - unlocked  * `true` - locked    Please use `isLocked` instead.  All users of this customer will be blocked and can not login anymore.")
  public Boolean isLockStatus() {
    return lockStatus;
  }

  public void setLockStatus(Boolean lockStatus) {
    this.lockStatus = lockStatus;
  }

  public Customer customerUuid(String customerUuid) {
    this.customerUuid = customerUuid;
    return this;
  }

   /**
   * &amp;#128640; Since v4.21.0  Customer UUID
   * @return customerUuid
  **/
  @Schema(required = true, description = "&#128640; Since v4.21.0  Customer UUID")
  public String getCustomerUuid() {
    return customerUuid;
  }

  public void setCustomerUuid(String customerUuid) {
    this.customerUuid = customerUuid;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Customer customer = (Customer) o;
    return Objects.equals(this.id, customer.id) &&
        Objects.equals(this.companyName, customer.companyName) &&
        Objects.equals(this.customerContractType, customer.customerContractType) &&
        Objects.equals(this.quotaMax, customer.quotaMax) &&
        Objects.equals(this.quotaUsed, customer.quotaUsed) &&
        Objects.equals(this.userMax, customer.userMax) &&
        Objects.equals(this.userUsed, customer.userUsed) &&
        Objects.equals(this.createdAt, customer.createdAt) &&
        Objects.equals(this.isLocked, customer.isLocked) &&
        Objects.equals(this.trialDaysLeft, customer.trialDaysLeft) &&
        Objects.equals(this.updatedAt, customer.updatedAt) &&
        Objects.equals(this.lastLoginAt, customer.lastLoginAt) &&
        Objects.equals(this.customerAttributes, customer.customerAttributes) &&
        Objects.equals(this.providerCustomerId, customer.providerCustomerId) &&
        Objects.equals(this.webhooksMax, customer.webhooksMax) &&
        Objects.equals(this.activationCode, customer.activationCode) &&
        Objects.equals(this.lockStatus, customer.lockStatus) &&
        Objects.equals(this.customerUuid, customer.customerUuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, companyName, customerContractType, quotaMax, quotaUsed, userMax, userUsed, createdAt, isLocked, trialDaysLeft, updatedAt, lastLoginAt, customerAttributes, providerCustomerId, webhooksMax, activationCode, lockStatus, customerUuid);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Customer {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    customerContractType: ").append(toIndentedString(customerContractType)).append("\n");
    sb.append("    quotaMax: ").append(toIndentedString(quotaMax)).append("\n");
    sb.append("    quotaUsed: ").append(toIndentedString(quotaUsed)).append("\n");
    sb.append("    userMax: ").append(toIndentedString(userMax)).append("\n");
    sb.append("    userUsed: ").append(toIndentedString(userUsed)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    isLocked: ").append(toIndentedString(isLocked)).append("\n");
    sb.append("    trialDaysLeft: ").append(toIndentedString(trialDaysLeft)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    lastLoginAt: ").append(toIndentedString(lastLoginAt)).append("\n");
    sb.append("    customerAttributes: ").append(toIndentedString(customerAttributes)).append("\n");
    sb.append("    providerCustomerId: ").append(toIndentedString(providerCustomerId)).append("\n");
    sb.append("    webhooksMax: ").append(toIndentedString(webhooksMax)).append("\n");
    sb.append("    activationCode: ").append(toIndentedString(activationCode)).append("\n");
    sb.append("    lockStatus: ").append(toIndentedString(lockStatus)).append("\n");
    sb.append("    customerUuid: ").append(toIndentedString(customerUuid)).append("\n");
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
