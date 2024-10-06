package com.twitter.x.services

import com.twitter.x.populators.UserPopulator
import com.twitter.x.services.impl.UserServiceImpl
import spock.lang.Specification
import com.twitter.x.repositories.UserRepository
import com.twitter.x.entities.User
import org.bson.types.ObjectId

class UserServiceSpec extends Specification {

    UserRepository userRepository = Mock()
    UserPopulator userPopulator = Mock()
    UserServiceImpl userService = new UserServiceImpl(userRepository, userPopulator)

    def "should create a user"() {
        given:
        def user = new User()
        def savedUser = new User(id: new ObjectId())

        when:
        def createdUser = userService.createUser(user)

        then:
        createdUser == savedUser
        1 * userRepository.save(_ as User) >> savedUser
    }

    def "should update a user"() {
        given:
        def userId = new ObjectId()
        def user = new User(id: userId)
        def existingUser = new User(id: userId)
        def updatedUser = new User(id: userId)

        when:
        userService.updateUser(user)

        then:
        1 * userRepository.findById(userId) >> Optional.of(existingUser)
        1 * userPopulator.populateUser(user, existingUser) >> updatedUser
        1 * userRepository.save(updatedUser) >> updatedUser
    }

    def "should delete a user"() {
        given:
        def userId = new ObjectId()

        when:
        userService.deleteUser(userId)

        then:
        1 * userRepository.deleteById(userId)
    }

    def "should subscribe to another user"() {
        given:
        def userId = new ObjectId()
        def targetUserId = new ObjectId()
        def user = new User(id: userId, subscriptions: [])
        def targetUser = new User(id: targetUserId, followers: [])

        when:
        userService.subscribeToUser(userId, targetUserId)

        then:
        1 * userRepository.findById(userId) >> Optional.of(user)
        1 * userRepository.findById(targetUserId) >> Optional.of(targetUser)
        1 * userRepository.save(user)
        1 * userRepository.save(targetUser)
        user.subscriptions.contains(targetUserId)
        targetUser.followers.contains(userId)
    }

    def "should unsubscribe from another user"() {
        given:
        def userId = new ObjectId()
        def targetUserId = new ObjectId()
        def user = new User(id: userId, subscriptions: [targetUserId])
        def targetUser = new User(id: targetUserId, followers: [userId])

        when:
        userService.unsubscribeFromUser(userId, targetUserId)

        then:
        1 * userRepository.findById(userId) >> Optional.of(user)
        1 * userRepository.findById(targetUserId) >> Optional.of(targetUser)
        1 * userRepository.save(user)
        1 * userRepository.save(targetUser)
        !user.subscriptions.contains(targetUserId)
        !targetUser.followers.contains(userId)
    }
}