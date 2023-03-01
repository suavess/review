package com.suave.redis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suave.redis.entity.Blog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Mapper
public interface BlogMapper extends BaseMapper<Blog> {

}