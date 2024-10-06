package com.twitter.x.entities

import com.twitter.x.dtos.UserResponse
import groovy.transform.Canonical
import groovy.transform.TupleConstructor

@TupleConstructor
@Canonical
class UserProfile {

    private UserResponse user
    private List<PostDetails> posts

    UserProfile(UserResponse user, List<PostDetails> posts) {
        this.user = user
        this.posts = posts
    }
}
