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
 * WebhookTestsBody2
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-07-12T10:20:43.848213+02:00[Europe/Paris]")
public class WebhookTestsBody2 {
  @JsonProperty("url")
  private String url = null;

  @JsonProperty("method")
  private String method = null;

  @JsonProperty("encoding")
  private String encoding = null;

  @JsonProperty("headers")
  private Object headers = null;

  @JsonProperty("body")
  private Object body = null;

  @JsonProperty("action")
  private String action = null;

  public WebhookTestsBody2 url(String url) {
    this.url = url;
    return this;
  }

   /**
   * URL for testing the webhook.
   * @return url
  **/
  @Schema(example = "https://www.site.com/...", required = true, description = "URL for testing the webhook.")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public WebhookTestsBody2 method(String method) {
    this.method = method;
    return this;
  }

   /**
   * HTTP method(GET or POST).
   * @return method
  **/
  @Schema(example = "GET", description = "HTTP method(GET or POST).")
  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public WebhookTestsBody2 encoding(String encoding) {
    this.encoding = encoding;
    return this;
  }

   /**
   * HTTP encoding method.  Can be JSON, XML, or RAW (form data).
   * @return encoding
  **/
  @Schema(example = "RAW", description = "HTTP encoding method.  Can be JSON, XML, or RAW (form data).")
  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public WebhookTestsBody2 headers(Object headers) {
    this.headers = headers;
    return this;
  }

   /**
   * Additional request headers.
   * @return headers
  **/
  @Schema(example = "x-test-header => testvalue", description = "Additional request headers.")
  public Object getHeaders() {
    return headers;
  }

  public void setHeaders(Object headers) {
    this.headers = headers;
  }

  public WebhookTestsBody2 body(Object body) {
    this.body = body;
    return this;
  }

   /**
   * Additional body parameters.
   * @return body
  **/
  @Schema(example = "test-param => testvalue", description = "Additional body parameters.")
  public Object getBody() {
    return body;
  }

  public void setBody(Object body) {
    this.body = body;
  }

  public WebhookTestsBody2 action(String action) {
    this.action = action;
    return this;
  }

   /**
   * action for test body
   * @return action
  **/
  @Schema(example = "test", description = "action for test body")
  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WebhookTestsBody2 webhookTestsBody2 = (WebhookTestsBody2) o;
    return Objects.equals(this.url, webhookTestsBody2.url) &&
        Objects.equals(this.method, webhookTestsBody2.method) &&
        Objects.equals(this.encoding, webhookTestsBody2.encoding) &&
        Objects.equals(this.headers, webhookTestsBody2.headers) &&
        Objects.equals(this.body, webhookTestsBody2.body) &&
        Objects.equals(this.action, webhookTestsBody2.action);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, method, encoding, headers, body, action);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WebhookTestsBody2 {\n");
    
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    method: ").append(toIndentedString(method)).append("\n");
    sb.append("    encoding: ").append(toIndentedString(encoding)).append("\n");
    sb.append("    headers: ").append(toIndentedString(headers)).append("\n");
    sb.append("    body: ").append(toIndentedString(body)).append("\n");
    sb.append("    action: ").append(toIndentedString(action)).append("\n");
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
