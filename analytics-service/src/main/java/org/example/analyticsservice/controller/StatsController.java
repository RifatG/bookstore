package org.example.analyticsservice.controller;

import java.util.List;
import org.example.analyticsservice.model.BookViewStats;
import org.example.analyticsservice.repository.BookViewStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    @Autowired
    private BookViewStatsRepository statsRepository;

    @GetMapping("/api/popular")
    public List<BookViewStats> getPopularBooks(@RequestParam(defaultValue = "10") int limit) {
        return statsRepository.findTopN(limit);
    }
}