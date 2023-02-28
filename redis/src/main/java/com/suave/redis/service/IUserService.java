package com.suave.redis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suave.redis.dto.LoginFormDTO;
import com.suave.redis.dto.Result;
import com.suave.redis.entity.User;
import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IUserService extends IService<User> {
    Result sendCode(String phone, HttpServletRequest request);

    Result login(LoginFormDTO loginForm, HttpServletRequest request);
}
