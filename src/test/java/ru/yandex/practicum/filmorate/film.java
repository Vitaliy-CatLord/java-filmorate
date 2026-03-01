package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class film {
    private static final Instant MIN_TIME_OF_RELEASE = LocalDateTime.of(1895, 12, 28, 12, 0)
            .atZone(ZoneId.of("Europe/Paris"))
            .toInstant();

    private static final FilmController filmController = new FilmController();

    //variants of normal films
    Film filmAllField = new Film("normallName", "normallDescription", MIN_TIME_OF_RELEASE.plus(6000, ChronoUnit.HOURS), Duration.ofMinutes(90));
    Film filmWithoutDescription = new Film("descName",  MIN_TIME_OF_RELEASE.plus(6000, ChronoUnit.HOURS), Duration.ofMinutes(90));
    Film filmWithoutReleaseDate = new Film("releaseName", "releaseDescription", Duration.ofMinutes(90));
    Film filmWithoutDuration = new Film("duratName", "duratDescription", MIN_TIME_OF_RELEASE.plus(6000, ChronoUnit.HOURS));
    Film filmOnlyName =  new Film("onlyName");;

    @BeforeEach
    void cleanStorage(){
        filmController.cleanStorage();
    }

    @Test
    void testFilmGetReturnEmpty() {
        assertEquals(0 , filmController.getAllFilms().size());
    }

    @Test
    void testFilmPostGetReturnOneFilm(){
        filmController.postFilm(filmAllField);
        assertEquals(1 , filmController.getAllFilms().size());
        assertEquals("[" + filmAllField.toString() + "]", filmController.getAllFilms().toString());
    }

    @Test
    void testFilmPostGetReturnFiveNormalTypeFilms(){
        filmController.postFilm(filmAllField);
        filmController.postFilm(filmWithoutDescription);
        filmController.postFilm(filmWithoutReleaseDate);
        filmController.postFilm(filmWithoutDuration);
        filmController.postFilm(filmOnlyName);
        assertEquals(5 , filmController.getAllFilms().size());
    }

    @Test
    void testFilmPostSetID() {
        Film Film = filmController.postFilm(filmAllField);
        assertNotNull(Film.getId());
    }

    @Test
    void testFilmPostErrorName() {
        Film FilmBlank = new Film("   ");
        ValidationException exp = assertThrows(
                ValidationException.class, () -> filmController.postFilm(FilmBlank));
        assertEquals("Незаполненно поле Название фильма", exp.getMessage());
    }

    @Test
    void testFilmPostErrorDescription() {
        String noLongDescrip = "L".repeat(200);
        Film FilmNoLongDescrip = new Film("notLongName", noLongDescrip);
        filmController.postFilm(FilmNoLongDescrip);
        assertEquals(1 , filmController.getAllFilms().size());

        String soLongDescrip = "L".repeat(201);
        Film FilmsoLongDescrip = new Film("soLongName", soLongDescrip);
        ValidationException exp = assertThrows(
                ValidationException.class, () -> filmController.postFilm(FilmsoLongDescrip));
        assertEquals("Максимальная длина описания — 200 символов", exp.getMessage());
    }

    @Test
    void testFilmPostErrorRelease() {
        Film FilmEarlyRelease = new Film("earlyReleaseName", MIN_TIME_OF_RELEASE.minus(6000, ChronoUnit.HOURS));
        Film FilmMinRelease = new Film("minReleaseName", MIN_TIME_OF_RELEASE);
        Film FilmNormRelease = new Film("normReleaseName", MIN_TIME_OF_RELEASE.plus(6000, ChronoUnit.HOURS));
        Film FilmMaxRelease = new Film("maxReleaseName", Instant.now());
        Film FilmLateRelease = new Film("lateReleaseName", Instant.now().plus(6000, ChronoUnit.HOURS));
        ValidationException expEarly = assertThrows(
                ValidationException.class, () -> filmController.postFilm(FilmEarlyRelease));
        assertEquals("Дата релиза указана неверно", expEarly.getMessage());
        filmController.postFilm(FilmMinRelease);
        filmController.postFilm(FilmNormRelease);
        filmController.postFilm(FilmMaxRelease);
        assertEquals(3 , filmController.getAllFilms().size());
        ValidationException expLate = assertThrows(
                ValidationException.class, () -> filmController.postFilm(FilmLateRelease));
        assertEquals("Дата релиза указана неверно", expLate.getMessage());
    }

    @Test
    void testFilmPutReturnModified() {
        Film FilmOld = filmController.postFilm(filmOnlyName);
        FilmOld.setName("newName");
        FilmOld.setDescription("newDescription");
        FilmOld.setReleaseDate(Instant.now().minus(10000, ChronoUnit.HOURS));
        Film FilmNew = filmController.putFilm(FilmOld);
        assertEquals(1 , filmController.getAllFilms().size());
        assertEquals(FilmOld.toString() , FilmNew.toString());
    }

    @Test
    void testFilmPutReturnModifiedAndOld() {
        Film FilmFirst = filmController.postFilm(filmWithoutDescription);
        Film FilmOld = filmController.postFilm(filmOnlyName);
        FilmOld.setName("newName");
        FilmOld.setDescription("newDescription");
        FilmOld.setReleaseDate(Instant.now().minus(10000, ChronoUnit.HOURS));
        filmController.putFilm(FilmOld);
        assertEquals(2 , filmController.getAllFilms().size());
        assertEquals("[" + FilmFirst.toString()+ ", " + FilmOld.toString() + "]",
                filmController.getAllFilms().toString());
    }

    @Test
    void testFilmPutErrorBadId() {
        filmController.postFilm(filmAllField);
        filmController.postFilm(filmWithoutDescription);
        filmController.postFilm(filmWithoutReleaseDate);
        filmController.postFilm(filmOnlyName);

        Film FilmNullId = filmAllField;
        FilmNullId.setId(null);
        ValidationException expNull = assertThrows(
                ValidationException.class, () -> filmController.putFilm(FilmNullId));
        assertEquals("Id должен быть указан", expNull.getMessage());

        Film FilmBadId = filmAllField;
        FilmBadId.setId((long)7);
        ValidationException expBad = assertThrows(
                ValidationException.class, () -> filmController.putFilm(FilmBadId));
        assertEquals("Фильма с таким ID не существует", expBad.getMessage());

        assertEquals(4 , filmController.getAllFilms().size());
    }

    @Test
    void testFilmPutErrorName() {
        Film FilmBlank = new Film((long)3, "   ");
        ValidationException exp = assertThrows(
                ValidationException.class, () -> filmController.postFilm(FilmBlank));
        assertEquals("Незаполненно поле Название фильма", exp.getMessage());
    }

    @Test
    void testFilmPutErrorDescription() {
        filmController.postFilm(filmOnlyName);
        String noLongDescrip = "L".repeat(200);
        Film filmNoLongDescrip = new Film((long)1, "notLongName");
        filmNoLongDescrip.setDescription(noLongDescrip);
        filmController.putFilm(filmNoLongDescrip);
        assertEquals(1 , filmController.getAllFilms().size());

        String soLongDescrip = "L".repeat(201);
        Film filmSoLongDescrip = new Film((long)1, "soLongName");
        filmSoLongDescrip.setDescription(soLongDescrip);
        ValidationException exp = assertThrows(
                ValidationException.class, () -> filmController.putFilm(filmSoLongDescrip));
        assertEquals("Максимальная длина описания — 200 символов", exp.getMessage());
    }

    @Test
    void testFilmPutErrorRelease() {
        filmController.postFilm(filmOnlyName);
        Film FilmEarlyRelease = new Film((long)1, "earlyReleaseName", MIN_TIME_OF_RELEASE.minus(6000, ChronoUnit.HOURS));
        Film FilmMinRelease = new Film((long)1, "minReleaseName", MIN_TIME_OF_RELEASE);
        Film FilmNormRelease = new Film((long)1, "normReleaseName", MIN_TIME_OF_RELEASE.plus(6000, ChronoUnit.HOURS));
        Film FilmMaxRelease = new Film((long)1, "maxReleaseName", Instant.now());
        Film FilmLateRelease = new Film((long)1, "lateReleaseName", Instant.now().plus(6000, ChronoUnit.HOURS));
        ValidationException expEarly = assertThrows(
                ValidationException.class, () -> filmController.putFilm(FilmEarlyRelease));
        assertEquals("Дата релиза указана неверно", expEarly.getMessage());
        filmController.putFilm(FilmMinRelease);
        filmController.putFilm(FilmNormRelease);
        filmController.putFilm(FilmMaxRelease);
        assertEquals(1 , filmController.getAllFilms().size());
        ValidationException expLate = assertThrows(
                ValidationException.class, () -> filmController.putFilm(FilmLateRelease));
        assertEquals("Дата релиза указана неверно", expLate.getMessage());
    }
}
