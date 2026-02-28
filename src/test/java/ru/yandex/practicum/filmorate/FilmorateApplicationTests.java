package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

	private static final Instant MIN_TIME_OF_BIRTHDAY = LocalDateTime.of(1909, 8, 21, 0, 0)
			.atZone(ZoneId.of("Europe/Paris"))
			.toInstant();

	private static final FilmController filmController = new FilmController();
	private static final UserController userController = new UserController();
	//variants of normal users
	User userAllField = new User("normall@mail", "normallLogin", "normallName", MIN_TIME_OF_BIRTHDAY.plus(6000, ChronoUnit.HOURS));
	User userWithoutBirthday = new User("normBirt@mail", "normBirthLogin", "normBirthName");
	User userWithoutName = new User("normName@mail", "normNameLogin",  MIN_TIME_OF_BIRTHDAY.plus(10000, ChronoUnit.HOURS));
	User userOnlyNeeded = new User("normNeed@mail", "normNeedLogin");
	//variants of normal films


	@BeforeEach
	void cleanStorage(){
		filmController.cleanStorage();
		userController.cleanStorage();
	}

	//сначала тесты для Users
	@Test
	void testGetReturnEmpty() {
		assertEquals(0 , userController.getAllUsers().size());
	}

	@Test
	void testPostGetReturnOneUser(){
		userController.postUser(userAllField);
		assertEquals(1 , userController.getAllUsers().size());
		assertEquals("[" + userAllField.toString() + "]", userController.getAllUsers().toString());
	}

	@Test
	void testPostGetReturnFourNormalTypeUsers(){
		userController.postUser(userAllField);
		userController.postUser(userWithoutBirthday);
		userController.postUser(userWithoutName);
		userController.postUser(userOnlyNeeded);
		assertEquals(4 , userController.getAllUsers().size());
	}

	@Test
	void testPostSetID() {
		User user = userController.postUser(userAllField);
		assertNotNull(user.getId());
	}

	@Test
	void testPostErrorEmailBlank() {
		User userBlank = new User("   ", "dogLogin");
		ValidationException exp = assertThrows(
				ValidationException.class, () -> userController.postUser(userBlank));
		assertEquals("Емеил должен быть заполнен", exp.getMessage());
	}

	@Test
	void testPostErrorEmailDogless() {
		User userWithoutDog = new User("dogMail", "dogLogin");
		ValidationException exp = assertThrows(
				ValidationException.class, () -> userController.postUser(userWithoutDog));
		assertEquals("Емаил должен содержать @", exp.getMessage());
	}

	@Test
	void testPostErrorEmailEmployed() {
		User userMail = new User("Mail@", "mailLogin");
		User userSameMail = new User("Mail@", "secondLogin");
		userController.postUser(userMail);
		DuplicatedDataException exp = assertThrows(
				DuplicatedDataException.class, () -> userController.postUser(userSameMail));
		assertEquals("Данный емеил занят", exp.getMessage());
	}

	@Test
	void testPostErrorLoginBlank() {
		User user = new User("mail@", "");
		ValidationException exp = assertThrows(
				ValidationException.class, () -> userController.postUser(user));
		assertEquals("Неверно указан логин", exp.getMessage());
	}

	@Test
	void testPostErrorLoginSpace() {
		User user = new User("mail@", "with space");
		ValidationException exp = assertThrows(
				ValidationException.class, () -> userController.postUser(user));
		assertEquals("Логин не должен содержать пробелов", exp.getMessage());
	}

	@Test
	void testPostWithoutNameAKALogin() {
		User user = userController.postUser(userOnlyNeeded);
		assertEquals(user.getName(), user.getLogin());
	}

	@Test
	void testPostErrorBirthday(){
		User userPast = new User("normName@mail", "normNameLogin",  MIN_TIME_OF_BIRTHDAY.minus(10000, ChronoUnit.HOURS));
		User userFuture = new User("normName@mail", "normNameLogin",  Instant.now().plus(10000, ChronoUnit.HOURS));
		ValidationException expPast = assertThrows(
				ValidationException.class, () -> userController.postUser(userPast));
		assertEquals("Неверно указана дата рождения", expPast.getMessage());
		ValidationException expFuture = assertThrows(
				ValidationException.class, () -> userController.postUser(userFuture));
		assertEquals("Неверно указана дата рождения", expPast.getMessage());
	}

	@Test
	void testPutReturnModified() {
		User userOld = userController.postUser(userOnlyNeeded);
		userOld.setName("newName");
		userOld.setLogin("newLogin");
		userOld.setBirthday(Instant.now().minus(10000, ChronoUnit.HOURS));
		User userNew = userController.putUser(userOld);
		assertEquals(1 , userController.getAllUsers().size());
		assertEquals(userOld.toString() , userNew.toString());
	}

	@Test
	void testPutReturnModifiedAndOld() {
		User userFirst = userController.postUser(userWithoutBirthday);
		User userOld = userController.postUser(userOnlyNeeded);
		userOld.setName("newName");
		userOld.setLogin("newLogin");
		userOld.setBirthday(Instant.now().minus(10000, ChronoUnit.HOURS));
		User userNew = userController.putUser(userOld);
		assertEquals(2 , userController.getAllUsers().size());
		assertEquals("[" + userFirst.toString()+ ", " + userOld.toString() + "]",
				userController.getAllUsers().toString());
	}

	@Test
	void testPutErrorBadId() {
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
	void testPutErrorEmail() {
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





}
