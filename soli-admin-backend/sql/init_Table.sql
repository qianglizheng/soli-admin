-- 系统用户表
drop table if exists sys_user;
create table sys_user (
    id             bigint(20)        not null                 comment '用户id',
    username       varchar(32)       not null                 comment '用户名',
    password       varchar(100)      not null                 comment '用户密码',
    nickname       varchar(32)       default null             comment '用户昵称',
    email          varchar(50)       default null             comment '用户邮箱',
    phone          varchar(11)       default null             comment '用户手机',
    avatar         varchar(100)      default null             comment '用户头像',
    type           char(1)           default '1' not null     comment '用户类型（0超管-拥有所有权限 1普通用户）',
    sex            char(1)           default '0' not null     comment '用户性别（0男 1女）',
    login_ip       varchar(128)      default null             comment '最后登录ip',
    login_time     datetime          default null             comment '最后登录时间',
    status         char(1)           default '0' not null     comment '用户状态（0正常 1停用）',
    create_by      varchar(32)       default null             comment '创建者',
    create_time    datetime          default null             comment '创建时间',
    update_by      varchar(32)       default null             comment '更新者',
    update_time    datetime          default null             comment '更新时间',
    note           varchar(500)      default null             comment '备注',
    primary key (id)
) engine=innodb comment '系统用户表';

-- 系统用户角色表
drop table if exists sys_role;
create table sys_role (
    id             bigint(20)        not null                 comment '角色ID',
    name           varchar(30)       not null                 comment '角色名称',
    code           varchar(100)      not null                 comment '角色权限字符串',
    sort           int(4)            default 1 not null       comment '显示顺序',
    data_scope     char(1)           default '1' not null     comment '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
    status         char(1)           default 0 not null       comment '角色状态（0正常 1停用）',
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
    primary key(user_id, role_id)
) engine=innodb comment = '系统用户角色关联表';

drop table if exists sys_menu;
create table sys_menu (
  id             bigint(20)        not null                 comment '菜单ID',
  name           varchar(50)       not null                 comment '菜单名称',
  parent_id      bigint(20)        default 0                comment '父菜单ID',
  sort           int(4)            default 0                comment '显示顺序',
  path           varchar(200)      default ''               comment '路由地址',
  component      varchar(255)      default null             comment '组件路径',
  type           char(1)           default ''               comment '菜单类型（0目录 1菜单 2按钮）',
  perms          varchar(100)      default null             comment '权限标识',
  icon           varchar(100)      default '#'              comment '菜单图标',
  status         char(1)           default 0 not null       comment '菜单状态（0正常 1停用）',
  create_by      varchar(32)       default null             comment '创建者',
  create_time    datetime          default null             comment '创建时间',
  update_by      varchar(32)       default null             comment '更新者',
  update_time    datetime          default null             comment '更新时间',
  note           varchar(500)      default null             comment '备注',
  primary key (id)
) engine=innodb auto_increment=2000 comment = '菜单权限表';

drop table if exists sys_role_menu;
create table sys_role_menu (
  role_id   bigint(20) not null comment '角色ID',
  menu_id   bigint(20) not null comment '菜单ID',
  primary key(role_id, menu_id)
) engine=innodb comment = '角色和菜单关联表';