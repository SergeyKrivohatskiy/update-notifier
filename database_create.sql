drop database if exists update_notifier;
create database if not exists update_notifier character set utf8;
use update_notifier;
# Index for user's mail?
create table users (
	id bigint(20) unsigned auto_increment, 
	name varchar(30) not null,
	surname varchar(30) not null,
	email varchar(50) not null,
	primary key (id)
);

create table resources (
	id bigint(20) unsigned auto_increment, 
	user_id bigint(20) unsigned not null,
	name varchar(50) not null,
	url varchar(255) not null,
	schedule_code tinyint not null,
	filter varchar(255),
	last_modified timestamp null default null,
	hash int not null,
	page_id bigint(20) unsigned not null,
	last_update timestamp not null DEFAULT '1973-04-28 00:00:00',
	primary key (id),
	foreign key (user_id) references users(id) on delete cascade
);

create table tags (
	id bigint(20) unsigned auto_increment, 
	user_id bigint(20) unsigned not null,
	name varchar(30) CHARACTER SET utf8 COLLATE utf8_bin not null,
	primary key (id),
	constraint no_duplicate_tags unique(user_id, name),
	foreign key (user_id) references users(id) on delete cascade
);

create table resource_tag (
	resource_id bigint(20) unsigned, 
	tag_id bigint(20) unsigned, 
	primary key (tag_id, resource_id),
	foreign key (tag_id) references tags(id) on delete cascade,
	foreign key (resource_id) references resources(id) on delete cascade
);

create table filters (
	id bigint(20) unsigned auto_increment,
	resource_id bigint(20) unsigned not null,
	path varchar(255) CHARACTER SET utf8 COLLATE utf8_bin not null,
	primary key (id),
	constraint no_duplicate_filters unique(resource_id, path),
	foreign key (resource_id) references resources(id) on delete cascade
);

create table attributes (
	id bigint(20) unsigned auto_increment,
	filter_id bigint(20) unsigned not null,
	attr_name varchar(255) CHARACTER SET utf8 COLLATE utf8_bin not null,
	primary key (id),
	constraint no_duplicate_attributes unique(filter_id, attr_name),
	foreign key (filter_id) references filters(id) on delete cascade
);

create table pages (
	id bigint(20) unsigned auto_increment,
	page longtext,  
	primary key (id)
);