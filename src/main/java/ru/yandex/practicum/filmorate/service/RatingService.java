package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.RatingDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoudException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingService {
    RatingDbStorage ratingDbStorage;

    public List<MpaRating> getRating() {
        return ratingDbStorage.findAll();
    }

    public MpaRating getRatingById(long id) {
        return ratingDbStorage.findById(id)
                .orElseThrow(() -> new NotFoudException("Рейтинг не найден"));
    }

}
