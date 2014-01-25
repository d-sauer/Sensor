# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table c_sessions (
  id                        bigint not null,
  uuid                      varchar(100) not null,
  ip_address                varchar(40),
  created                   timestamp,
  expired                   timestamp,
  session_data              text,
  constraint uq_c_sessions_uuid unique (uuid),
  constraint pk_c_sessions primary key (id))
;

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
  constraint pk_c_sms_q primary key (id))
;

create table c_users (
  id                        bigint not null,
  email                     varchar(100) not null,
  user_name                 varchar(100) not null,
  password                  varchar(50),
  first_name                varchar(50),
  last_name                 varchar(50),
  phone_land                varchar(20),
  phone_mobile              varchar(20),
  city                      varchar(50),
  post_number               varchar(10),
  street                    varchar(100),
  active                    integer default 0,
  constraint uq_c_users_email unique (email),
  constraint uq_c_users_user_name unique (user_name),
  constraint pk_c_users primary key (id))
;

create table s_user_groups (
  id                        bigint not null,
  user_id                   bigint,
  name                      varchar(100) not null,
  description               varchar(500),
  constraint pk_s_user_groups primary key (id))
;

create table s_user_sensors (
  id                        bigint not null,
  sensor_id                 bigint,
  user_id                   bigint,
  sensor_class              varchar(300),
  table_name                varchar(50),
  table_column              varchar(50),
  label                     varchar(100) not null,
  description               varchar(500),
  constraint pk_s_user_sensors primary key (id))
;

create table s_user_sensor_groups (
  id                        bigint not null,
  user_group_id             bigint,
  user_sensor_id            bigint,
  constraint pk_s_user_sensor_groups primary key (id))
;

create table s_user_sms_cron (
  id                        bigint not null,
  user_id                   bigint,
  active                    boolean not null,
  mobile_number             varchar(65),
  scedule_rule              varchar(255),
  next_scedule              timestamp,
  last_scedule              timestamp,
  repeated                  integer not null,
  constraint pk_s_user_sms_cron primary key (id))
;

create table s_user_sms_cron_sensors (
  id                        bigint not null,
  sms_cron_id               bigint,
  user_sensor_id            bigint,
  constraint pk_s_user_sms_cron_sensors primary key (id))
;

create table s_sensors (
  id                        bigint not null,
  group_id                  bigint,
  user_id                   bigint,
  name                      varchar(100) not null,
  description               varchar(400),
  last_update               timestamp,
  active                    boolean,
  visible                   boolean,
  sensor_class              varchar(300),
  constraint pk_s_sensors primary key (id))
;

create table s_sensor_raw_data (
  id                        bigint not null,
  group_id                  bigint,
  when_created              timestamp not null,
  date_time                 timestamp not null,
  data                      text,
  constraint pk_s_sensor_raw_data primary key (id))
;

create table s_data_customs (
  id                        bigint not null,
  sensor_id                 bigint,
  sensor_data_id            bigint,
  date_time                 timestamp not null,
  data_type                 varchar(9),
  cool                      float,
  constraint ck_s_data_customs_data_type check (data_type in ('issRecept','arcInt')),
  constraint pk_s_data_customs primary key (id))
;

create table s_data_degree_days (
  id                        bigint not null,
  sensor_id                 bigint,
  sensor_data_id            bigint,
  date_time                 timestamp not null,
  heat                      float,
  cool                      float,
  constraint pk_s_data_degree_days primary key (id))
;

create table s_data_densitys (
  id                        bigint not null,
  sensor_id                 bigint,
  sensor_data_id            bigint,
  date_time                 timestamp not null,
  density                   float,
  constraint pk_s_data_densitys primary key (id))
;

create table s_data_emc (
  id                        bigint not null,
  sensor_id                 bigint,
  sensor_data_id            bigint,
  date_time                 timestamp not null,
  emc                       float,
  constraint pk_s_data_emc primary key (id))
;

create table s_data_humiditys (
  id                        bigint not null,
  sensor_id                 bigint,
  sensor_data_id            bigint,
  date_time                 timestamp not null,
  humidity                  float,
  absolute_humidity         float,
  relative_humidity         float,
  specific_humidity         float,
  constraint pk_s_data_humiditys primary key (id))
;

create table s_data_moistures (
  id                        bigint not null,
  sensor_id                 bigint,
  sensor_data_id            bigint,
  date_time                 timestamp not null,
  moisture                  float,
  constraint pk_s_data_moistures primary key (id))
;

create table s_data_pressures (
  id                        bigint not null,
  sensor_id                 bigint,
  sensor_data_id            bigint,
  date_time                 timestamp not null,
  pressure_pascal           float,
  pressure_bar              float,
  constraint pk_s_data_pressures primary key (id))
;

create table s_data_rains (
  id                        bigint not null,
  sensor_id                 bigint,
  sensor_data_id            bigint,
  date_time                 timestamp not null,
  rain                      float,
  rain_rate                 float,
  constraint pk_s_data_rains primary key (id))
;

create table s_data_temperatures (
  id                        bigint not null,
  sensor_id                 bigint,
  sensor_data_id            bigint,
  date_time                 timestamp not null,
  temperature               float,
  heat                      float,
  heat_index                float,
  thw_index                 float,
  constraint pk_s_data_temperatures primary key (id))
;

create table s_data_temperatures_high_low (
  id                        bigint not null,
  sensor_id                 bigint,
  sensor_data_id            bigint,
  date_time                 timestamp not null,
  heat_high                 float,
  heat_low                  float,
  temperature_high          float,
  temperature_low           float,
  constraint pk_s_data_temperatures_high_low primary key (id))
;

create table s_data_wetness (
  id                        bigint not null,
  sensor_id                 bigint,
  sensor_data_id            bigint,
  date_time                 timestamp not null,
  wetness                   float,
  dry                       float,
  dew_point                 float,
  precipitation             float,
  constraint pk_s_data_wetness primary key (id))
;

create table s_data_winds (
  id                        bigint not null,
  sensor_id                 bigint,
  sensor_data_id            bigint,
  date_time                 timestamp not null,
  wind_speed                float,
  wind_run                  float,
  direction                 varchar(255),
  direction_degree          integer,
  wind_chill                float,
  wind_sample               float,
  wind_tx                   float,
  constraint pk_s_data_winds primary key (id))
;

create table s_data_winds_high_low (
  id                        bigint not null,
  sensor_id                 bigint,
  sensor_data_id            bigint,
  date_time                 timestamp not null,
  high_speed                float,
  high_speed_direction      varchar(255),
  high_speed_direction_degree integer,
  low_speed                 float,
  low_speed_direction       varchar(255),
  low_speed_direction_degree integer,
  constraint pk_s_data_winds_high_low primary key (id))
;

create table s_sensor_groups (
  id                        bigint not null,
  user_id                   bigint,
  name                      varchar(100) not null,
  description               varchar(400),
  active                    boolean,
  visible                   boolean,
  sensor_group_class        varchar(300),
  constraint pk_s_sensor_groups primary key (id))
;

create table s_sensor_group_properties (
  id                        bigint not null,
  group_id                  bigint,
  property                  varchar(70) not null,
  value                     varchar(400),
  user_label                varchar(255),
  user_description          varchar(500),
  db_table                  varchar(255),
  db_column                 varchar(255),
  updated                   timestamp not null,
  constraint pk_s_sensor_group_properties primary key (id))
;

create table s_sensor_properties (
  id                        bigint not null,
  sensor_id                 bigint,
  property                  varchar(70) not null,
  value                     varchar(400),
  user_label                varchar(255),
  user_description          varchar(500),
  db_table                  varchar(255),
  db_column                 varchar(255),
  updated                   timestamp not null,
  constraint pk_s_sensor_properties primary key (id))
;

create sequence c_sessions_seq;

create sequence c_sms_q_seq;

create sequence c_users_seq;

create sequence s_user_groups_seq;

create sequence s_user_sensors_seq;

create sequence s_user_sensor_groups_seq;

create sequence s_user_sms_cron_seq;

create sequence s_user_sms_cron_sensors_seq;

create sequence s_sensors_seq;

create sequence s_sensor_raw_data_seq;

create sequence s_data_customs_seq;

create sequence s_data_degree_days_seq;

create sequence s_data_densitys_seq;

create sequence s_data_emc_seq;

create sequence s_data_humiditys_seq;

create sequence s_data_moistures_seq;

create sequence s_data_pressures_seq;

create sequence s_data_rains_seq;

create sequence s_data_temperatures_seq;

create sequence s_data_temperatures_high_low_seq;

create sequence s_data_wetness_seq;

create sequence s_data_winds_seq;

create sequence s_data_winds_high_low_seq;

create sequence s_sensor_groups_seq;

create sequence s_sensor_group_properties_seq;

create sequence s_sensor_properties_seq;

alter table c_sms_q add constraint fk_c_sms_q_user_1 foreign key (user_id) references c_users (id);
create index ix_c_sms_q_user_1 on c_sms_q (user_id);
alter table s_user_groups add constraint fk_s_user_groups_user_2 foreign key (user_id) references c_users (id);
create index ix_s_user_groups_user_2 on s_user_groups (user_id);
alter table s_user_sensors add constraint fk_s_user_sensors_sensor_3 foreign key (sensor_id) references s_sensors (id);
create index ix_s_user_sensors_sensor_3 on s_user_sensors (sensor_id);
alter table s_user_sensors add constraint fk_s_user_sensors_user_4 foreign key (user_id) references c_users (id);
create index ix_s_user_sensors_user_4 on s_user_sensors (user_id);
alter table s_user_sensor_groups add constraint fk_s_user_sensor_groups_userGr_5 foreign key (user_group_id) references s_user_groups (id);
create index ix_s_user_sensor_groups_userGr_5 on s_user_sensor_groups (user_group_id);
alter table s_user_sensor_groups add constraint fk_s_user_sensor_groups_userSe_6 foreign key (user_sensor_id) references s_user_sensors (id);
create index ix_s_user_sensor_groups_userSe_6 on s_user_sensor_groups (user_sensor_id);
alter table s_user_sms_cron add constraint fk_s_user_sms_cron_user_7 foreign key (user_id) references c_users (id);
create index ix_s_user_sms_cron_user_7 on s_user_sms_cron (user_id);
alter table s_user_sms_cron_sensors add constraint fk_s_user_sms_cron_sensors_sms_8 foreign key (sms_cron_id) references s_user_sms_cron (id);
create index ix_s_user_sms_cron_sensors_sms_8 on s_user_sms_cron_sensors (sms_cron_id);
alter table s_user_sms_cron_sensors add constraint fk_s_user_sms_cron_sensors_use_9 foreign key (user_sensor_id) references s_user_sensors (id);
create index ix_s_user_sms_cron_sensors_use_9 on s_user_sms_cron_sensors (user_sensor_id);
alter table s_sensors add constraint fk_s_sensors_group_10 foreign key (group_id) references s_sensor_groups (id);
create index ix_s_sensors_group_10 on s_sensors (group_id);
alter table s_sensors add constraint fk_s_sensors_user_11 foreign key (user_id) references c_users (id);
create index ix_s_sensors_user_11 on s_sensors (user_id);
alter table s_sensor_raw_data add constraint fk_s_sensor_raw_data_group_12 foreign key (group_id) references s_sensor_groups (id);
create index ix_s_sensor_raw_data_group_12 on s_sensor_raw_data (group_id);
alter table s_data_customs add constraint fk_s_data_customs_sensor_13 foreign key (sensor_id) references s_sensors (id);
create index ix_s_data_customs_sensor_13 on s_data_customs (sensor_id);
alter table s_data_customs add constraint fk_s_data_customs_sensorData_14 foreign key (sensor_data_id) references s_sensor_raw_data (id);
create index ix_s_data_customs_sensorData_14 on s_data_customs (sensor_data_id);
alter table s_data_degree_days add constraint fk_s_data_degree_days_sensor_15 foreign key (sensor_id) references s_sensors (id);
create index ix_s_data_degree_days_sensor_15 on s_data_degree_days (sensor_id);
alter table s_data_degree_days add constraint fk_s_data_degree_days_sensorD_16 foreign key (sensor_data_id) references s_sensor_raw_data (id);
create index ix_s_data_degree_days_sensorD_16 on s_data_degree_days (sensor_data_id);
alter table s_data_densitys add constraint fk_s_data_densitys_sensor_17 foreign key (sensor_id) references s_sensors (id);
create index ix_s_data_densitys_sensor_17 on s_data_densitys (sensor_id);
alter table s_data_densitys add constraint fk_s_data_densitys_sensorData_18 foreign key (sensor_data_id) references s_sensor_raw_data (id);
create index ix_s_data_densitys_sensorData_18 on s_data_densitys (sensor_data_id);
alter table s_data_emc add constraint fk_s_data_emc_sensor_19 foreign key (sensor_id) references s_sensors (id);
create index ix_s_data_emc_sensor_19 on s_data_emc (sensor_id);
alter table s_data_emc add constraint fk_s_data_emc_sensorData_20 foreign key (sensor_data_id) references s_sensor_raw_data (id);
create index ix_s_data_emc_sensorData_20 on s_data_emc (sensor_data_id);
alter table s_data_humiditys add constraint fk_s_data_humiditys_sensor_21 foreign key (sensor_id) references s_sensors (id);
create index ix_s_data_humiditys_sensor_21 on s_data_humiditys (sensor_id);
alter table s_data_humiditys add constraint fk_s_data_humiditys_sensorDat_22 foreign key (sensor_data_id) references s_sensor_raw_data (id);
create index ix_s_data_humiditys_sensorDat_22 on s_data_humiditys (sensor_data_id);
alter table s_data_moistures add constraint fk_s_data_moistures_sensor_23 foreign key (sensor_id) references s_sensors (id);
create index ix_s_data_moistures_sensor_23 on s_data_moistures (sensor_id);
alter table s_data_moistures add constraint fk_s_data_moistures_sensorDat_24 foreign key (sensor_data_id) references s_sensor_raw_data (id);
create index ix_s_data_moistures_sensorDat_24 on s_data_moistures (sensor_data_id);
alter table s_data_pressures add constraint fk_s_data_pressures_sensor_25 foreign key (sensor_id) references s_sensors (id);
create index ix_s_data_pressures_sensor_25 on s_data_pressures (sensor_id);
alter table s_data_pressures add constraint fk_s_data_pressures_sensorDat_26 foreign key (sensor_data_id) references s_sensor_raw_data (id);
create index ix_s_data_pressures_sensorDat_26 on s_data_pressures (sensor_data_id);
alter table s_data_rains add constraint fk_s_data_rains_sensor_27 foreign key (sensor_id) references s_sensors (id);
create index ix_s_data_rains_sensor_27 on s_data_rains (sensor_id);
alter table s_data_rains add constraint fk_s_data_rains_sensorData_28 foreign key (sensor_data_id) references s_sensor_raw_data (id);
create index ix_s_data_rains_sensorData_28 on s_data_rains (sensor_data_id);
alter table s_data_temperatures add constraint fk_s_data_temperatures_sensor_29 foreign key (sensor_id) references s_sensors (id);
create index ix_s_data_temperatures_sensor_29 on s_data_temperatures (sensor_id);
alter table s_data_temperatures add constraint fk_s_data_temperatures_sensor_30 foreign key (sensor_data_id) references s_sensor_raw_data (id);
create index ix_s_data_temperatures_sensor_30 on s_data_temperatures (sensor_data_id);
alter table s_data_temperatures_high_low add constraint fk_s_data_temperatures_high_l_31 foreign key (sensor_id) references s_sensors (id);
create index ix_s_data_temperatures_high_l_31 on s_data_temperatures_high_low (sensor_id);
alter table s_data_temperatures_high_low add constraint fk_s_data_temperatures_high_l_32 foreign key (sensor_data_id) references s_sensor_raw_data (id);
create index ix_s_data_temperatures_high_l_32 on s_data_temperatures_high_low (sensor_data_id);
alter table s_data_wetness add constraint fk_s_data_wetness_sensor_33 foreign key (sensor_id) references s_sensors (id);
create index ix_s_data_wetness_sensor_33 on s_data_wetness (sensor_id);
alter table s_data_wetness add constraint fk_s_data_wetness_sensorData_34 foreign key (sensor_data_id) references s_sensor_raw_data (id);
create index ix_s_data_wetness_sensorData_34 on s_data_wetness (sensor_data_id);
alter table s_data_winds add constraint fk_s_data_winds_sensor_35 foreign key (sensor_id) references s_sensors (id);
create index ix_s_data_winds_sensor_35 on s_data_winds (sensor_id);
alter table s_data_winds add constraint fk_s_data_winds_sensorData_36 foreign key (sensor_data_id) references s_sensor_raw_data (id);
create index ix_s_data_winds_sensorData_36 on s_data_winds (sensor_data_id);
alter table s_data_winds_high_low add constraint fk_s_data_winds_high_low_sens_37 foreign key (sensor_id) references s_sensors (id);
create index ix_s_data_winds_high_low_sens_37 on s_data_winds_high_low (sensor_id);
alter table s_data_winds_high_low add constraint fk_s_data_winds_high_low_sens_38 foreign key (sensor_data_id) references s_sensor_raw_data (id);
create index ix_s_data_winds_high_low_sens_38 on s_data_winds_high_low (sensor_data_id);
alter table s_sensor_groups add constraint fk_s_sensor_groups_user_39 foreign key (user_id) references c_users (id);
create index ix_s_sensor_groups_user_39 on s_sensor_groups (user_id);
alter table s_sensor_group_properties add constraint fk_s_sensor_group_properties__40 foreign key (group_id) references s_sensor_groups (id);
create index ix_s_sensor_group_properties__40 on s_sensor_group_properties (group_id);
alter table s_sensor_properties add constraint fk_s_sensor_properties_sensor_41 foreign key (sensor_id) references s_sensors (id);
create index ix_s_sensor_properties_sensor_41 on s_sensor_properties (sensor_id);


