package com.soli.system.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.yitter.idgen.YitIdHelper;
import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.PageQuery;
import com.soli.common.api.vo.PageResult;
import com.soli.common.core.entity.IdEntity;
import com.soli.system.core.mapper.BaseCrudMapper;
import com.soli.system.core.service.Converter;
import com.soli.system.service.BaseCrudService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 具有基础增删改查能力的抽象类
 *
 * @param <D> DTO 对象
 * @param <E> Entity 对象
 * @param <Q> PageQuery 对象
 * @author lizhengqiang
 * @since 2026-03-19 22:22
 */
public abstract class BaseCrudServiceImpl<D, E extends IdEntity, Q extends PageQuery> implements BaseCrudService<D, Q> {

    public final Converter<D, E> converter;

    public final BaseCrudMapper<E, Q> mapper;

    public BaseCrudServiceImpl(final BaseCrudMapper<E, Q> mapper, final Converter<D, E> converter) {
        this.mapper = mapper;
        this.converter = converter;
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(D dto) throws BusinessException {
        beforeCreate(dto);
        E entity = converter.toEntity(dto);
        entity.setId(YitIdHelper.nextId());
        if (mapper.insert(entity) == 0) {
            throw new BusinessException(moduleName() + "创建失败");
        }
        afterCreate(dto, entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) throws BusinessException {
        beforeRemove(id);
        if (mapper.deleteById(id) == 0) {
            throw new BusinessException(moduleName() + "删除失败");
        }
        afterRemove(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void modify(D dto) throws BusinessException {
        beforeModify(dto);
        E entity = converter.toEntity(dto);
        if (mapper.update(entity) == 0) {
            throw new BusinessException(moduleName() + "更新失败");
        }
        afterModify(dto, entity);
    }

    @SuppressWarnings("resource")
    public PageResult<D> page(Q query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<E> entityList = mapper.select(query);
        List<D> dtoList = converter.toDTOList(entityList);
        PageInfo<E> pageInfo = new PageInfo<>(entityList);
        return PageResult.of(
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getTotal(),
                dtoList
        );
    }

    public Optional<D> getById(Long id) {
        E entity = mapper.selectById(id);
        if (entity == null) {
            return Optional.empty();
        }
        D dto = converter.toDTO(entity);
        return Optional.of(dto);
    }

    /**
     * 模块名称
     *
     * @return 模块名称
     */
    protected abstract String moduleName();

    /**
     * 创建之前的操作，留给子类自行判断是否实现
     *
     * @param dto DTO 对象
     */
    protected void beforeCreate(D dto) {

    }

    /**
     * 创建成功之后的操作，留给子类自行判断是否实现
     *
     * @param dto DTO 对象
     * @param entity Entity 对象
     */
    protected void afterCreate(D dto, E entity) {

    }

    /**
     * 删除之前的操作，留给子类自行判断是否实现
     *
     * @param id ID
     */
    protected void beforeRemove(Long id) {

    }

    /**
     * 删除成功之后的操作，留给子类自行判断是否实现
     *
     * @param id ID
     */
    protected void afterRemove(Long id) {

    }

    /**
     * 修改之前的操作，留给子类自行判断是否实现
     *
     * @param dto DTO 对象
     */
    protected void beforeModify(D dto) {

    }

    /**
     * 修改成功之后的操作，留给子类自行判断是否实现
     *
     * @param dto DTO 对象
     * @param entity Entity 对象
     */
    protected void afterModify(D dto, E entity) {

    }
}