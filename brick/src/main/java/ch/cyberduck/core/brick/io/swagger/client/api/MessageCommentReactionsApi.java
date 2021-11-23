package ch.cyberduck.core.brick.io.swagger.client.api;

import ch.cyberduck.core.brick.io.swagger.client.ApiException;
import ch.cyberduck.core.brick.io.swagger.client.ApiClient;
import ch.cyberduck.core.brick.io.swagger.client.ApiResponse;
import ch.cyberduck.core.brick.io.swagger.client.Configuration;
import ch.cyberduck.core.brick.io.swagger.client.Pair;

import javax.ws.rs.core.GenericType;

import ch.cyberduck.core.brick.io.swagger.client.model.MessageCommentReactionEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2021-06-30T21:29:25.490+02:00")
public class MessageCommentReactionsApi {
  private ApiClient apiClient;

  public MessageCommentReactionsApi() {
    this(Configuration.getDefaultApiClient());
  }

  public MessageCommentReactionsApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Delete Message Comment Reaction
   * Delete Message Comment Reaction
   * @param id Message Comment Reaction ID. (required)
   * @throws ApiException if fails to make API call
   */
  public void deleteMessageCommentReactionsId(Integer id) throws ApiException {

    deleteMessageCommentReactionsIdWithHttpInfo(id);
  }

  /**
   * Delete Message Comment Reaction
   * Delete Message Comment Reaction
   * @param id Message Comment Reaction ID. (required)
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteMessageCommentReactionsIdWithHttpInfo(Integer id) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling deleteMessageCommentReactionsId");
    }
    
    // create path and map variables
    String localVarPath = "/message_comment_reactions/{id}"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };


    return apiClient.invokeAPI(localVarPath, "DELETE", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }
  /**
   * List Message Comment Reactions
   * List Message Comment Reactions
   * @param messageCommentId Message comment to return reactions for. (required)
   * @param userId User ID.  Provide a value of &#x60;0&#x60; to operate the current session&#39;s user. (optional)
   * @param cursor Used for pagination.  Send a cursor value to resume an existing list from the point at which you left off.  Get a cursor from an existing list via the X-Files-Cursor-Next header. (optional)
   * @param perPage Number of records to show per page.  (Max: 10,000, 1,000 or less is recommended). (optional)
   * @return List&lt;MessageCommentReactionEntity&gt;
   * @throws ApiException if fails to make API call
   */
  public List<MessageCommentReactionEntity> getMessageCommentReactions(Integer messageCommentId, Integer userId, String cursor, Integer perPage) throws ApiException {
    return getMessageCommentReactionsWithHttpInfo(messageCommentId, userId, cursor, perPage).getData();
      }

  /**
   * List Message Comment Reactions
   * List Message Comment Reactions
   * @param messageCommentId Message comment to return reactions for. (required)
   * @param userId User ID.  Provide a value of &#x60;0&#x60; to operate the current session&#39;s user. (optional)
   * @param cursor Used for pagination.  Send a cursor value to resume an existing list from the point at which you left off.  Get a cursor from an existing list via the X-Files-Cursor-Next header. (optional)
   * @param perPage Number of records to show per page.  (Max: 10,000, 1,000 or less is recommended). (optional)
   * @return ApiResponse&lt;List&lt;MessageCommentReactionEntity&gt;&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<List<MessageCommentReactionEntity>> getMessageCommentReactionsWithHttpInfo(Integer messageCommentId, Integer userId, String cursor, Integer perPage) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'messageCommentId' is set
    if (messageCommentId == null) {
      throw new ApiException(400, "Missing the required parameter 'messageCommentId' when calling getMessageCommentReactions");
    }
    
    // create path and map variables
    String localVarPath = "/message_comment_reactions";

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    localVarQueryParams.addAll(apiClient.parameterToPairs("", "user_id", userId));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "cursor", cursor));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "per_page", perPage));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "message_comment_id", messageCommentId));

    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };

    GenericType<List<MessageCommentReactionEntity>> localVarReturnType = new GenericType<List<MessageCommentReactionEntity>>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * Show Message Comment Reaction
   * Show Message Comment Reaction
   * @param id Message Comment Reaction ID. (required)
   * @return MessageCommentReactionEntity
   * @throws ApiException if fails to make API call
   */
  public MessageCommentReactionEntity getMessageCommentReactionsId(Integer id) throws ApiException {
    return getMessageCommentReactionsIdWithHttpInfo(id).getData();
      }

  /**
   * Show Message Comment Reaction
   * Show Message Comment Reaction
   * @param id Message Comment Reaction ID. (required)
   * @return ApiResponse&lt;MessageCommentReactionEntity&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<MessageCommentReactionEntity> getMessageCommentReactionsIdWithHttpInfo(Integer id) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling getMessageCommentReactionsId");
    }
    
    // create path and map variables
    String localVarPath = "/message_comment_reactions/{id}"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };

    GenericType<MessageCommentReactionEntity> localVarReturnType = new GenericType<MessageCommentReactionEntity>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * Create Message Comment Reaction
   * Create Message Comment Reaction
   * @param emoji Emoji to react with. (required)
   * @param userId User ID.  Provide a value of &#x60;0&#x60; to operate the current session&#39;s user. (optional)
   * @return MessageCommentReactionEntity
   * @throws ApiException if fails to make API call
   */
  public MessageCommentReactionEntity postMessageCommentReactions(String emoji, Integer userId) throws ApiException {
    return postMessageCommentReactionsWithHttpInfo(emoji, userId).getData();
      }

  /**
   * Create Message Comment Reaction
   * Create Message Comment Reaction
   * @param emoji Emoji to react with. (required)
   * @param userId User ID.  Provide a value of &#x60;0&#x60; to operate the current session&#39;s user. (optional)
   * @return ApiResponse&lt;MessageCommentReactionEntity&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<MessageCommentReactionEntity> postMessageCommentReactionsWithHttpInfo(String emoji, Integer userId) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'emoji' is set
    if (emoji == null) {
      throw new ApiException(400, "Missing the required parameter 'emoji' when calling postMessageCommentReactions");
    }
    
    // create path and map variables
    String localVarPath = "/message_comment_reactions";

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    if (userId != null)
      localVarFormParams.put("user_id", userId);
if (emoji != null)
      localVarFormParams.put("emoji", emoji);

    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      "application/json", "application/x-www-form-urlencoded", "multipart/form-data"
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };

    GenericType<MessageCommentReactionEntity> localVarReturnType = new GenericType<MessageCommentReactionEntity>() {};
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
}
