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
import java.util.ArrayList;
import java.util.List;
/**
 * AutomationsBody
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-07-23T20:47:00.742522+02:00[Europe/Paris]")
public class AutomationsBody {
  /**
   * Automation type
   */
  public enum AutomationEnum {
    CREATE_FOLDER("create_folder"),
    REQUEST_FILE("request_file"),
    REQUEST_MOVE("request_move"),
    COPY_NEWEST_FILE("copy_newest_file"),
    DELETE_FILE("delete_file"),
    COPY_FILE("copy_file"),
    MOVE_FILE("move_file");

    private String value;

    AutomationEnum(String value) {
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
    public static AutomationEnum fromValue(String text) {
      for (AutomationEnum b : AutomationEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("automation")
  private AutomationEnum automation = null;

  @JsonProperty("source")
  private String source = null;

  @JsonProperty("destination")
  private String destination = null;

  @JsonProperty("destinations")
  private List<String> destinations = null;

  @JsonProperty("destination_replace_from")
  private String destinationReplaceFrom = null;

  @JsonProperty("destination_replace_to")
  private String destinationReplaceTo = null;

  @JsonProperty("interval")
  private String interval = null;

  @JsonProperty("path")
  private String path = null;

  @JsonProperty("user_ids")
  private String userIds = null;

  @JsonProperty("group_ids")
  private String groupIds = null;

  @JsonProperty("schedule")
  private Object schedule = null;

  /**
   * How this automation is triggered to run. One of: &#x60;realtime&#x60;, &#x60;daily&#x60;, &#x60;custom_schedule&#x60;, &#x60;webhook&#x60;, &#x60;email&#x60;, or &#x60;action&#x60;.
   */
  public enum TriggerEnum {
    REALTIME("realtime"),
    DAILY("daily"),
    CUSTOM_SCHEDULE("custom_schedule"),
    WEBHOOK("webhook"),
    EMAIL("email"),
    ACTION("action");

    private String value;

    TriggerEnum(String value) {
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
    public static TriggerEnum fromValue(String text) {
      for (TriggerEnum b : TriggerEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("trigger")
  private TriggerEnum trigger = null;

  @JsonProperty("trigger_actions")
  private List<String> triggerActions = null;

  @JsonProperty("trigger_action_path")
  private String triggerActionPath = null;

  @JsonProperty("value")
  private Object value = null;

  public AutomationsBody automation(AutomationEnum automation) {
    this.automation = automation;
    return this;
  }

   /**
   * Automation type
   * @return automation
  **/
  @Schema(example = "create_folder", required = true, description = "Automation type")
  public AutomationEnum getAutomation() {
    return automation;
  }

  public void setAutomation(AutomationEnum automation) {
    this.automation = automation;
  }

  public AutomationsBody source(String source) {
    this.source = source;
    return this;
  }

   /**
   * Source Path
   * @return source
  **/
  @Schema(example = "source", description = "Source Path")
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public AutomationsBody destination(String destination) {
    this.destination = destination;
    return this;
  }

   /**
   * DEPRECATED: Destination Path. Use &#x60;destinations&#x60; instead.
   * @return destination
  **/
  @Schema(description = "DEPRECATED: Destination Path. Use `destinations` instead.")
  public String getDestination() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public AutomationsBody destinations(List<String> destinations) {
    this.destinations = destinations;
    return this;
  }

  public AutomationsBody addDestinationsItem(String destinationsItem) {
    if (this.destinations == null) {
      this.destinations = new ArrayList<>();
    }
    this.destinations.add(destinationsItem);
    return this;
  }

   /**
   * A list of String destination paths or Hash of folder_path and optional file_path.
   * @return destinations
  **/
  @Schema(example = "[\"folder_a/file_a.txt\", {\"folder_path\":\"folder_b\", \"file_path\":\"file_b.txt\"}, {\"folder_path\":\"folder_c\"}]", description = "A list of String destination paths or Hash of folder_path and optional file_path.")
  public List<String> getDestinations() {
    return destinations;
  }

  public void setDestinations(List<String> destinations) {
    this.destinations = destinations;
  }

  public AutomationsBody destinationReplaceFrom(String destinationReplaceFrom) {
    this.destinationReplaceFrom = destinationReplaceFrom;
    return this;
  }

   /**
   * If set, this string in the destination path will be replaced with the value in &#x60;destination_replace_to&#x60;.
   * @return destinationReplaceFrom
  **/
  @Schema(description = "If set, this string in the destination path will be replaced with the value in `destination_replace_to`.")
  public String getDestinationReplaceFrom() {
    return destinationReplaceFrom;
  }

  public void setDestinationReplaceFrom(String destinationReplaceFrom) {
    this.destinationReplaceFrom = destinationReplaceFrom;
  }

  public AutomationsBody destinationReplaceTo(String destinationReplaceTo) {
    this.destinationReplaceTo = destinationReplaceTo;
    return this;
  }

   /**
   * If set, this string will replace the value &#x60;destination_replace_from&#x60; in the destination filename. You can use special patterns here.
   * @return destinationReplaceTo
  **/
  @Schema(description = "If set, this string will replace the value `destination_replace_from` in the destination filename. You can use special patterns here.")
  public String getDestinationReplaceTo() {
    return destinationReplaceTo;
  }

  public void setDestinationReplaceTo(String destinationReplaceTo) {
    this.destinationReplaceTo = destinationReplaceTo;
  }

  public AutomationsBody interval(String interval) {
    this.interval = interval;
    return this;
  }

   /**
   * How often to run this automation? One of: &#x60;day&#x60;, &#x60;week&#x60;, &#x60;week_end&#x60;, &#x60;month&#x60;, &#x60;month_end&#x60;, &#x60;quarter&#x60;, &#x60;quarter_end&#x60;, &#x60;year&#x60;, &#x60;year_end&#x60;
   * @return interval
  **/
  @Schema(example = "year", description = "How often to run this automation? One of: `day`, `week`, `week_end`, `month`, `month_end`, `quarter`, `quarter_end`, `year`, `year_end`")
  public String getInterval() {
    return interval;
  }

  public void setInterval(String interval) {
    this.interval = interval;
  }

  public AutomationsBody path(String path) {
    this.path = path;
    return this;
  }

   /**
   * Path on which this Automation runs.  Supports globs.
   * @return path
  **/
  @Schema(description = "Path on which this Automation runs.  Supports globs.")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public AutomationsBody userIds(String userIds) {
    this.userIds = userIds;
    return this;
  }

   /**
   * A list of user IDs the automation is associated with. If sent as a string, it should be comma-delimited.
   * @return userIds
  **/
  @Schema(description = "A list of user IDs the automation is associated with. If sent as a string, it should be comma-delimited.")
  public String getUserIds() {
    return userIds;
  }

  public void setUserIds(String userIds) {
    this.userIds = userIds;
  }

  public AutomationsBody groupIds(String groupIds) {
    this.groupIds = groupIds;
    return this;
  }

   /**
   * A list of group IDs the automation is associated with. If sent as a string, it should be comma-delimited.
   * @return groupIds
  **/
  @Schema(description = "A list of group IDs the automation is associated with. If sent as a string, it should be comma-delimited.")
  public String getGroupIds() {
    return groupIds;
  }

  public void setGroupIds(String groupIds) {
    this.groupIds = groupIds;
  }

  public AutomationsBody schedule(Object schedule) {
    this.schedule = schedule;
    return this;
  }

   /**
   * Custom schedule for running this automation.
   * @return schedule
  **/
  @Schema(example = "{\"days_of_week\": [ 0, 1, 3 ], \"times_of_day\": [ \"7:30\", \"11:30\" ], \"time_zone\": \"Eastern Time (US & Canada)\"}", description = "Custom schedule for running this automation.")
  public Object getSchedule() {
    return schedule;
  }

  public void setSchedule(Object schedule) {
    this.schedule = schedule;
  }

  public AutomationsBody trigger(TriggerEnum trigger) {
    this.trigger = trigger;
    return this;
  }

   /**
   * How this automation is triggered to run. One of: &#x60;realtime&#x60;, &#x60;daily&#x60;, &#x60;custom_schedule&#x60;, &#x60;webhook&#x60;, &#x60;email&#x60;, or &#x60;action&#x60;.
   * @return trigger
  **/
  @Schema(example = "realtime", description = "How this automation is triggered to run. One of: `realtime`, `daily`, `custom_schedule`, `webhook`, `email`, or `action`.")
  public TriggerEnum getTrigger() {
    return trigger;
  }

  public void setTrigger(TriggerEnum trigger) {
    this.trigger = trigger;
  }

  public AutomationsBody triggerActions(List<String> triggerActions) {
    this.triggerActions = triggerActions;
    return this;
  }

  public AutomationsBody addTriggerActionsItem(String triggerActionsItem) {
    if (this.triggerActions == null) {
      this.triggerActions = new ArrayList<>();
    }
    this.triggerActions.add(triggerActionsItem);
    return this;
  }

   /**
   * If trigger is &#x60;action&#x60;, this is the list of action types on which to trigger the automation. Valid actions are create, read, update, destroy, move, copy
   * @return triggerActions
  **/
  @Schema(example = "[ \"create\" ]", description = "If trigger is `action`, this is the list of action types on which to trigger the automation. Valid actions are create, read, update, destroy, move, copy")
  public List<String> getTriggerActions() {
    return triggerActions;
  }

  public void setTriggerActions(List<String> triggerActions) {
    this.triggerActions = triggerActions;
  }

  public AutomationsBody triggerActionPath(String triggerActionPath) {
    this.triggerActionPath = triggerActionPath;
    return this;
  }

   /**
   * If trigger is &#x60;action&#x60;, this is the path to watch for the specified trigger actions.
   * @return triggerActionPath
  **/
  @Schema(example = "path/to/file/or/folder", description = "If trigger is `action`, this is the path to watch for the specified trigger actions.")
  public String getTriggerActionPath() {
    return triggerActionPath;
  }

  public void setTriggerActionPath(String triggerActionPath) {
    this.triggerActionPath = triggerActionPath;
  }

  public AutomationsBody value(Object value) {
    this.value = value;
    return this;
  }

   /**
   * A Hash of attributes specific to the automation type.
   * @return value
  **/
  @Schema(example = "{\"limit\": \"1\"}", description = "A Hash of attributes specific to the automation type.")
  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
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
    AutomationsBody automationsBody = (AutomationsBody) o;
    return Objects.equals(this.automation, automationsBody.automation) &&
        Objects.equals(this.source, automationsBody.source) &&
        Objects.equals(this.destination, automationsBody.destination) &&
        Objects.equals(this.destinations, automationsBody.destinations) &&
        Objects.equals(this.destinationReplaceFrom, automationsBody.destinationReplaceFrom) &&
        Objects.equals(this.destinationReplaceTo, automationsBody.destinationReplaceTo) &&
        Objects.equals(this.interval, automationsBody.interval) &&
        Objects.equals(this.path, automationsBody.path) &&
        Objects.equals(this.userIds, automationsBody.userIds) &&
        Objects.equals(this.groupIds, automationsBody.groupIds) &&
        Objects.equals(this.schedule, automationsBody.schedule) &&
        Objects.equals(this.trigger, automationsBody.trigger) &&
        Objects.equals(this.triggerActions, automationsBody.triggerActions) &&
        Objects.equals(this.triggerActionPath, automationsBody.triggerActionPath) &&
        Objects.equals(this.value, automationsBody.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(automation, source, destination, destinations, destinationReplaceFrom, destinationReplaceTo, interval, path, userIds, groupIds, schedule, trigger, triggerActions, triggerActionPath, value);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AutomationsBody {\n");
    
    sb.append("    automation: ").append(toIndentedString(automation)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
    sb.append("    destination: ").append(toIndentedString(destination)).append("\n");
    sb.append("    destinations: ").append(toIndentedString(destinations)).append("\n");
    sb.append("    destinationReplaceFrom: ").append(toIndentedString(destinationReplaceFrom)).append("\n");
    sb.append("    destinationReplaceTo: ").append(toIndentedString(destinationReplaceTo)).append("\n");
    sb.append("    interval: ").append(toIndentedString(interval)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    userIds: ").append(toIndentedString(userIds)).append("\n");
    sb.append("    groupIds: ").append(toIndentedString(groupIds)).append("\n");
    sb.append("    schedule: ").append(toIndentedString(schedule)).append("\n");
    sb.append("    trigger: ").append(toIndentedString(trigger)).append("\n");
    sb.append("    triggerActions: ").append(toIndentedString(triggerActions)).append("\n");
    sb.append("    triggerActionPath: ").append(toIndentedString(triggerActionPath)).append("\n");
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
