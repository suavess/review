package com.suave.redis.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.suave.redis.dto.Result;
import com.suave.redis.entity.SeckillVoucher;
import com.suave.redis.entity.Voucher;
import com.suave.redis.entity.VoucherOrder;
import com.suave.redis.mapper.VoucherOrderMapper;
import com.suave.redis.service.ISeckillVoucherService;
import com.suave.redis.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suave.redis.utils.RedisIdWorker;
import com.suave.redis.utils.SimpleRedisLock;
import com.suave.redis.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.server.ExportException;
import java.time.LocalDateTime;

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
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Autowired
    private ISeckillVoucherService seckillVoucherService;

    @Autowired
    private RedisIdWorker redisIdWorker;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result seckillVoucher(Long voucherId) {
        SeckillVoucher voucher = seckillVoucherService.getById(voucherId);
        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
            return Result.fail("秒杀尚未开始");
        }
        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
            return Result.fail("秒杀已经结束");
        }
        if (voucher.getStock() < 1) {
            return Result.fail("库存不足");
        }
        Long userId = UserHolder.getUser().getId();
        SimpleRedisLock simpleRedisLock = new SimpleRedisLock("voucherOrder" + userId, stringRedisTemplate);
        if (!simpleRedisLock.tryLock(1200)) {
            seckillVoucher(voucherId);
        }
        try {
            return SpringUtil.getBean(VoucherOrderServiceImpl.class).createVoucherOder(voucherId);
        } finally {
            simpleRedisLock.unlock();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result createVoucherOder(Long voucherId) {
        // 一人一单
        Long userId = UserHolder.getUser().getId();
        Long count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        if (count > 0) {
            return Result.fail("该用户已经购买过了");
        }
        boolean success = seckillVoucherService.update().setSql("stock = stock - 1").eq("voucher_id", voucherId).gt("stock", 0).update();
        if (!success) {
            return Result.fail("库存不足");
        }
        VoucherOrder voucherOrder = new VoucherOrder();
        long orderId = redisIdWorker.nextId("order");
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);
        save(voucherOrder);
        return Result.ok(orderId);
    }
}
