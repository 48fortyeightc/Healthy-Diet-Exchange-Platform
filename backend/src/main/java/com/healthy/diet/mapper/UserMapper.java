package com.healthy.diet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthy.diet.entity.User;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;

public interface UserMapper extends BaseMapper<User> {
    
    // 调用存储过程进行安全注册
    @Select("{call sp_register_user(#{username, mode=IN}, #{passwordHash, mode=IN}, #{email, mode=IN}, #{phone, mode=IN}, #{nickname, mode=IN})}")
    @Options(statementType = StatementType.CALLABLE)
    void registerUser(String username, String passwordHash, String email, String phone, String nickname);
}



