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
    RatingDbStorage ratingStorage;

    public List<MpaRating> getRating() {
        return ratingStorage.findAll();
    }

    public MpaRating getRatingById(int id) {
        return ratingStorage.findById(id)
                .orElseThrow(() -> new NotFoudException("Рейтинг не найден"));
    }

}
