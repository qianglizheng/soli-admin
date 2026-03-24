package com.soli.system.core.mapper;

import com.soli.system.core.service.impl.sysorgpost.SysOrgPostEntity;
import com.soli.system.core.service.impl.sysorgpost.SysOrgPostDetailModel;
import com.soli.system.core.service.impl.sysorgpost.SysOrgPostTreeNodeModel;
import com.soli.system.core.service.impl.sysorgpost.SysOrgPostUserModel;
import com.soli.system.core.service.impl.sysorgpost.SysOrgUnitEntity;
import com.soli.system.core.service.impl.sysorgpost.SysUserOrgPostEntity;
import com.soli.system.service.sysorgpost.SysOrgPostQuery;
import com.soli.system.service.sysorgpost.SysOrgPostUserQuery;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 岗位管理持久层
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
public interface SysOrgPostMapper extends BaseCrudMapper<SysOrgPostEntity, SysOrgPostQuery> {

    /**
     * 查询组织树节点
     *
     * @return 组织树节点列表
     */
    List<SysOrgPostTreeNodeModel> selectOrgUnitTreeNodes();

    /**
     * 查询岗位树节点
     *
     * @return 岗位树节点列表
     */
    List<SysOrgPostTreeNodeModel> selectPostTreeNodes();

    /**
     * 根据岗位 ID 查询岗位详情
     *
     * @param id 岗位 ID
     * @return 岗位详情
     */
    SysOrgPostDetailModel selectDetailById(@Param("id") Long id);

    /**
     * 根据组织和岗位编码查询岗位
     *
     * @param orgUnitId 组织 ID
     * @param postCode 岗位编码
     * @return 岗位实体
     */
    SysOrgPostEntity selectByOrgUnitIdAndPostCode(@Param("orgUnitId") Long orgUnitId, @Param("postCode") String postCode);

    /**
     * 统计组织数量
     *
     * @param orgUnitId 组织 ID
     * @return 组织数量
     */
    int countOrgUnitById(@Param("orgUnitId") Long orgUnitId);

    SysOrgUnitEntity selectOrgUnitById(@Param("id") Long id);

    SysOrgUnitEntity selectOrgUnitByCode(@Param("orgCode") String orgCode);

    int insertOrgUnit(SysOrgUnitEntity entity);

    /**
     * 统计用户数量
     *
     * @param userId 用户 ID
     * @return 用户数量
     */
    int countUserById(@Param("userId") Long userId);

    /**
     * 批量统计用户数量
     *
     * @param userIds 用户 ID 列表
     * @return 用户数量
     */
    int countUsersByIds(@Param("userIds") List<Long> userIds);

    /**
     * 统计下级岗位数量
     *
     * @param parentPostId 父岗位 ID
     * @return 下级岗位数量
     */
    int countChildPostByParentId(@Param("parentPostId") Long parentPostId);

    /**
     * 统计岗位员工关系数量
     *
     * @param orgPostId 岗位 ID
     * @return 员工关系数量
     */
    int countUserRelationByOrgPostId(@Param("orgPostId") Long orgPostId);

    /**
     * 更新后代岗位祖级路径
     *
     * @param oldPath 原路径
     * @param newPath 新路径
     * @param updateTime 更新时间
     * @return 影响行数
     */
    int updateDescendantAncestors(@Param("oldPath") String oldPath, @Param("newPath") String newPath, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 分页查询岗位员工
     *
     * @param query 查询对象
     * @return 岗位员工列表
     */
    List<SysOrgPostUserModel> selectUserPage(SysOrgPostUserQuery query);

    /**
     * 分页查询可绑定用户
     *
     * @param query 查询对象
     * @return 可绑定用户列表
     */
    List<SysOrgPostUserModel> selectUserOptionPage(SysOrgPostUserQuery query);

    /**
     * 查询已存在的岗位员工关系用户 ID
     *
     * @param orgPostId 岗位 ID
     * @param userIds 用户 ID 列表
     * @return 已存在关系的用户 ID 列表
     */
    List<Long> selectExistingRelationUserIds(@Param("orgPostId") Long orgPostId, @Param("userIds") List<Long> userIds);

    /**
     * 批量新增岗位员工关系
     *
     * @param relationList 关系列表
     * @return 影响行数
     */
    int insertUserRelations(@Param("relationList") List<SysUserOrgPostEntity> relationList);

    /**
     * 批量删除岗位员工关系
     *
     * @param orgPostId 岗位 ID
     * @param userIds 用户 ID 列表
     * @return 影响行数
     */
    int deleteUserRelations(@Param("orgPostId") Long orgPostId, @Param("userIds") List<Long> userIds);

}
