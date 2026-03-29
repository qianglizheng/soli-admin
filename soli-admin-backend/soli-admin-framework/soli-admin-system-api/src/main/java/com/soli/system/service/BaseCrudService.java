package com.soli.system.service;

import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.PageQuery;
import com.soli.common.api.vo.PageResult;

import java.util.Optional;

/**
 * 基础增删改查服务接口
 *
 * @param <D> DTO 对象
 * @param <Q> 分页查询对象
 * @author lizhengqiang
 * @since 2026-03-19 22:23
 */
public interface BaseCrudService<D, Q extends PageQuery> {

    /**
     * 新增数据
     *
     * @param dto DTO 对象
     * @throws BusinessException 业务异常
     */
    void create(D dto) throws BusinessException;

    /**
     * 删除数据
     *
     * @param id 主键 ID
     * @throws BusinessException 业务异常
     */
    void remove(Long id) throws BusinessException;

    /**
     * 修改数据
     *
     * @param dto DTO 对象
     * @throws BusinessException 业务异常
     */
    void modify(D dto) throws BusinessException;

    /**
     * 分页查询
     *
     * @param query 分页查询参数
     * @return 分页结果
     */
    PageResult<D> page(Q query);

    /**
     * 根据 ID 查询单条数据
     *
     * @param id 主键 ID
     * @return DTO 对象
     */
    Optional<D> getById(Long id);

}
