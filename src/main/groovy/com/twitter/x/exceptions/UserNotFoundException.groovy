package com.twitter.x.exceptions

class UserNotFoundException extends RuntimeException {
    UserNotFoundException() {
        super()
    }

    UserNotFoundException(String message) {
        super(message)
    }
}