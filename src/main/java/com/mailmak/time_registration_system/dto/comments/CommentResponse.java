package com.mailmak.time_registration_system.dto.comments;

import com.mailmak.time_registration_system.dto.users.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class CommentResponse {
    private UUID id;
    private LocalDateTime createdDate;
    private String content;
    private UUID taskId;
    private UserResponse user;
}
