package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.BookRepository;
import com.example.my_book_shop_app.repositories.RatingRepository;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.book.rating.RatingEntity;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BooksRatingAndPopulatityService {

    private final BookRepository bookRepository;
    private final RatingRepository ratingRepository;
    private static final double RATING = 2;

    @Autowired
    public BooksRatingAndPopulatityService(BookRepository bookRepository, RatingRepository ratingRepository) {
        this.bookRepository = bookRepository;
        this.ratingRepository = ratingRepository;
    }

    public List<Book> getPageOfPopularBooksBySql(Integer offset, Integer limit) {
        offset = offset * limit;
        return bookRepository.getPageOfPopularBooks(RATING, offset, limit);
    }

    public Integer getCountOfPopularBooks() {
        return bookRepository.getPopularBooks(RATING).size();
    }

    public boolean addRatingToBook(String slug, UserEntity user, Integer value) {
        Book book = bookRepository.findBookBySlug(slug);
        RatingEntity rating = this.ratingRepository.findRatingEntityByBookAndUser(book, user);
        if(rating == null) {
            rating = new RatingEntity();
            rating.setRatingCount(value);
            rating.setBook(this.bookRepository.findBookBySlug(slug));
            rating.setUser(user);
        } else {
            rating.setRatingCount(value);
        }
        this.ratingRepository.save(rating);
        return true;
    }
}
