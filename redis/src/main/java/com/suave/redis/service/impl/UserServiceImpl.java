package com.suave.redis.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.server.HttpServerRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suave.redis.dto.LoginFormDTO;
import com.suave.redis.dto.Result;
import com.suave.redis.entity.User;
import com.suave.redis.mapper.UserMapper;
import com.suave.redis.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.AbstractQueue;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Override
    public Result sendCode(String phone, HttpServletRequest request) {
        if (!Validator.isMobile(phone)) {
            return Result.fail("手机号格式错误");
        }
        String code = RandomUtil.randomNumbers(6);
        request.getSession().setAttribute("code", code);
        log.info("发送验证码成功，验证码:{}", code);
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpServletRequest request) {
        String phone = loginForm.getPhone();
        if (!Validator.isMobile(phone)) {
            return Result.fail("手机号格式错误");
        }
        if (!Objects.equals(loginForm.getCode(), request.getSession().getAttribute("code"))) {
            return Result.fail("验证码错误");
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = baseMapper.selectOne(wrapper);
        if (Objects.isNull(user)) {
            user = new User();
            user.setPhone(phone);
            user.setNickName(IdUtil.fastSimpleUUID());
            baseMapper.insert(user);
        }
        request.getSession().setAttribute("user", user);
        return Result.ok();
    }
}
