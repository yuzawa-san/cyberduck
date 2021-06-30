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


package ch.cyberduck.core.brick.io.swagger.client.api;

import ch.cyberduck.core.brick.io.swagger.client.ApiException;
import ch.cyberduck.core.brick.io.swagger.client.model.BehaviorEntity;
import java.io.File;
import ch.cyberduck.core.brick.io.swagger.client.model.StatusEntity;
import org.junit.Test;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for BehaviorsApi
 */
@Ignore
public class BehaviorsApiTest {

    private final BehaviorsApi api = new BehaviorsApi();

    
    /**
     * Delete Behavior
     *
     * Delete Behavior
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void deleteBehaviorsIdTest() throws ApiException {
        Integer id = null;
        api.deleteBehaviorsId(id);

        // TODO: test validations
    }
    
    /**
     * List Behaviors
     *
     * List Behaviors
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getBehaviorsTest() throws ApiException {
        String cursor = null;
        Integer perPage = null;
        Map<String, String> sortBy = null;
        Map<String, String> filter = null;
        Map<String, String> filterGt = null;
        Map<String, String> filterGteq = null;
        Map<String, String> filterLike = null;
        Map<String, String> filterLt = null;
        Map<String, String> filterLteq = null;
        String behavior = null;
        List<BehaviorEntity> response = api.getBehaviors(cursor, perPage, sortBy, filter, filterGt, filterGteq, filterLike, filterLt, filterLteq, behavior);

        // TODO: test validations
    }
    
    /**
     * Show Behavior
     *
     * Show Behavior
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getBehaviorsIdTest() throws ApiException {
        Integer id = null;
        BehaviorEntity response = api.getBehaviorsId(id);

        // TODO: test validations
    }
    
    /**
     * List Behaviors by path
     *
     * List Behaviors by path
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void listForPathTest() throws ApiException {
        String path = null;
        String cursor = null;
        Integer perPage = null;
        Map<String, String> sortBy = null;
        Map<String, String> filter = null;
        Map<String, String> filterGt = null;
        Map<String, String> filterGteq = null;
        Map<String, String> filterLike = null;
        Map<String, String> filterLt = null;
        Map<String, String> filterLteq = null;
        String recursive = null;
        String behavior = null;
        List<BehaviorEntity> response = api.listForPath(path, cursor, perPage, sortBy, filter, filterGt, filterGteq, filterLike, filterLt, filterLteq, recursive, behavior);

        // TODO: test validations
    }
    
    /**
     * Update Behavior
     *
     * Update Behavior
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void patchBehaviorsIdTest() throws ApiException {
        Integer id = null;
        String value = null;
        File attachmentFile = null;
        String behavior = null;
        String path = null;
        BehaviorEntity response = api.patchBehaviorsId(id, value, attachmentFile, behavior, path);

        // TODO: test validations
    }
    
    /**
     * Create Behavior
     *
     * Create Behavior
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void postBehaviorsTest() throws ApiException {
        String path = null;
        String behavior = null;
        String value = null;
        File attachmentFile = null;
        BehaviorEntity response = api.postBehaviors(path, behavior, value, attachmentFile);

        // TODO: test validations
    }
    
    /**
     * Test webhook.
     *
     * Test webhook.
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void postBehaviorsWebhookTestTest() throws ApiException {
        String url = null;
        String method = null;
        String encoding = null;
        Map<String, String> headers = null;
        Map<String, String> body = null;
        String action = null;
        StatusEntity response = api.postBehaviorsWebhookTest(url, method, encoding, headers, body, action);

        // TODO: test validations
    }
    
}
