package com.twitter.x.controllers

import com.twitter.x.dtos.CommentResponse
import com.twitter.x.dtos.PostResponse
import com.twitter.x.dtos.RegisterRequest
import com.twitter.x.dtos.UserResponse
import com.twitter.x.entities.Comment
import com.twitter.x.entities.Post
import com.twitter.x.entities.PostDetails
import com.twitter.x.entities.User
import com.twitter.x.entities.UserProfile
import com.twitter.x.exceptions.UserNotFoundException
import com.twitter.x.exceptions.UserNotSubscribedException
import com.twitter.x.services.CommentService
import com.twitter.x.services.PostService
import com.twitter.x.services.UserService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController {

    private final UserService userService
    private final PostService postService
    private final CommentService commentService

    @Autowired
    UserController(UserService userService, PostService postService, CommentService commentService) {
        this.userService = userService
        this.postService = postService
        this.commentService = commentService
    }

    @PostMapping
    ResponseEntity<UserResponse> createUser(@RequestBody RegisterRequest request) {
        User user = userService.createUser(request.toUser())
        UserResponse userResponse = userMapper.toResponse(user)
        return userResponse != null ? ResponseEntity.ok(userResponse) : ResponseEntity.badRequest().build()
    }

    @GetMapping("/{id}")
    ResponseEntity<UserResponse> getUserById(@PathVariable(name = "id") ObjectId id) {
        UserResponse user = userService.getUserById(id)
        UserResponse userResponse = userMapper.toResponse(user)
        return userResponse != null ? ResponseEntity.ok(userResponse) : ResponseEntity.notFound().build()
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> updateUser(@PathVariable(name = "id") ObjectId id, @RequestBody User user) {
        user.setId(id)
        userService.updateUser(user)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{userId}/subscribe/{targetUserId}")
    ResponseEntity<Void> subscribeToUser(@PathVariable(name = "userId") ObjectId userId, @PathVariable(name = "targetUserId") ObjectId targetUserId) {
        try {
            userService.subscribeToUser(userId, targetUserId)
            return ResponseEntity.noContent().build()
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{userId}/unsubscribe/{targetUserId}")
    ResponseEntity<Void> unsubscribeFromUser(@PathVariable(name = "userId") ObjectId userId, @PathVariable(name = "targetUserId") ObjectId targetUserId) {
        try {
            userService.unsubscribeFromUser(userId, targetUserId)
            return ResponseEntity.noContent().build()
        } catch (UserNotFoundException | UserNotSubscribedException e) {
            return ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{userId}/profile")
    ResponseEntity<UserProfile> getUserProfile(@PathVariable(name = "userId") ObjectId userId) {
        User user = userService.getUserById(userId)
        UserResponse userResponse = userMapper.toResponse(user)
        List<Post> posts = postService.getPostsByUserId(userId)
        List<PostResponse> postResponses = postMapper.toResponseList(posts)

        List<PostDetails> postDetailsList = new ArrayList<>()
        for (PostResponse post : postResponses) {
            List<Comment> comments = commentService.getCommentsByPostId(post.getId())
            List<CommentResponse> commentResponses = commentMapper.toResponseList(comments)
            int likesCount = postService.getLikesCountByPostId(post.getId())
            PostDetails postDetails = new PostDetails(post, commentResponses, likesCount)
            postDetailsList.add(postDetails)
        }

        UserProfile userProfile = new UserProfile(userResponse, postDetailsList)
        return ResponseEntity.ok(userProfile)
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable(name = "id") ObjectId id) {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}
