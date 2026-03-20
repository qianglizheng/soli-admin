package com.soli.system.core.service;

import java.util.List;

/**
 * 对象转换接口
 *
 * @author lizhengqiang
 * @since 2026-03-17 22:27
 */
public interface Converter<D, E> {

    D toDTO(E e);

    List<D> toDTOList(List<E> e);

    E toEntity(D d);

    List<E> toEntityList(List<D> d);

}