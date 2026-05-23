package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DirectorService {
    DirectorDbStorage directorStorage;

    public List<DirectorDto> getAllDirectors() {
        return directorStorage.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public DirectorDto getDirectorById(long id) {
        return directorStorage.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Режиссёр с id " + id + " не найден"));
    }

    public DirectorDto createDirector(DirectorDto dto) {
        Director director = toModel(dto);
        Director saved = directorStorage.save(director);
        log.info("Добавлен режиссёр: {}", saved);
        return toDto(saved);
    }

    public DirectorDto updateDirector(DirectorDto dto) {
        directorStorage.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Режиссёр с id " + dto.getId() + " не найден"));
        Director director = toModel(dto);
        Director updated = directorStorage.update(director);
        log.info("Обновлён режиссёр: {}", updated);
        return toDto(updated);
    }

    public void deleteDirector(long id) {
        directorStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Режиссёр с id " + id + " не найден"));
        directorStorage.delete(id);
        log.info("Удалён режиссёр с id {}", id);
    }

    private DirectorDto toDto(Director director) {
        DirectorDto dto = new DirectorDto();
        dto.setId(director.getDirectorId());
        dto.setName(director.getName());
        return dto;
    }

    private Director toModel(DirectorDto dto) {
        Director director = new Director();
        director.setDirectorId(dto.getId());
        director.setName(dto.getName());
        return director;
    }
}