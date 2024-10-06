package com.twitter.x.controllers

import com.twitter.x.dtos.CommentResponse
import com.twitter.x.dtos.CommentRequest
import com.twitter.x.entities.Comment
import com.twitter.x.mappers.CommentMapper
import com.twitter.x.services.CommentService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts/{postId}/comments")
class CommentController {

    private final CommentService commentService
    private final CommentMapper commentMapper

    @Autowired
    CommentController(CommentService commentService, CommentMapper commentMapper) {
        this.commentService = commentService
        this.commentMapper = commentMapper
    }
    @GetMapping
    List<CommentResponse> getCommentsByPostId(@PathVariable(name = "postId") ObjectId postId) {
        def comments = commentService.getCommentsByPostId(postId)
        return commentMapper.toResponseList(comments)
    }

    @PostMapping
    ResponseEntity<CommentResponse> createComment(@PathVariable(name = "postId") ObjectId postId, @RequestBody CommentRequest request, @RequestParam("userId") ObjectId userId) {
        Comment comment = commentMapper.toEntity(request)
        comment.setPostId(postId)
        comment.setUserId(userId)
        Comment createdComment = commentService.createComment(comment)
        CommentResponse commentResponse = commentMapper.toResponse(createdComment)
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse)
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> updateComment(@PathVariable(name = "id") ObjectId id, @RequestBody CommentRequest request) {
        Comment comment = commentMapper.toEntity(request)
        comment.setId(id)
        commentService.updateComment(comment)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteComment(@PathVariable(name = "id") ObjectId id) {
        commentService.deleteComment(id)
        return ResponseEntity.noContent().build()
    }
}
