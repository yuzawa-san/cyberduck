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
 * Show site DNS configuration.
 */
@ApiModel(description = "Show site DNS configuration.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2021-06-30T21:29:25.490+02:00")
public class DnsRecordEntity {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("domain")
  private String domain = null;

  @JsonProperty("rrtype")
  private String rrtype = null;

  @JsonProperty("value")
  private String value = null;

  public DnsRecordEntity id(String id) {
    this.id = id;
    return this;
  }

   /**
   * Unique label for DNS record; used by Zapier and other integrations.
   * @return id
  **/
  @ApiModelProperty(example = "customdomain.com-CNAME-site.files.com", value = "Unique label for DNS record; used by Zapier and other integrations.")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public DnsRecordEntity domain(String domain) {
    this.domain = domain;
    return this;
  }

   /**
   * DNS record domain name
   * @return domain
  **/
  @ApiModelProperty(example = "my-custom-domain.com", value = "DNS record domain name")
  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public DnsRecordEntity rrtype(String rrtype) {
    this.rrtype = rrtype;
    return this;
  }

   /**
   * DNS record type
   * @return rrtype
  **/
  @ApiModelProperty(example = "CNAME", value = "DNS record type")
  public String getRrtype() {
    return rrtype;
  }

  public void setRrtype(String rrtype) {
    this.rrtype = rrtype;
  }

  public DnsRecordEntity value(String value) {
    this.value = value;
    return this;
  }

   /**
   * DNS record value
   * @return value
  **/
  @ApiModelProperty(example = "mysite.files.com", value = "DNS record value")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DnsRecordEntity dnsRecordEntity = (DnsRecordEntity) o;
    return Objects.equals(this.id, dnsRecordEntity.id) &&
        Objects.equals(this.domain, dnsRecordEntity.domain) &&
        Objects.equals(this.rrtype, dnsRecordEntity.rrtype) &&
        Objects.equals(this.value, dnsRecordEntity.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, domain, rrtype, value);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DnsRecordEntity {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    domain: ").append(toIndentedString(domain)).append("\n");
    sb.append("    rrtype: ").append(toIndentedString(rrtype)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

