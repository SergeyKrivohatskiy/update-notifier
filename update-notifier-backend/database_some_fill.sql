use update_notifier;

insert into users value 
	(null, 'mail@post.com'), #Assume, that user id equals 1
	(null, 'another_mail@post.com');
	
insert into resources values
	(null, 1, 'http://ya.ru', 			0, '', null, 0, null),	#1
	(null, 1, 'http://rambler.ru', 		0, '', null, 0, null),	#2
	(null, 1, 'http://thumbtack.ru', 	0, '', null, 0, null),	#3
	(null, 1, 'http://issart.com', 		0, '', null, 0, null),	#4
	(null, 1, 'http://mail.ru', 		0, '', null, 0, null),	#5
	(null, 1, 'http://mail.google.com', 0, '', null, 0, null),	#6
	(null, 1, 'http://vk.com', 			0, '', null, 0, null),	#7
	(null, 1, 'http://odnoklassniki.ru', 0, '', null, 0, null),	#8
	(null, 1, 'http://habrahabr.com', 	0, '', null, 0, null),	#9

	(null, 2, 'http://rambler.ru', 		0, '', null, 0, null),	#10
	(null, 2, 'http://habrahabr.com', 	0, '', null, 0, null),	#11
	(null, 2, 'http://rutracker.org', 	0, '', null, 0, null),	#12
	(null, 2, 'http://nnm-club.ru', 	0, '', null, 0, null),	#13
	(null, 2, 'http://google.com', 		0, '', null, 0, null),	#14
	(null, 2, 'http://vk.com', 			0, '', null, 0, null),	#15
	(null, 2, 'http://plus.google.com', 0, '', null, 0, null);	#16

insert into tags values 
	(null, 1, 'search'), 
	(null, 1, 'mail'), 
	(null, 1, 'it'),
	(null, 1, 'social'),
	(null, 1, 'favorites'),
	(null, 1, 'job'),

	(null, 2, 'search'), 
	(null, 2, 'torrent'), 
	(null, 2, 'net');

insert into resource_tag values 
	(1,1), (2,1), 
	(3,3), (4,3), 
	(5,2), (6,2), 
	(7,4), (8,4),
	(3,5), (7,5),
	(3,6), (4,6),

	(10,7), (14,7), 
	(12,8), (13,8), 
	(15,9), (16,9);
