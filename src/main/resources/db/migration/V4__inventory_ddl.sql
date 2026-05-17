-- 재고
create table inventories (
    id bigint auto_increment primary key,
    place_id bigint not null,
    product_id bigint not null,
    quantity int not null default 0,
    created_by varchar(40),
    updated_by varchar(40),
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp,
    constraint uk_inventories_place_product unique (place_id, product_id)
);

-- 재고 이력
create table inventory_transactions (
    id bigint auto_increment primary key,
    type varchar(50) not null,
    product_id bigint not null,
    quantity int not null,
    place_id bigint,
    from_place_id bigint,
    to_place_id bigint,
    reason varchar(500),
    created_by varchar(40),
    created_at datetime default current_timestamp
);
create index idx_inventory_transactions_product_id on inventory_transactions(product_id);
create index idx_inventory_transactions_place_id on inventory_transactions(place_id);