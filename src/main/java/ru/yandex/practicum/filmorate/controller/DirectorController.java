package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;
    private static final String DIRECTOR_PATH = "/{id}";

    @GetMapping
    public List<DirectorDto> getAllDirectors() {
        log.info("Выполнение запроса на получение всех режиссёров");
        return directorService.getAllDirectors();
    }

    @GetMapping(DIRECTOR_PATH)
    public DirectorDto getDirectorById(@PathVariable long id) {
        log.info("Выполнение запроса на получение режиссёра с ID {}", id);
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public DirectorDto createDirector(@Valid @RequestBody DirectorDto dto) {
        log.info("Выполнение запроса на создание режиссёра {}", dto);
        return directorService.createDirector(dto);
    }

    @PutMapping
    public DirectorDto updateDirector(@Valid @RequestBody DirectorDto dto) {
        log.info("Выполнение запроса на обновление режиссёра {}", dto);
        return directorService.updateDirector(dto);
    }

    @DeleteMapping(DIRECTOR_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDirector(@PathVariable long id) {
        log.info("Выполнение запроса на удаление режиссёра с ID {}", id);
        directorService.deleteDirector(id);
    }
}