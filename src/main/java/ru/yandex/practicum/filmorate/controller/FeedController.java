package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.service.FeedService;

import java.util.List;

@Slf4j
@RestController
@RestControllerAdvice
@RequestMapping("/users")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping("{id}/feed")
    public List<EventDto> getUserFeeds(@PathVariable Long id) {
        return feedService.getUserFeeds(id);
    }
}
