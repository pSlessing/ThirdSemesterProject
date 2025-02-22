package com.mailmak.time_registration_system.dto.comments;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateCommentRequest {
    private UUID taskId;
    private UUID userId;
    private String content;
}
