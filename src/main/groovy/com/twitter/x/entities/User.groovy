package com.twitter.x.entities

import groovy.transform.Canonical
import groovy.transform.TupleConstructor
import groovy.transform.builder.Builder
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@TupleConstructor
@Canonical
@Builder
@Document(collection = "users")
class User {

    @Id
    ObjectId id
    String username
    String password
    String email
    Date registrationDate
    Set<ObjectId> subscriptions = new HashSet<>()
    Set<ObjectId> followers = new HashSet<>()
}
