package com.twitter.x.entities

import groovy.transform.Canonical
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Canonical
@Document(collection = "comments")
class Comment {

    @Id
    ObjectId id
    String content
    ObjectId postId
    ObjectId userId
    Date createdAt
}
