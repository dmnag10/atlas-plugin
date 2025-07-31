package com.atlassian.sample.api;

import com.atlassian.sample.dto.CommentDTO;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * REST resource for managing comments.
 * Supports adding new comments and retrieving them by issue ID or author.
 */
@Path("/comment")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CommentResource {

    private static final Logger log = LoggerFactory.getLogger(CommentResource.class);

    private static final Map<String, CommentDTO> store = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Adds a new comment to the in-memory store.
     *
     * @param jsonInput the JSON input containing comment fields
     * @return 200 OK with saved comment or 400 if invalid
     */
    @POST
    public Response addComment(String jsonInput) {
        try {
            CommentDTO dto = objectMapper.readValue(jsonInput, CommentDTO.class);

            if (dto.getAuthor() == null || dto.getAuthor().isBlank() ||
                    dto.getIssueId() == null || dto.getIssueId().isBlank() ||
                    dto.getMessage() == null || dto.getMessage().isBlank()) {

                log.warn("Validation failed: missing or blank fields in comment input: {}", jsonInput);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"All fields (author, issueId, message) are required\"}")
                        .build();
            }

            String id = String.valueOf(idGenerator.incrementAndGet());
            dto.setId(id);
            store.put(id, dto);
            log.info("Saved new comment with ID={}, issueId={}, author={}", id, dto.getIssueId(), dto.getAuthor());

            return Response.ok(objectMapper.writeValueAsString(dto)).build();
        } catch (IOException e) {
            log.error("Failed to parse input JSON: {}", e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Invalid JSON format\"}")
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error while adding comment: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Server error while adding comment\"}")
                    .build();
        }
    }

    /**
     * Retrieves comments by issue ID or author.
     * - If `issueId` is provided, returns comments for that issue.
     * - If `author` is provided, returns comments by that author.
     * - If neither is provided, returns 400.
     *
     * @param issueId the issue ID to filter comments
     * @param author  the author name to filter comments
     * @return JSON array of comments
     */
    @GET
    public Response getCommentByQuery(
            @QueryParam("issueId") String issueId,
            @QueryParam("author") String author) {

        try {
            List<CommentDTO> results;
            if (issueId != null && !issueId.trim().isEmpty()) {
                results = store.values().stream()
                        .filter(c -> issueId.equals(c.getIssueId()))
                        .collect(Collectors.toList());
                log.info("Returning {} comments for issueId={}", results.size(), issueId);
            } else if (author != null && !author.trim().isEmpty()) {
                results = store.values().stream()
                        .filter(c -> author.equalsIgnoreCase(c.getAuthor()))
                        .collect(Collectors.toList());
                log.info("Returning {} comments by author={}", results.size(), author);
            } else {
                log.warn("Missing query parameter: either 'issueId' or 'author' must be provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Either 'issueId' or 'author' must be provided\"}")
                        .build();
            }

            return Response.ok(objectMapper.writeValueAsString(results)).build();
        } catch (Exception e) {
            log.error("Failed to fetch comments: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Failed to fetch comments\"}")
                    .build();
        }
    }
}