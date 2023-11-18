create table tag
(
    id         int auto_increment
        primary key,
    name       varchar(20) not null,
    created_at datetime(6) not null,
    constraint UK_1wdpsed5kna2y38hnbgrnhi5b
        unique (name)
);

create table users
(
    id         binary(16)   not null
        primary key,
    email      varchar(30)  not null,
    username   varchar(30)  not null,
    password   varchar(200) not null,
    bio        varchar(500) null,
    image_url  varchar(200) null,
    created_at datetime(6)  not null,
    constraint UK_6dotkott2kjsp8vw4d0m25fb7
        unique (email),
    constraint UK_r43af9ap4edm43mmtq01oddj6
        unique (username)
);

create table article
(
    id          int auto_increment
        primary key,
    author_id   binary(16)    not null,
    slug        varchar(50)   not null,
    title       varchar(50)   not null,
    description varchar(50)   not null,
    content     varchar(1000) not null,
    created_at  datetime(6)   not null,
    updated_at  datetime(6)   null,
    constraint UK_a521x5asfhsahfocegwqfqwh1
        unique (slug),
    constraint UK_571gx7oqo5xpmgocegaidlcu9
        unique (title),
    constraint FKmjgtny2i22jf4dqncmd436s0u
        foreign key (author_id) references users (id)
);

create table article_favorite
(
    id         int auto_increment
        primary key,
    article_id int         null,
    user_id    binary(16)  null,
    created_at datetime(6) not null,
    constraint UKpatixq7onikvvq0vw8cg039fu
        unique (user_id, article_id),
    constraint FKb8vit5sr2sivwocuy4qxatn8c
        foreign key (article_id) references article (id),
    constraint FK8m1q6r31lmkejfp8l8eka6n93
        foreign key (user_id) references users (id)
);

create table article_tag
(
    id         int auto_increment
        primary key,
    article_id int         null,
    tag_id     int         null,
    created_at datetime(6) not null,
    constraint UKhd3y4301iwajewaay994i6rpj
        unique (article_id, tag_id),
    constraint FKenqeees0y8hkm7x1p1ittuuye
        foreign key (article_id) references article (id),
    constraint FKesqp7s9jj2wumlnhssbme5ule
        foreign key (tag_id) references tag (id)
);

create table article_comment
(
    id         int auto_increment
        primary key,
    article_id int          not null,
    author_id  binary(16)   not null,
    content    varchar(500) not null,
    created_at datetime(6)  not null,
    constraint FK5yx0uphgjc6ik6hb82kkw501y
        foreign key (article_id) references article (id),
    constraint FKir20vhrx08eh4itgpbfxip0s1
        foreign key (author_id) references users (id)
);

create table user_follow
(
    id           int auto_increment
        primary key,
    follower_id  binary(16)  null,
    following_id binary(16)  null,
    created_at   datetime(6) not null,
    constraint UKp8vhuhxu2u1fm8qg7hvn4y1gs
        unique (follower_id, following_id),
    constraint FKi9gqdjcgtclgypxp2krye61n7
        foreign key (follower_id) references users (id),
    constraint FK474orxbed564dk0mvggvt4xon
        foreign key (following_id) references users (id)
);

