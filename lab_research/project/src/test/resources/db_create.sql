use labresearch;

drop table if exists `protocol`;
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
('Петров', 'Петр', 'Петрович', 'Мужской', 123456, 1234, '2020-06-25 15:00:00',
'На рассмотрении',
(select id from `user` where login = 'client1'));

insert into request (surname, name, patronymic, sex, passport_series,
    passport_number, arrival_time, status, client_id) values
('Петрова', 'Алена', 'Ивановна', 'Женский', 777777, 7777, '2020-06-26 18:00:00',
'На рассмотрении',
(select id from `user` where login = 'client1'));

insert into request (surname, name, patronymic, sex, passport_series,
    passport_number, arrival_time, status, client_id) values
('Кириллова', 'Анна', 'Андреевна', 'Женский', 248732, 2873, '2020-06-26 15:30:00',
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

create table protocol (
    id int not null auto_increment primary key,
    analysis_id int not null,
    data text not null,
    foreign key (analysis_id) references analysis(id)
);

insert into protocol (analysis_id, data)
values ((select id from analysis where name like '%крови%'),
'Большая часть клинических лабораторных исследований проводится в образцах крови: венозной, артериальной или капиллярной. Венозная кровь - лучший материал для определения гематологических, биохимических, гормональных, серологических и иммунологических показателей.

Для исследования аналитов в цельной крови, сыворотке или плазме образец крови берут чаще всего из локтевой вены. Показания для взятия крови из пальца на клиническое исследование крови:

- при ожогах, занимающих большую площадь поверхности тела пациента;

- при наличии у пациента очень мелких вен или когда они труднодоступны;

- при выраженном ожирении пациента;

- при установленной склонности к венозному тромбозу;

- у новорожденных.

При взятии образца крови из венозного или артериального катетера, через который проводилось вливание инфузионного раствора, катетер следует предварительно промыть изотоническим солевым раствором в объеме, соответствующем объему катетера, и отбросить первые 5 мл (миллилитров) взятой из катетера крови. Недостаточное промывание катетера может привести к загрязнению образца крови препаратами, вводившимися через катетер. Из катетеров, обработанных гепарином, нельзя брать образцы крови для исследований системы свертывания крови.

В зависимости от назначенного вида исследования образец крови должен собираться при наличии строго определенных добавок [1]. Для получения плазмы кровь собирают с добавлением антикоагулянтов: этилендиаминтетрауксусной кислоты, цитрата, оксалата, гепарина [2]. Для исследований системы свертывания крови применяется только цитратная плазма (в точном соотношении одной части 3,8%-ного (0,129 моль/л) раствора цитрата натрия и девяти частей крови). В большинстве гематологических исследований используют венозную кровь с солями этилендиаминтетрауксусной кислоты (ЭДТА, КГОСТ Р 53079.4-2008 Технологии лабораторные клинические. Обеспечение качества клинических лабораторных исследований. Часть 4. Правила ведения преаналитического этапа или КГОСТ Р 53079.4-2008 Технологии лабораторные клинические. Обеспечение качества клинических лабораторных исследований. Часть 4. Правила ведения преаналитического этапа-ЭДТА). Для получения сыворотки кровь собирают без антикоагулянтов. Для исследования глюкозы кровь собирают с добавлением ингибиторов гликолиза (фтористого натрия или йодоацетата).

Для исследования ряда нестабильных гормонов (остеокальцина, кальцитонина, адренокортикотропного гормона) используют ингибитор апротинин.

Для получения из образцов крови вариантов проб для различных видов исследований рекомендуется следующая последовательность наполнения пробирок:

- кровь без добавок - для получения гемокультуры, используемой в микробиологических исследованиях;

- кровь без антикоагулянтов - для получения сыворотки, используемой при клинико-химических и серологических исследованиях;

- кровь с цитратом - для получения плазмы, используемой при коагулологических исследованиях;

- кровь с гепарином - для получения плазмы, используемой при биохимических исследованиях;

- кровь с ЭДТА - для получения цельной крови, используемой для гематологических исследований, и плазмы, используемой для некоторых клинико-химических исследований.

С целью сохранения в образце крови эритроцитов применяют смесь антикоагулянтов с добавками, например, АЦД (антикоагулянт - цитрат-декстроза или кислота-цитрат-декстроза).

Во избежание ятрогенной анемизации пациентов объем забираемой для исследований крови должен быть рационально рассчитан, исходя из того, что в конечном итоге непосредственно для анализа расходуется лишь половина от первоначально взятого объема (с учетом использования сыворотки или плазмы при гематокрите 0,5).

При использовании современных анализаторов достаточны следующие объемы образцов:

- для биохимических исследований: 4-5 мл; при использовании гепаринизированной плазмы: 3-4 мл;

- для гематологических исследований: 2-3 мл крови с ЭДТА;

- для исследований свертывающей системы: 2-3 мл цитратной крови;

- для иммуноисследований, включая исследования белков и др.: 1 мл цельной крови для 3-4 иммуноанализов;

- для исследования скорости оседания эритроцитов: 2-3 мл цитратной крови;

- для исследования газов крови: капиллярная кровь - 50 мкл (микролитров); артериальная или венозная кровь с гепарином - 1 мл.

Рационально применение пробирок для взятия крови небольшого объема (4-5 мл) при соотношении диаметра и высоты пробирки 13 на 75 мм. Использование плазмы вместо сыворотки дает увеличение на 15%-20% выхода анализируемого материала при одном и том же объеме взятой у пациента крови. Взятие венозной крови облегчается применением вакуумных пробирок. Под влиянием вакуума кровь из вены быстро поступает в пробирку, что упрощает процедуру взятия и сокращает время наложения жгута.

Для обозначения содержимого пробирок с различными добавочными компонентами применяют цветное кодирование закрывающих их пробок. Так, для пробирок с антикоагулянтами лиловый цвет пробки означает наличие ЭДТА, зеленый цвет - гепарина, голубой - цитрата. Добавление в пробирку ингибиторов гликолиза (фторида, йодацетата) как одних, так и в комбинации с антикоагулянтами (гепарином, ЭДТА), кодируется пробкой серого цвета (см. таблицу 1).


Таблица 1 - Добавки в пробирках с цветным кодом

Содержимое пробирки

Применение

Цвет кода

Пустая, без добавок, для сыворотки

Биохимия, серология

Красный/белый

Гепарин (12-30 Ед/мл)

Плазма для биохимии

Зеленый/оранжевый

КГОСТ Р 53079.4-2008 Технологии лабораторные клинические. Обеспечение качества клинических лабораторных исследований. Часть 4. Правила ведения преаналитического этапа или КГОСТ Р 53079.4-2008 Технологии лабораторные клинические. Обеспечение качества клинических лабораторных исследований. Часть 4. Правила ведения преаналитического этапа-ЭДТА (1,2-2,0 мг/мл)

Гематология и отдельные химические анализы в плазме

Лиловый/красный

Цитрат натрия (0,105-0,129 моль/л)

Коагулологические тесты

Голубой/зеленый

Фторид натрия (2-4 мг/мл)/оксалат калия (1-3 мг/мл)

Глюкоза, лактат

Серый

КГОСТ Р 53079.4-2008 Технологии лабораторные клинические. Обеспечение качества клинических лабораторных исследований. Часть 4. Правила ведения преаналитического этапа-ЭДТА и апротинин

Нестабильные гормоны

Розовый

Примечание - Пробирки, содержащие кислоту-цитрат-декстрозу (АЦД, формула А и В) используют для сохранения клеток и кодируют желтым цветом.');

insert into protocol (analysis_id, data)
values ((select id from analysis where name like '%кала%'),
'Кал для исследования должен быть собран в чистую сухую посуду с широкой горловиной, желательно стеклянную (не следует собирать кал в баночки и флаконы с узким горлом, а также в коробочки, спичечные коробки, бумагу и т.д.). Следует избегать примеси к испражнениям мочи, выделений из половых органов и других веществ, в том числе лекарств. Если для какого-либо химического определения (например, уробилиногена) нужно точно знать количество выделенного кала, то посуду, в которую собирают испражнения, нужно предварительно взвесить.');

insert into protocol (analysis_id, data)
values ((select id from analysis where name like '%мочи%'),
'В зависимости от цели исследования образцы мочи собирают либо в виде отдельных порций, либо за определенный промежуток времени. Первая утренняя порция мочи (натощак, сразу после сна) используется для общего анализа, вторая утренняя порция мочи - для количественных исследований в соотношении с выделением креатинина и для бактериологического исследования, случайная порция - для качественных или количественных клинико-химических исследований, суточная моча - для количественного определения экскреции аналитов.

Желательно использовать сосуд с широкой горловиной и крышкой, по возможности надо собирать мочу сразу в посуду, в которой она будет доставлена в лабораторию. Мочу из судна, утки, горшка брать нельзя, так как даже после прополаскивания этих сосудов может сохраняться осадок фосфатов, способствующих разложению свежей мочи. Если в лабораторию доставляется не вся собранная моча, то перед сливанием ее части необходимо тщательное взбалтывание, чтобы осадок, содержащий форменные элементы и кристаллы, не был утрачен.

При взятии утренней мочи (например, для общего анализа) собирают всю порцию утренней мочи (желательно, чтобы предыдущее мочеиспускание было не позже, чем в два часа ночи) в сухую, чистую, но не стерильную посуду, при свободном мочеиспускании. При сборе суточной мочи пациент собирает ее в течение 24 ч на обычном питьевом режиме [1,5-2 л (литра) в сутки]. Утром в 6-8 ч он освобождает мочевой пузырь (эту порцию мочи выливают), а затем в течение суток собирает всю мочу в чистый сосуд с широкой горловиной и плотно закрывающейся крышкой, емкостью не менее 2 л. Последняя порция берется точно в то же время, когда накануне был начат сбор (время начала и конца сбора отмечают). Если не вся моча направляется в лабораторию, то количество суточной мочи измеряют мерным цилиндром, отливают часть в чистый сосуд, в котором ее доставляют в лабораторию, и обязательно указывают объем суточной мочи.

Если для анализа требуется собрать мочу за 10-12 ч, сбор обычно проводят в ночное время: перед сном больной опорожняет мочевой пузырь и отмечает время (эту порцию мочи отбрасывают), затем больной мочится через 10-12 ч в приготовленную посуду, эту порцию мочи доставляют для исследований в лабораторию. При невозможности удержать мочеиспускание 10-12 ч, больной мочится в приготовленную посуду в несколько приемов и отмечает время последнего мочеиспускания.

При необходимости сбора мочи за два или три часа, больной опорожняет мочевой пузырь (эта порция отбрасывается), отмечает время и ровно через 2 или 3 часа собирает мочу для исследования.

При проведении пробы трех сосудов (стаканов) собирают утреннюю порцию мочи следующим образом: утром натощак после пробуждения и тщательного туалета наружных половых органов больной начинает мочиться в первый сосуд, продолжает во второй и заканчивает - в третий. Преобладающей по объему должна быть вторая порция. При диагностике урологических заболеваний у женщин чаще используют пробу двух сосудов, то есть делят при мочеиспускании мочу на две части, важно, чтобы первая часть в этом случае была небольшой по объему. При проведении пробы трех сосудов у мужчин последнюю третью порцию мочи собирают после массажа предстательной железы. Все сосуды готовят предварительно, на каждом обязательно указывают номер порции.

В первую порцию собираемой за сутки мочи в зависимости от назначенного вида исследования добавляют различные консерванты: для большинства компонентов - тимол (несколько кристаллов тимола на 100 мл мочи), для глюкозы, мочевины, мочевой кислоты, калия, кальция, оксалата, цитрата - азид натрия (0,5 или 1,0 г) на все количество суточной мочи, для катехоламинов и их метаболитов, 5-гидрокси-уксусной кислоты, кальция, магния, фосфатов - соляная кислота (25 мл, что соответствует 6 моль/л на объем суточной мочи), для порфиринов, уробилиногена - карбонат натрия, 2 г на литр мочи. Возможно также применение жидкости Мюллера (10 г сульфата натрия, 25 г бихромата калия, 100 мл воды) по 5 мл на 100 мл мочи, борной кислоты по 3-4 гранулы на 100 мл мочи, ледяной уксусной кислоты по 5 мл на все количество суточной мочи, бензоата или фторида натрия по 5 г на все количество суточной мочи. Несколько миллилитров толуола добавляют в сосуд с мочой так, чтобы он тонким слоем покрывал всю поверхность мочи; это дает хороший бактериостатический эффект и не мешает химическим анализам, но вызывает легкую мутность. Формалин, добавленный из расчета приблизительно 3-4 капли на 100 мл мочи, задерживает рост бактерий, хорошо сохраняет клеточные элементы, но мешает при некоторых химических определениях (сахар, индикан). Хлороформ, добавляемый из расчета 2-3 мл хлороформной воды (5 мл хлороформа на 1 л воды) на 100 мл мочи, проявляет недостаточный эффект консервирования, а также неблагоприятно влияет на результаты исследования осадка мочи (изменение клеток) и результаты некоторых химических исследований.');
