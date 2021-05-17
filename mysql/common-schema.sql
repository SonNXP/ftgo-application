DROP DATABASE IF exists eventuate;
create database eventuate;
-- CREATE USER eventuate.* TO 'mysqluser'@'%' WITH GRANT OPTION;

USE eventuate;

create table cdc_monitoring (
  reader_id VARCHAR(1000) PRIMARY KEY,
  last_time BIGINT
);

