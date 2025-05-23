--liquibase formatted sql

--changeset lina:1

create table if not exists items
(
    id          bigserial primary key,
    name        varchar(50)   not null,
    description varchar(255)  not null,
    price       numeric(9, 2) not null,
    brand       varchar(50)   not null,
    count       bigint        not null
);

create table if not exists users
(
    id         bigserial primary key,
    username   varchar(255) not null,
    password   varchar(255) not null,
    lastname   varchar(255) not null,
    name       varchar(255) not null,
    patronymic varchar(255),
    age        integer      not null,
    email      varchar(255) not null,
    phone      varchar(255) not null,
    active     boolean      not null default true
);

create table if not exists user_likes
(
    user_id bigint not null,
    item_id bigint not null,
    primary key (user_id, item_id),
    foreign key (user_id) references users (id) on delete cascade,
    foreign key (item_id) references items (id) on delete cascade
);

create table if not exists item_rating
(
    id      bigserial primary key,
    item_id bigint not null unique,
    count   bigint not null default 0,
    rating  numeric(1, 2),
    foreign key (item_id) references items (id) on delete cascade
);

create table if not exists user_rating_history
(
    user_id bigint  not null,
    item_id bigint  not null,
    stars   integer not null,
    primary key (user_id, item_id),
    foreign key (item_id) references items (id) on delete cascade
);

create table if not exists orders
(
    id      bigserial primary key,
    user_id bigint    not null,
    status  varchar   not null,
    created timestamp not null,
    foreign key (user_id) references users (id) on delete cascade
);

create table if not exists order_items
(
    order_id bigint not null,
    item_id  bigint not null,
    count    bigint not null,
    primary key (order_id, item_id),
    foreign key (order_id) references orders (id) on delete cascade,
    foreign key (item_id) references items (id) on delete cascade
);

create table if not exists payment
(
    id                    bigserial primary key,
    user_id               bigint        not null,
    order_id              bigint        not null,
    amount                numeric(9, 2) not null,
    method                varchar       not null,
    created               timestamp     not null,
    active_period_minutes integer       not null,
    status                varchar       not null,
    foreign key (order_id) references orders (id),
    foreign key (user_id) references users (id)
);

--changeset lina:2

alter table if exists items
    add column img varchar;

--changeset lina:3

alter table item_rating
    alter column rating type numeric(3, 2);

--changeset lina:4

alter table if exists orders
    add column total_price numeric(9, 2) not null default 0;
alter table if exists orders
    add column delivery_type varchar;
alter table if exists payment
    rename column method to payment_type;
alter table order_items
    alter column count type integer;
alter table payment
    drop constraint if exists payment_user_id_fkey;
alter table payment
    drop column user_id cascade;

--changeset lina:5

alter table users
    add constraint username_uq unique (username, email, phone);

--changeset lina:6

alter table user_rating_history
    add column comment text;
