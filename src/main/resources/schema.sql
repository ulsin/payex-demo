-- test
create table Test
(
    field1 varchar(10) not null,
    field2 varchar(20) not null,
    primary key (field1)
);

create table Show
(
    name   varchar(255) not null,
    showId int          not null,
    primary key (name)
);