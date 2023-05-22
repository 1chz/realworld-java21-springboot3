create table realworld.tag
(
    tag_id     int auto_increment primary key,
    created_at datetime(6) not null,
    name       varchar(20) not null,
    constraint UK_1wdpsed5kna2y38hnbgrnhi5b unique (name)
);

create table realworld.users
(
    user_id    binary(16)   not null primary key,
    bio        varchar(500) not null,
    created_at datetime(6)  not null,
    email      varchar(30)  not null,
    image      varchar(100) null,
    password   varchar(200) not null,
    username   varchar(30)  not null,
    constraint UK_6dotkott2kjsp8vw4d0m25fb7 unique (email),
    constraint UK_r43af9ap4edm43mmtq01oddj6 unique (username)
);

create table realworld.article
(
    article_id  int auto_increment primary key,
    content     varchar(1000) not null,
    created_at  datetime(6)   not null,
    description varchar(50)   not null,
    slug        varchar(50)   not null,
    title       varchar(50)   not null,
    updated_at  datetime(6)   null,
    author_id   binary(16)    not null,
    constraint UK_571gx7oqo5xpmgocegaidlcu9 unique (title),
    constraint UK_lc76j4bqg2jrk06np18eve5yj unique (slug),
    constraint FKmjgtny2i22jf4dqncmd436s0u foreign key (author_id) references realworld.users (user_id)
);

create table realworld.article_favorite
(
    article_id int         not null,
    user_id    binary(16)  not null,
    created_at datetime(6) not null,
    primary key (article_id, user_id),
    constraint FK8m1q6r31lmkejfp8l8eka6n93 foreign key (user_id) references realworld.users (user_id),
    constraint FKb8vit5sr2sivwocuy4qxatn8c foreign key (article_id) references realworld.article (article_id)
);

create table realworld.article_tag
(
    article_id int         not null,
    tag_id     int         not null,
    created_at datetime(6) not null,
    primary key (article_id, tag_id),
    constraint FKenqeees0y8hkm7x1p1ittuuye foreign key (article_id) references realworld.article (article_id),
    constraint FKesqp7s9jj2wumlnhssbme5ule foreign key (tag_id) references realworld.tag (tag_id)
);

create table realworld.comment
(
    comment_id int auto_increment primary key,
    content    varchar(500) not null,
    created_at datetime(6)  not null,
    updated_at datetime(6)  null,
    article_id int          not null,
    author_id  binary(16)   not null,
    constraint FK5yx0uphgjc6ik6hb82kkw501y foreign key (article_id) references realworld.article (article_id),
    constraint FKir20vhrx08eh4itgpbfxip0s1 foreign key (author_id) references realworld.users (user_id)
);

create table realworld.user_follow
(
    from_id    binary(16)  not null,
    to_id      binary(16)  not null,
    created_at datetime(6) not null,
    primary key (from_id, to_id),
    constraint FK498fe2qxhhch65s9a2o3cw4sj foreign key (from_id) references realworld.users (user_id),
    constraint FKriwyuiru4jej6dbidctafebf foreign key (to_id) references realworld.users (user_id)
);

