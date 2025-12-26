package com.healthy.diet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthy.diet.entity.PostComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface PostCommentMapper extends BaseMapper<PostComment> {
    @Select("SELECT c.*, p.nickname as userName, p.avatar_url as userAvatar " +
            "FROM post_comments c " +
            "JOIN user_profiles p ON c.user_id = p.user_id " +
            "WHERE c.post_id = #{postId} " +
            "ORDER BY c.created_at DESC")
    List<Map<String, Object>> getCommentsByPostId(Long postId);
}

