alter table s_data_rains rename column raint_rate to rain_rate;
alter table s_data_temperatures rename column heatIndex to heat_index;
alter table s_data_temperatures rename column thwIndex to thw_index;

----

create table s_user_groups (
    id                        bigint not null,
    user_id                   bigint,
    name                      varchar(100) not null,
    description               varchar(500),
    constraint pk_s_user_groups primary key (id)
);

create table s_user_sensors (
    id                        bigint not null,
    sensor_id                 bigint,
    user_id                   bigint,
    sensor_class              varchar(300),
    table_name                varchar(50),
    table_column              varchar(50),
    label                     varchar(100) not null,
    description               varchar(500),
    constraint pk_s_user_sensors primary key (id)
);

create table s_user_sensor_groups (
    id                        bigint not null,
    user_sensor_id            bigint,
    user_group_id             bigint,
    constraint pk_s_user_sensor_groups primary key (id)
);


create sequence s_user_groups_seq;
alter table s_user_groups add constraint fk_s_user_groups_user_1 foreign key (user_id) references c_users (id);
create index ix_s_user_groups_user_1 on s_user_groups (user_id);
alter table s_user_sensor_groups add constraint fk_s_user_sensor_groups_userGr_5 foreign key (user_group_id) references s_user_groups (id);

create sequence s_user_sensors_seq;
alter table s_user_sensors add constraint fk_s_user_sensors_sensor_2 foreign key (sensor_id) references s_sensors (id);
create index ix_s_user_sensors_sensor_2 on s_user_sensors (sensor_id);
alter table s_user_sensors add constraint fk_s_user_sensors_user_3 foreign key (user_id) references c_users (id);
create index ix_s_user_sensors_user_3 on s_user_sensors (user_id);
alter table s_user_sensor_groups add constraint fk_s_user_sensor_groups_userSe_4 foreign key (user_sensor_id) references s_user_sensors (id);

create sequence s_user_sensor_groups_seq;
create index ix_s_user_sensor_groups_userSe_4 on s_user_sensor_groups (user_sensor_id);
create index ix_s_user_sensor_groups_userGr_5 on s_user_sensor_groups (user_group_id);
alter table s_user_sensor_groups add constraint fk_s_user_sensor_groups_userSe_4 foreign key (user_sensor_id) references s_user_sensors (id);
alter table s_user_sensor_groups add constraint fk_s_user_sensor_groups_userGr_5 foreign key (user_group_id) references s_user_groups (id);

--
-- SMS Q
--
create table c_sms_q (
    id                        bigint not null,
    user_id                   bigint,
    date_created              timestamp,
    date_send                 timestamp,
    date_confirm              timestamp,
    action                    varchar(8),
    mobile_number             varchar(20) not null,
    content                   varchar(600) not null,
    constraint ck_c_sms_q_action check (action in ('prepare','send','sending','sent','received','not_sent')),
    constraint pk_c_sms_q primary key (id)
);

create sequence c_sms_q_seq;

create table s_user_sms_cron (
    id                        bigint not null,
    user_id                   bigint,
    active                    boolean not null,
    mobile_number             varchar(65),
    scedule_rule              varchar(255) ,
    next_scedule              timestamp ,
    last_scedule              timestamp ,
    repeated                  integer not null,
    constraint pk_s_user_sms_cron primary key (id))
;


create table s_user_sms_cron_sensors (
id                        bigint not null,
sms_cron_id               bigint,
user_sensor_id            bigint,
constraint pk_s_user_sms_cron_sensors primary key (id))
;

create sequence s_user_sms_cron_seq;

create sequence s_user_sms_cron_sensors_seq;

alter table s_user_sms_cron add constraint fk_s_user_sms_cron_user_6 foreign key (user_id) references c_users (id);
create index ix_s_user_sms_cron_user_6 on s_user_sms_cron (user_id);
alter table s_user_sms_cron_sensors add constraint fk_s_user_sms_cron_sensors_sms_7 foreign key (sms_cron_id) references s_user_sms_cron (id);
create index ix_s_user_sms_cron_sensors_sms_7 on s_user_sms_cron_sensors (sms_cron_id);
alter table s_user_sms_cron_sensors add constraint fk_s_user_sms_cron_sensors_sen_8 foreign key (user_sensor_id) references s_user_sensors (id);
create index ix_s_user_sms_cron_sensors_sen_8 on s_user_sms_cron_sensors (user_sensor_id);

alter table c_sms_q add constraint fk_c_sms_q_user_1 foreign key (user_id) references c_users (id);
create index ix_c_sms_q_user_1 on c_sms_q (user_id);


