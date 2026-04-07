CREATE TABLE "user" (
  "id" bigint PRIMARY KEY,
  "email" varchar NOT NULL,
  "login" varchar,
  "name" varchar,
  "birthday" date
);

CREATE TABLE "friendList" (
  "id" integer PRIMARY KEY,
  "user_id" bigint,
  "friend_id" bigint,
  "friendshipStatus_id" integer
);

CREATE TABLE "friendshipStatus" (
  "friendshipStatus_id" integer PRIMARY KEY,
  "statusName" varchar NOT NULL
);

CREATE TABLE "film" (
  "id" bigint PRIMARY KEY,
  "name" varchar NOT NULL,
  "description" varchar,
  "releaseDate" date,
  "duration" integer,
  "mpaRating_id" integer
);

CREATE TABLE "likes" (
  "id" bigint PRIMARY KEY,
  "film_id" bigint,
  "user_id" bigint
);

CREATE TABLE "film_genres" (
  "id" integer PRIMARY KEY,
  "film_id" bigint,
  "genre_id" integer
);

CREATE TABLE "genre" (
  "genre_id" integer PRIMARY KEY,
  "name" varchar NOT NULL
);

CREATE TABLE "mpaRating" (
  "mpaRating_id" integer PRIMARY KEY,
  "name" varchar NOT NULL
);

ALTER TABLE "likes" ADD FOREIGN KEY ("film_id") REFERENCES "film" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "likes" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "friendshipStatus" ADD FOREIGN KEY ("friendshipStatus_id") REFERENCES "friendList" ("friendshipStatus_id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "friendList" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "friendList" ADD FOREIGN KEY ("friend_id") REFERENCES "user" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "film_genres" ADD FOREIGN KEY ("film_id") REFERENCES "film" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "film_genres" ADD FOREIGN KEY ("genre_id") REFERENCES "genre" ("genre_id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "film" ADD FOREIGN KEY ("mpaRating_id") REFERENCES "mpaRating" ("mpaRating_id") DEFERRABLE INITIALLY IMMEDIATE;

-- Defer constraint checking for INSERT
BEGIN;
SET CONSTRAINTS ALL DEFERRED;

INSERT INTO "friendshipStatus" ("friendshipStatus_id", "statusName")
VALUES
  (1, 'REQUEST'),
  (2, 'UNCONFIRMED'),
  (3, 'CONFIRMED');
INSERT INTO "genre" ("genre_id", "name")
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
INSERT INTO "mpaRating" ("mpaRating_id", "name")
VALUES
  (1, 'G'),
  (2, 'PG'),
  (3, 'PG_13'),
  (4, 'R'),
  (5, 'NC_17');

SET CONSTRAINTS ALL IMMEDIATE;
COMMIT;