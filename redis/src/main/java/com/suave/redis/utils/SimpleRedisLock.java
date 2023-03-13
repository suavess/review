package com.suave.redis.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
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
    public static final DefaultRedisScript<Long> REDIS_SCRIPT;

    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    static {
        REDIS_SCRIPT = new DefaultRedisScript<>();
        REDIS_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        REDIS_SCRIPT.setResultType(Long.class);
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
        stringRedisTemplate.execute(REDIS_SCRIPT, Collections.singletonList(KEY_PREFIX + name), value);
    }
}
