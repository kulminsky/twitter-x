package com.twitter.x.services.impl

import com.twitter.x.dtos.UserResponse
import com.twitter.x.entities.User
import com.twitter.x.exceptions.UserNotFoundException
import com.twitter.x.exceptions.UserNotSubscribedException
import com.twitter.x.populators.UserPopulator
import com.twitter.x.repositories.UserRepository
import com.twitter.x.services.UserService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserServiceImpl implements UserService {

    private final UserRepository userRepository
    private final UserPopulator userPopulator

    @Autowired
    UserServiceImpl(UserRepository userRepository, UserPopulator userPopulator) {
        this.userRepository = userRepository
        this.userPopulator = userPopulator
    }

    @Override
    User createUser(User user) {
        return userRepository.save(user)
    }

    @Override
    List<User> getAllUsers() {
        return userRepository.findAll()
    }

    @Override
    User getUserById(ObjectId id) {
        return userRepository.findById(id).orElse(null)
    }

    @Override
    void updateUser(User user) {
        ObjectId userId = user.getId()
        Optional<User> existingUserOptional = userRepository.findById(userId)
        if (existingUserOptional.isPresent()) {
            User existingUser = userPopulator.populateUser(user, existingUserOptional.get())
            userRepository.save(existingUser)
        } else {
            throw new IllegalArgumentException("User not found with ID: " + userId)
        }
    }

    @Override
    void deleteUser(ObjectId id) {
        userRepository.deleteById(id)
    }

    @Override
    void subscribeToUser(ObjectId userId, ObjectId targetUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId))

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + targetUserId))

        user.getSubscriptions().add(targetUser.id)
        targetUser.getFollowers().add(user.id)
        userRepository.save(user)
        userRepository.save(targetUser)
    }

    @Override
    void unsubscribeFromUser(ObjectId userId, ObjectId targetUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId))

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + targetUserId))

        if (!user.getSubscriptions().contains(targetUserId)) {
            throw new UserNotSubscribedException("User with ID " + userId + " is not subscribed to user with ID " + targetUserId)
        }

        user.getSubscriptions().remove(targetUserId);
        targetUser.getFollowers().remove(user.id)
        userRepository.save(user);
        userRepository.save(targetUser)
    }
}
