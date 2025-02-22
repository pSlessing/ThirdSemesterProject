package com.mailmak.time_registration_system.service;

import com.mailmak.time_registration_system.classes.Comment;
import com.mailmak.time_registration_system.dto.comments.CreateCommentRequest;
import com.mailmak.time_registration_system.dto.comments.UpdateCommentRequest;

import java.util.List;
import java.util.UUID;

public interface CommentServiceInterface {
    Comment createComment(CreateCommentRequest request);
    List<Comment> getComments(UUID taskId);
    void updateComment(UpdateCommentRequest request);
    void deleteComment(UUID commentId);
}
