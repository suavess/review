package com.suave.redis;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.suave.redis.utils.RedisIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootTest
class RedisApplicationTests {
    @Autowired
    private RedisIdWorker redisIdWorker;

    private ExecutorService es = Executors.newFixedThreadPool(500);

    @Test
    void contextLoads() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(300);
        TimeInterval timeInterval = new TimeInterval();
        for (int i = 0; i < 300; i++) {
            es.submit(() -> {
                for (int j = 0; j < 100; j++) {
                    log.info("id = {}", redisIdWorker.nextId("order"));
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        log.info("耗时:{}", timeInterval.interval());
    }

}
