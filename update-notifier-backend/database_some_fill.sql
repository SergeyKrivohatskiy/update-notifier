insert into users value 
	(null, 'mail@post.com'); #Assume, that user id equals 1

insert into resources value
	(null, 1, 'http://url.com'),
	(null, 1, 'http://url_too.com'),
	(null, 1, 'http://another_url.com');

insert into tags values 
	(null, 1, 'tag one'), 
	(null, 1, 'tag two'), 
	(null, 1, 'tag three');

insert into tag_resource value (1,1), (2,1), (3,1), (1,2), (3,2), (2,3), (3,3);
