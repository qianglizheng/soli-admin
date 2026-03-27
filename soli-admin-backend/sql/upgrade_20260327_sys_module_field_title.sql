-- 2026-03-27 字段标题表独立运行时字段模型升级脚本

alter table sys_module_field_title
    add column module_id bigint(20) default null comment '模块ID' after field_id,
    add column tab_id bigint(20) default null comment 'Tab ID' after module_id,
    add column field_scope varchar(32) default null comment '字段作用域' after tab_id,
    add column field_code varchar(64) default null comment '字段编码' after field_scope,
    add column default_title varchar(128) default null comment '默认标题' after field_code,
    add column component_type varchar(64) default null comment '组件类型' after help_text,
    add column data_path varchar(255) default null comment '数据路径' after component_type,
    add column value_type varchar(32) default null comment '值类型' after data_path,
    add column required_flag char(1) default null comment '必填标识' after value_type,
    add column sort int(11) default null comment '排序' after required_flag;

update sys_module_field_title t
inner join sys_module_field f on f.id = t.field_id
set t.module_id = f.module_id,
    t.tab_id = f.tab_id,
    t.field_scope = f.field_scope,
    t.field_code = f.field_code,
    t.default_title = f.default_title,
    t.component_type = f.component_type,
    t.data_path = f.data_path,
    t.value_type = f.value_type,
    t.required_flag = f.required_flag,
    t.sort = f.sort,
    t.status = f.status,
    t.note = f.note;

insert into sys_module_field_title (
    id, field_id, module_id, tab_id, field_scope, field_code, default_title, locale,
    display_title, placeholder, help_text, component_type, data_path, value_type, required_flag, sort, status,
    create_by, create_time, update_by, update_time, note
)
select f.id + 1000,
       f.id,
       f.module_id,
       f.tab_id,
       f.field_scope,
       f.field_code,
       f.default_title,
       'zh_CN',
       null,
       null,
       null,
       f.component_type,
       f.data_path,
       f.value_type,
       f.required_flag,
       f.sort,
       f.status,
       ifnull(f.create_by, 'system'),
       ifnull(f.create_time, now()),
       f.update_by,
       f.update_time,
       f.note
from sys_module_field f
where not exists (
    select 1
    from sys_module_field_title t
    where t.field_id = f.id
      and t.locale = 'zh_CN'
);

update sys_module_field_title
set required_flag = '0'
where required_flag is null;

update sys_module_field_title
set sort = 1
where sort is null;

update sys_module_field_title
set status = '0'
where status is null;

alter table sys_module_field_title
    modify column module_id bigint(20) not null comment '模块ID',
    modify column tab_id bigint(20) not null comment 'Tab ID',
    modify column field_scope varchar(32) not null comment '字段作用域',
    modify column field_code varchar(64) not null comment '字段编码',
    modify column default_title varchar(128) not null comment '默认标题',
    modify column component_type varchar(64) not null comment '组件类型',
    modify column data_path varchar(255) not null comment '数据路径',
    modify column value_type varchar(32) not null comment '值类型',
    modify column required_flag char(1) default '0' not null comment '必填标识',
    modify column sort int(11) default 1 not null comment '排序';

create index idx_sys_module_field_title_module on sys_module_field_title (module_id, locale);
