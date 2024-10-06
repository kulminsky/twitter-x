package com.twitter.x.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.twitter.x.entities.*
import com.twitter.x.exceptions.UserNotFoundException
import com.twitter.x.services.CommentService
import com.twitter.x.services.PostService
import com.twitter.x.services.UserService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.mockito.ArgumentMatchers.*
import static org.mockito.Mockito.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.http.MediaType

@WebMvcTest(UserController)
class UserControllerSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @MockBean
    UserService userService

    @MockBean
    PostService postService

    @MockBean
    CommentService commentService

    def mapper = new ObjectMapper()

    def "should update a user"() {
        given:
        def userId = new ObjectId()
        def user = new User()
        def userJson = mapper.writeValueAsString(user)
        doNothing().when(userService).updateUser(any(User))

        when:
        def resultActions = mockMvc.perform(put("/api/users/${userId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))

        then:
        resultActions.andExpect(status().isNoContent())
    }

    def "should subscribe to user"() {
        given:
        def userId = new ObjectId()
        def targetUserId = new ObjectId()
        doNothing().when(userService).subscribeToUser(userId, targetUserId)

        when:
        def resultActions = mockMvc.perform(put("/api/users/${userId}/subscribe/${targetUserId}"))

        then:
        resultActions.andExpect(status().isNoContent())
    }

    def "should handle UserNotFoundException when subscribing"() {
        given:
        def userId = new ObjectId()
        def targetUserId = new ObjectId()
        doThrow(new UserNotFoundException()).when(userService).subscribeToUser(userId, targetUserId)

        when:
        def resultActions = mockMvc.perform(put("/api/users/${userId}/subscribe/${targetUserId}"))

        then:
        resultActions.andExpect(status().isNotFound())
    }

    def "should unsubscribe from user"() {
        given:
        def userId = new ObjectId()
        def targetUserId = new ObjectId()
        doNothing().when(userService).unsubscribeFromUser(userId, targetUserId)

        when:
        def resultActions = mockMvc.perform(put("/api/users/${userId}/unsubscribe/${targetUserId}"))

        then:
        resultActions.andExpect(status().isNoContent())
    }

    def "should handle exceptions when unsubscribing"() {
        given:
        def userId = new ObjectId()
        def targetUserId = new ObjectId()
        doThrow(new UserNotFoundException()).when(userService).unsubscribeFromUser(userId, targetUserId)

        when:
        def resultActions = mockMvc.perform(put("/api/users/${userId}/unsubscribe/${targetUserId}"))

        then:
        resultActions.andExpect(status().isNotFound())
    }

    def "should delete a user"() {
        given:
        def userId = new ObjectId()
        doNothing().when(userService).deleteUser(userId)

        when:
        def resultActions = mockMvc.perform(delete("/api/users/${userId}"))

        then:
        resultActions.andExpect(status().isNoContent())
    }
}
