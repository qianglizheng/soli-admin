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
    type           char(1)           default '1' not null                 comment '用户类型（0超管-拥有所有权限 1普通用户）',
    sex            char(1)           default '0' not null     comment '用户性别（0男 1女）',
    status         char(1)           default '0' not null     comment '用户状态（0正常 1停用）',
    login_ip       varchar(128)      default null             comment '最后登录ip',
    login_time     datetime          default null             comment '最后登录时间',
    create_by      varchar(32)       default null             comment '创建者',
    create_time    datetime          default null             comment '创建时间',
    update_by      varchar(32)       default null             comment '更新者',
    update_time    datetime          default null             comment '更新时间',
    note           varchar(500)      default null             comment '备注',
    primary key (id)
) engine=innodb comment '系统用户表';
