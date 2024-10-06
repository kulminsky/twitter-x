package com.twitter.x.dtos

import org.bson.types.ObjectId

class UserResponse {
    ObjectId id
    String username
    String email
    Date registrationDate
    Set<ObjectId> subscriptions = new HashSet<>()
    Set<ObjectId> followers = new HashSet<>()
}
