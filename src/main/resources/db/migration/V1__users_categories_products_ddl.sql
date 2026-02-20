-- 유저
create table users (
    id binary(16) not null primary key,
    name varchar(100) not null,
    email varchar(100) not null unique,
    social_type varchar(30) not null,
    social_id varchar(200) not null
);

-- 품목
create table products (
    id bigint auto_increment primary key,
    store_id bigint not null,
    sku varchar(100) not null,
    status varchar(50) not null,
    name varchar(255) not null,
    barcode varchar(255),
    image_url varchar(255),
    created_by varchar(40),
    updated_by varchar(40),
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp
);
create index idx_products_store_id_status on products(store_id, status);

-- 물품 카테고리
create table category_products (
    id bigint auto_increment primary key,
    product_id bigint not null,
    category_id bigint not null,
    constraint uk_category_product unique (product_id, category_id)
);
create index idx_category_products_category_id on category_products(category_id);

-- 카테고리
create table categories (
    id bigint auto_increment primary key,
    store_id bigint not null,
    name varchar(255) not null,
    created_by varchar(40),
    updated_by varchar(40),
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp
);
