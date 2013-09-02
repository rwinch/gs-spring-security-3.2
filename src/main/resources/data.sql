insert into user(id,email,password,firstname,lastname) values (0,'rob@example.com','password','Rob','Winch');
insert into user(id,email,password,firstname,lastname) values (1,'luke@example.com','password','Luke','Taylor');

insert into message(id,created,to_id,summary,text) values (100,'2013-10-04 10:00:00',0,'Hello Rob','This message is for Rob');
insert into message(id,created,to_id,summary,text) values (110,'2013-10-04 10:00:00',1,'Hello Luke','This message is for Luke');
