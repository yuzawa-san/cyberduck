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
import ch.cyberduck.core.brick.io.swagger.client.model.MessageCommentReactionEntity;
import org.junit.Test;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for MessageCommentReactionsApi
 */
@Ignore
public class MessageCommentReactionsApiTest {

    private final MessageCommentReactionsApi api = new MessageCommentReactionsApi();

    
    /**
     * Delete Message Comment Reaction
     *
     * Delete Message Comment Reaction
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void deleteMessageCommentReactionsIdTest() throws ApiException {
        Integer id = null;
        api.deleteMessageCommentReactionsId(id);

        // TODO: test validations
    }
    
    /**
     * List Message Comment Reactions
     *
     * List Message Comment Reactions
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getMessageCommentReactionsTest() throws ApiException {
        Integer messageCommentId = null;
        Integer userId = null;
        String cursor = null;
        Integer perPage = null;
        List<MessageCommentReactionEntity> response = api.getMessageCommentReactions(messageCommentId, userId, cursor, perPage);

        // TODO: test validations
    }
    
    /**
     * Show Message Comment Reaction
     *
     * Show Message Comment Reaction
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getMessageCommentReactionsIdTest() throws ApiException {
        Integer id = null;
        MessageCommentReactionEntity response = api.getMessageCommentReactionsId(id);

        // TODO: test validations
    }
    
    /**
     * Create Message Comment Reaction
     *
     * Create Message Comment Reaction
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void postMessageCommentReactionsTest() throws ApiException {
        String emoji = null;
        Integer userId = null;
        MessageCommentReactionEntity response = api.postMessageCommentReactions(emoji, userId);

        // TODO: test validations
    }
    
}
