create table if not exists users
(
    user_id    bigint generated by default as identity primary key,
    user_name  varchar(200),
    user_email varchar(200),
    CONSTRAINT user_name_not_empty CHECK (users.user_name IS NOT NULL AND users.user_name <> ''),
    CONSTRAINT user_email_not_empty CHECK (users.user_email IS NOT NULL AND users.user_email <> ''),
    CONSTRAINT user_email_unique UNIQUE (user_email)
);

create table if not exists items
(
    item_id          bigint generated by default as identity primary key,
    item_name        varchar(200),
    item_description varchar(1000),
    is_available     boolean,
    owner_id         bigint references users (user_id) on delete cascade,
    request_id       bigint,
    CONSTRAINT item_name_not_empty CHECK (items.item_name IS NOT NULL AND items.item_name <> ''),
    CONSTRAINT item_description_not_empty CHECK (items.item_description IS NOT NULL AND items.item_description <> ''),
    CONSTRAINT item_owner_id_not_null CHECK (owner_id IS NOT NULL),
    CONSTRAINT item_available_not_null CHECK (items.is_available IS NOT NULL)
);

create table if not exists bookings
 (
     booking_id bigint generated by default as identity primary key,
     start_date timestamp without time zone,
     end_date   timestamp without time zone,
     item_id    bigint references items (item_id) on delete cascade,
     booker_id  bigint references users (user_id) on delete cascade,
     status     int,
     CONSTRAINT start_date_not_null CHECK (start_date IS NOT NULL),
     CONSTRAINT end_date_not_null CHECK (end_date IS NOT NULL),
     CONSTRAINT item_id_not_null CHECK ( item_id IS NOT NULL),
     CONSTRAINT booker_id_not_null CHECK ( booker_id IS NOT NULL),
     CONSTRAINT end_is_after_start CHECK (end_date > start_date),
     CONSTRAINT status_not_null CHECK (status IS NOT NULL)
 );

create table if not exists requests
 (
     request_id          bigint generated by default as identity primary key,
     request_description varchar(1000),
     requestor_id        bigint references users (user_id) on delete cascade,
     CONSTRAINT requestor_id_not_null CHECK ( requestor_id IS NOT NULL)
 );

create table if not exists comments
(
    comment_id bigint generated by default as identity primary key,
    text        varchar(2000),
    item_id     bigint references items (item_id) on delete cascade,
    author_id    bigint references users (user_id) on delete cascade,
    created timestamp without time zone,
    CONSTRAINT text_is_not_empty CHECK (text IS NOT NULL AND text <> ''),
    CONSTRAINT created_is_not_null CHECK (created IS NOT NULL),
    CONSTRAINT item_id_not_null_comments CHECK ( item_id IS NOT NULL),
    CONSTRAINT author_id_not_null CHECK ( author_id IS NOT NULL)
);