create user 'biserverdb'@'localhost' identified by 'password';
use mysql;
select host, user, password from user;
create database biserver;
show databases;
/*
 * grnat biserverdb user to biserver database (the main database)
 */
grant all on biserver.* to 'biserverdb'@'localhost';
/*
 * quit, login as biserverdb
 */
source biserver_dump.sql
/*
 * grant biserverdb user to foodmart demo database
 */
grant all on foodmart.* to 'biserverdb'@'localhost';

/*
 * grant biserverdb user to sugarcrm demo database
 */
grant all on sugarcrm.* to 'biserverdb'@'localhost';
