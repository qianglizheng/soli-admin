package com.soli.system.service.sysrole;

import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.PageResult;

import java.util.List;
import java.util.Optional;

/**
 * 系统角色服务
 *
 * @author lizhengqiang
 * @since 2026-03-14 15:32
*/
public interface SysRoleService {

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<SysRoleDTO> queryAll();

    /**
     * 根据系统用户 Id 查询角色
     *
     * @param userId 系统用户 Id
     * @return 角色
     */
    List<SysRoleDTO> getByUserId(Long userId);

    /**
     * 创建角色
     *
     * @param dto 角色信息
     * @throws BusinessException 异常
     */
    void create(SysRoleDTO dto) throws BusinessException;

    /**
     * 根据角色 Id 获取角色信息
     *
     * @param id 角色 Id
     * @return 角色信息
     */
    Optional<SysRoleDTO> getById(Long id);

    /**
     * 修改角色
     *
     * @param dto 角色信息
     * @throws BusinessException 异常
     */
    void modify(SysRoleDTO dto) throws BusinessException;

    /**
     * 删除角色
     *
     * @param id 角色 Id
     * @throws BusinessException 异常
     */
    void remove(Long id) throws BusinessException;

    /**
     * 分页查询角色
     *
     * @param query 查询参数
     * @return 分页角色信息
     */
    PageResult<SysRoleDTO> page(SysRoleQuery query);
}