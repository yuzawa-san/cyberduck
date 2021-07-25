/*
 * DRACOON API
 * REST Web Services for DRACOON<br>built at: 1970-01-01 00:00:00.000<br><br>This page provides an overview of all available and documented DRACOON APIs, which are grouped by tags.<br>Each tag provides a collection of APIs that are intended for a specific area of the DRACOON.<br><br><a title='Developer Information' href='https://developer.dracoon.com'>Developer Information</a>&emsp;&emsp;<a title='Get SDKs on GitHub' href='https://github.com/dracoon'>Get SDKs on GitHub</a><br><br><a title='Terms of service' href='https://www.dracoon.com/terms/general-terms-and-conditions/'>Terms of service</a>
 *
 * OpenAPI spec version: 4.28.3
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ch.cyberduck.core.sds.io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import ch.cyberduck.core.sds.io.swagger.client.model.MoveNode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
/**
 * Request model for moving nodes
 */
@Schema(description = "Request model for moving nodes")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-07-25T23:34:01.480829+02:00[Europe/Paris]")
public class MoveNodesRequest {
  @JsonProperty("items")
  private List<MoveNode> items = null;

  /**
   * Node conflict resolution strategy:  * &#x60;autorename&#x60;  * &#x60;overwrite&#x60;  * &#x60;fail&#x60;
   */
  public enum ResolutionStrategyEnum {
    AUTORENAME("autorename"),
    OVERWRITE("overwrite"),
    FAIL("fail");

    private String value;

    ResolutionStrategyEnum(String value) {
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
    public static ResolutionStrategyEnum fromValue(String text) {
      for (ResolutionStrategyEnum b : ResolutionStrategyEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("resolutionStrategy")
  private ResolutionStrategyEnum resolutionStrategy = ResolutionStrategyEnum.AUTORENAME;

  @JsonProperty("keepShareLinks")
  private Boolean keepShareLinks = false;

  @JsonProperty("nodeIds")
  private List<Long> nodeIds = null;

  public MoveNodesRequest items(List<MoveNode> items) {
    this.items = items;
    return this;
  }

  public MoveNodesRequest addItemsItem(MoveNode itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

   /**
   * List of nodes to be moved
   * @return items
  **/
  @Schema(description = "List of nodes to be moved")
  public List<MoveNode> getItems() {
    return items;
  }

  public void setItems(List<MoveNode> items) {
    this.items = items;
  }

  public MoveNodesRequest resolutionStrategy(ResolutionStrategyEnum resolutionStrategy) {
    this.resolutionStrategy = resolutionStrategy;
    return this;
  }

   /**
   * Node conflict resolution strategy:  * &#x60;autorename&#x60;  * &#x60;overwrite&#x60;  * &#x60;fail&#x60;
   * @return resolutionStrategy
  **/
  @Schema(description = "Node conflict resolution strategy:  * `autorename`  * `overwrite`  * `fail`")
  public ResolutionStrategyEnum getResolutionStrategy() {
    return resolutionStrategy;
  }

  public void setResolutionStrategy(ResolutionStrategyEnum resolutionStrategy) {
    this.resolutionStrategy = resolutionStrategy;
  }

  public MoveNodesRequest keepShareLinks(Boolean keepShareLinks) {
    this.keepShareLinks = keepShareLinks;
    return this;
  }

   /**
   * Preserve Download Share Links and point them to the new node.
   * @return keepShareLinks
  **/
  @Schema(description = "Preserve Download Share Links and point them to the new node.")
  public Boolean isKeepShareLinks() {
    return keepShareLinks;
  }

  public void setKeepShareLinks(Boolean keepShareLinks) {
    this.keepShareLinks = keepShareLinks;
  }

  public MoveNodesRequest nodeIds(List<Long> nodeIds) {
    this.nodeIds = nodeIds;
    return this;
  }

  public MoveNodesRequest addNodeIdsItem(Long nodeIdsItem) {
    if (this.nodeIds == null) {
      this.nodeIds = new ArrayList<>();
    }
    this.nodeIds.add(nodeIdsItem);
    return this;
  }

   /**
   * &amp;#128679; Deprecated since v4.5.0  Node IDs  Please use &#x60;items&#x60; instead.
   * @return nodeIds
  **/
  @Schema(description = "&#128679; Deprecated since v4.5.0  Node IDs  Please use `items` instead.")
  public List<Long> getNodeIds() {
    return nodeIds;
  }

  public void setNodeIds(List<Long> nodeIds) {
    this.nodeIds = nodeIds;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MoveNodesRequest moveNodesRequest = (MoveNodesRequest) o;
    return Objects.equals(this.items, moveNodesRequest.items) &&
        Objects.equals(this.resolutionStrategy, moveNodesRequest.resolutionStrategy) &&
        Objects.equals(this.keepShareLinks, moveNodesRequest.keepShareLinks) &&
        Objects.equals(this.nodeIds, moveNodesRequest.nodeIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(items, resolutionStrategy, keepShareLinks, nodeIds);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MoveNodesRequest {\n");
    
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    resolutionStrategy: ").append(toIndentedString(resolutionStrategy)).append("\n");
    sb.append("    keepShareLinks: ").append(toIndentedString(keepShareLinks)).append("\n");
    sb.append("    nodeIds: ").append(toIndentedString(nodeIds)).append("\n");
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
