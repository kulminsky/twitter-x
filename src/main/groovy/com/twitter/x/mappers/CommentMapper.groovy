package com.twitter.x.mappers

import com.twitter.x.dtos.CommentResponse
import com.twitter.x.dtos.CommentRequest
import com.twitter.x.entities.Comment
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface CommentMapper {

    Comment toEntity(CommentRequest request)

    CommentResponse toResponse(Comment comment)

    List<CommentResponse> toResponseList(List<Comment> comments)
}