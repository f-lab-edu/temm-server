-- 스토어
create table stores (
    id bigint auto_increment primary key,
    name varchar(255) not null,
    created_by varchar(40),
    updated_by varchar(40),
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp
);

-- 유저 스토어
create table user_stores (
    id          bigint auto_increment primary key,
    user_id binary(16) not null,
    store_id bigint not null,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp,
    constraint uk_user_store unique (user_id, store_id)
);
