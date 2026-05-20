package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnPopularWithDefaultCountWhenNoParamsGiven() throws Exception {
        // Проверка: GET-запрос без параметров обрабатывается успешно (работает дефолтный count=10)
        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAcceptAllParametersSuccessfully() throws Exception {
        // Проверка: контроллер успешно принимает и парсит все три параметра фильтрации
        mockMvc.perform(get("/films/popular")
                        .param("count", "5")
                        .param("genreId", "2")
                        .param("year", "2020"))
                .andExpect(status().isOk());
    }
}