package com.soli.system.core.mapper;

import com.soli.common.api.vo.PageQuery;

import java.util.List;

/**
 * 实现基础 Crud 的 Mapper 接口
 *
 * @author lizhengqiang
 * @since 2026-03-19 21:44
 */
public interface BaseCrudMapper<E, Q extends PageQuery> {

    /**
     * 插入
     *
     * @param entity 实体
     * @return 影响的行数
     */
    @SuppressWarnings("all")
    int insert(E entity);

    /**
     * 根据 id 删除
     * @param id id
     * @return 影响的行数
     */
    @SuppressWarnings("all")
    int deleteById(Long id);

    /**
     * 更新
     *
     * @param entity 实体
     * @return 影响的行数
     */
    @SuppressWarnings("all")
    int update(E entity);

    /**
     * 分页查询
     *
     * @param query 查询参数
     * @return 分页信息
     */
    @SuppressWarnings("all")
    List<E> select(Q query);

    /**
     * 根据 id 查询
     * @param id id
     * @return entity 实体
     */
    @SuppressWarnings("all")
    E selectById(Long id);

}