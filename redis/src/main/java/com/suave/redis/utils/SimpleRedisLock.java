package com.suave.redis.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Redis锁实现类
 *
 * @author Suave
 * @since 2023/03/06 13:43
 */
public class SimpleRedisLock implements ILock {

    private String name;
    private StringRedisTemplate stringRedisTemplate;

    public static final String KEY_PREFIX = "lock:";

    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean tryLock(long time) {
        long threadId = Thread.currentThread().getId();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent(KEY_PREFIX + name, threadId + "", time, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(lock);
    }

    @Override
    public void unlock() {
        String value = stringRedisTemplate.opsForValue().get(KEY_PREFIX + name);
        if (Objects.equals(value, Thread.currentThread().getId() + "")) {
            stringRedisTemplate.delete(KEY_PREFIX + name);
        }
    }
}
