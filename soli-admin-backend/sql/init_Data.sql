-- 角色初始化
delete from sys_role;
insert into sys_role (id, name, code, sort, data_scope, status, create_by, create_time, update_by, update_time, note) values (1, '超级管理员', 'admin', 1, '1', '0', 'system', now(), null, null, null);

-- 用户初始化
delete from sys_user;
insert into sys_user (id, username, password, nickname, email, phone, avatar, type, sex, login_ip, login_time, status, create_by, create_time, update_by, update_time, note) values (1, 'admin', '123456', '超级管理员', 'superadmin@test.com', '13800000001', null, '0', '0', null, null, '0', 'system', now(), null, null, null);
insert into sys_user (id, username, password, nickname, email, phone, avatar, type, sex, login_ip, login_time, status, create_by, create_time, update_by, update_time, note) values (2, 'zhangsan', '123456', '张三', 'zhangsan@test.com', '13800000002', null, '1', '0', null, null, '0', 'system', now(), null, null, '采购岗位示例用户');
insert into sys_user (id, username, password, nickname, email, phone, avatar, type, sex, login_ip, login_time, status, create_by, create_time, update_by, update_time, note) values (3, 'lisi', '123456', '李四', 'lisi@test.com', '13800000003', null, '1', '0', null, null, '0', 'system', now(), null, null, '销售岗位示例用户');
insert into sys_user (id, username, password, nickname, email, phone, avatar, type, sex, login_ip, login_time, status, create_by, create_time, update_by, update_time, note) values (4, 'wangwu', '123456', '王五', 'wangwu@test.com', '13800000004', null, '1', '0', null, null, '0', 'system', now(), null, null, '未分配岗位示例用户');

-- 用户角色关联
delete from sys_user_role;
insert into sys_user_role (user_id, role_id) values (1, 1);

-- 菜单初始化
delete from sys_menu;
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1000, '系统管理', 0, 1, 'system', null, '0', null, 'Tools', '0', 'system', now(), null, null, null);

insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1100, '用户管理', 1000, 1, 'user', 'system/user/index', '1', null, 'User', '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1101, '用户新增', 1100, 1, null, null, '2', 'sys:user:create', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1102, '用户删除', 1100, 2, null, null, '2', 'sys:user:remove', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1103, '用户修改', 1100, 3, null, null, '2', 'sys:user:modify', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1104, '用户分页', 1100, 4, null, null, '2', 'sys:user:page', null, '0', 'system', now(), null, null, null);

insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1200, '角色管理', 1000, 2, 'role', 'system/role/index', '1', null, 'Avatar', '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1201, '角色新增', 1200, 1, null, null, '2', 'sys:role:create', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1202, '角色删除', 1200, 2, null, null, '2', 'sys:role:remove', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1203, '角色修改', 1200, 3, null, null, '2', 'sys:role:modify', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1204, '角色分页', 1200, 4, null, null, '2', 'sys:role:page', null, '0', 'system', now(), null, null, null);

insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1300, '菜单管理', 1000, 3, 'menu', 'system/menu/index', '1', null, 'Menu', '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1301, '菜单新增', 1300, 1, null, null, '2', 'sys:menu:create', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1302, '菜单删除', 1300, 2, null, null, '2', 'sys:menu:remove', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1303, '菜单修改', 1300, 3, null, null, '2', 'sys:menu:modify', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1304, '菜单分页', 1300, 4, null, null, '2', 'sys:menu:page', null, '0', 'system', now(), null, null, null);

insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1400, '字典管理', 1000, 4, 'dict', 'system/dict/index', '1', null, 'Reading', '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1401, '字典新增', 1400, 1, null, null, '2', 'sys:dict:create', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1402, '字典删除', 1400, 2, null, null, '2', 'sys:dict:remove', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1403, '字典修改', 1400, 3, null, null, '2', 'sys:dict:modify', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1404, '字典分页', 1400, 4, null, null, '2', 'sys:dict:page', null, '0', 'system', now(), null, null, null);

insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1500, '参数设置', 1000, 5, 'config', 'system/config/index', '1', null, 'Tools', '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1501, '参数新增', 1500, 1, null, null, '2', 'sys:config:create', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1502, '参数删除', 1500, 2, null, null, '2', 'sys:config:remove', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1503, '参数修改', 1500, 3, null, null, '2', 'sys:config:modify', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1504, '参数分页', 1500, 4, null, null, '2', 'sys:config:page', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1505, '刷新缓存', 1500, 5, null, null, '2', 'sys:config:refreshCache', null, '0', 'system', now(), null, null, null);

insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1600, '日志管理', 1000, 6, 'log', null, '0', null, 'Monitor', '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1610, '操作日志', 1600, 1, 'operlog', 'system/monitor/operlog/index', '1', null, 'Document', '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1611, '操作日志新增', 1610, 1, null, null, '2', 'sys:operlog:create', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1612, '操作日志删除', 1610, 2, null, null, '2', 'sys:operlog:remove', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1613, '操作日志修改', 1610, 3, null, null, '2', 'sys:operlog:modify', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1614, '操作日志分页', 1610, 4, null, null, '2', 'sys:operlog:page', null, '0', 'system', now(), null, null, null);

insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1620, '登录日志', 1600, 2, 'logininfor', 'system/monitor/logininfor/index', '1', null, 'Key', '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1621, '登录日志新增', 1620, 1, null, null, '2', 'sys:logininfor:create', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1622, '登录日志删除', 1620, 2, null, null, '2', 'sys:logininfor:remove', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1623, '登录日志修改', 1620, 3, null, null, '2', 'sys:logininfor:modify', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1624, '登录日志分页', 1620, 4, null, null, '2', 'sys:logininfor:page', null, '0', 'system', now(), null, null, null);

insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1700, '岗位管理', 1000, 7, 'post-manage', 'system/post-manage/index', '1', null, 'OfficeBuilding', '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1701, '岗位新增', 1700, 1, null, null, '2', 'sys:org-post:create', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1702, '岗位删除', 1700, 2, null, null, '2', 'sys:org-post:remove', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1703, '岗位修改', 1700, 3, null, null, '2', 'sys:org-post:modify', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1704, '岗位查询', 1700, 4, null, null, '2', 'sys:org-post:page', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1705, '岗位员工查询', 1700, 5, null, null, '2', 'sys:org-post:user:page', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1706, '岗位员工绑定', 1700, 6, null, null, '2', 'sys:org-post:user:bind', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1707, '岗位员工解绑', 1700, 7, null, null, '2', 'sys:org-post:user:unbind', null, '0', 'system', now(), null, null, null);

insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1800, '模块中心', 1000, 8, 'module-center', 'system/module-center/index', '1', null, 'Grid', '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1801, '模块查询', 1800, 1, null, null, '2', 'sys:module:page', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1802, '模块新增', 1800, 2, null, null, '2', 'sys:module:create', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1803, '模块修改', 1800, 3, null, null, '2', 'sys:module:modify', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1804, '模块删除', 1800, 4, null, null, '2', 'sys:module:remove', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1805, 'Tab新增', 1800, 5, null, null, '2', 'sys:module:tab:create', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1806, 'Tab修改', 1800, 6, null, null, '2', 'sys:module:tab:modify', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1807, 'Tab删除', 1800, 7, null, null, '2', 'sys:module:tab:remove', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1808, '字段新增', 1800, 8, null, null, '2', 'sys:module:field:create', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1809, '字段修改', 1800, 9, null, null, '2', 'sys:module:field:modify', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1810, '字段删除', 1800, 10, null, null, '2', 'sys:module:field:remove', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1811, '按钮新增', 1800, 11, null, null, '2', 'sys:module:button:create', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1812, '按钮修改', 1800, 12, null, null, '2', 'sys:module:button:modify', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1813, '按钮删除', 1800, 13, null, null, '2', 'sys:module:button:remove', null, '0', 'system', now(), null, null, null);

insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1900, '功能授权', 1000, 9, 'function-auth', 'system/function-auth/index', '1', null, 'Lock', '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1901, '功能授权查询', 1900, 1, null, null, '2', 'sys:function-auth:page', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1902, '功能授权保存', 1900, 2, null, null, '2', 'sys:function-auth:save', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (1903, '功能授权复制', 1900, 3, null, null, '2', 'sys:function-auth:copy', null, '0', 'system', now(), null, null, null);

insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (2000, '字段标题中心', 1000, 10, 'module-title', 'system/module-title/index', '1', null, 'EditPen', '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (2001, '字段标题查询', 2000, 1, null, null, '2', 'sys:module-title:page', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (2002, '字段标题保存', 2000, 2, null, null, '2', 'sys:module-title:save', null, '0', 'system', now(), null, null, null);

insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (2100, '状态权限中心', 1000, 11, 'state-auth', 'system/state-auth/index', '1', null, 'SetUp', '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (2101, '状态权限查询', 2100, 1, null, null, '2', 'sys:state-auth:page', null, '0', 'system', now(), null, null, null);
insert into sys_menu (id, name, parent_id, sort, path, component, type, perms, icon, status, create_by, create_time, update_by, update_time, note) values (2102, '状态权限保存', 2100, 2, null, null, '2', 'sys:state-auth:save', null, '0', 'system', now(), null, null, null);

-- 超级管理员拥有全部菜单权限
delete from sys_role_menu;
insert into sys_role_menu (role_id, menu_id) values (1, 1000);
insert into sys_role_menu (role_id, menu_id) values (1, 1100);
insert into sys_role_menu (role_id, menu_id) values (1, 1101);
insert into sys_role_menu (role_id, menu_id) values (1, 1102);
insert into sys_role_menu (role_id, menu_id) values (1, 1103);
insert into sys_role_menu (role_id, menu_id) values (1, 1104);
insert into sys_role_menu (role_id, menu_id) values (1, 1200);
insert into sys_role_menu (role_id, menu_id) values (1, 1201);
insert into sys_role_menu (role_id, menu_id) values (1, 1202);
insert into sys_role_menu (role_id, menu_id) values (1, 1203);
insert into sys_role_menu (role_id, menu_id) values (1, 1204);
insert into sys_role_menu (role_id, menu_id) values (1, 1300);
insert into sys_role_menu (role_id, menu_id) values (1, 1301);
insert into sys_role_menu (role_id, menu_id) values (1, 1302);
insert into sys_role_menu (role_id, menu_id) values (1, 1303);
insert into sys_role_menu (role_id, menu_id) values (1, 1304);
insert into sys_role_menu (role_id, menu_id) values (1, 1400);
insert into sys_role_menu (role_id, menu_id) values (1, 1401);
insert into sys_role_menu (role_id, menu_id) values (1, 1402);
insert into sys_role_menu (role_id, menu_id) values (1, 1403);
insert into sys_role_menu (role_id, menu_id) values (1, 1404);
insert into sys_role_menu (role_id, menu_id) values (1, 1500);
insert into sys_role_menu (role_id, menu_id) values (1, 1501);
insert into sys_role_menu (role_id, menu_id) values (1, 1502);
insert into sys_role_menu (role_id, menu_id) values (1, 1503);
insert into sys_role_menu (role_id, menu_id) values (1, 1504);
insert into sys_role_menu (role_id, menu_id) values (1, 1505);
insert into sys_role_menu (role_id, menu_id) values (1, 1600);
insert into sys_role_menu (role_id, menu_id) values (1, 1610);
insert into sys_role_menu (role_id, menu_id) values (1, 1611);
insert into sys_role_menu (role_id, menu_id) values (1, 1612);
insert into sys_role_menu (role_id, menu_id) values (1, 1613);
insert into sys_role_menu (role_id, menu_id) values (1, 1614);
insert into sys_role_menu (role_id, menu_id) values (1, 1620);
insert into sys_role_menu (role_id, menu_id) values (1, 1621);
insert into sys_role_menu (role_id, menu_id) values (1, 1622);
insert into sys_role_menu (role_id, menu_id) values (1, 1623);
insert into sys_role_menu (role_id, menu_id) values (1, 1624);
insert into sys_role_menu (role_id, menu_id) values (1, 1700);
insert into sys_role_menu (role_id, menu_id) values (1, 1701);
insert into sys_role_menu (role_id, menu_id) values (1, 1702);
insert into sys_role_menu (role_id, menu_id) values (1, 1703);
insert into sys_role_menu (role_id, menu_id) values (1, 1704);
insert into sys_role_menu (role_id, menu_id) values (1, 1705);
insert into sys_role_menu (role_id, menu_id) values (1, 1706);
insert into sys_role_menu (role_id, menu_id) values (1, 1707);
insert into sys_role_menu (role_id, menu_id) values (1, 1800);
insert into sys_role_menu (role_id, menu_id) values (1, 1801);
insert into sys_role_menu (role_id, menu_id) values (1, 1802);
insert into sys_role_menu (role_id, menu_id) values (1, 1803);
insert into sys_role_menu (role_id, menu_id) values (1, 1804);
insert into sys_role_menu (role_id, menu_id) values (1, 1805);
insert into sys_role_menu (role_id, menu_id) values (1, 1806);
insert into sys_role_menu (role_id, menu_id) values (1, 1807);
insert into sys_role_menu (role_id, menu_id) values (1, 1808);
insert into sys_role_menu (role_id, menu_id) values (1, 1809);
insert into sys_role_menu (role_id, menu_id) values (1, 1810);
insert into sys_role_menu (role_id, menu_id) values (1, 1811);
insert into sys_role_menu (role_id, menu_id) values (1, 1812);
insert into sys_role_menu (role_id, menu_id) values (1, 1813);
insert into sys_role_menu (role_id, menu_id) values (1, 1900);
insert into sys_role_menu (role_id, menu_id) values (1, 1901);
insert into sys_role_menu (role_id, menu_id) values (1, 1902);
insert into sys_role_menu (role_id, menu_id) values (1, 1903);
insert into sys_role_menu (role_id, menu_id) values (1, 2000);
insert into sys_role_menu (role_id, menu_id) values (1, 2001);
insert into sys_role_menu (role_id, menu_id) values (1, 2002);
insert into sys_role_menu (role_id, menu_id) values (1, 2100);
insert into sys_role_menu (role_id, menu_id) values (1, 2101);
insert into sys_role_menu (role_id, menu_id) values (1, 2102);

-- 字典初始化
delete from sys_dict_type;
insert into sys_dict_type (id, name, type, status, create_by, create_time, update_by, update_time, note) values (3001, '用户性别', 'sys_user_sex', '0', 'system', now(), null, null, '用户性别列表');
insert into sys_dict_type (id, name, type, status, create_by, create_time, update_by, update_time, note) values (3002, '显示状态', 'sys_show_hide', '0', 'system', now(), null, null, '菜单显示状态列表');
insert into sys_dict_type (id, name, type, status, create_by, create_time, update_by, update_time, note) values (3003, '系统开关', 'sys_normal_disable', '0', 'system', now(), null, null, '系统开关列表');
insert into sys_dict_type (id, name, type, status, create_by, create_time, update_by, update_time, note) values (3004, '是否字典', 'sys_yes_no', '0', 'system', now(), null, null, '是否字典列表');

delete from sys_dict_data;
insert into sys_dict_data (id, dict_type_id, label, value, sort, css_class, list_class, default_flag, status, create_by, create_time, update_by, update_time, note) values (4001, 3001, '男', '0', 1, null, 'primary', 'N', '0', 'system', now(), null, null, '用户性别男');
insert into sys_dict_data (id, dict_type_id, label, value, sort, css_class, list_class, default_flag, status, create_by, create_time, update_by, update_time, note) values (4002, 3001, '女', '1', 2, null, 'danger', 'N', '0', 'system', now(), null, null, '用户性别女');
insert into sys_dict_data (id, dict_type_id, label, value, sort, css_class, list_class, default_flag, status, create_by, create_time, update_by, update_time, note) values (4003, 3001, '未知', '2', 3, null, 'info', 'Y', '0', 'system', now(), null, null, '用户性别未知');
insert into sys_dict_data (id, dict_type_id, label, value, sort, css_class, list_class, default_flag, status, create_by, create_time, update_by, update_time, note) values (4004, 3002, '显示', '0', 1, null, 'success', 'Y', '0', 'system', now(), null, null, '菜单显示');
insert into sys_dict_data (id, dict_type_id, label, value, sort, css_class, list_class, default_flag, status, create_by, create_time, update_by, update_time, note) values (4005, 3002, '隐藏', '1', 2, null, 'warning', 'N', '0', 'system', now(), null, null, '菜单隐藏');
insert into sys_dict_data (id, dict_type_id, label, value, sort, css_class, list_class, default_flag, status, create_by, create_time, update_by, update_time, note) values (4006, 3003, '正常', '0', 1, null, 'success', 'Y', '0', 'system', now(), null, null, '系统正常状态');
insert into sys_dict_data (id, dict_type_id, label, value, sort, css_class, list_class, default_flag, status, create_by, create_time, update_by, update_time, note) values (4007, 3003, '停用', '1', 2, null, 'danger', 'N', '0', 'system', now(), null, null, '系统停用状态');
insert into sys_dict_data (id, dict_type_id, label, value, sort, css_class, list_class, default_flag, status, create_by, create_time, update_by, update_time, note) values (4008, 3004, '是', 'Y', 1, null, 'success', 'Y', '0', 'system', now(), null, null, '是否字典是');
insert into sys_dict_data (id, dict_type_id, label, value, sort, css_class, list_class, default_flag, status, create_by, create_time, update_by, update_time, note) values (4009, 3004, '否', 'N', 2, null, 'info', 'N', '0', 'system', now(), null, null, '是否字典否');

-- 参数配置初始化
delete from sys_config;
insert into sys_config (id, config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, note) values (5001, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'system', now(), null, null, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
insert into sys_config (id, config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, note) values (5002, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'system', now(), null, null, '初始化密码 123456');
insert into sys_config (id, config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, note) values (5003, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', 'system', now(), null, null, '是否开启验证码');

-- 岗位管理初始化
delete from sys_user_org_post;
delete from sys_org_post;
delete from sys_org_unit;

insert into sys_org_unit (id, parent_id, ancestors, org_code, org_name, org_type, sort, leader_user_id, status, create_by, create_time, update_by, update_time, note) values (9001, 0, '0', 'SOLI_GROUP', 'Soli集团', 'GROUP', 1, 1, '0', 'system', now(), null, null, '岗位管理组织树根节点');
insert into sys_org_unit (id, parent_id, ancestors, org_code, org_name, org_type, sort, leader_user_id, status, create_by, create_time, update_by, update_time, note) values (9002, 9001, '0,9001', 'SOLI_HQ', '总公司', 'HEADQUARTERS', 1, 1, '0', 'system', now(), null, null, '总部组织节点');
insert into sys_org_unit (id, parent_id, ancestors, org_code, org_name, org_type, sort, leader_user_id, status, create_by, create_time, update_by, update_time, note) values (9003, 9001, '0,9001', 'SOLI_EAST', '华东分公司', 'BRANCH', 2, 3, '0', 'system', now(), null, null, '华东业务组织节点');

insert into sys_org_post (id, org_unit_id, parent_post_id, ancestors, post_code, post_name, post_type, manager_user_id, sort, status, create_by, create_time, update_by, update_time, note) values (9101, 9002, 0, '0', 'PURCHASE_CENTER', '采购中心', 'MANAGER', 1, 1, '0', 'system', now(), null, null, '总部采购中心');
insert into sys_org_post (id, org_unit_id, parent_post_id, ancestors, post_code, post_name, post_type, manager_user_id, sort, status, create_by, create_time, update_by, update_time, note) values (9102, 9002, 9101, '0,9101', 'BUYER', '采购员', 'BUYER', 2, 1, '0', 'system', now(), null, null, '采购执行岗位');
insert into sys_org_post (id, org_unit_id, parent_post_id, ancestors, post_code, post_name, post_type, manager_user_id, sort, status, create_by, create_time, update_by, update_time, note) values (9103, 9003, 0, '0', 'SALES_CENTER', '销售中心', 'MANAGER', 3, 1, '0', 'system', now(), null, null, '华东销售中心');

insert into sys_user_org_post (id, user_id, org_post_id, primary_flag, status, create_by, create_time, update_by, update_time, note) values (9201, 1, 9101, 'Y', '0', 'system', now(), null, null, 'admin 兼岗采购中心');
insert into sys_user_org_post (id, user_id, org_post_id, primary_flag, status, create_by, create_time, update_by, update_time, note) values (9202, 2, 9102, 'Y', '0', 'system', now(), null, null, '张三分配到采购员');
insert into sys_user_org_post (id, user_id, org_post_id, primary_flag, status, create_by, create_time, update_by, update_time, note) values (9203, 3, 9103, 'Y', '0', 'system', now(), null, null, '李四分配到销售中心');

-- 模块权限平台初始化
delete from sys_module_publish_log;
delete from sys_module_state_button_auth;
delete from sys_module_state_field_auth;
delete from sys_org_post_button_auth;
delete from sys_org_post_field_auth;
delete from sys_org_post_module_auth;
delete from sys_module_transition;
delete from sys_module_state;
delete from sys_module_button;
delete from sys_module_field;
delete from sys_module_tab;
delete from sys_module;

insert into sys_module (id, parent_id, ancestors, module_code, module_name, module_type, route_path, component_path, icon, sort, nav_visible, stateful_flag, state_field_code, context_version, status, create_by, create_time, update_by, update_time, note) values (100, 0, '0', 'purchase', '采购管理', 'CATALOG', null, null, 'ShoppingCart', 1, '1', '0', null, 3, '0', 'system', now(), null, null, '采购业务模块目录');
insert into sys_module (id, parent_id, ancestors, module_code, module_name, module_type, route_path, component_path, icon, sort, nav_visible, stateful_flag, state_field_code, context_version, status, create_by, create_time, update_by, update_time, note) values (101, 100, '0,100', 'purchase_order', '采购订单', 'BILL', '/purchase/order', 'purchase/order/index', 'Document', 1, '1', '1', 'status', 5, '0', 'system', now(), null, null, '适用于采购下单、提交审核、发运和完结流程。');
insert into sys_module (id, parent_id, ancestors, module_code, module_name, module_type, route_path, component_path, icon, sort, nav_visible, stateful_flag, state_field_code, context_version, status, create_by, create_time, update_by, update_time, note) values (200, 0, '0', 'sales', '销售管理', 'CATALOG', null, null, 'Goods', 2, '1', '0', null, 2, '0', 'system', now(), null, null, '销售业务模块目录');
insert into sys_module (id, parent_id, ancestors, module_code, module_name, module_type, route_path, component_path, icon, sort, nav_visible, stateful_flag, state_field_code, context_version, status, create_by, create_time, update_by, update_time, note) values (201, 200, '0,200', 'sales_order', '销售订单', 'BILL', '/sales/order', 'sales/order/index', 'Tickets', 1, '1', '1', 'status', 4, '0', 'system', now(), null, null, '适用于销售下单、审核和发运流程。');

insert into sys_module_tab (id, module_id, tab_scope, tab_code, tab_name, sort, status, create_by, create_time, update_by, update_time, note) values (10101, 101, 'HEADER', 'base_info', '基础信息', 1, '0', 'system', now(), null, null, '采购订单头信息');
insert into sys_module_tab (id, module_id, tab_scope, tab_code, tab_name, sort, status, create_by, create_time, update_by, update_time, note) values (10103, 101, 'HEADER', 'audit_info', '审核信息', 2, '0', 'system', now(), null, null, '采购审核追踪信息');
insert into sys_module_tab (id, module_id, tab_scope, tab_code, tab_name, sort, status, create_by, create_time, update_by, update_time, note) values (10102, 101, 'DETAIL', 'item_detail', '物料明细', 1, '0', 'system', now(), null, null, '采购订单明细行');
insert into sys_module_tab (id, module_id, tab_scope, tab_code, tab_name, sort, status, create_by, create_time, update_by, update_time, note) values (20101, 201, 'HEADER', 'base_info', '基础信息', 1, '0', 'system', now(), null, null, '销售订单头信息');
insert into sys_module_tab (id, module_id, tab_scope, tab_code, tab_name, sort, status, create_by, create_time, update_by, update_time, note) values (20102, 201, 'DETAIL', 'item_detail', '商品明细', 1, '0', 'system', now(), null, null, '销售订单明细行');

insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (1010101, 101, 10101, 'HEADER', 'supplierName', '供应商名称', null, null, null, 'search-select', 'header.supplierName', 'string', '1', 1, '0', 'system', now(), null, null, '支持按名称、编码联想选择');
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (1010102, 101, 10101, 'HEADER', 'sourceBillNo', '来源单号', null, null, null, 'input', 'header.sourceBillNo', 'string', '0', 2, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (1010103, 101, 10101, 'HEADER', 'sourceType', '来源类型', null, null, null, 'tag', 'header.sourceType', 'string', '0', 3, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (1010104, 101, 10101, 'HEADER', 'amount', '订单金额', null, null, null, 'amount', 'header.amount', 'decimal', '1', 4, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (1010301, 101, 10103, 'HEADER', 'status', '单据状态', null, null, null, 'tag', 'header.status', 'string', '1', 1, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (1010302, 101, 10103, 'HEADER', 'auditUserName', '审核人', null, null, null, 'text', 'header.auditUserName', 'string', '0', 2, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (1010303, 101, 10103, 'HEADER', 'auditTime', '审核时间', null, null, null, 'datetime', 'header.auditTime', 'datetime', '0', 3, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (1010201, 101, 10102, 'DETAIL', 'materialName', '物料名称', null, null, null, 'search-select', 'detail.items.materialName', 'string', '1', 1, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (1010202, 101, 10102, 'DETAIL', 'qty', '数量', null, null, null, 'number', 'detail.items.qty', 'decimal', '1', 2, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (1010203, 101, 10102, 'DETAIL', 'price', '单价', null, null, null, 'amount', 'detail.items.price', 'decimal', '1', 3, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (1010204, 101, 10102, 'DETAIL', 'lineAmount', '行金额', null, null, null, 'amount', 'detail.items.lineAmount', 'decimal', '1', 4, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (2010101, 201, 20101, 'HEADER', 'customerName', '客户名称', null, null, null, 'search-select', 'header.customerName', 'string', '1', 1, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (2010102, 201, 20101, 'HEADER', 'amount', '订单金额', null, null, null, 'amount', 'header.amount', 'decimal', '1', 2, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (2010103, 201, 20101, 'HEADER', 'status', '单据状态', null, null, null, 'tag', 'header.status', 'string', '1', 3, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (2010104, 201, 20101, 'HEADER', 'auditUserName', '审核人', null, null, null, 'text', 'header.auditUserName', 'string', '0', 4, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (2010105, 201, 20101, 'HEADER', 'auditTime', '审核时间', null, null, null, 'datetime', 'header.auditTime', 'datetime', '0', 5, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (2010201, 201, 20102, 'DETAIL', 'goodsName', '商品名称', null, null, null, 'search-select', 'detail.items.goodsName', 'string', '1', 1, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (2010202, 201, 20102, 'DETAIL', 'qty', '数量', null, null, null, 'number', 'detail.items.qty', 'decimal', '1', 2, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (2010203, 201, 20102, 'DETAIL', 'price', '单价', null, null, null, 'amount', 'detail.items.price', 'decimal', '1', 3, '0', 'system', now(), null, null, null);
insert into sys_module_field (id, module_id, tab_id, field_scope, field_code, default_title, display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status, create_by, create_time, update_by, update_time, note) values (2010204, 201, 20102, 'DETAIL', 'lineAmount', '行金额', null, null, null, 'amount', 'detail.items.lineAmount', 'decimal', '1', 4, '0', 'system', now(), null, null, null);

insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (101001, 101, 'create', '新增', 'LIST_TOOLBAR', 1, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (101002, 101, 'export', '导出', 'LIST_TOOLBAR', 2, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (101003, 101, 'detail', '查看详情', 'LIST_ROW_BUTTON', 3, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (101004, 101, 'edit', '编辑', 'LIST_ROW_BUTTON', 4, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (101005, 101, 'save', '保存', 'HEADER_TOOLBAR', 5, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (101006, 101, 'submit', '提交', 'HEADER_TOOLBAR', 6, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (101007, 101, 'audit', '审核', 'HEADER_TOOLBAR', 7, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (101008, 101, 'ship', '发运', 'HEADER_TOOLBAR', 8, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (101009, 101, 'complete', '完成', 'HEADER_TOOLBAR', 9, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (101010, 101, 'addRow', '新增行', 'DETAIL_ROW_BUTTON', 10, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (101011, 101, 'removeRow', '删除行', 'DETAIL_ROW_BUTTON', 11, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (201001, 201, 'create', '新增', 'LIST_TOOLBAR', 1, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (201002, 201, 'detail', '查看详情', 'LIST_ROW_BUTTON', 2, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (201003, 201, 'edit', '编辑', 'LIST_ROW_BUTTON', 3, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (201004, 201, 'save', '保存', 'HEADER_TOOLBAR', 4, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (201005, 201, 'submit', '提交', 'HEADER_TOOLBAR', 5, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (201006, 201, 'audit', '审核', 'HEADER_TOOLBAR', 6, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (201007, 201, 'ship', '发运', 'HEADER_TOOLBAR', 7, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (201008, 201, 'complete', '完成', 'HEADER_TOOLBAR', 8, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (201009, 201, 'addRow', '新增行', 'DETAIL_ROW_BUTTON', 9, '0', 'system', now(), null, null, null);
insert into sys_module_button (id, module_id, button_code, default_title, button_area, sort, status, create_by, create_time, update_by, update_time, note) values (201010, 201, 'removeRow', '删除行', 'DETAIL_ROW_BUTTON', 10, '0', 'system', now(), null, null, null);

insert into sys_module_state (id, module_id, state_code, state_name, sort, is_initial, is_final, status, create_by, create_time, update_by, update_time, note) values (101101, 101, 'unaudited', '未审核', 1, '1', '0', '0', 'system', now(), null, null, null);
insert into sys_module_state (id, module_id, state_code, state_name, sort, is_initial, is_final, status, create_by, create_time, update_by, update_time, note) values (101102, 101, 'pre_audited', '待审核', 2, '0', '0', '0', 'system', now(), null, null, null);
insert into sys_module_state (id, module_id, state_code, state_name, sort, is_initial, is_final, status, create_by, create_time, update_by, update_time, note) values (101103, 101, 'audited', '已审核', 3, '0', '0', '0', 'system', now(), null, null, null);
insert into sys_module_state (id, module_id, state_code, state_name, sort, is_initial, is_final, status, create_by, create_time, update_by, update_time, note) values (101104, 101, 'shipped', '已发运', 4, '0', '0', '0', 'system', now(), null, null, null);
insert into sys_module_state (id, module_id, state_code, state_name, sort, is_initial, is_final, status, create_by, create_time, update_by, update_time, note) values (101105, 101, 'completed', '已完成', 5, '0', '1', '0', 'system', now(), null, null, null);
insert into sys_module_state (id, module_id, state_code, state_name, sort, is_initial, is_final, status, create_by, create_time, update_by, update_time, note) values (201101, 201, 'unaudited', '未审核', 1, '1', '0', '0', 'system', now(), null, null, null);
insert into sys_module_state (id, module_id, state_code, state_name, sort, is_initial, is_final, status, create_by, create_time, update_by, update_time, note) values (201102, 201, 'pre_audited', '待审核', 2, '0', '0', '0', 'system', now(), null, null, null);
insert into sys_module_state (id, module_id, state_code, state_name, sort, is_initial, is_final, status, create_by, create_time, update_by, update_time, note) values (201103, 201, 'audited', '已审核', 3, '0', '0', '0', 'system', now(), null, null, null);
insert into sys_module_state (id, module_id, state_code, state_name, sort, is_initial, is_final, status, create_by, create_time, update_by, update_time, note) values (201104, 201, 'shipped', '已发运', 4, '0', '0', '0', 'system', now(), null, null, null);
insert into sys_module_state (id, module_id, state_code, state_name, sort, is_initial, is_final, status, create_by, create_time, update_by, update_time, note) values (201105, 201, 'completed', '已完成', 5, '0', '1', '0', 'system', now(), null, null, null);

insert into sys_module_transition (id, module_id, action_button_code, from_state_code, to_state_code, sort, status, create_by, create_time, update_by, update_time, note) values (101201, 101, 'submit', 'unaudited', 'pre_audited', 1, '0', 'system', now(), null, null, null);
insert into sys_module_transition (id, module_id, action_button_code, from_state_code, to_state_code, sort, status, create_by, create_time, update_by, update_time, note) values (101202, 101, 'audit', 'pre_audited', 'audited', 2, '0', 'system', now(), null, null, null);
insert into sys_module_transition (id, module_id, action_button_code, from_state_code, to_state_code, sort, status, create_by, create_time, update_by, update_time, note) values (101203, 101, 'ship', 'audited', 'shipped', 3, '0', 'system', now(), null, null, null);
insert into sys_module_transition (id, module_id, action_button_code, from_state_code, to_state_code, sort, status, create_by, create_time, update_by, update_time, note) values (101204, 101, 'complete', 'shipped', 'completed', 4, '0', 'system', now(), null, null, null);
insert into sys_module_transition (id, module_id, action_button_code, from_state_code, to_state_code, sort, status, create_by, create_time, update_by, update_time, note) values (201201, 201, 'submit', 'unaudited', 'pre_audited', 1, '0', 'system', now(), null, null, null);
insert into sys_module_transition (id, module_id, action_button_code, from_state_code, to_state_code, sort, status, create_by, create_time, update_by, update_time, note) values (201202, 201, 'audit', 'pre_audited', 'audited', 2, '0', 'system', now(), null, null, null);
insert into sys_module_transition (id, module_id, action_button_code, from_state_code, to_state_code, sort, status, create_by, create_time, update_by, update_time, note) values (201203, 201, 'ship', 'audited', 'shipped', 3, '0', 'system', now(), null, null, null);
insert into sys_module_transition (id, module_id, action_button_code, from_state_code, to_state_code, sort, status, create_by, create_time, update_by, update_time, note) values (201204, 201, 'complete', 'shipped', 'completed', 4, '0', 'system', now(), null, null, null);

insert into sys_org_post_module_auth (id, org_post_id, module_id, module_visible, nav_visible, create_by, create_time, update_by, update_time, note) values (9301, 9101, 100, '1', '1', 'system', now(), null, null, 'admin 岗位默认拥有采购目录权限');
insert into sys_org_post_module_auth (id, org_post_id, module_id, module_visible, nav_visible, create_by, create_time, update_by, update_time, note) values (9302, 9101, 101, '1', '1', 'system', now(), null, null, 'admin 岗位默认拥有采购订单权限');
insert into sys_org_post_module_auth (id, org_post_id, module_id, module_visible, nav_visible, create_by, create_time, update_by, update_time, note) values (9303, 9101, 200, '1', '1', 'system', now(), null, null, 'admin 岗位默认拥有销售目录权限');
insert into sys_org_post_module_auth (id, org_post_id, module_id, module_visible, nav_visible, create_by, create_time, update_by, update_time, note) values (9304, 9101, 201, '1', '1', 'system', now(), null, null, 'admin 岗位默认拥有销售订单权限');

insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9401, 9101, 101, 1010101, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9402, 9101, 101, 1010102, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9403, 9101, 101, 1010103, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9404, 9101, 101, 1010104, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9405, 9101, 101, 1010301, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9406, 9101, 101, 1010302, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9407, 9101, 101, 1010303, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9408, 9101, 101, 1010201, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9409, 9101, 101, 1010202, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9410, 9101, 101, 1010203, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9411, 9101, 101, 1010204, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9412, 9101, 201, 2010101, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9413, 9101, 201, 2010102, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9414, 9101, 201, 2010103, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9415, 9101, 201, 2010104, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9416, 9101, 201, 2010105, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9417, 9101, 201, 2010201, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9418, 9101, 201, 2010202, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9419, 9101, 201, 2010203, 2, 'system', now(), null, null, null);
insert into sys_org_post_field_auth (id, org_post_id, module_id, field_id, permission_level, create_by, create_time, update_by, update_time, note) values (9420, 9101, 201, 2010204, 2, 'system', now(), null, null, null);

insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9501, 9101, 101, 101001, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9502, 9101, 101, 101002, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9503, 9101, 101, 101003, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9504, 9101, 101, 101004, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9505, 9101, 101, 101005, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9506, 9101, 101, 101006, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9507, 9101, 101, 101007, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9508, 9101, 101, 101008, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9509, 9101, 101, 101009, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9510, 9101, 101, 101010, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9511, 9101, 101, 101011, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9512, 9101, 201, 201001, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9513, 9101, 201, 201002, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9514, 9101, 201, 201003, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9515, 9101, 201, 201004, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9516, 9101, 201, 201005, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9517, 9101, 201, 201006, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9518, 9101, 201, 201007, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9519, 9101, 201, 201008, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9520, 9101, 201, 201009, 2, 'system', now(), null, null, null);
insert into sys_org_post_button_auth (id, org_post_id, module_id, button_id, permission_level, create_by, create_time, update_by, update_time, note) values (9521, 9101, 201, 201010, 2, 'system', now(), null, null, null);
