package com.healthy.diet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthy.diet.entity.Post;
import com.healthy.diet.entity.PostComment;
import com.healthy.diet.mapper.PostMapper;
import com.healthy.diet.mapper.PostCommentMapper;
import com.healthy.diet.mapper.PostLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostCommentMapper postCommentMapper;

    @Autowired
    private PostLikeMapper postLikeMapper;

    @GetMapping("/list")
    public Page<Post> list(@RequestParam(defaultValue = "1") Integer page, 
                          @RequestParam(defaultValue = "15") Integer size) {
        Page<Post> pageParam = new Page<>(page, size);
        return postMapper.selectPage(pageParam, new QueryWrapper<Post>().eq("status", "PUBLISHED").orderByDesc("created_at"));
    }

    @GetMapping("/my")
    public List<Post> getMyPosts(@RequestParam Long userId) {
        return postMapper.selectList(new QueryWrapper<Post>().eq("author_id", userId).orderByDesc("created_at"));
    }

    @GetMapping("/{id}")
    public Map<String, Object> getDetail(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        Post post = postMapper.selectById(id);
        if (post != null) {
            post.setViewCount(post.getViewCount() + 1);
            postMapper.updateById(post);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("post", post);
        result.put("comments", postCommentMapper.getCommentsByPostId(id));
        result.put("likeCount", postLikeMapper.countByPostId(id));
        if (userId != null) {
            result.put("isLiked", postLikeMapper.checkLiked(userId, id) > 0);
        }
        return result;
    }

    @PostMapping("/like")
    public String toggleLike(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long postId = Long.valueOf(params.get("postId").toString());
        boolean isLiked = postLikeMapper.checkLiked(userId, postId) > 0;
        if (isLiked) {
            postLikeMapper.deleteLike(userId, postId);
            return "unliked";
        } else {
            postLikeMapper.insertLike(userId, postId);
            return "liked";
        }
    }

    @PostMapping("/comment")
    public String addComment(@RequestBody PostComment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        postCommentMapper.insert(comment);
        return "评论成功";
    }

    @PostMapping("/publish")
    public String publishPost(@RequestBody Post post) {
        if (post.getId() != null) {
            postMapper.updateById(post);
        } else {
            post.setCreatedAt(LocalDateTime.now());
            postMapper.insert(post);
        }
        return "文章发布成功";
    }

    @DeleteMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postMapper.deleteById(id);
        return "文章已删除";
    }
}

