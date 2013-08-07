drop database if exists update_notifier;
create database if not exists update_notifier character set utf8;
use update_notifier;
# Index for user's mail?
create table users (
	id bigint(20) unsigned auto_increment, 
	email varchar(50) not null,
	primary key (id)
);

create table resources (
	id bigint(20) unsigned auto_increment, 
	user_id bigint(20) unsigned not null,
	url varchar(255) not null,
	shedule_code tinyint not null,
	dom_path varchar(255) not null,
	filter varchar(255),
	hash int not null,
	last_update timestamp,
	primary key (id),
	foreign key (user_id) references users(id) on delete cascade
);

create table tags (
	id bigint(20) unsigned auto_increment, 
	user_id bigint(20) unsigned not null,
	name varchar(30) not null,
	primary key (id),
	constraint no_duplicate_tags unique(user_id, name),
	foreign key (user_id) references users(id) on delete cascade
);

create table resource_tag (
	resource_id bigint(20) unsigned, 
	tag_id bigint(20) unsigned, 
	primary key (tag_id, resource_id),
	foreign key (tag_id) references tags(id),
	foreign key (resource_id) references resources(id) on delete cascade
);
