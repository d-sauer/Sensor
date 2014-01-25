# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table dbuser (
  id                        bigint not null,
  email                     varchar(255),
  password                  varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  constraint pk_dbuser primary key (id))
;

create sequence dbuser_seq;




# --- !Downs

drop table if exists dbuser cascade;

drop sequence if exists dbuser_seq;

