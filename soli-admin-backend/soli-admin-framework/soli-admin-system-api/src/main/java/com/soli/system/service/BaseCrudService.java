package com.soli.system.service;

import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.PageQuery;
import com.soli.common.api.vo.PageResult;

import java.util.Optional;

/**
 * @author lizhengqiang
 * @since 2026-03-19 22:05
 */
public interface BaseCrudService<D, Q extends PageQuery> {

    /**
     * 创建
     *
     * @param dto dto
     * @throws BusinessException 异常
     */
    void create(D dto) throws BusinessException;

    /**
     * 删除角色
     *
     * @param id Id
     * @throws BusinessException 异常
     */
    void remove(Long id) throws BusinessException;

    /**
     * 修改
     *
     * @param dto dto
     * @throws BusinessException 异常
     */
    void modify(D dto) throws BusinessException;

    /**
     * 分页查询
     *
     * @param query 查询参数
     * @return 分页信息
     */
    PageResult<D> page(Q query);

    /**
     * 根据角色 Id 获取
     *
     * @param id Id
     * @return dto
     */
    Optional<D> getById(Long id);

}