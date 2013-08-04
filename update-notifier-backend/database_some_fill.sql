delete from users;
delete from resources;
delete from tags;
delete from resource_tag;

insert into users value 
	(null, 'mail@post.com'), #Assume, that user id equals 1
	(null, 'another_mail@post.com');
	
insert into resources value
	(null, 1, 'http://ya.ru', 0, 0),			#1
	(null, 1, 'http://rambler.ru', 0, 0),		#2
	(null, 1, 'http://thumbtack.ru', 0, 0),		#3
	(null, 1, 'http://issart.com', 0, 0),		#4
	(null, 1, 'http://mail.ru', 0, 0),			#5
	(null, 1, 'http://mail.google.com', 0, 0),	#6
	(null, 1, 'http://vk.com', 0, 0),			#7
	(null, 1, 'http://odnoklassniki.ru', 0, 0),	#8

	(null, 2, 'http://rambler.ru', 0, 0),		#9
	(null, 2, 'http://habrahabr.com', 0, 0),	#10
	(null, 2, 'http://rutracker.org', 0, 0),	#11
	(null, 2, 'http://nnm-club.ru', 0, 0),		#12
	(null, 2, 'http://google.com', 0, 0),		#13
	(null, 2, 'http://vk.com', 0, 0),			#14
	(null, 2, 'http://plus.google.com', 0, 0);	#15

insert into tags values 
	(null, 1, 'search'), 
	(null, 1, 'mail'), 
	(null, 1, 'it'),
	(null, 1, 'social'),

	(null, 2, 'search'), 
	(null, 2, 'torrent'), 
	(null, 2, 'net');

insert into resource_tag values 
	(1,1), (2,1), 
	(3,3), (4,3), 
	(5,2), (6,2), 
	(7,4), (8,4),

	(9,1), (13,1), 
	(11,2), (12,2), 
	(14,3), (15,3);
