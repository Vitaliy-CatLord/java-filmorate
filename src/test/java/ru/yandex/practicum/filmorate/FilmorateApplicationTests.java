package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

	private static final LocalDate MIN_TIME_OF_BIRTHDAY = LocalDate.of(1909, 8, 21);
	private static final LocalDate MIN_TIME_OF_RELEASE = LocalDate.of(1895, 12, 28);

	private static final FilmController filmController = new FilmController();
	private static final UserController userController = new UserController();
	//variants of normal users
	User userAllField = new User("normall@mail", "normallLogin", "normallName", MIN_TIME_OF_BIRTHDAY.plusYears(50));
	User userWithoutBirthday = new User("normBirt@mail", "normBirthLogin", "normBirthName");
	User userWithoutName = new User("normName@mail", "normNameLogin",  MIN_TIME_OF_BIRTHDAY.plusYears(60));
	User userOnlyNeeded = new User("normNeed@mail", "normNeedLogin");
	//variants of normal films
	Film filmAllField = new Film("normallName", "normallDescription", MIN_TIME_OF_RELEASE.plusYears(50), 90);
	Film filmWithoutDescription = new Film("descName",  MIN_TIME_OF_RELEASE.plusYears(60), 90);
	Film filmWithoutReleaseDate = new Film("releaseName", "releaseDescription", 90);
	Film filmWithoutDuration = new Film("duratName", "duratDescription", MIN_TIME_OF_RELEASE.plusYears(70));
	Film filmOnlyName =  new Film("onlyName");;

	@BeforeEach
	void cleanStorage(){
		filmController.cleanStorage();
		userController.cleanStorage();
	}

	//сначала тесты для Users
	@Test
	void testUserGetReturnEmpty() {
		assertEquals(0 , userController.getAllUsers().size());
	}

	@Test
	void testUserPostGetReturnOneUser(){
		userController.postUser(userAllField);
		assertEquals(1 , userController.getAllUsers().size());
		assertEquals("[" + userAllField.toString() + "]", userController.getAllUsers().toString());
	}

	@Test
	void testUserPostGetReturnFourNormalTypeUsers(){
		userController.postUser(userAllField);
		userController.postUser(userWithoutBirthday);
		userController.postUser(userWithoutName);
		userController.postUser(userOnlyNeeded);
		assertEquals(4 , userController.getAllUsers().size());
	}

	@Test
	void testUserPostSetID() {
		User user = userController.postUser(userAllField);
		assertNotNull(user.getId());
	}

	@Test
	void testUserPostErrorEmailBlank() {
		User userBlank = new User("   ", "blankLogin");
		ValidationException expBlank = assertThrows(
				ValidationException.class, () -> userController.postUser(userBlank));
		assertEquals("Емеил должен быть заполнен", expBlank.getMessage());

		User userWithoutDog = new User("dogMail", "dogLogin");
		ValidationException expDog = assertThrows(
				ValidationException.class, () -> userController.postUser(userWithoutDog));
		assertEquals("Емаил должен содержать @", expDog.getMessage());

		User userMail = new User("Mail@", "mailLogin");
		User userSameMail = new User("Mail@", "secondLogin");
		userController.postUser(userMail);
		DuplicatedDataException expEmployed = assertThrows(
				DuplicatedDataException.class, () -> userController.postUser(userSameMail));
		assertEquals("Данный емеил занят", expEmployed.getMessage());
	}

		@Test
	void testUserPostErrorLoginBlank() {
		User userBlank = new User("mail@", "");
		ValidationException expBlank = assertThrows(
				ValidationException.class, () -> userController.postUser(userBlank));
		assertEquals("Неверно указан логин", expBlank.getMessage());

			User userSpace = new User("mail@", "with space");
			ValidationException expSpace = assertThrows(
					ValidationException.class, () -> userController.postUser(userSpace));
			assertEquals("Логин не должен содержать пробелов", expSpace.getMessage());

			User user = userController.postUser(userOnlyNeeded);
			assertEquals(1 , userController.getAllUsers().size());
			assertEquals(user.getName(), user.getLogin());

	}

	@Test
	void testUserPostErrorBirthday(){
		User userPast = new User("normName@mail", "normNameLogin",  MIN_TIME_OF_BIRTHDAY.minusDays(1));
		User userFuture = new User("normName@mail", "normNameLogin",  LocalDate.now().plusDays(1));
		ValidationException expPast = assertThrows(
				ValidationException.class, () -> userController.postUser(userPast));
		assertEquals("Неверно указана дата рождения", expPast.getMessage());
		ValidationException expFuture = assertThrows(
				ValidationException.class, () -> userController.postUser(userFuture));
		assertEquals("Неверно указана дата рождения", expPast.getMessage());
	}

	@Test
	void testUserPutReturnModified() {
		User userOld = userController.postUser(userOnlyNeeded);
		userOld.setName("newName");
		userOld.setLogin("newLogin");
		userOld.setBirthday(LocalDate.now().minusDays(1));
		User userNew = userController.putUser(userOld);
		assertEquals(1 , userController.getAllUsers().size());
		assertEquals(userOld.toString() , userNew.toString());
	}

	@Test
	void testUserPutReturnModifiedAndOld() {
		User userFirst = userController.postUser(userWithoutBirthday);
		User userOld = userController.postUser(userOnlyNeeded);
		userOld.setName("newName");
		userOld.setLogin("newLogin");
		userOld.setBirthday(LocalDate.now().minusDays(1));
		User userNew = userController.putUser(userOld);
		assertEquals(2 , userController.getAllUsers().size());
		assertEquals("[" + userFirst.toString()+ ", " + userOld.toString() + "]",
				userController.getAllUsers().toString());
	}

	@Test
	void testUserPutErrorBadId() {
		userController.postUser(userAllField);
		userController.postUser(userWithoutBirthday);
		userController.postUser(userWithoutName);
		userController.postUser(userOnlyNeeded);

		User userNullId = userAllField;
		userNullId.setId(null);
		ValidationException expNull = assertThrows(
				ValidationException.class, () -> userController.putUser(userNullId));
		assertEquals("Id должен быть указан", expNull.getMessage());

		User userBadId = userAllField;
		userBadId.setId((long)7);
		ValidationException expBad = assertThrows(
				ValidationException.class, () -> userController.putUser(userBadId));
		assertEquals("Пользователя с таким ID не существует", expBad.getMessage());

		assertEquals(4 , userController.getAllUsers().size());
	}

	@Test
	void testUserPutErrorEmail() {
		userController.postUser(userAllField);
		userController.postUser(userWithoutBirthday);
		userController.postUser(userWithoutName);
		userController.postUser(userOnlyNeeded);
		User userEmailBlank = userAllField;
		userEmailBlank.setEmail("   ");
		ValidationException expEmailBlank = assertThrows(
				ValidationException.class, () -> userController.putUser(userEmailBlank));
		assertEquals("Емеил должен быть заполнен", expEmailBlank.getMessage());

		User userEmailDog = userAllField;
		userEmailDog.setEmail("newDogMail");
		ValidationException expEmailDog = assertThrows(
				ValidationException.class, () -> userController.putUser(userEmailDog));
		assertEquals("Емаил должен содержать @", expEmailDog.getMessage());
	}


	//FilmController
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
		Film FilmEarlyRelease = new Film("earlyReleaseName", MIN_TIME_OF_RELEASE.minusDays(1));
		Film FilmMinRelease = new Film("minReleaseName", MIN_TIME_OF_RELEASE);
		Film FilmNormRelease = new Film("normReleaseName", MIN_TIME_OF_RELEASE.plusDays(1));
		Film FilmMaxRelease = new Film("maxReleaseName", LocalDate.now());
		Film FilmLateRelease = new Film("lateReleaseName", LocalDate.now().plusDays(1));
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
		FilmOld.setReleaseDate(LocalDate.now().minusDays(1));
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
		FilmOld.setReleaseDate(LocalDate.now().minusDays(1));
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
		Film FilmEarlyRelease = new Film((long)1, "earlyReleaseName", MIN_TIME_OF_RELEASE.minusDays(1));
		Film FilmMinRelease = new Film((long)1, "minReleaseName", MIN_TIME_OF_RELEASE);
		Film FilmNormRelease = new Film((long)1, "normReleaseName", MIN_TIME_OF_RELEASE.plusDays(1));
		Film FilmMaxRelease = new Film((long)1, "maxReleaseName", LocalDate.now());
		Film FilmLateRelease = new Film((long)1, "lateReleaseName", LocalDate.now().plusDays(1));
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
