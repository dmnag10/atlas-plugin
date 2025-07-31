package com.atlassian.sample.dto;

import lombok.Data;

@Data
public class CommentDTO {
    private String id;
    private String issueId;
    private String author;
    private String message;
}
