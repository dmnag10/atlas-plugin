package com.atlassian.sample.api;


import com.atlassian.sample.dto.CommentDTO;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class CommentResourceTest {

    private CommentResource resource;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        resource = new CommentResource();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testAddComment_success() throws Exception {
        CommentDTO dto = new CommentDTO();
        dto.setIssueId("ISSUE-123");
        dto.setAuthor("john");
        dto.setMessage("This is a test comment");

        String json = objectMapper.writeValueAsString(dto);
        Response response = resource.addComment(json);

        assertEquals(200, response.getStatus());
        String jsonResponse = (String) response.getEntity();
        CommentDTO returned = objectMapper.readValue(jsonResponse, CommentDTO.class);
        assertEquals("john", returned.getAuthor());
        assertNotNull(returned.getId());
    }

    @Test
    public void testAddComment_missingFields() {
        String json = "{\"issueId\": \"ISSUE-123\"}"; // Missing author and message
        Response response = resource.addComment(json);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void testGetCommentByIssueId_success() throws Exception {
        String commentJson = "{\"issueId\": \"ISSUE-42\", \"author\": \"alice\", \"message\": \"ok\"}";
        resource.addComment(commentJson);

        Response response = resource.getCommentByQuery("ISSUE-42", null);
        assertEquals(200, response.getStatus());
        String jsonResponse = (String) response.getEntity();
        List<CommentDTO> list = objectMapper.readValue(jsonResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, CommentDTO.class));
        assertFalse(list.isEmpty());
        assertEquals("alice", list.get(0).getAuthor());
    }

    @Test
    public void testGetCommentByAuthor_success() throws Exception {
        String commentJson = "{\"issueId\": \"ISSUE-1\", \"author\": \"bob\", \"message\": \"Looks good\"}";
        resource.addComment(commentJson);

        Response response = resource.getCommentByQuery(null, "bob");
        assertEquals(200, response.getStatus());
        String jsonResponse = (String) response.getEntity();
        List<CommentDTO> list = objectMapper.readValue(jsonResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, CommentDTO.class));
        assertFalse(list.isEmpty());
        assertEquals("bob", list.get(0).getAuthor());
    }

    @Test
    public void testGetCommentByQuery_missingParams() {
        Response response = resource.getCommentByQuery(null, null);
        assertEquals(400, response.getStatus());
    }
}