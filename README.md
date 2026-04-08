# Java-filmorate
BackEnd- приложение для оценки фильма пользователями. Кроме возможности ставить фильмам лайки, реализована возможность 
добавлять контент, управлять пользователю списком его друзей

## Схема базы данных
Для визуализации схемы используется DMBL, схема в файле BD_diagram, код расположен в файле BD_code
* **Users** - информация о профиле юзера
* **FriendList** - таблица связи/дружбы между пользователями
* **FriendshipStatus** - таблица статусов дружбы
* **LikesUserID** - учет лайкнутых пользователями фильмов
* **Film** - информация о фильме
* **MpaRating** - таблица с возрастными рейтингами
* **Film_genres** - связующая таблица для жанров музыки
* **Genres** - справочник возможных жанров для фильма

## Возможные SQL-запросы

* ### Работа с юзерами
Выгрузка всех пользователей
```SQL
SELECT *
FROM users;
```
Выгрузка определенного пользователя
```SQL
SELECT *
FROM users
WHERE id = {id};
```
* ### Работа со списком друзей
Выгрузка имен всех друзей пользователя 37
```SQL
SELECT name
FROM users
WHERE id IN ( SELECT friend_id
              FROM friendList
              WHERE user_id=37
              AND frienshipStatus_id=3
) ;
```
Выгрузка всех общих друзей пользователя 37 с пользователем 11
```SQL
SELECT u1.name AS user_name, u2.name AS friend_name
FROM users u1
INNER JOIN friendList fl ON u1.id = fl.user_id
INNER JOIN users u2 ON fl.friend_id = u2.id
WHERE fl.friendshipStatus_id = 3
    AND u1.id = 37
    AND u2.id = 11;
```
* ### Работа со списком лайками
Выгрузка названий ТОП-10 фильмов по кол-ву лайков
```SQL
SELECT f.name
        COUNT(l.user_id) AS like_count
FROM film f
INNER JOIN likes l ON l.film_id=f.id
GROUP BY f.id, f.name
ORDER BY like_count DESC
LIMIT 10;
```

* ### Работа с фильмами
Выгрузка всех фильмов из базы
```SQL
SELECT *
FROM film;
```
Получение списка понравившихся фильмов юзеру 11
```SQL
SELECT f.name AS film_name, u.name AS user_name
FROM film f
JOIN likes l ON l.film_id=f.id
JOIN user u ON u.id=l.user_id
WHERE l.user_id=11;

```
Получение списка жанров для фильма 17
```SQL
SELECT f.name AS film_name, 
FROM film f
JOIN film_genres fg ON fg.film_id=f.id
JOIN genre g ON g.genre_id=fg.genre_id
```

