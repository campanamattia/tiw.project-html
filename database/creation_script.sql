create table USER(
	UserName varchar(50) primary key,
    Password varchar(50) not null
);

create table ALBUM(
	Id int primary key auto_increment,
    Title varchar(50) not null,
    FileImage varchar(50) not null,
    Singer varchar(50) not null,
    PublicationYear year not null
);

create table SONG(
	Id int primary key auto_increment,
    Title varchar(50) not null,
    Genre varchar(50) not null,
    FileAudio varchar(50) not null,
    User varchar(50) not null references USER(UserName) on delete cascade on update cascade,
    Album int not null references ALBUM(Id) on delete cascade on update cascade
);

create table PLAYLIST(
    Name varchar(50) not null,
    UserName varchar(50) not null references USER(UserName) on delete cascade on update cascade,
    CreationDate date not null,
    primary key(Name,UserName)
);

create table CONTAINS(
	PlaylistName varchar(50) not null,
	PlaylistUser varchar(50) not null,
    Song int not null references SONG(Id) on delete cascade on update cascade,
    primary key(PlaylistName,PlaylistUser,Song),
    foreign key (PlaylistName,PlaylistUser) references PLAYLIST(Name,UserName) on delete cascade on update cascade
)