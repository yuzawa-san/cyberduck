package ch.cyberduck.core.eue.io.swagger.client.api;

import ch.cyberduck.core.eue.io.swagger.client.ApiException;
import ch.cyberduck.core.eue.io.swagger.client.ApiClient;
import ch.cyberduck.core.eue.io.swagger.client.Configuration;
import ch.cyberduck.core.eue.io.swagger.client.Pair;

import javax.ws.rs.core.GenericType;

import ch.cyberduck.core.eue.io.swagger.client.model.OptionsQueryParam;
import ch.cyberduck.core.eue.io.swagger.client.model.ResourceCreationRepresentationArrayInner;
import ch.cyberduck.core.eue.io.swagger.client.model.ResourceCreationResponseEntries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-10-14T22:10:10.297090+02:00[Europe/Zurich]")public class PostChildrenForAliasApi {
  private ApiClient apiClient;

  public PostChildrenForAliasApi() {
    this(Configuration.getDefaultApiClient());
  }

  public PostChildrenForAliasApi(ApiClient apiClient) {
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
   * Prepare upload of files or create container/nested containers like \&quot;mkdir -p\&quot;
   * @param alias id of the resource (resourceURI) (required)
   * @param body  (optional)
   * @param cookie cookie (optional)
   * @param ifMatch ifMatchHeader (optional)
   * @param conflictResolution conflictResolution - rename or none (default) (optional)
   * @param lockToken the lock token used to access a locked resource (optional)
   * @param option Optional parameter indicating if the upload should be with internal Uri (optional)
   * @return ResourceCreationResponseEntries
   * @throws ApiException if fails to make API call
   */
  public ResourceCreationResponseEntries resourceAliasAliasChildrenPost(String alias, List<ResourceCreationRepresentationArrayInner> body, String cookie, String ifMatch, String conflictResolution, String lockToken, OptionsQueryParam option) throws ApiException {
    Object localVarPostBody = body;
    // verify the required parameter 'alias' is set
    if (alias == null) {
      throw new ApiException(400, "Missing the required parameter 'alias' when calling resourceAliasAliasChildrenPost");
    }
    // create path and map variables
    String localVarPath = "/resourceAlias/{alias}/children"
      .replaceAll("\\{" + "alias" + "\\}", apiClient.escapeString(alias.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    localVarQueryParams.addAll(apiClient.parameterToPairs("", "conflictResolution", conflictResolution));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "lockToken", lockToken));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "option", option));

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

    GenericType<ResourceCreationResponseEntries> localVarReturnType = new GenericType<ResourceCreationResponseEntries>() {};
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
  }
}
