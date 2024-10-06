package com.twitter.x.controllers

import com.twitter.x.dtos.PostRequest
import com.twitter.x.dtos.PostResponse
import com.twitter.x.entities.Post
import com.twitter.x.services.PostService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts")
class PostController {

    private final PostService postService

    @Autowired
    PostController(PostService postService) {
        this.postService = postService
    }

    @GetMapping
    List<Post> getAllPosts() {
        postService.getAllPosts()
    }

    @GetMapping("/{id}")
    ResponseEntity<PostResponse> getPostById(@PathVariable(name = "id") ObjectId id) {
        Post post = postService.getPostById(id)
        PostResponse postResponse = postMapper.toResponse(post)
        return postResponse ? ResponseEntity.ok(postResponse) : ResponseEntity.notFound().build() as ResponseEntity<PostResponse>
    }

    @PostMapping
    ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request, @RequestParam("userId") ObjectId userId) {
        Post post = postMapper.toEntity(request)
        post.setUserId(userId)
        Post createdPost = postService.createPost(post)
        PostResponse postResponse = postMapper.toResponse(createdPost)
        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse)
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> updatePost(@PathVariable(name = "id") ObjectId id, @RequestBody PostRequest request) {
        Post post = postMapper.toEntity(request)
        post.setId(id)
        postService.updatePost(post)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{postId}/like")
    ResponseEntity<?> likePost(@PathVariable(name = "postId") ObjectId postId) {
        postService.likePost(postId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{postId}/unlike")
    ResponseEntity<?> unlikePost(@PathVariable(name = "postId") ObjectId postId) {
        postService.unlikePost(postId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletePost(@PathVariable(name = "id") ObjectId id) {
        postService.deletePost(id)
        return ResponseEntity.noContent().build()
    }
}