package com.twitter.x.services

import com.twitter.x.entities.Comment
import com.twitter.x.populators.CommentPopulator
import com.twitter.x.repositories.CommentRepository
import com.twitter.x.services.impl.CommentServiceImpl
import org.bson.types.ObjectId
import spock.lang.Specification

class CommentServiceSpec extends Specification {

    CommentRepository commentRepository = Mock()
    CommentPopulator commentPopulator = Mock()
    def commentService = new CommentServiceImpl(commentRepository, commentPopulator)

    def "should create a comment"() {
        given:
        def comment = new Comment(content: "Test Comment")
        def savedComment = new Comment(
                id: new ObjectId(),
                content: "Test Comment",
                createdAt: new Date()
        )

        when:
        def createdComment = commentService.createComment(comment)

        then:
        createdComment != null
        createdComment == savedComment
        1 * commentRepository.save({ it.content == "Test Comment" && it.createdAt != null }) >> savedComment
    }

    def "should get all comments"() {
        given:
        def comments = [new Comment(content: "Test Comment")]

        when:
        def result = commentService.getAllComments()

        then:
        result != null
        result == comments
        1 * commentRepository.findAll() >> comments
    }

    def "should get a comment by ID"() {
        given:
        def commentId = new ObjectId()
        def comment = new Comment(id: commentId, content: "Test Comment")
        
        when:
        def result = commentService.getCommentById(commentId)

        then:
        result != null
        result == comment
        1 * commentRepository.findById({ it == commentId }) >> Optional.of(comment)
    }

    def "should update a comment"() {
        given:
        def commentId = new ObjectId()
        def existingComment = new Comment(id: commentId, content: "Old Content")
        def updatedComment = new Comment(id: commentId, content: "Updated Content")

        when:
        commentService.updateComment(updatedComment)

        then:
        1 * commentRepository.findById({ it == commentId }) >> Optional.of(existingComment)
        1 * commentPopulator.populateComment({ it.id == commentId }, existingComment) >> updatedComment
        1 * commentRepository.save({ it.id == commentId && it.content == "Updated Content" }) >> updatedComment
    }

    def "should get comments by post ID"() {
        given:
        def postId = new ObjectId()
        def comments = [new Comment(content: "Test Comment")]

        when:
        def result = commentService.getCommentsByPostId(postId)

        then:
        result != null
        result == comments
        1 * commentRepository.findByPostId(postId) >> comments
    }
}
