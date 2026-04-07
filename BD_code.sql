CREATE TABLE "User" (
  "id" bigint PRIMARY KEY,
  "email" varchar NOT NULL,
  "login" varchar,
  "name" varchar,
  "birthday" date
);

CREATE TABLE "FriendList" (
  "id" integer PRIMARY KEY,
  "user_id" bigint,
  "friend_id" bigint,
  "friendshipStatus_id" integer
);

CREATE TABLE "FriendshipStatus" (
  "friendshipStatus_id" integer PRIMARY KEY,
  "statusName" varchar NOT NULL
);

CREATE TABLE "Film" (
  "id" bigint PRIMARY KEY,
  "name" varchar NOT NULL,
  "description" varchar,
  "releaseDate" date,
  "duration" integer,
  "mpaPating_id" integer
);

CREATE TABLE "LikesUserId" (
  "id" bigint PRIMARY KEY,
  "film_id" bigint,
  "user_id" bigint
);

CREATE TABLE "film_genres" (
  "id" integer PRIMARY KEY,
  "film_id" bigint,
  "genre_id" integer
);

CREATE TABLE "Genre" (
  "genre_id" integer PRIMARY KEY,
  "name" varchar NOT NULL
);

CREATE TABLE "MpaRating" (
  "mpaRating_id" integer PRIMARY KEY,
  "name" varchar NOT NULL
);

ALTER TABLE "LikesUserId" ADD FOREIGN KEY ("film_id") REFERENCES "Film" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "LikesUserId" ADD FOREIGN KEY ("user_id") REFERENCES "User" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "FriendshipStatus" ADD FOREIGN KEY ("friendshipStatus_id") REFERENCES "FriendList" ("friendshipStatus_id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "FriendList" ADD FOREIGN KEY ("friend_id") REFERENCES "User" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "film_genres" ADD FOREIGN KEY ("film_id") REFERENCES "Film" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "film_genres" ADD FOREIGN KEY ("genre_id") REFERENCES "Genre" ("genre_id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "Film" ADD FOREIGN KEY ("mpaPating_id") REFERENCES "MpaRating" ("mpaRating_id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "FriendList" ADD FOREIGN KEY ("user_id") REFERENCES "User" ("id") DEFERRABLE INITIALLY IMMEDIATE;

-- Defer constraint checking for INSERT
BEGIN;
SET CONSTRAINTS ALL DEFERRED;

INSERT INTO "FriendshipStatus" ("friendshipStatus_id", "statusName")
VALUES
  (1, 'REQUEST'),
  (2, 'UNCONFIRMED'),
  (3, 'CONFIRMED');
INSERT INTO "Genre" ("genre_id", "name")
VALUES
  (1, 'COMEDY '),
  (2, 'DRAMA'),
  (3, 'ANIMATION'),
  (4, 'THRILLER'),
  (5, 'DOCUMENTARY'),
  (6, 'ACTION'),
  (7, 'ROMANCE'),
  (8, 'HORROR'),
  (9, 'SCI_FI'),
  (10, 'FANTASY'),
  (11, 'ADVENTURE'),
  (12, 'CRIME'),
  (13, 'MYSTERY'),
  (14, 'BIOGRAPHY'),
  (15, 'HISTORY'),
  (16, 'MUSICAL'),
  (17, 'WESTERN'),
  (18, 'SPORT'),
  (19, 'WAR'),
  (20, 'FAMILY');
INSERT INTO "MpaRating" ("mpaRating_id", "name")
VALUES
  (1, 'G'),
  (2, 'PG'),
  (3, 'PG_13'),
  (4, 'R'),
  (5, 'NC_17');

SET CONSTRAINTS ALL IMMEDIATE;
COMMIT;