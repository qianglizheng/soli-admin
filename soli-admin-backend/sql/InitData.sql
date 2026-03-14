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
insert into sys_user values
                         (1,'admin','123456','系统管理员','admin@test.com','13800000001',null,'0','0',null,null,'0','system',now(),null,null,null),
                         (2,'manager','123456','部门管理员','manager@test.com','13800000002',null,'1','0',null,null,'0','system',now(),null,null,null),
                         (3,'user01','123456','普通用户1','user01@test.com','13800000003',null,'1','0',null,null,'0','system',now(),null,null,null),
                         (4,'user02','123456','普通用户2','user02@test.com','13800000004',null,'1','1',null,null,'0','system',now(),null,null,null);

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
insert into sys_role values
(1,'超级管理员','admin',1,'1','0','system',now(),null,null,null),
(2,'系统管理员','manager',2,'1','0','system',now(),null,null,null),
(3,'普通用户','user',3,'1','0','system',now(),null,null,null);

-- 系统用户角色关联表
drop table if exists sys_user_role;
create table sys_user_role (
    user_id        bigint(20)        not null                 comment '用户ID',
    role_id        bigint(20)        not null                 comment '角色ID',
    primary key(user_id, role_id)
) engine=innodb comment = '系统用户角色关联表';
insert into sys_user_role values
(1,1),
(2,2),
(3,3),
(4,3);

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
insert into sys_menu values
(1000,'系统管理',0,1,'/system',null,'0',null,'system','0','system',now(),null,null,null),

(1001,'用户管理',1000,1,'user','system/user/index','1',null,'user','0','system',now(),null,null,null),
(1002,'角色管理',1000,2,'role','system/role/index','1',null,'peoples','0','system',now(),null,null,null),
(1003,'菜单管理',1000,3,'menu','system/menu/index','1',null,'tree','0','system',now(),null,null,null),

(1101,'用户查询',1001,1,null,null,'2','sys:user:list',null,'0','system',now(),null,null,null),
(1102,'用户新增',1001,2,null,null,'2','sys:user:add',null,'0','system',now(),null,null,null),
(1103,'用户修改',1001,3,null,null,'2','sys:user:update',null,'0','system',now(),null,null,null),
(1104,'用户删除',1001,4,null,null,'2','sys:user:delete',null,'0','system',now(),null,null,null),

(1201,'角色查询',1002,1,null,null,'2','sys:role:list',null,'0','system',now(),null,null,null),
(1202,'角色新增',1002,2,null,null,'2','sys:role:add',null,'0','system',now(),null,null,null),
(1203,'角色修改',1002,3,null,null,'2','sys:role:update',null,'0','system',now(),null,null,null),
(1204,'角色删除',1002,4,null,null,'2','sys:role:delete',null,'0','system',now(),null,null,null),

(1301,'菜单查询',1003,1,null,null,'2','sys:menu:list',null,'0','system',now(),null,null,null),
(1302,'菜单新增',1003,2,null,null,'2','sys:menu:add',null,'0','system',now(),null,null,null),
(1303,'菜单修改',1003,3,null,null,'2','sys:menu:update',null,'0','system',now(),null,null,null),
(1304,'菜单删除',1003,4,null,null,'2','sys:menu:delete',null,'0','system',now(),null,null,null);

drop table if exists sys_role_menu;
create table sys_role_menu (
  role_id   bigint(20) not null comment '角色ID',
  menu_id   bigint(20) not null comment '菜单ID',
  primary key(role_id, menu_id)
) engine=innodb comment = '角色和菜单关联表';
insert into sys_role_menu
select 1,id from sys_menu;
insert into sys_role_menu values
(2,1000),
(2,1001),
(2,1002),
(2,1101),
(2,1102),
(2,1103),
(2,1201),
(2,1203);
insert into sys_role_menu values
(3,1000),
(3,1001),
(3,1101);