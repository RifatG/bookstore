package org.example.analyticsservice.streams;

import java.time.Duration;
import com.example.my_book_shop_app.event.BookViewEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JacksonJsonSerde;
import org.springframework.stereotype.Component;
import org.apache.kafka.streams.state.WindowStore;

@Component
public class PopularityTopology {

    @Autowired
    public void buildTopology(StreamsBuilder builder) {
        JacksonJsonSerde<BookViewEvent> bookViewSerde = new JacksonJsonSerde<>(BookViewEvent.class);
        bookViewSerde.ignoreTypeHeaders();
        long windowSize = Duration.ofMinutes(5).toMillis();

        KStream<String, BookViewEvent> views = builder
                .stream("book-views", Consumed.with(Serdes.String(), bookViewSerde));

        TimeWindows window = TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5))
                .advanceBy(Duration.ofMinutes(1));

        views
                .groupBy((key, value) -> value.bookId().toString())
                .windowedBy(window)
                .count(Materialized.as("popularity-store"))
                .toStream()
                .peek((key, count) -> System.out.printf(
                        "[%s] Книга %s: %d просмотров за %s%n",
                        java.time.LocalTime.now(), key.key(), count, key.window()))
                .to("popular-books", Produced.with(WindowedSerdes.timeWindowedSerdeFrom(String.class, windowSize), Serdes.Long()));
    }
}