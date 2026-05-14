package org.example.analyticsservice.listener;

import java.time.Instant;
import com.example.my_book_shop_app.event.BookViewEvent;
import org.example.analyticsservice.model.BookViewStats;
import org.example.analyticsservice.repository.BookViewStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BookViewEventListener {

    private static final Logger log = LoggerFactory.getLogger(BookViewEventListener.class);

    @Autowired
    private BookViewStatsRepository statsRepository;

    @KafkaListener(topics = "book-views", groupId = "analytics-group")
    public void handleBookViewEvent(BookViewEvent event) {
        log.info("Received event: {}", event);

        BookViewStats stats = statsRepository.findByBookId(event.bookId())
                .orElse(new BookViewStats());

        stats.setBookId(event.bookId());
        stats.setBookSlug(event.bookSlug());
        stats.setBookTitle(event.bookTitle());
        stats.setViews(stats.getViews() + 1);
        stats.setLastViewed(Instant.now());

        statsRepository.save(stats);

        log.info("Updated stats for book {}: {} views", event.bookId(), stats.getViews());
    }
}