MERGE  INTO friendshipStatus (friendshipStatus_id, statusName)
VALUES
  (1, 'REQUEST'),
  (2, 'UNCONFIRMED'),
  (3, 'CONFIRMED');
MERGE  INTO genres (genre_id, name)
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
MERGE  INTO mpaRating (mpaRating_id, name)
VALUES
  (1, 'G'),
  (2, 'PG'),
  (3, 'PG_13'),
  (4, 'R'),
  (5, 'NC_17');