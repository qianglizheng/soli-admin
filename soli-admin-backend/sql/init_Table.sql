-- 系统用户表
drop table if exists sys_user;
create table sys_user (
    id             bigint(20)        not null auto_increment  comment '用户ID',
    username       varchar(32)       not null                 comment '用户名',
    password       varchar(100)      not null                 comment '用户密码',
    nickname       varchar(32)       default null             comment '用户昵称',
    email          varchar(50)       default null             comment '用户邮箱',
    phone          varchar(11)       default null             comment '用户手机',
    avatar         varchar(100)      default null             comment '用户头像',
    type           char(1)           default '1' not null     comment '用户类型（0超级管理员 1普通用户）',
    sex            char(1)           default '0' not null     comment '用户性别（0男 1女）',
    login_ip       varchar(128)      default null             comment '最后登录IP',
    login_time     datetime          default null             comment '最后登录时间',
    status         char(1)           default '0' not null     comment '用户状态（0正常 1停用）',
    create_by      varchar(32)       default null             comment '创建者',
    create_time    datetime          default null             comment '创建时间',
    update_by      varchar(32)       default null             comment '更新者',
    update_time    datetime          default null             comment '更新时间',
    note           varchar(500)      default null             comment '备注',
    primary key (id)
) engine=innodb comment = '系统用户表';

-- 系统角色表
drop table if exists sys_role;
create table sys_role (
    id             bigint(20)        not null auto_increment  comment '角色ID',
    name           varchar(30)       not null                 comment '角色名称',
    code           varchar(100)      not null                 comment '角色权限字符',
    sort           int(4)            default 1 not null       comment '显示顺序',
    data_scope     char(1)           default '1' not null     comment '数据范围（1全部 2自定义 3本部门 4本部门及以下）',
    status         char(1)           default '0' not null     comment '角色状态（0正常 1停用）',
    create_by      varchar(32)       default null             comment '创建者',
    create_time    datetime          default null             comment '创建时间',
    update_by      varchar(32)       default null             comment '更新者',
    update_time    datetime          default null             comment '更新时间',
    note           varchar(500)      default null             comment '备注',
    primary key (id)
) engine=innodb comment = '系统角色表';

-- 系统用户角色关联表
drop table if exists sys_user_role;
create table sys_user_role (
    user_id        bigint(20)        not null                 comment '用户ID',
    role_id        bigint(20)        not null                 comment '角色ID',
    primary key (user_id, role_id)
) engine=innodb comment = '系统用户角色关联表';

-- 系统菜单表
drop table if exists sys_menu;
create table sys_menu (
    id             bigint(20)        not null auto_increment  comment '菜单ID',
    name           varchar(50)       not null                 comment '菜单名称',
    parent_id      bigint(20)        default 0                comment '父菜单ID',
    sort           int(4)            default 0                comment '显示顺序',
    path           varchar(200)      default ''               comment '路由地址',
    component      varchar(255)      default null             comment '组件路径',
    type           char(1)           default ''               comment '菜单类型（0目录 1菜单 2按钮）',
    perms          varchar(100)      default null             comment '权限标识',
    icon           varchar(100)      default '#'              comment '菜单图标',
    status         char(1)           default '0' not null     comment '菜单状态（0正常 1停用）',
    create_by      varchar(32)       default null             comment '创建者',
    create_time    datetime          default null             comment '创建时间',
    update_by      varchar(32)       default null             comment '更新者',
    update_time    datetime          default null             comment '更新时间',
    note           varchar(500)      default null             comment '备注',
    primary key (id)
) engine=innodb auto_increment=2000 comment = '菜单权限表';

-- 系统角色菜单关联表
drop table if exists sys_role_menu;
create table sys_role_menu (
    role_id        bigint(20)        not null                 comment '角色ID',
    menu_id        bigint(20)        not null                 comment '菜单ID',
    primary key (role_id, menu_id)
) engine=innodb comment = '角色和菜单关联表';

-- 字典类型表
drop table if exists sys_dict_type;
create table sys_dict_type (
    id             bigint(20)        not null auto_increment  comment '字典ID',
    name           varchar(100)      not null                 comment '字典名称',
    type           varchar(100)      not null                 comment '字典类型',
    status         char(1)           default '0' not null     comment '状态（0正常 1停用）',
    create_by      varchar(32)       default null             comment '创建者',
    create_time    datetime          default null             comment '创建时间',
    update_by      varchar(32)       default null             comment '更新者',
    update_time    datetime          default null             comment '更新时间',
    note           varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_dict_type_type (type)
) engine=innodb auto_increment=3000 comment = '字典类型表';

-- 字典数据表
drop table if exists sys_dict_data;
create table sys_dict_data (
    id             bigint(20)        not null auto_increment  comment '字典数据ID',
    dict_type_id   bigint(20)        not null                 comment '字典类型ID',
    label          varchar(100)      not null                 comment '字典标签',
    value          varchar(100)      not null                 comment '字典键值',
    sort           int(4)            default 0 not null       comment '显示排序',
    css_class      varchar(100)      default null             comment '样式属性',
    list_class     varchar(100)      default null             comment '表格回显样式',
    default_flag   char(1)           default 'N' not null     comment '默认（Y是 N否）',
    status         char(1)           default '0' not null     comment '状态（0正常 1停用）',
    create_by      varchar(32)       default null             comment '创建者',
    create_time    datetime          default null             comment '创建时间',
    update_by      varchar(32)       default null             comment '更新者',
    update_time    datetime          default null             comment '更新时间',
    note           varchar(500)      default null             comment '备注',
    primary key (id),
    key idx_sys_dict_data_type_id (dict_type_id),
    unique key uk_sys_dict_data_type_value (dict_type_id, value)
) engine=innodb auto_increment=4000 comment = '字典数据表';

-- 参数配置表
drop table if exists sys_config;
create table sys_config (
    id             bigint(20)        not null auto_increment  comment '参数主键',
    config_name    varchar(100)      not null                 comment '参数名称',
    config_key     varchar(100)      not null                 comment '参数键名',
    config_value   varchar(500)      default null             comment '参数键值',
    config_type    char(1)           default 'N' not null     comment '系统内置（Y是 N否）',
    create_by      varchar(32)       default null             comment '创建者',
    create_time    datetime          default null             comment '创建时间',
    update_by      varchar(32)       default null             comment '更新者',
    update_time    datetime          default null             comment '更新时间',
    note           varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_config_key (config_key)
) engine=innodb auto_increment=5000 comment = '参数配置表';

-- 组织单元表
drop table if exists sys_org_unit;
create table sys_org_unit (
    id             bigint(20)        not null                 comment '组织ID',
    parent_id      bigint(20)        default 0 not null       comment '父组织ID',
    ancestors      varchar(500)      default '0'              comment '祖级路径',
    org_code       varchar(64)       not null                 comment '组织编码',
    org_name       varchar(128)      not null                 comment '组织名称',
    org_type       varchar(32)       not null                 comment '组织类型',
    sort           int(11)           default 1 not null       comment '显示顺序',
    leader_user_id bigint(20)        default null             comment '负责人用户ID',
    status         char(1)           default '0' not null     comment '状态（0正常 1停用）',
    create_by      varchar(32)       default null             comment '创建者',
    create_time    datetime          default null             comment '创建时间',
    update_by      varchar(32)       default null             comment '更新者',
    update_time    datetime          default null             comment '更新时间',
    note           varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_org_unit_code (org_code),
    key idx_sys_org_unit_parent (parent_id, sort),
    key idx_sys_org_unit_type_status (org_type, status)
) engine=innodb comment = '组织单元表';

-- 组织岗位表
drop table if exists sys_org_post;
create table sys_org_post (
    id              bigint(20)        not null                 comment '岗位ID',
    org_unit_id     bigint(20)        not null                 comment '所属组织ID',
    parent_post_id  bigint(20)        default 0 not null       comment '父岗位ID',
    ancestors       varchar(500)      default '0'              comment '岗位祖级路径',
    post_code       varchar(64)       not null                 comment '岗位编码',
    post_name       varchar(128)      not null                 comment '岗位名称',
    post_type       varchar(32)       default null             comment '岗位类型',
    manager_user_id bigint(20)        default null             comment '岗位负责人用户ID',
    sort            int(11)           default 1 not null       comment '显示顺序',
    status          char(1)           default '0' not null     comment '状态（0正常 1停用）',
    create_by       varchar(32)       default null             comment '创建者',
    create_time     datetime          default null             comment '创建时间',
    update_by       varchar(32)       default null             comment '更新者',
    update_time     datetime          default null             comment '更新时间',
    note            varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_org_post_unit_code (org_unit_id, post_code),
    key idx_sys_org_post_parent (parent_post_id, sort),
    key idx_sys_org_post_unit_status (org_unit_id, status)
) engine=innodb comment = '组织岗位表';

-- 用户组织岗位关联表
drop table if exists sys_user_org_post;
create table sys_user_org_post (
    id            bigint(20)        not null                 comment '主键ID',
    user_id       bigint(20)        not null                 comment '用户ID',
    org_post_id   bigint(20)        not null                 comment '组织岗位ID',
    primary_flag  char(1)           default 'N' not null     comment '是否主岗位（Y是 N否）',
    status        char(1)           default '0' not null     comment '状态（0正常 1停用）',
    create_by     varchar(32)       default null             comment '创建者',
    create_time   datetime          default null             comment '创建时间',
    update_by     varchar(32)       default null             comment '更新者',
    update_time   datetime          default null             comment '更新时间',
    note          varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_user_org_post (user_id, org_post_id),
    key idx_sys_user_org_post_post (org_post_id, status),
    key idx_sys_user_org_post_user (user_id, status)
) engine=innodb comment = '用户组织岗位关联表';

-- 模块主表
drop table if exists sys_module;
create table sys_module (
    id               bigint(20)        not null                 comment '模块ID',
    parent_id        bigint(20)        default 0 not null       comment '父模块ID',
    ancestors        varchar(500)      default '0'              comment '祖级路径',
    module_code      varchar(64)       not null                 comment '模块编码',
    module_name      varchar(128)      not null                 comment '模块名称',
    module_type      varchar(32)       not null                 comment '模块类型',
    route_path       varchar(255)      default null             comment '路由地址',
    component_path   varchar(255)      default null             comment '组件路径',
    icon             varchar(64)       default null             comment '图标',
    sort             int(11)           default 1 not null       comment '显示顺序',
    nav_visible      char(1)           default '1' not null     comment '导航可见（0隐藏 1显示）',
    stateful_flag    char(1)           default '0' not null     comment '是否状态型模块（0否 1是）',
    state_field_code varchar(64)       default null             comment '状态字段编码',
    context_version  int(11)           default 1 not null       comment '上下文版本',
    status           char(1)           default '0' not null     comment '状态（0正常 1停用）',
    create_by        varchar(32)       default null             comment '创建者',
    create_time      datetime          default null             comment '创建时间',
    update_by        varchar(32)       default null             comment '更新者',
    update_time      datetime          default null             comment '更新时间',
    note             varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_module_code (module_code),
    key idx_sys_module_parent (parent_id, sort),
    key idx_sys_module_type_status (module_type, status)
) engine=innodb comment = '模块主表';

-- 模块Tab定义表
drop table if exists sys_module_tab;
create table sys_module_tab (
    id          bigint(20)        not null                 comment 'TabID',
    module_id   bigint(20)        not null                 comment '模块ID',
    tab_scope   varchar(16)       not null                 comment '作用域（HEADER/DETAIL）',
    tab_code    varchar(64)       not null                 comment 'Tab编码',
    tab_name    varchar(128)      not null                 comment 'Tab名称',
    sort        int(11)           default 1 not null       comment '显示顺序',
    status      char(1)           default '0' not null     comment '状态（0正常 1停用）',
    create_by   varchar(32)       default null             comment '创建者',
    create_time datetime          default null             comment '创建时间',
    update_by   varchar(32)       default null             comment '更新者',
    update_time datetime          default null             comment '更新时间',
    note        varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_module_tab (module_id, tab_scope, tab_code),
    key idx_sys_module_tab_scope (module_id, tab_scope, sort)
) engine=innodb comment = '模块Tab定义表';

-- 模块字段定义表
drop table if exists sys_module_field;
create table sys_module_field (
    id             bigint(20)        not null                 comment '字段ID',
    module_id      bigint(20)        not null                 comment '模块ID',
    tab_id         bigint(20)        not null                 comment 'TabID',
    field_scope    varchar(16)       not null                 comment '作用域（HEADER/DETAIL）',
    field_code     varchar(64)       not null                 comment '字段编码',
    default_title  varchar(128)      not null                 comment '默认标题',
    display_title  varchar(128)      default null             comment '显示标题',
    placeholder    varchar(255)      default null             comment '占位提示',
    help_text      varchar(500)      default null             comment '帮助说明',
    component_type varchar(32)       default null             comment '组件类型',
    data_path      varchar(255)      default null             comment '数据路径',
    value_type     varchar(32)       default null             comment '值类型',
    required_flag  char(1)           default '0' not null     comment '是否必填（0否 1是）',
    sort           int(11)           default 1 not null       comment '显示顺序',
    status         char(1)           default '0' not null     comment '状态（0正常 1停用）',
    create_by      varchar(32)       default null             comment '创建者',
    create_time    datetime          default null             comment '创建时间',
    update_by      varchar(32)       default null             comment '更新者',
    update_time    datetime          default null             comment '更新时间',
    note           varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_module_field (module_id, field_code),
    key idx_sys_module_field_tab (tab_id, sort),
    key idx_sys_module_field_scope (module_id, field_scope, status)
) engine=innodb comment = '模块字段定义表';

-- 模块按钮定义表
drop table if exists sys_module_button;
create table sys_module_button (
    id            bigint(20)        not null                 comment '按钮ID',
    module_id     bigint(20)        not null                 comment '模块ID',
    button_code   varchar(64)       not null                 comment '按钮编码',
    default_title varchar(128)      not null                 comment '默认标题',
    button_area   varchar(32)       not null                 comment '按钮区域',
    sort          int(11)           default 1 not null       comment '显示顺序',
    status        char(1)           default '0' not null     comment '状态（0正常 1停用）',
    create_by     varchar(32)       default null             comment '创建者',
    create_time   datetime          default null             comment '创建时间',
    update_by     varchar(32)       default null             comment '更新者',
    update_time   datetime          default null             comment '更新时间',
    note          varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_module_button (module_id, button_code),
    key idx_sys_module_button_area (module_id, button_area, sort)
) engine=innodb comment = '模块按钮定义表';

-- 模块状态定义表
drop table if exists sys_module_state;
create table sys_module_state (
    id          bigint(20)        not null                 comment '状态ID',
    module_id   bigint(20)        not null                 comment '模块ID',
    state_code  varchar(64)       not null                 comment '状态编码',
    state_name  varchar(128)      not null                 comment '状态名称',
    sort        int(11)           default 1 not null       comment '显示顺序',
    is_initial  char(1)           default '0' not null     comment '是否初始状态（0否 1是）',
    is_final    char(1)           default '0' not null     comment '是否最终状态（0否 1是）',
    status      char(1)           default '0' not null     comment '状态（0正常 1停用）',
    create_by   varchar(32)       default null             comment '创建者',
    create_time datetime          default null             comment '创建时间',
    update_by   varchar(32)       default null             comment '更新者',
    update_time datetime          default null             comment '更新时间',
    note        varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_module_state (module_id, state_code),
    key idx_sys_module_state_sort (module_id, sort)
) engine=innodb comment = '模块状态定义表';

-- 模块状态流转表
drop table if exists sys_module_transition;
create table sys_module_transition (
    id                 bigint(20)        not null                 comment '流转ID',
    module_id          bigint(20)        not null                 comment '模块ID',
    action_button_code varchar(64)       not null                 comment '触发按钮编码',
    from_state_code    varchar(64)       not null                 comment '来源状态编码',
    to_state_code      varchar(64)       not null                 comment '目标状态编码',
    sort               int(11)           default 1 not null       comment '显示顺序',
    status             char(1)           default '0' not null     comment '状态（0正常 1停用）',
    create_by          varchar(32)       default null             comment '创建者',
    create_time        datetime          default null             comment '创建时间',
    update_by          varchar(32)       default null             comment '更新者',
    update_time        datetime          default null             comment '更新时间',
    note               varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_module_transition (module_id, from_state_code, action_button_code),
    key idx_sys_module_transition_state (module_id, from_state_code, status)
) engine=innodb comment = '模块状态流转表';

-- 岗位模块权限表
drop table if exists sys_org_post_module_auth;
create table sys_org_post_module_auth (
    id             bigint(20)        not null                 comment '主键ID',
    org_post_id    bigint(20)        not null                 comment '岗位ID',
    module_id      bigint(20)        not null                 comment '模块ID',
    module_visible char(1)           default '1' not null     comment '模块可见（0否 1是）',
    nav_visible    char(1)           default '1' not null     comment '导航可见（0否 1是）',
    create_by      varchar(32)       default null             comment '创建者',
    create_time    datetime          default null             comment '创建时间',
    update_by      varchar(32)       default null             comment '更新者',
    update_time    datetime          default null             comment '更新时间',
    note           varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_org_post_module_auth (org_post_id, module_id),
    key idx_sys_org_post_module_visible (org_post_id, module_visible)
) engine=innodb comment = '岗位模块权限表';

-- 岗位字段权限表
drop table if exists sys_org_post_field_auth;
create table sys_org_post_field_auth (
    id               bigint(20)        not null                 comment '主键ID',
    org_post_id      bigint(20)        not null                 comment '岗位ID',
    module_id        bigint(20)        not null                 comment '模块ID',
    field_id         bigint(20)        not null                 comment '字段ID',
    permission_level tinyint(4)        default 2 not null       comment '权限级别（0隐藏 1只读 2可写）',
    create_by        varchar(32)       default null             comment '创建者',
    create_time      datetime          default null             comment '创建时间',
    update_by        varchar(32)       default null             comment '更新者',
    update_time      datetime          default null             comment '更新时间',
    note             varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_org_post_field_auth (org_post_id, field_id),
    key idx_sys_org_post_field_module (org_post_id, module_id, permission_level),
    key idx_sys_org_post_field_page (module_id, field_id)
) engine=innodb comment = '岗位字段权限表';

-- 岗位按钮权限表
drop table if exists sys_org_post_button_auth;
create table sys_org_post_button_auth (
    id               bigint(20)        not null                 comment '主键ID',
    org_post_id      bigint(20)        not null                 comment '岗位ID',
    module_id        bigint(20)        not null                 comment '模块ID',
    button_id        bigint(20)        not null                 comment '按钮ID',
    permission_level tinyint(4)        default 2 not null       comment '权限级别（0隐藏 1禁用 2可用）',
    create_by        varchar(32)       default null             comment '创建者',
    create_time      datetime          default null             comment '创建时间',
    update_by        varchar(32)       default null             comment '更新者',
    update_time      datetime          default null             comment '更新时间',
    note             varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_org_post_button_auth (org_post_id, button_id),
    key idx_sys_org_post_button_module (org_post_id, module_id, permission_level)
) engine=innodb comment = '岗位按钮权限表';

-- 状态字段权限表
drop table if exists sys_module_state_field_auth;
create table sys_module_state_field_auth (
    id               bigint(20)        not null                 comment '主键ID',
    module_id        bigint(20)        not null                 comment '模块ID',
    state_code       varchar(64)       not null                 comment '状态编码',
    field_id         bigint(20)        not null                 comment '字段ID',
    permission_level tinyint(4)        default 2 not null       comment '权限级别（0隐藏 1只读 2不收紧）',
    create_by        varchar(32)       default null             comment '创建者',
    create_time      datetime          default null             comment '创建时间',
    update_by        varchar(32)       default null             comment '更新者',
    update_time      datetime          default null             comment '更新时间',
    note             varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_module_state_field_auth (module_id, state_code, field_id),
    key idx_sys_module_state_field_state (module_id, state_code, permission_level)
) engine=innodb comment = '状态字段权限表';

-- 状态按钮权限表
drop table if exists sys_module_state_button_auth;
create table sys_module_state_button_auth (
    id               bigint(20)        not null                 comment '主键ID',
    module_id        bigint(20)        not null                 comment '模块ID',
    state_code       varchar(64)       not null                 comment '状态编码',
    button_id        bigint(20)        not null                 comment '按钮ID',
    permission_level tinyint(4)        default 2 not null       comment '权限级别（0隐藏 1禁用 2不收紧）',
    create_by        varchar(32)       default null             comment '创建者',
    create_time      datetime          default null             comment '创建时间',
    update_by        varchar(32)       default null             comment '更新者',
    update_time      datetime          default null             comment '更新时间',
    note             varchar(500)      default null             comment '备注',
    primary key (id),
    unique key uk_sys_module_state_button_auth (module_id, state_code, button_id),
    key idx_sys_module_state_button_state (module_id, state_code, permission_level)
) engine=innodb comment = '状态按钮权限表';

-- 模块发布日志表
drop table if exists sys_module_publish_log;
create table sys_module_publish_log (
    id             bigint(20)        not null                 comment '日志ID',
    module_id      bigint(20)        not null                 comment '模块ID',
    publish_type   varchar(32)       not null                 comment '发布类型',
    version_before int(11)           default null             comment '发布前版本',
    version_after  int(11)           default null             comment '发布后版本',
    snapshot_json  longtext                                   comment '快照JSON',
    publish_by     varchar(32)       default null             comment '发布人',
    publish_time   datetime          default null             comment '发布时间',
    note           varchar(500)      default null             comment '备注',
    primary key (id),
    key idx_sys_module_publish_log_module (module_id, publish_time)
) engine=innodb comment = '模块发布日志表';
