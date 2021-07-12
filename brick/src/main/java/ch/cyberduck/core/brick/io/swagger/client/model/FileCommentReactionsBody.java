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
 * FileCommentReactionsBody
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-07-12T12:23:43.971535+02:00[Europe/Paris]")
public class FileCommentReactionsBody {
  @JsonProperty("user_id")
  private Integer userId = null;

  @JsonProperty("file_comment_id")
  private Integer fileCommentId = null;

  @JsonProperty("emoji")
  private String emoji = null;

  public FileCommentReactionsBody userId(Integer userId) {
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

  public FileCommentReactionsBody fileCommentId(Integer fileCommentId) {
    this.fileCommentId = fileCommentId;
    return this;
  }

   /**
   * ID of file comment to attach reaction to.
   * @return fileCommentId
  **/
  @Schema(required = true, description = "ID of file comment to attach reaction to.")
  public Integer getFileCommentId() {
    return fileCommentId;
  }

  public void setFileCommentId(Integer fileCommentId) {
    this.fileCommentId = fileCommentId;
  }

  public FileCommentReactionsBody emoji(String emoji) {
    this.emoji = emoji;
    return this;
  }

   /**
   * Emoji to react with.
   * @return emoji
  **/
  @Schema(required = true, description = "Emoji to react with.")
  public String getEmoji() {
    return emoji;
  }

  public void setEmoji(String emoji) {
    this.emoji = emoji;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FileCommentReactionsBody fileCommentReactionsBody = (FileCommentReactionsBody) o;
    return Objects.equals(this.userId, fileCommentReactionsBody.userId) &&
        Objects.equals(this.fileCommentId, fileCommentReactionsBody.fileCommentId) &&
        Objects.equals(this.emoji, fileCommentReactionsBody.emoji);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, fileCommentId, emoji);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FileCommentReactionsBody {\n");
    
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    fileCommentId: ").append(toIndentedString(fileCommentId)).append("\n");
    sb.append("    emoji: ").append(toIndentedString(emoji)).append("\n");
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
