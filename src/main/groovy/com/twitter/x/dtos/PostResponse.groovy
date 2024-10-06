package com.twitter.x.dtos

import org.bson.types.ObjectId

class PostResponse {
    ObjectId id
    String content
    Date createdAt
}
