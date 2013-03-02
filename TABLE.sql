CREATE TABLE users
(
uID int(8) AUTO_INCREMENT not null,
email varchar(255) not null,
first_n varchar(100) not null,
middle_n varchar(100) default '',
last_n varchar(100) not null,
affil varchar(255) default '',
ischair bit default 0,
unique index email_unique (email),
primary key (uID)
) ENGINE=INNODB;

CREATE TABLE conferences
(
cID int(8) AUTO_INCREMENT not null,
cname varchar(255),
substart datetime,
subend datetime,
num_revs int not null,
max_revs int not null,
iscalc bit default 0,
primary key (cID)
) ENGINE=INNODB;

CREATE TABLE papers
(
pID int(8) AUTO_INCREMENT not null,
title varchar(255) not null,
abstract text,
content text,
numReviewed int(8) default 0, 
p_stat varchar(20),
primary key (pID)
) ENGINE=INNODB;

CREATE TABLE roles
(
confID int(8),
userID int(8),
paperID int(8),
roleName varchar(10),
rating int(8),
comments varchar(255),
unique index unique_entry(confID, userID, paperID, roleName)
) ENGINE=INNODB;

CREATE TABLE conf_reviewer
(
confID int(8),
userID int(8),
unique index unique_set(confID, userID)
) ENGINE=INNODB;

insert into users(uID, email, first_n, middle_n, last_n, affil, ischair)
values(1, 'chair@cmt.com', 'Chair', 'C', 'admin', 'CMT', 1);
