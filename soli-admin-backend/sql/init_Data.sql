-- 角色初始化
insert into sys_role values (1,'超级管理员','admin',1,'1','0','system',now(),null,null,null);

-- 用户初始化
insert into sys_user values (1,'admin','123456','超级管理员','superadmin@test.com','13800000001',null,'0','0',null,null,'0','system',now(),null,null,null);

-- 用户角色关联
insert into sys_user_role values (1,1);

-- 菜单初始化
insert into sys_menu values (1000,'系统管理',0,1,'system',null,'0',null,'Tools','0','system',now(),null,null,null);

insert into sys_menu values (1100,'用户管理',1000,1,'user','system/user/index','1',null,'User','0','system',now(),null,null,null);
insert into sys_menu values (1101,'用户新增',1100,1,null,null,'2','sys:user:create',null,'0','system',now(),null,null,null);
insert into sys_menu values (1102,'用户删除',1100,2,null,null,'2','sys:user:remove',null,'0','system',now(),null,null,null);
insert into sys_menu values (1103,'用户修改',1100,3,null,null,'2','sys:user:modify',null,'0','system',now(),null,null,null);
insert into sys_menu values (1104,'用户分页',1100,4,null,null,'2','sys:user:page',null,'0','system',now(),null,null,null);

insert into sys_menu values (1200,'角色管理',1000,2,'role','system/role/index','1',null,'Avatar','0','system',now(),null,null,null);
insert into sys_menu values (1201,'角色新增',1200,1,null,null,'2','sys:role:create',null,'0','system',now(),null,null,null);
insert into sys_menu values (1202,'角色删除',1200,2,null,null,'2','sys:role:remove',null,'0','system',now(),null,null,null);
insert into sys_menu values (1203,'角色修改',1200,3,null,null,'2','sys:role:modify',null,'0','system',now(),null,null,null);
insert into sys_menu values (1204,'角色分页',1200,4,null,null,'2','sys:role:page',null,'0','system',now(),null,null,null);

insert into sys_menu values (1300,'菜单管理',1000,3,'menu','system/menu/index','1',null,'Menu','0','system',now(),null,null,null);
insert into sys_menu values (1301,'菜单新增',1300,1,null,null,'2','sys:menu:create',null,'0','system',now(),null,null,null);
insert into sys_menu values (1302,'菜单删除',1300,2,null,null,'2','sys:menu:remove',null,'0','system',now(),null,null,null);
insert into sys_menu values (1303,'菜单修改',1300,3,null,null,'2','sys:menu:modify',null,'0','system',now(),null,null,null);
insert into sys_menu values (1304,'菜单分页',1300,4,null,null,'2','sys:menu:page',null,'0','system',now(),null,null,null);

insert into sys_menu values (1400,'字典管理',1000,4,'dict','system/dict/index','1',null,'Reading','0','system',now(),null,null,null);
insert into sys_menu values (1401,'字典新增',1400,1,null,null,'2','sys:dict:create',null,'0','system',now(),null,null,null);
insert into sys_menu values (1402,'字典删除',1400,2,null,null,'2','sys:dict:remove',null,'0','system',now(),null,null,null);
insert into sys_menu values (1403,'字典修改',1400,3,null,null,'2','sys:dict:modify',null,'0','system',now(),null,null,null);
insert into sys_menu values (1404,'字典分页',1400,4,null,null,'2','sys:dict:page',null,'0','system',now(),null,null,null);

insert into sys_menu values (1500,'参数设置',1000,5,'config','system/config/index','1',null,'Tools','0','system',now(),null,null,null);
insert into sys_menu values (1501,'参数新增',1500,1,null,null,'2','sys:config:create',null,'0','system',now(),null,null,null);
insert into sys_menu values (1502,'参数删除',1500,2,null,null,'2','sys:config:remove',null,'0','system',now(),null,null,null);
insert into sys_menu values (1503,'参数修改',1500,3,null,null,'2','sys:config:modify',null,'0','system',now(),null,null,null);
insert into sys_menu values (1504,'参数分页',1500,4,null,null,'2','sys:config:page',null,'0','system',now(),null,null,null);

insert into sys_menu values (1600,'日志管理',1000,6,'log',null,'0',null,'Monitor','0','system',now(),null,null,null);
insert into sys_menu values (1610,'操作日志',1600,1,'operlog','system/monitor/operlog/index','1',null,'Document','0','system',now(),null,null,null);
insert into sys_menu values (1611,'操作日志新增',1610,1,null,null,'2','sys:operlog:create',null,'0','system',now(),null,null,null);
insert into sys_menu values (1612,'操作日志删除',1610,2,null,null,'2','sys:operlog:remove',null,'0','system',now(),null,null,null);
insert into sys_menu values (1613,'操作日志修改',1610,3,null,null,'2','sys:operlog:modify',null,'0','system',now(),null,null,null);
insert into sys_menu values (1614,'操作日志分页',1610,4,null,null,'2','sys:operlog:page',null,'0','system',now(),null,null,null);

insert into sys_menu values (1620,'登录日志',1600,2,'logininfor','system/monitor/logininfor/index','1',null,'Key','0','system',now(),null,null,null);
insert into sys_menu values (1621,'登录日志新增',1620,1,null,null,'2','sys:logininfor:create',null,'0','system',now(),null,null,null);
insert into sys_menu values (1622,'登录日志删除',1620,2,null,null,'2','sys:logininfor:remove',null,'0','system',now(),null,null,null);
insert into sys_menu values (1623,'登录日志修改',1620,3,null,null,'2','sys:logininfor:modify',null,'0','system',now(),null,null,null);
insert into sys_menu values (1624,'登录日志分页',1620,4,null,null,'2','sys:logininfor:page',null,'0','system',now(),null,null,null);

-- 超级管理员拥有全部菜单权限
insert into sys_role_menu values (1,1000);
insert into sys_role_menu values (1,1100);
insert into sys_role_menu values (1,1101);
insert into sys_role_menu values (1,1102);
insert into sys_role_menu values (1,1103);
insert into sys_role_menu values (1,1104);
insert into sys_role_menu values (1,1200);
insert into sys_role_menu values (1,1201);
insert into sys_role_menu values (1,1202);
insert into sys_role_menu values (1,1203);
insert into sys_role_menu values (1,1204);
insert into sys_role_menu values (1,1300);
insert into sys_role_menu values (1,1301);
insert into sys_role_menu values (1,1302);
insert into sys_role_menu values (1,1303);
insert into sys_role_menu values (1,1304);
insert into sys_role_menu values (1,1400);
insert into sys_role_menu values (1,1401);
insert into sys_role_menu values (1,1402);
insert into sys_role_menu values (1,1403);
insert into sys_role_menu values (1,1404);
insert into sys_role_menu values (1,1500);
insert into sys_role_menu values (1,1501);
insert into sys_role_menu values (1,1502);
insert into sys_role_menu values (1,1503);
insert into sys_role_menu values (1,1504);
insert into sys_role_menu values (1,1600);
insert into sys_role_menu values (1,1610);
insert into sys_role_menu values (1,1611);
insert into sys_role_menu values (1,1612);
insert into sys_role_menu values (1,1613);
insert into sys_role_menu values (1,1614);
insert into sys_role_menu values (1,1620);
insert into sys_role_menu values (1,1621);
insert into sys_role_menu values (1,1622);
insert into sys_role_menu values (1,1623);
insert into sys_role_menu values (1,1624);
