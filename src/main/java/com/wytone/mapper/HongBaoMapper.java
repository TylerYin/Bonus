package com.wytone.mapper;

import com.wytone.po.HongBao;

/**
 * MyBatis红包实体映射类
 *
 * @author Tyler
 */
public interface HongBaoMapper {
    /**
     * 插入红包记录
     *
     * @param record
     * @return
     */
    int insertSelective(HongBao record);

    /**
     * 更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(HongBao record);
}
