package com.twitter.x.dtos

import com.twitter.x.entities.User
import groovy.transform.Canonical
import groovy.transform.TupleConstructor

@TupleConstructor
@Canonical
class RegisterRequest {

    private String username
    private String email
    private String password

    User toUser() {
        return  User.builder()
                .username(username)
                .email(email)
                .password(password)
                .build()
    }
}
