package com.twitter.x.mappers

import com.twitter.x.dtos.PostRequest
import com.twitter.x.dtos.PostResponse
import com.twitter.x.entities.Post
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface PostMapper {

    Post toEntity(PostRequest request)

    PostResponse toResponse(Post post)

    List<PostResponse> toResponseList(List<Post> posts)
}