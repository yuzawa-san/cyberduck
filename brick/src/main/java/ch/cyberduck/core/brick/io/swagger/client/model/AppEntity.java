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
 * List Apps
 */
@Schema(description = "List Apps")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-07-12T12:23:43.971535+02:00[Europe/Paris]")
public class AppEntity {
  /**
   * The type of the App
   */
  public enum AppTypeEnum {
    SDK("sdk"),
    SSO("sso"),
    REMOTE_SERVER("remote_server"),
    FOLDER_BEHAVIOR("folder_behavior"),
    CLIENT_APP("client_app"),
    APP_INTEGRATION("app_integration");

    private String value;

    AppTypeEnum(String value) {
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
    public static AppTypeEnum fromValue(String text) {
      for (AppTypeEnum b : AppTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("app_type")
  private AppTypeEnum appType = null;

  @JsonProperty("documentation_links")
  private String documentationLinks = null;

  @JsonProperty("extended_description")
  private String extendedDescription = null;

  @JsonProperty("external_homepage_url")
  private String externalHomepageUrl = null;

  @JsonProperty("featured")
  private Boolean featured = null;

  /**
   * Associated Folder Behavior type, if any
   */
  public enum FolderBehaviorTypeEnum {
    WEBHOOK("webhook"),
    FILE_EXPIRATION("file_expiration"),
    AUTO_ENCRYPT("auto_encrypt"),
    LOCK_SUBFOLDERS("lock_subfolders"),
    STORAGE_REGION("storage_region"),
    SERVE_PUBLICLY("serve_publicly"),
    CREATE_USER_FOLDERS("create_user_folders"),
    REMOTE_SERVER_SYNC("remote_server_sync"),
    INBOX("inbox"),
    APPEND_TIMESTAMP("append_timestamp"),
    LIMIT_FILE_EXTENSIONS("limit_file_extensions"),
    LIMIT_FILE_REGEX("limit_file_regex"),
    AMAZON_SNS("amazon_sns"),
    WATERMARK("watermark"),
    REMOTE_SERVER_MOUNT("remote_server_mount"),
    SLACK_WEBHOOK("slack_webhook");

    private String value;

    FolderBehaviorTypeEnum(String value) {
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
    public static FolderBehaviorTypeEnum fromValue(String text) {
      for (FolderBehaviorTypeEnum b : FolderBehaviorTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("folder_behavior_type")
  private FolderBehaviorTypeEnum folderBehaviorType = null;

  @JsonProperty("icon_url")
  private String iconUrl = null;

  @JsonProperty("logo_thumbnail_url")
  private String logoThumbnailUrl = null;

  @JsonProperty("logo_url")
  private String logoUrl = null;

  @JsonProperty("marketing_youtube_url")
  private String marketingYoutubeUrl = null;

  @JsonProperty("name")
  private String name = null;

  /**
   * Associated Remote Server type, if any
   */
  public enum RemoteServerTypeEnum {
    FTP("ftp"),
    SFTP("sftp"),
    S3("s3"),
    GOOGLE_CLOUD_STORAGE("google_cloud_storage"),
    WEBDAV("webdav"),
    WASABI("wasabi"),
    BACKBLAZE_B2("backblaze_b2"),
    ONE_DRIVE("one_drive"),
    RACKSPACE("rackspace"),
    BOX("box"),
    DROPBOX("dropbox"),
    GOOGLE_DRIVE("google_drive"),
    AZURE("azure"),
    SHAREPOINT("sharepoint"),
    S3_COMPATIBLE("s3_compatible");

    private String value;

    RemoteServerTypeEnum(String value) {
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
    public static RemoteServerTypeEnum fromValue(String text) {
      for (RemoteServerTypeEnum b : RemoteServerTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("remote_server_type")
  private RemoteServerTypeEnum remoteServerType = null;

  @JsonProperty("screenshot_list_urls")
  private String screenshotListUrls = null;

  @JsonProperty("short_description")
  private String shortDescription = null;

  /**
   * Associated SSO Strategy type, if any
   */
  public enum SsoStrategyTypeEnum {
    GOOGLE("google"),
    AUTH0("auth0"),
    OKTA("okta"),
    ATLASSIAN("atlassian"),
    AZURE("azure"),
    BOX("box"),
    DROPBOX("dropbox"),
    SLACK("slack"),
    _UNUSED_UBUNTU("_unused_ubuntu"),
    ONELOGIN("onelogin"),
    SAML("saml"),
    IDAPTIVE("idaptive"),
    LDAP("ldap"),
    SCIM("scim");

    private String value;

    SsoStrategyTypeEnum(String value) {
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
    public static SsoStrategyTypeEnum fromValue(String text) {
      for (SsoStrategyTypeEnum b : SsoStrategyTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("sso_strategy_type")
  private SsoStrategyTypeEnum ssoStrategyType = null;

  @JsonProperty("tutorial_youtube_url")
  private String tutorialYoutubeUrl = null;

  public AppEntity appType(AppTypeEnum appType) {
    this.appType = appType;
    return this;
  }

   /**
   * The type of the App
   * @return appType
  **/
  @Schema(description = "The type of the App")
  public AppTypeEnum getAppType() {
    return appType;
  }

  public void setAppType(AppTypeEnum appType) {
    this.appType = appType;
  }

  public AppEntity documentationLinks(String documentationLinks) {
    this.documentationLinks = documentationLinks;
    return this;
  }

   /**
   * Collection of named links to documentation
   * @return documentationLinks
  **/
  @Schema(example = "Important Info => http://files.test/learn-more", description = "Collection of named links to documentation")
  public String getDocumentationLinks() {
    return documentationLinks;
  }

  public void setDocumentationLinks(String documentationLinks) {
    this.documentationLinks = documentationLinks;
  }

  public AppEntity extendedDescription(String extendedDescription) {
    this.extendedDescription = extendedDescription;
    return this;
  }

   /**
   * Long form description of the App
   * @return extendedDescription
  **/
  @Schema(description = "Long form description of the App")
  public String getExtendedDescription() {
    return extendedDescription;
  }

  public void setExtendedDescription(String extendedDescription) {
    this.extendedDescription = extendedDescription;
  }

  public AppEntity externalHomepageUrl(String externalHomepageUrl) {
    this.externalHomepageUrl = externalHomepageUrl;
    return this;
  }

   /**
   * Link to external homepage
   * @return externalHomepageUrl
  **/
  @Schema(description = "Link to external homepage")
  public String getExternalHomepageUrl() {
    return externalHomepageUrl;
  }

  public void setExternalHomepageUrl(String externalHomepageUrl) {
    this.externalHomepageUrl = externalHomepageUrl;
  }

  public AppEntity featured(Boolean featured) {
    this.featured = featured;
    return this;
  }

   /**
   * Is featured on the App listing?
   * @return featured
  **/
  @Schema(example = "true", description = "Is featured on the App listing?")
  public Boolean isFeatured() {
    return featured;
  }

  public void setFeatured(Boolean featured) {
    this.featured = featured;
  }

  public AppEntity folderBehaviorType(FolderBehaviorTypeEnum folderBehaviorType) {
    this.folderBehaviorType = folderBehaviorType;
    return this;
  }

   /**
   * Associated Folder Behavior type, if any
   * @return folderBehaviorType
  **/
  @Schema(description = "Associated Folder Behavior type, if any")
  public FolderBehaviorTypeEnum getFolderBehaviorType() {
    return folderBehaviorType;
  }

  public void setFolderBehaviorType(FolderBehaviorTypeEnum folderBehaviorType) {
    this.folderBehaviorType = folderBehaviorType;
  }

  public AppEntity iconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
    return this;
  }

   /**
   * App icon
   * @return iconUrl
  **/
  @Schema(description = "App icon")
  public String getIconUrl() {
    return iconUrl;
  }

  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }

  public AppEntity logoThumbnailUrl(String logoThumbnailUrl) {
    this.logoThumbnailUrl = logoThumbnailUrl;
    return this;
  }

   /**
   * Logo thumbnail for the App
   * @return logoThumbnailUrl
  **/
  @Schema(description = "Logo thumbnail for the App")
  public String getLogoThumbnailUrl() {
    return logoThumbnailUrl;
  }

  public void setLogoThumbnailUrl(String logoThumbnailUrl) {
    this.logoThumbnailUrl = logoThumbnailUrl;
  }

  public AppEntity logoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
    return this;
  }

   /**
   * Full size logo for the App
   * @return logoUrl
  **/
  @Schema(description = "Full size logo for the App")
  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }

  public AppEntity marketingYoutubeUrl(String marketingYoutubeUrl) {
    this.marketingYoutubeUrl = marketingYoutubeUrl;
    return this;
  }

   /**
   * Marketing video page
   * @return marketingYoutubeUrl
  **/
  @Schema(description = "Marketing video page")
  public String getMarketingYoutubeUrl() {
    return marketingYoutubeUrl;
  }

  public void setMarketingYoutubeUrl(String marketingYoutubeUrl) {
    this.marketingYoutubeUrl = marketingYoutubeUrl;
  }

  public AppEntity name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Name of the App
   * @return name
  **/
  @Schema(description = "Name of the App")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AppEntity remoteServerType(RemoteServerTypeEnum remoteServerType) {
    this.remoteServerType = remoteServerType;
    return this;
  }

   /**
   * Associated Remote Server type, if any
   * @return remoteServerType
  **/
  @Schema(description = "Associated Remote Server type, if any")
  public RemoteServerTypeEnum getRemoteServerType() {
    return remoteServerType;
  }

  public void setRemoteServerType(RemoteServerTypeEnum remoteServerType) {
    this.remoteServerType = remoteServerType;
  }

  public AppEntity screenshotListUrls(String screenshotListUrls) {
    this.screenshotListUrls = screenshotListUrls;
    return this;
  }

   /**
   * Screenshots of the App
   * @return screenshotListUrls
  **/
  @Schema(description = "Screenshots of the App")
  public String getScreenshotListUrls() {
    return screenshotListUrls;
  }

  public void setScreenshotListUrls(String screenshotListUrls) {
    this.screenshotListUrls = screenshotListUrls;
  }

  public AppEntity shortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
    return this;
  }

   /**
   * Short description of the App
   * @return shortDescription
  **/
  @Schema(description = "Short description of the App")
  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public AppEntity ssoStrategyType(SsoStrategyTypeEnum ssoStrategyType) {
    this.ssoStrategyType = ssoStrategyType;
    return this;
  }

   /**
   * Associated SSO Strategy type, if any
   * @return ssoStrategyType
  **/
  @Schema(description = "Associated SSO Strategy type, if any")
  public SsoStrategyTypeEnum getSsoStrategyType() {
    return ssoStrategyType;
  }

  public void setSsoStrategyType(SsoStrategyTypeEnum ssoStrategyType) {
    this.ssoStrategyType = ssoStrategyType;
  }

  public AppEntity tutorialYoutubeUrl(String tutorialYoutubeUrl) {
    this.tutorialYoutubeUrl = tutorialYoutubeUrl;
    return this;
  }

   /**
   * Tutorial video page
   * @return tutorialYoutubeUrl
  **/
  @Schema(description = "Tutorial video page")
  public String getTutorialYoutubeUrl() {
    return tutorialYoutubeUrl;
  }

  public void setTutorialYoutubeUrl(String tutorialYoutubeUrl) {
    this.tutorialYoutubeUrl = tutorialYoutubeUrl;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AppEntity appEntity = (AppEntity) o;
    return Objects.equals(this.appType, appEntity.appType) &&
        Objects.equals(this.documentationLinks, appEntity.documentationLinks) &&
        Objects.equals(this.extendedDescription, appEntity.extendedDescription) &&
        Objects.equals(this.externalHomepageUrl, appEntity.externalHomepageUrl) &&
        Objects.equals(this.featured, appEntity.featured) &&
        Objects.equals(this.folderBehaviorType, appEntity.folderBehaviorType) &&
        Objects.equals(this.iconUrl, appEntity.iconUrl) &&
        Objects.equals(this.logoThumbnailUrl, appEntity.logoThumbnailUrl) &&
        Objects.equals(this.logoUrl, appEntity.logoUrl) &&
        Objects.equals(this.marketingYoutubeUrl, appEntity.marketingYoutubeUrl) &&
        Objects.equals(this.name, appEntity.name) &&
        Objects.equals(this.remoteServerType, appEntity.remoteServerType) &&
        Objects.equals(this.screenshotListUrls, appEntity.screenshotListUrls) &&
        Objects.equals(this.shortDescription, appEntity.shortDescription) &&
        Objects.equals(this.ssoStrategyType, appEntity.ssoStrategyType) &&
        Objects.equals(this.tutorialYoutubeUrl, appEntity.tutorialYoutubeUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(appType, documentationLinks, extendedDescription, externalHomepageUrl, featured, folderBehaviorType, iconUrl, logoThumbnailUrl, logoUrl, marketingYoutubeUrl, name, remoteServerType, screenshotListUrls, shortDescription, ssoStrategyType, tutorialYoutubeUrl);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AppEntity {\n");
    
    sb.append("    appType: ").append(toIndentedString(appType)).append("\n");
    sb.append("    documentationLinks: ").append(toIndentedString(documentationLinks)).append("\n");
    sb.append("    extendedDescription: ").append(toIndentedString(extendedDescription)).append("\n");
    sb.append("    externalHomepageUrl: ").append(toIndentedString(externalHomepageUrl)).append("\n");
    sb.append("    featured: ").append(toIndentedString(featured)).append("\n");
    sb.append("    folderBehaviorType: ").append(toIndentedString(folderBehaviorType)).append("\n");
    sb.append("    iconUrl: ").append(toIndentedString(iconUrl)).append("\n");
    sb.append("    logoThumbnailUrl: ").append(toIndentedString(logoThumbnailUrl)).append("\n");
    sb.append("    logoUrl: ").append(toIndentedString(logoUrl)).append("\n");
    sb.append("    marketingYoutubeUrl: ").append(toIndentedString(marketingYoutubeUrl)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    remoteServerType: ").append(toIndentedString(remoteServerType)).append("\n");
    sb.append("    screenshotListUrls: ").append(toIndentedString(screenshotListUrls)).append("\n");
    sb.append("    shortDescription: ").append(toIndentedString(shortDescription)).append("\n");
    sb.append("    ssoStrategyType: ").append(toIndentedString(ssoStrategyType)).append("\n");
    sb.append("    tutorialYoutubeUrl: ").append(toIndentedString(tutorialYoutubeUrl)).append("\n");
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
