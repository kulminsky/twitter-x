package com.twitter.x.entities

import groovy.transform.Canonical
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Canonical
@Document(collection = "posts")
class Post {

    @Id
    ObjectId id
    String content
    ObjectId userId
    Date createdAt
    int likes = 0
    int comments
}
