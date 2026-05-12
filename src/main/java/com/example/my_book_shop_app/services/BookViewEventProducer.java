package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.event.BookViewEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookViewEventProducer {

    private static final String BOOK_VIEWS_TOPIC = "book-views";

    private final KafkaTemplate<String, BookViewEvent> kafkaTemplate;

    public BookViewEventProducer(KafkaTemplate<String, BookViewEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(BookViewEvent event) {
        kafkaTemplate.send(BOOK_VIEWS_TOPIC, event.bookId().toString(), event);
    }
}
