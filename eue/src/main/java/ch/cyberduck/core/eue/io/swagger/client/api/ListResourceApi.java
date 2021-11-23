package ch.cyberduck.core.eue.io.swagger.client.api;

import ch.cyberduck.core.eue.io.swagger.client.ApiException;
import ch.cyberduck.core.eue.io.swagger.client.ApiClient;
import ch.cyberduck.core.eue.io.swagger.client.Configuration;
import ch.cyberduck.core.eue.io.swagger.client.Pair;

import javax.ws.rs.core.GenericType;

import ch.cyberduck.core.eue.io.swagger.client.model.UiFsModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-10-14T22:10:10.297090+02:00[Europe/Zurich]")public class ListResourceApi {
  private ApiClient apiClient;

  public ListResourceApi() {
    this(Configuration.getDefaultApiClient());
  }

  public ListResourceApi(ApiClient apiClient) {
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
   * Retrieve the metadata of a file or of a container.
   * @param resourceId id of the resource (resourceURI) (required)
   * @param cookie cookie (optional)
   * @param ifNoneMatch optional If-None-Match header (optional)
   * @param downloadURIValidity optional parameter to suggest a lifetime for downloadURIs received through the options &#x27;open&#x27; and &#x27;download&#x27; (optional)
   * @param fields  (optional)
   * @param length max number of items to return (containers only) (optional)
   * @param offset start offset (containers only) (optional)
   * @param option decorator option (&lt; string &gt; array(csv)) (optional)
   * @param sort list of sort criteria (containers only) (optional)
   * @return UiFsModel
   * @throws ApiException if fails to make API call
   */
  public UiFsModel resourceResourceIdGet(String resourceId, String cookie, String ifNoneMatch, String downloadURIValidity, String fields, Integer length, Integer offset, String option, String sort) throws ApiException {
    Object localVarPostBody = null;
    // verify the required parameter 'resourceId' is set
    if (resourceId == null) {
      throw new ApiException(400, "Missing the required parameter 'resourceId' when calling resourceResourceIdGet");
    }
    // create path and map variables
    String localVarPath = "/resource/{resourceId}"
      .replaceAll("\\{" + "resourceId" + "\\}", apiClient.escapeString(resourceId.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    localVarQueryParams.addAll(apiClient.parameterToPairs("", "downloadURIValidity", downloadURIValidity));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "fields", fields));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "length", length));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "offset", offset));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "option", option));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "sort", sort));

    if (cookie != null)
      localVarHeaderParams.put("cookie", apiClient.parameterToString(cookie));
    if (ifNoneMatch != null)
      localVarHeaderParams.put("If-None-Match", apiClient.parameterToString(ifNoneMatch));

    final String[] localVarAccepts = {
      "application/json;charset=utf-8"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "bearerAuth" };

    GenericType<UiFsModel> localVarReturnType = new GenericType<UiFsModel>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
  }
}
