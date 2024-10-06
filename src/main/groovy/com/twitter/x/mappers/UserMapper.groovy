package com.twitter.x.mappers

import com.twitter.x.dtos.UserResponse
import com.twitter.x.entities.User
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface UserMapper {

    UserResponse toResponse(User user)

    List<UserResponse> toResponseList(List<User> users)
}