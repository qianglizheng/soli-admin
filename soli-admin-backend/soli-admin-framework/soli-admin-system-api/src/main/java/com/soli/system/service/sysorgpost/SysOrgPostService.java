package com.soli.system.service.sysorgpost;

import com.soli.common.api.vo.PageResult;
import com.soli.system.service.BaseCrudService;

import java.util.List;

/**
 * 岗位管理服务接口
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
public interface SysOrgPostService extends BaseCrudService<SysOrgPostDTO, SysOrgPostQuery> {

    /**
     * 查询组织岗位树
     *
     * @return 组织岗位树
     */
    List<SysOrgPostTreeNodeDTO> queryTreeList();

    /**
     * 根据岗位 ID 查询岗位详情
     *
     * @param id 岗位 ID
     * @return 岗位详情
     */
    SysOrgPostDetailDTO queryDetailById(Long id);

    /**
     * 新增组织单元
     *
     * @param dto 组织单元对象
     */
    void createOrgUnit(SysOrgUnitDTO dto);

    /**
     * 根据组织单元 ID 查询组织详情
     *
     * @param id 组织单元 ID
     * @return 组织单元详情
     */
    SysOrgUnitDTO queryOrgUnitById(Long id);

    /**
     * 修改组织单元
     *
     * @param dto 组织单元对象
     */
    void modifyOrgUnit(SysOrgUnitDTO dto);

    /**
     * 删除组织单元
     *
     * @param id 组织单元 ID
     */
    void removeOrgUnit(Long id);

    /**
     * 分页查询岗位员工
     *
     * @param query 查询对象
     * @return 岗位员工分页结果
     */
    PageResult<SysOrgPostUserDTO> queryUserPage(SysOrgPostUserQuery query);

    /**
     * 分页查询可绑定员工
     *
     * @param query 查询对象
     * @return 可绑定员工分页结果
     */
    PageResult<SysOrgPostUserDTO> queryUserOptionPage(SysOrgPostUserQuery query);

    /**
     * 批量绑定岗位员工
     *
     * @param orgPostId 岗位 ID
     * @param userIds 用户 ID 列表
     */
    void bindUsers(Long orgPostId, List<Long> userIds);

    /**
     * 批量解绑岗位员工
     *
     * @param orgPostId 岗位 ID
     * @param userIds 用户 ID 列表
     */
    void unbindUsers(Long orgPostId, List<Long> userIds);

}
