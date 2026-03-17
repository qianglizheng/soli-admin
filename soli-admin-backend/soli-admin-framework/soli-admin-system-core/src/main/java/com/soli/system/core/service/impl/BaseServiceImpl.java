package com.soli.system.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.yitter.idgen.YitIdHelper;
import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.PageQuery;
import com.soli.common.api.vo.PageResult;
import com.soli.common.core.entity.IdEntity;
import com.soli.system.core.service.Converter;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public abstract class BaseServiceImpl<D, E extends IdEntity, Q extends PageQuery> {

    public final Converter<D, E> converter;

    public BaseServiceImpl(Converter<D, E> converter) {
        this.converter = converter;
    }

    @SuppressWarnings("resource")
    public PageResult<D> page(Q query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<E> entityList = selectByQuery(query);
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
        E entity = selectById(id);
        if (entity == null) {
            return Optional.empty();
        }
        D dto = converter.toDTO(entity);
        return Optional.of(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(D dto) throws BusinessException {
        E entity = converter.toEntity(dto);
        entity.setId(YitIdHelper.nextId());
        if (insert(entity) == 0) {
            throw new BusinessException(moduleName() + "创建失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void modify(D dto) throws BusinessException {
        E entity = converter.toEntity(dto);
        if (update(entity) == 0) {
            throw new BusinessException(moduleName() + "更新失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) throws BusinessException {
        if (deleteById(id) == 0) {
            throw new BusinessException(moduleName() + "删除失败");
        }
    }

    /**
     * 根据 id 查询数据
     *
     * @param id 主表 id
     * @return entity 对象
     * @throws BusinessException 业务异常
     */
    protected abstract E selectById(Long id) throws BusinessException;

    /**
     * 新增数据
     *
     * @param entity 实体对象
     * @return 返回 0 失败
     * @throws BusinessException 抛出具体的异常信息
     */
    protected abstract int insert(E entity) throws BusinessException;

    /**
     * 更新数据
     *
     * @param entity 实体对象
     * @return 0失败
     * @throws BusinessException 抛出具体的异常信息
     */
    protected abstract int update(E entity) throws BusinessException;

    /**
     * 根据 id 删除数据
     *
     * @param id 主表 id
     * @return 0失败
     * @throws BusinessException 抛出具体的异常信息
     */
    protected abstract int deleteById(Long id) throws BusinessException;

    /**
     * 分页查询
     *
     * @param query 查询数据
     * @return 分页数据
     * @throws BusinessException 抛出具体的异常信息
     */
    protected abstract List<E> selectByQuery(Q query);

    /**
     * 模块名称
     *
     * @return 模块名称
     */
    protected abstract String moduleName();

}