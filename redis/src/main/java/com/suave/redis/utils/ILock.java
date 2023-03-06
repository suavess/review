package com.suave.redis.utils;

/**
 * @author Suave
 * @since 2023/03/06 13:38
 */
public interface ILock {

    boolean tryLock(long time);

    void unlock();
}
