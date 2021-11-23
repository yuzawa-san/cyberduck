package ch.cyberduck.core.gmxcloud.io.swagger.client.api;

import ch.cyberduck.core.gmxcloud.io.swagger.client.ApiException;
import ch.cyberduck.core.gmxcloud.io.swagger.client.ApiClient;
import ch.cyberduck.core.gmxcloud.io.swagger.client.Configuration;
import ch.cyberduck.core.gmxcloud.io.swagger.client.Pair;

import javax.ws.rs.core.GenericType;

import ch.cyberduck.core.gmxcloud.io.swagger.client.model.ResourceCopyResponseEntries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-10-14T22:10:10.297090+02:00[Europe/Zurich]")public class CopyChildrenApi {
  private ApiClient apiClient;

  public CopyChildrenApi() {
    this(Configuration.getDefaultApiClient());
  }

  public CopyChildrenApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * 
   * Copy resources from one container to another.
   * @param resourceId  (required)
   * @param body  (optional)
   * @param cookie cookie (optional)
   * @param ifMatch ifMatchHeader (optional)
   * @param autoRename (deprecated) flag for enforcing automatic rename on conflict (optional)
   * @param conflictResolution conflictResolution - overwrite or rename (optional)
   * @param lockToken the lock token used to access a locked resource (optional)
   * @return ResourceCopyResponseEntries
   * @throws ApiException if fails to make API call
   */
  public ResourceCopyResponseEntries resourceResourceIdChildrenCopyPost(String resourceId, List<String> body, String cookie, String ifMatch, Boolean autoRename, String conflictResolution, String lockToken) throws ApiException {
    Object localVarPostBody = body;
    // verify the required parameter 'resourceId' is set
    if (resourceId == null) {
      throw new ApiException(400, "Missing the required parameter 'resourceId' when calling resourceResourceIdChildrenCopyPost");
    }
    // create path and map variables
    String localVarPath = "/resource/{resourceId}/children/copy"
      .replaceAll("\\{" + "resourceId" + "\\}", apiClient.escapeString(resourceId.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    localVarQueryParams.addAll(apiClient.parameterToPairs("", "autoRename", autoRename));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "conflictResolution", conflictResolution));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "lockToken", lockToken));

    if (cookie != null)
      localVarHeaderParams.put("cookie", apiClient.parameterToString(cookie));
    if (ifMatch != null)
      localVarHeaderParams.put("If-Match", apiClient.parameterToString(ifMatch));

    final String[] localVarAccepts = {
      "appplication/json:charset=utf-8", "application/json;charset=utf-8"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      "application/json"
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "bearerAuth" };

    GenericType<ResourceCopyResponseEntries> localVarReturnType = new GenericType<ResourceCopyResponseEntries>() {};
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
  }
}
