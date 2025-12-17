package com.atguigu.gmall.ums.mapper;

import com.atguigu.gmall.ums.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表
 * 
 * @author Guan FuQing
 * @email moumouguan@gmail.com
 * @date 2025-12-17 04:55:27
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
	
}
