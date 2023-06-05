-- test
create table Test
(
    field1 varchar(10) not null,
    field2 varchar(20) not null,
    primary key (field1)
);

create table Show
(
    name    varchar(255) not null,
    showId  int          not null,
    rating  double       not null,
    network varchar(255) not null,
    primary key (name)
);

create table Episode
(
    showId  int          not null,
    name    varchar(255) not null,
    season  int          not null,
    episode int          not null,
    rating  double       not null,
    primary key (showId, name, season, episode)
);