package com.mailmak.time_registration_system.dto.comments;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateCommentRequest {
    private UUID commentId;
    private String content;
}
