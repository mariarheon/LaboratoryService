use labresearch;

drop table if exists `busy`;
drop table if exists `schedule`;
drop table if exists `form_field_link`;
drop table if exists `form`;
drop table if exists `form_field`;
drop table if exists `request_analysis_link`;
drop table if exists `analysis`;
drop table if exists `request`;
drop table if exists `user`;

create table `user` (
  id int not null auto_increment primary key,
  login varchar(100) unique not null,
  surname varchar(100) not null,
  name varchar(100) not null,
  patronymic varchar(100) not null,
  sex enum("Мужской", "Женский") not null,
  passport_series int not null,
  passport_number int not null,
  role enum("Администратор", "Лаборант", "Клиент") not null,
  phone varchar(100) not null,
  password char(40)   # sha1 hash
);

insert into `user`(login, surname, name, patronymic, sex, passport_series,
passport_number, role, phone, password) values ('admin',
'Лихачёв', 'Абрам', 'Миронович', 'Мужской', 182766, 9899,
'Администратор', '89001111111', sha1('pass'));

insert into `user`(login, surname, name, patronymic, sex, passport_series,
passport_number, role, phone, password) values ('assistant1',
'Трофимова', 'Зинаида', 'Тихоновна', 'Женский', 900999, 2987,
'Лаборант', '89002222222', sha1('pass'));

insert into `user`(login, surname, name, patronymic, sex, passport_series,
passport_number, role, phone, password) values ('assistant2',
'Филатов', 'Денис', 'Дмитрьевич', 'Мужской', 987384, 3444,
'Лаборант', '89002378769', sha1('pass'));

insert into `user`(login, surname, name, patronymic, sex, passport_series,
passport_number, role, phone, password) values ('client1',
'Мишина', 'Наталья', 'Евсеевна', 'Женский', 946512, 3965,
'Клиент', '89003333333', sha1('pass'));

insert into `user`(login, surname, name, patronymic, sex, passport_series,
passport_number, role, phone, password) values ('client2',
'Артемьев', 'Варлаам', 'Тимофеевич', 'Мужской', 236779, 7462,
'Клиент', '89004444444', sha1('pass'));

create table request (
	id int not null auto_increment primary key,
    surname varchar(100) not null,
    name varchar(100) not null,
    patronymic varchar(100) not null,
    sex enum("Мужской", "Женский") not null,
    passport_series int not null,
    passport_number int not null,
    arrival_time datetime null,
    status enum("На рассмотрении", "Принята", "В работе", "Отклонена", "Завершена", "Клиент осведомлен") not null,
    client_id int not null,
    foreign key (client_id) references `user`(id)
);

insert into request (surname, name, patronymic, sex, passport_series,
    passport_number, arrival_time, status, client_id) values
('Петров', 'Петр', 'Петрович', 'Мужской', 123456, 1234, '2020-06-20 15:00:00',
'На рассмотрении',
(select id from `user` where login = 'client1'));

insert into request (surname, name, patronymic, sex, passport_series,
    passport_number, arrival_time, status, client_id) values
('Петрова', 'Алена', 'Ивановна', 'Женский', 777777, 7777, '2020-06-21 18:00:00',
'На рассмотрении',
(select id from `user` where login = 'client1'));

insert into request (surname, name, patronymic, sex, passport_series,
    passport_number, arrival_time, status, client_id) values
('Кириллова', 'Анна', 'Андреевна', 'Женский', 248732, 2873, '2020-06-17 15:30:00',
'На рассмотрении',
(select id from `user` where login = 'client2'));

create table analysis (
    id int not null auto_increment primary key,
    name varchar(512) unique not null,
    collection_minutes int not null,
    research_minutes int not null
);

insert into analysis (name, collection_minutes, research_minutes) values
('Общий анализ мочи с микроскопией осадка', 5, 30);

insert into analysis (name, collection_minutes, research_minutes) values
('Биохимический анализ кала', 5, 40);

insert into analysis (name, collection_minutes, research_minutes) values
('Клинический анализ крови', 20, 45);

create table request_analysis_link (
    request_id int not null,
    analysis_id int not null,
    primary key(request_id, analysis_id),
    foreign key (request_id) references request(id),
    foreign key (analysis_id) references analysis(id)
);

insert into request_analysis_link (request_id, analysis_id) values (
(select id from request where concat(surname, ' ', name, ' ', patronymic) = 'Петров Петр Петрович'),
(select id from analysis where name like '%мочи%')
);

insert into request_analysis_link (request_id, analysis_id) values (
(select id from request where concat(surname, ' ', name, ' ', patronymic) = 'Петров Петр Петрович'),
(select id from analysis where name like '%кала%')
);

insert into request_analysis_link (request_id, analysis_id) values (
(select id from request where concat(surname, ' ', name, ' ', patronymic) = 'Петрова Алена Ивановна'),
(select id from analysis where name like '%крови%')
);

insert into request_analysis_link (request_id, analysis_id) values (
(select id from request where concat(surname, ' ', name, ' ', patronymic) = 'Кириллова Анна Андреевна'),
(select id from analysis where name like '%крови%')
);

create table form_field (
    id int not null auto_increment primary key,
    analysis_id int not null,
    order_num int not null,
    name varchar(256) not null,
    description varchar(256) not null,
    units varchar(128) not null,
    foreign key (analysis_id) references analysis(id)
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%крови%'),
1, 'RBC', 'Эритроциты', 'x 10^12/л'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%крови%'),
2, 'HGB, Hb', 'Гемоглобин', 'г/л'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%крови%'),
3, 'RDWc', 'Ширина распределения эритроцитов', '%'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%крови%'),
4, 'MCV', 'Объем эритроцита', 'фл'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%крови%'),
5, 'MCH', 'Содержание в эритроците гемоглобина', 'gu (pg)'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%крови%'),
6, 'MCHC', 'Концентрация в эритроците гемоглобина', 'г/л'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%крови%'),
7, 'HCT', 'Гематокрит', '%'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%крови%'),
8, 'RLT', 'Тромбоциты', 'х 109/л'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%крови%'),
9, 'WBC', 'Лейкоциты', 'х 109/л'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%крови%'),
10, 'GRA, GRAN', 'Гранулоциты', 'х 109/л'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%крови%'),
11, 'MON', 'Моноциты', 'х 109/л'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%крови%'),
12, 'LYM', 'Лимфоциты', 'х109/л'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
1, 'Цвет', 'Цвет', ''
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
2, 'Запах', 'Запах', ''
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
3, 'Внешний вид', 'Внешний вид', ''
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
4, 'Относительная плотность', 'Относительная плотность', ''
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
5, 'pH', 'pH', ''
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
6, 'Белок', 'Белок', 'г/л'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
7, 'Глюкоза', 'Глюкоза', 'ммоль/л'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
8, 'Кетоновые тела', 'Кетоновые тела', 'ммоль/л'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
9, 'Билирубин', 'Билирубин', 'мкмоль/л'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
10, 'Уробилиноген', 'Уробилиноген', 'мкмоль/л'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
11, 'Гемоглобин', 'Гемоглобин', 'мкмоль/л'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
12, 'Бактерии (нитритный тест)', 'Бактерии (нитритный тест)', 'мкмоль/л'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
13, 'Эритроциты', 'Эритроциты', 'в поле зрения'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
14, 'Лейкоциты', 'Лейкоциты', 'в поле зрения'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
15, 'Эпителиальные клетки', 'Эпителиальные клетки', 'в поле зрения'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
16, 'Кристаллы', 'Кристаллы', ''
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
17, 'Бактерии', 'Бактерии', ''
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
18, 'Дрожжевые грибы', 'Дрожжевые грибы', ''
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%мочи%'),
19, 'Паразиты', 'Паразиты', ''
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%кала%'),
1, 'C2 абс.', 'Абсолютное содержание уксусной кислоты', 'мг/г'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%кала%'),
2, 'C2 отн.', 'Относительное содержание уксусной кислоты', 'Ед'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%кала%'),
3, 'C3 абс.', 'Абсолютное содержание пропионовой кислоты', 'мг/г'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%кала%'),
4, 'C3 отн.', 'Относительное содержание пропионовой кислоты', 'Ед'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%кала%'),
5, 'C4 абс.', 'Абсолютное содержание масляной кислоты', 'мг/г'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%кала%'),
6, 'C4 отн.', 'Относительное содержание масляной кислоты', 'Ед'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%кала%'),
7, 'изоС4 + изоС5 + изоС6 абс.', 'Абсолютное содержание ИзоCn', 'мг/г'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%кала%'),
8, 'изоС4 + изоС5 + изоС6 отн.', 'Относительное содержание ИзоCn', 'Ед'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%кала%'),
9, 'ИзоCn/Cn', 'ИзоCn/Cn', 'Ед'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%кала%'),
10, 'C2+...C6', 'Общее содержание', 'мг/г'
);

insert into form_field (analysis_id, order_num, name, description, units) values
(
(select id from analysis where name like '%кала%'),
11, 'C2-C4', 'Анаэробный индекс', 'Ед'
);

create table form (
	id int not null auto_increment primary key,
    request_id int not null,
    analysis_id int not null,
    assistant_id int not null,
    barcode varchar(50) unique not null,
    status enum("В работе", "Завершена") not null,
    foreign key (request_id) references request(id),
    foreign key (analysis_id) references analysis(id),
    foreign key (assistant_id) references `user`(id)
);

create table form_field_link (
    form_id int not null,
    field_id int not null,
    value varchar(100) not null,
    primary key(form_id, field_id),
    foreign key(form_id) references form(id),
    foreign key(field_id) references form_field(id)
);

create table schedule (
    id int not null auto_increment primary key,
    assistant_id int not null,
    weekday enum('пн', 'вт', 'ср', 'чт', 'пт', 'сб', 'вс') not null,
    start_time time not null,
    end_time time not null,
    foreign key (assistant_id) references user(id)
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Трофимова Зинаида Тихоновна'),
'пн',
'09:00',
'12:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Трофимова Зинаида Тихоновна'),
'пн',
'13:00',
'18:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Трофимова Зинаида Тихоновна'),
'вт',
'09:00',
'12:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Трофимова Зинаида Тихоновна'),
'вт',
'13:00',
'18:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Трофимова Зинаида Тихоновна'),
'ср',
'09:00',
'12:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Трофимова Зинаида Тихоновна'),
'ср',
'13:00',
'18:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Трофимова Зинаида Тихоновна'),
'чт',
'09:00',
'12:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Трофимова Зинаида Тихоновна'),
'чт',
'13:00',
'18:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Трофимова Зинаида Тихоновна'),
'пт',
'09:00',
'12:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Трофимова Зинаида Тихоновна'),
'пт',
'13:00',
'18:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Филатов Денис Дмитрьевич'),
'пн',
'09:00',
'12:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Филатов Денис Дмитрьевич'),
'пн',
'13:00',
'18:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Филатов Денис Дмитрьевич'),
'вт',
'09:00',
'12:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Филатов Денис Дмитрьевич'),
'вт',
'13:00',
'18:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Филатов Денис Дмитрьевич'),
'ср',
'09:00',
'12:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Филатов Денис Дмитрьевич'),
'ср',
'13:00',
'18:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Филатов Денис Дмитрьевич'),
'чт',
'09:00',
'12:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Филатов Денис Дмитрьевич'),
'чт',
'13:00',
'18:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Филатов Денис Дмитрьевич'),
'пт',
'09:00',
'12:00'
);

insert into schedule (assistant_id, weekday, start_time, end_time)
values (
(select id from `user` where concat(surname, ' ', name, ' ', patronymic) = 'Филатов Денис Дмитрьевич'),
'пт',
'13:00',
'18:00'
);

create table busy (
    id int not null auto_increment primary key,
    assistant_id int not null,
    the_date date not null,
    start_time time not null,
    end_time time not null,
    form_id int not null,
    reason enum('сбор', 'исследование') not null,
    foreign key (assistant_id) references `user`(id),
    foreign key (form_id) references form(id),
    unique (form_id, reason)
);
