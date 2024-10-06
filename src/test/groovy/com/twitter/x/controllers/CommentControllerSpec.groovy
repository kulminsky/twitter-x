package com.twitter.x.controllers

import com.twitter.x.dtos.CommentResponse
import com.twitter.x.mappers.CommentMapper
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import spock.lang.Specification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.bson.types.ObjectId
import com.twitter.x.entities.Comment
import com.twitter.x.services.CommentService

import static org.mockito.Mockito.*

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(CommentController)
class CommentControllerSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @MockBean
    CommentService commentService

    @MockBean
    CommentMapper commentMapper

    def "should get comments by post ID"() {
        given:
        def postId = new ObjectId()
        def comments = [new Comment()]
        def commentsResponse = [new CommentResponse()]
        commentService.getCommentsByPostId(postId) >> comments
        commentMapper.toResponseList(comments) >> commentsResponse

        when:
        def resultActions = mockMvc.perform(get("/api/posts/${postId}/comments"))

        then:
        resultActions.andExpect(status().isOk())
    }

    def "should delete a comment"() {
        given:
        def postId = new ObjectId()
        def commentId = new ObjectId()

        when:
        def resultActions = mockMvc.perform(delete("/api/posts/${postId}/comments/${commentId}"))

        then:
        resultActions.andExpect(status().isNoContent())
        verify(commentService, times(1)).deleteComment(commentId)
    }
}
