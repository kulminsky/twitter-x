package com.twitter.x.entities

import com.twitter.x.dtos.CommentResponse
import com.twitter.x.dtos.PostResponse
import groovy.transform.Canonical

@Canonical
class PostDetails {

    private PostResponse post
    private List<CommentResponse> comments
    private int likesCount

    PostDetails(PostResponse post, List<CommentResponse> comments, int likesCount) {
        this.post = post
        this.comments = comments
        this.likesCount = likesCount
    }
}
