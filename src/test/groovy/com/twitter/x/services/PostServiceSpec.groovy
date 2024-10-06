package com.twitter.x.services

import com.twitter.x.populators.PostPopulator
import com.twitter.x.services.impl.PostServiceImpl
import spock.lang.Specification
import com.twitter.x.repositories.PostRepository
import com.twitter.x.entities.Post

import org.bson.types.ObjectId
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations

class PostServiceSpec extends Specification {

    PostRepository postRepository = Mock()
    PostPopulator postPopulator = Mock()
    RedisTemplate<String, Integer> redisTemplate = Mock()
    PostServiceImpl postService = new PostServiceImpl(postRepository, postPopulator, redisTemplate)

    def "should create a post"() {
        given:
        def post = new Post()
        def savedPost = new Post(id: new ObjectId())

        when:
        postService.createPost(post)

        then:
        1 * postRepository.save(_ as Post) >> savedPost
    }

    def "should update a post"() {
        given:
        def postId = new ObjectId()
        def post = new Post(id: postId)
        def existingPost = new Post(id: postId)
        def updatedPost = new Post(id: postId)

        when:
        postService.updatePost(post)

        then:
        1 * postRepository.findById(postId) >> Optional.of(existingPost)
        1 * postPopulator.populatePost(post, existingPost) >> updatedPost
        1 * postRepository.save(updatedPost) >> updatedPost
    }

    def "should delete a post and its likes from Redis"() {
        given:
        def postId = new ObjectId()

        when:
        postService.deletePost(postId)

        then:
        1 * postRepository.deleteById(postId)
        1 * redisTemplate.delete("post:likes:$postId")
    }

    def "should like a post by incrementing likes in Redis"() {
        given:
        def postId = new ObjectId()
        ValueOperations<String, Integer> opsForValue = Mock()
        redisTemplate.opsForValue() >> opsForValue

        when:
        postService.likePost(postId)

        then:
        1 * opsForValue.increment("post:likes:$postId", 1)
    }

    def "should unlike a post by decrementing likes in Redis"() {
        given:
        def postId = new ObjectId()
        ValueOperations<String, Integer> opsForValue = Mock()
        redisTemplate.opsForValue() >> opsForValue

        when:
        postService.unlikePost(postId)

        then:
        1 * opsForValue.decrement("post:likes:$postId", 1)
    }
}
