package com.twitter.x.services.impl

import com.twitter.x.dtos.PostResponse
import com.twitter.x.entities.Post
import com.twitter.x.populators.PostPopulator
import com.twitter.x.repositories.PostRepository
import com.twitter.x.services.PostService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.data.redis.core.RedisTemplate

@EnableAsync
@Service
class PostServiceImpl implements PostService {

    private final PostRepository postRepository
    private final PostPopulator postPopulator
    private final RedisTemplate<String, Integer> redisTemplate

    @Autowired
    PostServiceImpl(PostRepository postRepository, PostPopulator postPopulator, RedisTemplate<String, Integer> redisTemplate) {
        this.postRepository = postRepository
        this.postPopulator = postPopulator
        this.redisTemplate = redisTemplate
    }

    @Override
    List<Post> getAllPosts() {
        return postRepository.findAll()
    }

    @Override
    Post getPostById(ObjectId id) {
        return postRepository.findById(id).orElse(null)
    }

    @Override
    Post createPost(Post post) {
        post.setCreatedAt(new Date())
        return postRepository.save(post)
    }

    @Override
    void updatePost(Post post) {
        ObjectId postId = post.getId()
        Optional<Post> existingPostOptional = postRepository.findById(postId)
        if (existingPostOptional.isPresent()) {
            Post existingPost = postPopulator.populatePost(post, existingPostOptional.get())
            postRepository.save(existingPost)
        } else {
            throw new IllegalArgumentException("Post not found with ID: " + postId)
        }
    }

    @Override
    List<Post> getPostsByUserId(ObjectId userId) {
        return postRepository.findByUserId(userId)
    }

    @Override
    void deletePost(ObjectId id) {
        postRepository.deleteById(id)

        String redisKey = "post:likes:" + id.toString()
        redisTemplate.delete(redisKey)
    }

    @Override
    int getLikesCountByPostId(ObjectId postId) {
        String key = "post:likes:" + postId.toString()
        return redisTemplate.opsForValue().get(key) ?: 0
    }

    @Override
    void likePost(ObjectId postId) {
        String key = "post:likes:" + postId.toString()
        redisTemplate.opsForValue().increment(key, 1)
    }

    @Override
    void unlikePost(ObjectId postId) {
        String key = "post:likes:" + postId.toString()
        redisTemplate.opsForValue().decrement(key, 1)
    }

    @Scheduled(fixedDelay = 60000)
    void syncLikesWithDatabase() {
        Set<String> keys = redisTemplate.keys("post:likes:*")

        keys.each { key ->
            Integer likes = redisTemplate.opsForValue().get(key)

            if (likes != null) {
                String postIdStr = key.replace("post:likes:", "")
                ObjectId postId = new ObjectId(postIdStr)

                Post post = postRepository.findById(postId)
                        .orElse(null)

                if (post != null) {
                    post.setLikes(likes)
                    postRepository.save(post)
                }
            }
        }
    }
}
