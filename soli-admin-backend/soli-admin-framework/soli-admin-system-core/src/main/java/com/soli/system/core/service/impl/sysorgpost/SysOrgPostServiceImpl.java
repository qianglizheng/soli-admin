package com.soli.system.core.service.impl.sysorgpost;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.yitter.idgen.YitIdHelper;
import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.PageResult;
import com.soli.system.core.mapper.SysOrgPostMapper;
import com.soli.system.core.service.impl.BaseCrudServiceImpl;
import com.soli.system.service.sysorgpost.SysOrgCompanyDTO;
import com.soli.system.service.sysorgpost.SysOrgPostDTO;
import com.soli.system.service.sysorgpost.SysOrgPostDetailDTO;
import com.soli.system.service.sysorgpost.SysOrgPostQuery;
import com.soli.system.service.sysorgpost.SysOrgPostService;
import com.soli.system.service.sysorgpost.SysOrgPostTreeNodeDTO;
import com.soli.system.service.sysorgpost.SysOrgPostUserDTO;
import com.soli.system.service.sysorgpost.SysOrgPostUserQuery;
import com.soli.system.service.sysorgpost.SysOrgUnitDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 岗位管理服务实现
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
@Service
public class SysOrgPostServiceImpl extends BaseCrudServiceImpl<SysOrgPostDTO, SysOrgPostEntity, SysOrgPostQuery>
        implements SysOrgPostService {

    private static final String ORG_TYPE_GROUP = "GROUP";

    private static final String ORG_TYPE_HEADQUARTERS = "HEADQUARTERS";

    private static final String ORG_TYPE_BRANCH = "BRANCH";

    private static final int DEFAULT_PAGE_NUM = 1;

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int MAX_PAGE_SIZE = 100;

    private final SysOrgPostMapper sysOrgPostMapper;

    private final SysOrgPostConverter sysOrgPostConverter;

    private final SysOrgUnitConverter sysOrgUnitConverter;

    public SysOrgPostServiceImpl(final SysOrgPostMapper mapper,
                                 final SysOrgPostConverter converter,
                                 final SysOrgUnitConverter sysOrgUnitConverter) {
        super(mapper, converter);
        this.sysOrgPostMapper = mapper;
        this.sysOrgPostConverter = converter;
        this.sysOrgUnitConverter = sysOrgUnitConverter;
    }

    @Override
    public List<SysOrgPostTreeNodeDTO> queryTreeList() {
        List<SysOrgPostTreeNodeDTO> orgNodes = sysOrgPostConverter.toTreeNodeDTOList(sysOrgPostMapper.selectOrgUnitTreeNodes());
        List<SysOrgPostTreeNodeDTO> postNodes = sysOrgPostConverter.toTreeNodeDTOList(sysOrgPostMapper.selectPostTreeNodes());
        Map<String, SysOrgPostTreeNodeDTO> nodeMap = new LinkedHashMap<>();
        List<SysOrgPostTreeNodeDTO> rootList = new ArrayList<>();

        orgNodes.forEach(node -> {
            node.setChildren(new ArrayList<>());
            nodeMap.put(node.getNodeKey(), node);
        });
        orgNodes.forEach(node -> appendNode(rootList, nodeMap, node));

        postNodes.forEach(node -> {
            node.setChildren(new ArrayList<>());
            nodeMap.put(node.getNodeKey(), node);
        });
        postNodes.forEach(node -> appendNode(rootList, nodeMap, node));

        sortTree(rootList);
        return flattenGroupNodes(rootList);
    }

    @Override
    public List<SysOrgCompanyDTO> queryUserCompanyList(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return sysOrgPostMapper.selectCompanyListByUserId(userId).stream()
                .map(this::toCompanyDTO)
                .toList();
    }

    @Override
    public SysOrgCompanyDTO queryUserCompany(Long userId, Long companyId) {
        if (companyId == null) {
            throw new BusinessException("公司 ID 不能为空");
        }
        return queryUserCompanyList(userId).stream()
                .filter(company -> Objects.equals(company.getId(), companyId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("当前用户无权进入该公司"));
    }

    @Override
    public SysOrgPostDetailDTO queryDetailById(Long id) {
        SysOrgPostDetailDTO detail = sysOrgPostConverter.toDetailDTO(sysOrgPostMapper.selectDetailById(id));
        if (detail == null) {
            throw new BusinessException("岗位不存在");
        }
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrgUnit(SysOrgUnitDTO dto) {
        normalizeOrgUnit(dto);
        SysOrgUnitEntity parentOrgUnit = resolveParentOrgUnit(dto.getParentId());
        validateHeadquarterParent(parentOrgUnit);
        validateLeaderUser(dto.getLeaderUserId());
        validateOrgUnitCodeUnique(dto.getOrgCode(), null);
        dto.setOrgType(ORG_TYPE_BRANCH);
        dto.setAncestors(resolveOrgUnitAncestors(parentOrgUnit));
        dto.setCreateTime(LocalDateTime.now());

        SysOrgUnitEntity entity = sysOrgUnitConverter.toEntity(dto);
        entity.setId(YitIdHelper.nextId());
        if (sysOrgPostMapper.insertOrgUnit(entity) == 0) {
            throw new BusinessException("组织单元创建失败");
        }
        dto.setId(entity.getId());
    }

    @Override
    public SysOrgUnitDTO queryOrgUnitById(Long id) {
        return sysOrgUnitConverter.toDTO(requireOrgUnit(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyOrgUnit(SysOrgUnitDTO dto) {
        normalizeOrgUnit(dto);
        SysOrgUnitEntity currentEntity = requireOrgUnit(dto.getId());
        validateBranchOrgUnit(currentEntity);
        SysOrgUnitEntity parentOrgUnit = resolveParentOrgUnit(dto.getParentId());
        validateHeadquarterParent(parentOrgUnit);
        if (!Objects.equals(currentEntity.getParentId(), dto.getParentId())) {
            throw new BusinessException("不支持调整分公司所属层级");
        }
        validateLeaderUser(dto.getLeaderUserId());
        validateOrgUnitCodeUnique(dto.getOrgCode(), dto.getId());
        dto.setOrgType(currentEntity.getOrgType());
        dto.setAncestors(currentEntity.getAncestors());
        dto.setUpdateTime(LocalDateTime.now());
        if (sysOrgPostMapper.updateOrgUnit(sysOrgUnitConverter.toEntity(dto)) == 0) {
            throw new BusinessException("组织单元更新失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeOrgUnit(Long id) {
        SysOrgUnitEntity entity = requireOrgUnit(id);
        validateBranchOrgUnit(entity);
        if (sysOrgPostMapper.countChildOrgUnitByParentId(id) > 0) {
            throw new BusinessException("当前分公司存在下级组织，不能删除");
        }
        if (sysOrgPostMapper.countPostByOrgUnitId(id) > 0) {
            throw new BusinessException("当前分公司下已存在岗位，不能删除");
        }
        if (sysOrgPostMapper.deleteOrgUnitById(id) == 0) {
            throw new BusinessException("组织单元删除失败");
        }
    }

    @Override
    public PageResult<SysOrgPostUserDTO> queryUserPage(SysOrgPostUserQuery query) {
        validateUserPageQuery(query);
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<SysOrgPostUserModel> modelList = sysOrgPostMapper.selectUserPage(query);
        List<SysOrgPostUserDTO> dtoList = sysOrgPostConverter.toUserDTOList(modelList);
        PageInfo<SysOrgPostUserModel> pageInfo = new PageInfo<>(modelList);
        return PageResult.of(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), dtoList);
    }

    @Override
    public PageResult<SysOrgPostUserDTO> queryUserOptionPage(SysOrgPostUserQuery query) {
        validateUserPageQuery(query);
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<SysOrgPostUserModel> modelList = sysOrgPostMapper.selectUserOptionPage(query);
        List<SysOrgPostUserDTO> dtoList = sysOrgPostConverter.toUserDTOList(modelList);
        PageInfo<SysOrgPostUserModel> pageInfo = new PageInfo<>(modelList);
        return PageResult.of(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), dtoList);
    }

    @Override
    public void bindUsers(Long orgPostId, List<Long> userIds) {
        validatePostExists(orgPostId);
        List<Long> distinctUserIds = distinctValidIds(userIds);
        if (distinctUserIds.isEmpty()) {
            return;
        }
        if (sysOrgPostMapper.countUsersByIds(distinctUserIds) != distinctUserIds.size()) {
            throw new BusinessException("绑定用户存在无效数据");
        }
        Set<Long> existingUserIdSet = new HashSet<>(sysOrgPostMapper.selectExistingRelationUserIds(orgPostId, distinctUserIds));
        List<SysUserOrgPostEntity> relationList = distinctUserIds.stream()
                .filter(userId -> !existingUserIdSet.contains(userId))
                .map(userId -> buildRelationEntity(orgPostId, userId))
                .toList();
        if (relationList.isEmpty()) {
            return;
        }
        sysOrgPostMapper.insertUserRelations(relationList);
    }

    @Override
    public void unbindUsers(Long orgPostId, List<Long> userIds) {
        validatePostExists(orgPostId);
        List<Long> distinctUserIds = distinctValidIds(userIds);
        if (distinctUserIds.isEmpty()) {
            return;
        }
        sysOrgPostMapper.deleteUserRelations(orgPostId, distinctUserIds);
    }

    @Override
    protected String moduleName() {
        return "岗位管理";
    }

    @Override
    protected void beforeCreate(SysOrgPostDTO dto) {
        normalizePost(dto);
        validateOrgUnitExists(dto.getOrgUnitId());
        validateManagerUser(dto.getManagerUserId());
        SysOrgPostEntity parentPost = resolveParentPost(dto.getParentPostId());
        if (parentPost != null) {
            dto.setOrgUnitId(parentPost.getOrgUnitId());
        }
        validatePostCodeUnique(dto.getOrgUnitId(), dto.getPostCode(), null);
        dto.setAncestors(resolveAncestors(parentPost));
        dto.setCreateTime(LocalDateTime.now());
    }

    @Override
    protected void afterCreate(SysOrgPostDTO dto, SysOrgPostEntity entity) {
        dto.setId(entity.getId());
    }

    @Override
    protected void beforeModify(SysOrgPostDTO dto) {
        normalizePost(dto);
        SysOrgPostEntity currentEntity = sysOrgPostMapper.selectById(dto.getId());
        if (currentEntity == null) {
            throw new BusinessException("岗位不存在");
        }
        validateManagerUser(dto.getManagerUserId());
        SysOrgPostEntity parentPost = resolveParentPost(dto.getParentPostId());
        if (parentPost != null) {
            if (Objects.equals(parentPost.getId(), dto.getId()) || containsId(parentPost.getAncestors(), dto.getId())) {
                throw new BusinessException("上级岗位不能选择当前岗位或其下级岗位");
            }
            if (!Objects.equals(currentEntity.getOrgUnitId(), parentPost.getOrgUnitId())) {
                throw new BusinessException("不支持跨组织移动岗位");
            }
            dto.setOrgUnitId(parentPost.getOrgUnitId());
        }
        if (!Objects.equals(currentEntity.getOrgUnitId(), dto.getOrgUnitId())) {
            throw new BusinessException("不支持跨组织移动岗位");
        }
        validateOrgUnitExists(dto.getOrgUnitId());
        validatePostCodeUnique(dto.getOrgUnitId(), dto.getPostCode(), dto.getId());

        String newAncestors = resolveAncestors(parentPost);
        dto.setAncestors(newAncestors);
        dto.setUpdateTime(LocalDateTime.now());

        String oldPath = buildSelfPath(currentEntity.getAncestors(), currentEntity.getId());
        String newPath = buildSelfPath(newAncestors, currentEntity.getId());
        if (!Objects.equals(oldPath, newPath)) {
            sysOrgPostMapper.updateDescendantAncestors(oldPath, newPath, LocalDateTime.now());
        }
    }

    @Override
    protected void beforeRemove(Long id) {
        validatePostExists(id);
        if (sysOrgPostMapper.countChildPostByParentId(id) > 0) {
            throw new BusinessException("当前岗位仍存在下级岗位，不能删除");
        }
        if (sysOrgPostMapper.countUserRelationByOrgPostId(id) > 0) {
            throw new BusinessException("当前岗位已绑定员工，不能删除");
        }
    }

    private void validateUserPageQuery(SysOrgPostUserQuery query) {
        if (query == null || query.getOrgPostId() == null) {
            throw new BusinessException("岗位ID不能为空");
        }
        validatePostExists(query.getOrgPostId());
        normalizePageQuery(query);
    }

    private void normalizePageQuery(SysOrgPostUserQuery query) {
        if (query.getPageNum() == null || query.getPageNum() <= 0) {
            query.setPageNum(DEFAULT_PAGE_NUM);
        }
        if (query.getPageSize() == null || query.getPageSize() <= 0) {
            query.setPageSize(DEFAULT_PAGE_SIZE);
        }
        if (query.getPageSize() > MAX_PAGE_SIZE) {
            query.setPageSize(MAX_PAGE_SIZE);
        }
    }

    private SysOrgCompanyDTO toCompanyDTO(SysOrgCompanyModel model) {
        SysOrgCompanyDTO dto = new SysOrgCompanyDTO();
        dto.setId(model.getId());
        dto.setNodeKey(model.getNodeKey());
        dto.setNodeCode(model.getNodeCode());
        dto.setNodeName(model.getNodeName());
        dto.setNodeType(model.getNodeType());
        dto.setSort(model.getSort());
        return dto;
    }

    private void normalizePost(SysOrgPostDTO dto) {
        if (dto.getSort() != null && dto.getSort() <= 0) {
            dto.setSort(1);
        }
    }

    private void normalizeOrgUnit(SysOrgUnitDTO dto) {
        if (dto.getSort() != null && dto.getSort() <= 0) {
            dto.setSort(1);
        }
    }

    private void validateHeadquarterParent(SysOrgUnitEntity parentOrgUnit) {
        if (!ORG_TYPE_HEADQUARTERS.equals(parentOrgUnit.getOrgType())) {
            throw new BusinessException("分公司只能挂在总公司下");
        }
    }

    private void validateOrgUnitExists(Long orgUnitId) {
        if (orgUnitId == null || sysOrgPostMapper.countOrgUnitById(orgUnitId) == 0) {
            throw new BusinessException("所属组织不存在");
        }
    }

    private void validateManagerUser(Long managerUserId) {
        if (managerUserId == null) {
            return;
        }
        if (sysOrgPostMapper.countUserById(managerUserId) == 0) {
            throw new BusinessException("岗位负责人不存在");
        }
    }

    private void validateLeaderUser(Long leaderUserId) {
        if (leaderUserId == null) {
            return;
        }
        if (sysOrgPostMapper.countUserById(leaderUserId) == 0) {
            throw new BusinessException("组织负责人不存在");
        }
    }

    private void validateOrgUnitCodeUnique(String orgCode, Long currentId) {
        SysOrgUnitEntity entity = sysOrgPostMapper.selectOrgUnitByCode(orgCode);
        if (entity == null) {
            return;
        }
        if (currentId != null && Objects.equals(entity.getId(), currentId)) {
            return;
        }
        throw new BusinessException("组织编码已存在");
    }

    private void validatePostCodeUnique(Long orgUnitId, String postCode, Long currentId) {
        SysOrgPostEntity entity = sysOrgPostMapper.selectByOrgUnitIdAndPostCode(orgUnitId, postCode);
        if (entity == null) {
            return;
        }
        if (currentId != null && Objects.equals(entity.getId(), currentId)) {
            return;
        }
        throw new BusinessException("同一组织下岗位编码已存在");
    }

    private SysOrgUnitEntity requireOrgUnit(Long orgUnitId) {
        SysOrgUnitEntity entity = sysOrgPostMapper.selectOrgUnitById(orgUnitId);
        if (entity == null) {
            throw new BusinessException("组织单元不存在");
        }
        return entity;
    }

    private void validateBranchOrgUnit(SysOrgUnitEntity orgUnitEntity) {
        if (!ORG_TYPE_BRANCH.equals(orgUnitEntity.getOrgType())) {
            throw new BusinessException("当前仅支持维护分公司节点");
        }
    }

    private void validatePostExists(Long orgPostId) {
        if (orgPostId == null || sysOrgPostMapper.selectById(orgPostId) == null) {
            throw new BusinessException("岗位不存在");
        }
    }

    private SysOrgPostEntity resolveParentPost(Long parentPostId) {
        if (parentPostId == null || parentPostId <= 0) {
            return null;
        }
        SysOrgPostEntity parentPost = sysOrgPostMapper.selectById(parentPostId);
        if (parentPost == null) {
            throw new BusinessException("上级岗位不存在");
        }
        return parentPost;
    }

    private SysOrgUnitEntity resolveParentOrgUnit(Long parentId) {
        if (parentId == null || parentId <= 0) {
            throw new BusinessException("上级组织不存在");
        }
        SysOrgUnitEntity parentOrgUnit = sysOrgPostMapper.selectOrgUnitById(parentId);
        if (parentOrgUnit == null) {
            throw new BusinessException("上级组织不存在");
        }
        return parentOrgUnit;
    }

    private String resolveAncestors(SysOrgPostEntity parentPost) {
        if (parentPost == null) {
            return "0";
        }
        return buildSelfPath(parentPost.getAncestors(), parentPost.getId());
    }

    private String resolveOrgUnitAncestors(SysOrgUnitEntity parentOrgUnit) {
        return buildSelfPath(parentOrgUnit.getAncestors(), parentOrgUnit.getId());
    }

    private String buildSelfPath(String ancestors, Long id) {
        if (!StringUtils.hasText(ancestors)) {
            return String.valueOf(id);
        }
        return ancestors + "," + id;
    }

    private boolean containsId(String ancestors, Long id) {
        if (!StringUtils.hasText(ancestors) || id == null) {
            return false;
        }
        return ("," + ancestors + ",").contains("," + id + ",");
    }

    private List<Long> distinctValidIds(List<Long> ids) {
        if (ids == null) {
            return List.of();
        }
        return ids.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private SysUserOrgPostEntity buildRelationEntity(Long orgPostId, Long userId) {
        SysUserOrgPostEntity entity = new SysUserOrgPostEntity();
        entity.setId(YitIdHelper.nextId());
        entity.setOrgPostId(orgPostId);
        entity.setUserId(userId);
        entity.setPrimaryFlag("N");
        entity.setStatus("0");
        entity.setCreateTime(LocalDateTime.now());
        return entity;
    }

    private void appendNode(List<SysOrgPostTreeNodeDTO> rootList, Map<String, SysOrgPostTreeNodeDTO> nodeMap, SysOrgPostTreeNodeDTO currentNode) {
        if (!StringUtils.hasText(currentNode.getParentNodeKey())) {
            rootList.add(currentNode);
            return;
        }
        SysOrgPostTreeNodeDTO parentNode = nodeMap.get(currentNode.getParentNodeKey());
        if (parentNode == null) {
            rootList.add(currentNode);
            return;
        }
        parentNode.getChildren().add(currentNode);
    }

    private List<SysOrgPostTreeNodeDTO> flattenGroupNodes(List<SysOrgPostTreeNodeDTO> nodes) {
        List<SysOrgPostTreeNodeDTO> result = new ArrayList<>();
        nodes.forEach(node -> {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                node.setChildren(flattenGroupNodes(node.getChildren()));
            }
            if (ORG_TYPE_GROUP.equals(node.getNodeType())) {
                if (node.getChildren() != null) {
                    result.addAll(node.getChildren());
                }
                return;
            }
            result.add(node);
        });
        return result;
    }

    private void sortTree(List<SysOrgPostTreeNodeDTO> nodes) {
        nodes.sort(Comparator
                .comparing(SysOrgPostTreeNodeDTO::getSort, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(SysOrgPostTreeNodeDTO::getNodeKey));
        nodes.forEach(node -> {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                sortTree(node.getChildren());
            }
        });
    }

}
