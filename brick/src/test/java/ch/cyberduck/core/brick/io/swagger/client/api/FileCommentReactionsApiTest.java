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
import ch.cyberduck.core.brick.io.swagger.client.model.FileCommentReactionEntity;
import org.junit.Test;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for FileCommentReactionsApi
 */
@Ignore
public class FileCommentReactionsApiTest {

    private final FileCommentReactionsApi api = new FileCommentReactionsApi();

    
    /**
     * Delete File Comment Reaction
     *
     * Delete File Comment Reaction
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void deleteFileCommentReactionsIdTest() throws ApiException {
        Integer id = null;
        api.deleteFileCommentReactionsId(id);

        // TODO: test validations
    }
    
    /**
     * Create File Comment Reaction
     *
     * Create File Comment Reaction
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void postFileCommentReactionsTest() throws ApiException {
        Integer fileCommentId = null;
        String emoji = null;
        Integer userId = null;
        FileCommentReactionEntity response = api.postFileCommentReactions(fileCommentId, emoji, userId);

        // TODO: test validations
    }
    
}
