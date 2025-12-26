package com.healthy.diet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthy.diet.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PostLikeMapper {
    @Select("SELECT COUNT(*) FROM post_likes WHERE user_id = #{userId} AND post_id = #{postId}")
    int checkLiked(Long userId, Long postId);

    @Insert("INSERT INTO post_likes (user_id, post_id) VALUES (#{userId}, #{postId})")
    int insertLike(Long userId, Long postId);

    @Delete("DELETE FROM post_likes WHERE user_id = #{userId} AND post_id = #{postId}")
    int deleteLike(Long userId, Long postId);

    @Select("SELECT COUNT(*) FROM post_likes WHERE post_id = #{postId}")
    int countByPostId(Long postId);
}

