drop table if exists trace_method;

/*==============================================================*/
/* Table: trace_method                                          */
/*==============================================================*/
create table trace_method
(
   id                   BIGINT not null comment '序号，主键',
   class_name           VARCHAR(150) comment '全类名',
   method_name          VARCHAR(50) comment '方法名',
   full_name            VARCHAR(400) comment '全类名.方法名(参数列表)',
   super_class_name     VARCHAR(50) comment '父类类名',
   interfaces           VARCHAR(100) comment '实现的接口，多个用逗号隔开',
   parameter_types      VARCHAR(200) comment '参数类型，多个用逗号隔开',
   modifier             VARCHAR(50) comment '方法修饰符',
   return_type          VARCHAR(150) comment '返回类型',
   request_url          VARCHAR(150) comment '请求URL，针对Controller方法而言',
   parent_id            BIGINT comment '父ID',
   create_date          DATETIME comment '创建时间',
   start_timestamp      BIGINT comment '开始时间戳',
   end_timestamp        BIGINT comment '结束时间戳',
   identify             BIGINT comment '唯一性ID',
   primary key (id)
);

alter table trace_method comment '调用方法信息';
