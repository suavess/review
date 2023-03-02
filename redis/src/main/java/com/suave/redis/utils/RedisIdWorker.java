package com.suave.redis.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisIdWorker {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public static final long BEGIN_TIMESTAMP = 1677767031491L;

    public long nextId(String keyPrefix){
        long timeStamp = System.currentTimeMillis() - BEGIN_TIMESTAMP;
        Long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + DateUtil.today());
        return timeStamp << 32 | count;
    }
}
