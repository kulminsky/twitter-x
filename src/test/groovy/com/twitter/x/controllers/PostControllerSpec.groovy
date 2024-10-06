package com.twitter.x.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.twitter.x.dtos.PostResponse
import com.twitter.x.services.PostService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.mockito.Mockito.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PostController)
class PostControllerSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @MockBean
    PostService postService

    def mapper = new ObjectMapper()

    def "should get all posts"() {
        given:
        def posts = [new PostResponse()]
        when(postService.getAllPosts()).thenReturn(posts)

        when:
        def resultActions = mockMvc.perform(get("/api/posts"))

        then:
        resultActions.andExpect(status().isOk())
    }

    def "should like a post"() {
        given:
        def postId = new ObjectId()
        doNothing().when(postService).likePost(postId)

        when:
        def resultActions = mockMvc.perform(post("/api/posts/${postId}/like"))

        then:
        resultActions.andExpect(status().isOk())
    }

    def "should unlike a post"() {
        given:
        def postId = new ObjectId()
        doNothing().when(postService).unlikePost(postId)

        when:
        def resultActions = mockMvc.perform(post("/api/posts/${postId}/unlike"))

        then:
        resultActions.andExpect(status().isOk())
    }

    def "should delete a post"() {
        given:
        def postId = new ObjectId()
        doNothing().when(postService).deletePost(postId)

        when:
        def resultActions = mockMvc.perform(delete("/api/posts/${postId}"))

        then:
        resultActions.andExpect(status().isNoContent())
    }
}
