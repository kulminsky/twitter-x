package com.twitter.x.services.impl

import com.twitter.x.entities.Comment
import com.twitter.x.populators.CommentPopulator
import com.twitter.x.repositories.CommentRepository
import com.twitter.x.services.CommentService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository
    private final CommentPopulator commentPopulator

    @Autowired
    CommentServiceImpl(CommentRepository commentRepository, CommentPopulator commentPopulator) {
        this.commentRepository = commentRepository
        this.commentPopulator = commentPopulator
    }

    @Override
    List<Comment> getAllComments() {
        return commentRepository.findAll()
    }

    @Override
    Comment getCommentById(ObjectId id) {
        return commentRepository.findById(id).orElse(null)
    }

    @Override
    Comment createComment(Comment comment) {
        comment.setCreatedAt(new Date())
       return commentRepository.save(comment)
    }

    @Override
    void updateComment(Comment comment) {
        ObjectId commentId = comment.getId()
        Optional<Comment> existingCommentOptional = commentRepository.findById(commentId)
        if (existingCommentOptional.isPresent()) {
            Comment result = commentPopulator.populateComment(comment, existingCommentOptional.get())
            commentRepository.save(result)
        } else {
            throw new IllegalArgumentException("Comment not found with ID: " + commentId)
        }
    }

    @Override
    List<Comment> getCommentsByPostId(ObjectId postId) {
        return commentRepository.findByPostId(postId)
    }

    @Override
    void deleteComment(ObjectId id) {
        commentRepository.deleteById(id)
    }
}
