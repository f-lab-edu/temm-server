-- 장소
create table places (
    id bigint auto_increment primary key,
    store_id bigint not null,
    name varchar(255) not null,
    created_by varchar(40),
    updated_by varchar(40),
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp
);
create index idx_places_store_id on places(store_id);
