create user 'biserverdb'@'localhost' identified by 'password';
use mysql;
select host, user, password from user;
create database biserver;
show databases;
grant all on biserver.* to 'biserverdb'@'localhost';
/*
 * quit, login as biserverdb
 */
source biserver_dump.sql
/*
 * grant biserverdb user to foodmart
 */
grant all on foodmart.* to 'biserverdb'@'localhost';

