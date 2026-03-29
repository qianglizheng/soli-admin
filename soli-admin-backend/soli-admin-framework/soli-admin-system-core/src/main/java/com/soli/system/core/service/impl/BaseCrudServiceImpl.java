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
 * 具备基础增删改查能力的抽象服务实现
 *
 * @param <D> DTO 对象
 * @param <E> 实体对象
 * @param <Q> 分页查询对象
 * @author lizhengqiang
 * @since 2026-03-19 22:22
 */
public abstract class BaseCrudServiceImpl<D, E extends IdEntity, Q extends PageQuery> implements BaseCrudService<D, Q> {

    /**
     * DTO 与实体转换器
     */
    public final Converter<D, E> converter;

    /**
     * 基础 Mapper
     */
    public final BaseCrudMapper<E, Q> mapper;

    public BaseCrudServiceImpl(final BaseCrudMapper<E, Q> mapper, final Converter<D, E> converter) {
        this.mapper = mapper;
        this.converter = converter;
    }

    /**
     * 新增数据
     *
     * @param dto DTO 对象
     * @throws BusinessException 业务异常
     */
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

    /**
     * 删除数据
     *
     * @param id 主键 ID
     * @throws BusinessException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) throws BusinessException {
        beforeRemove(id);
        if (mapper.deleteById(id) == 0) {
            throw new BusinessException(moduleName() + "删除失败");
        }
        afterRemove(id);
    }

    /**
     * 修改数据
     *
     * @param dto DTO 对象
     * @throws BusinessException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void modify(D dto) throws BusinessException {
        beforeModify(dto);
        E entity = converter.toEntity(dto);
        if (mapper.update(entity) == 0) {
            throw new BusinessException(moduleName() + "更新失败");
        }
        afterModify(dto, entity);
    }

    /**
     * 分页查询
     *
     * @param query 分页查询参数
     * @return 分页结果
     */
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

    /**
     * 根据 ID 查询数据
     *
     * @param id 主键 ID
     * @return DTO 对象
     */
    public Optional<D> getById(Long id) {
        E entity = mapper.selectById(id);
        if (entity == null) {
            return Optional.empty();
        }
        D dto = converter.toDTO(entity);
        return Optional.of(dto);
    }

    /**
     * 返回当前模块名称
     *
     * @return 模块名称
     */
    protected abstract String moduleName();

    /**
     * 新增前置处理
     *
     * @param dto DTO 对象
     */
    protected void beforeCreate(D dto) {

    }

    /**
     * 新增后置处理
     *
     * @param dto DTO 对象
     * @param entity 实体对象
     */
    protected void afterCreate(D dto, E entity) {

    }

    /**
     * 删除前置处理
     *
     * @param id 主键 ID
     */
    protected void beforeRemove(Long id) {

    }

    /**
     * 删除后置处理
     *
     * @param id 主键 ID
     */
    protected void afterRemove(Long id) {

    }

    /**
     * 修改前置处理
     *
     * @param dto DTO 对象
     */
    protected void beforeModify(D dto) {

    }

    /**
     * 修改后置处理
     *
     * @param dto DTO 对象
     * @param entity 实体对象
     */
    protected void afterModify(D dto, E entity) {

    }
}
