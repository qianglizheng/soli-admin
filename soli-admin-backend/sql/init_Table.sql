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
