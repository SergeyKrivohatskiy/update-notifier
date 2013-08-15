use update_notifier;

insert into users value 
	(null, 'cthutq66a@yandex.ru');
	
insert into resources values	
	(null, 1, 'https://www.roi.ru/poll/petition/biznes/otmenit-zakon-o-proizvolnyh-blokirovkah-internet-resursov-ot-02072013-187-fz-zakon-protiv-interneta', 0, '/0/1/1/1/0/2/0/1/0', null, 0, null);	#1

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
	(null, 1, 'roi');

insert into resource_tag values 
	(1, 1);
