SET @user = "chipben";
SET @password = "cb";

DROP DATABASE IF  EXISTS ftgo_order_service;
create database ftgo_order_service;
CREATE USER '@user'@'localhost' IDENTIFIED BY '@password';
USE ftgo_order_service;