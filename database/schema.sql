create table tag
(
    id   int auto_increment
        primary key,
    name varchar(255) null,
    constraint UK_1wdpsed5kna2y38hnbgrnhi5b
        unique (name)
);

create table users
(
    id         binary(16) not null
        primary key,
    bio        varchar(255) null,
    created_at datetime(6) null,
    email      varchar(255) null,
    image      varchar(255) null,
    password   varchar(255) null,
    username   varchar(255) null,
    constraint UK_6dotkott2kjsp8vw4d0m25fb7
        unique (email),
    constraint UK_r43af9ap4edm43mmtq01oddj6
        unique (username)
);

create table article
(
    id          int auto_increment
        primary key,
    content     varchar(255) null,
    created_at  datetime(6) null,
    description varchar(255) null,
    slug        varchar(255) null,
    title       varchar(255) null,
    updated_at  datetime(6) null,
    author_id   binary(16) null,
    constraint UK_lc76j4bqg2jrk06np18eve5yj
        unique (slug),
    constraint FKmjgtny2i22jf4dqncmd436s0u
        foreign key (author_id) references users (id)
);

create table article_favorites
(
    article_id int not null,
    user_id    binary(16) not null,
    primary key (article_id, user_id),
    constraint FKdvc7dl41ymynfseyl5lebxa92
        foreign key (article_id) references article (id),
    constraint FKogigrbv6299gc2lgy4yyo4pav
        foreign key (user_id) references users (id)
);

create table article_tags
(
    article_id int not null,
    tag_id     int not null,
    primary key (article_id, tag_id),
    constraint FK2mytld9v10mi8fmiphk8187q1
        foreign key (tag_id) references tag (id),
    constraint FK85ph188kqbfc5u1gq0tme7flk
        foreign key (article_id) references article (id)
);

create table comment
(
    id         int auto_increment
        primary key,
    content    varchar(255) null,
    created_at datetime(6) null,
    updated_at datetime(6) null,
    article_id int null,
    author_id  binary(16) null,
    constraint FK5yx0uphgjc6ik6hb82kkw501y
        foreign key (article_id) references article (id),
    constraint FKir20vhrx08eh4itgpbfxip0s1
        foreign key (author_id) references users (id)
);

create table users_follow
(
    follower_id  binary(16) not null,
    following_id binary(16) not null,
    primary key (follower_id, following_id),
    constraint FK2tt26aqxu5wkdfwk09ho9e3l2
        foreign key (follower_id) references users (id),
    constraint FKfa0lm5cjf528x4wa1si8yg5u8
        foreign key (following_id) references users (id)
);
