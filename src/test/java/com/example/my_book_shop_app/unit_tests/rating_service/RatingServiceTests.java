package com.example.my_book_shop_app.unit_tests.rating_service;

import com.example.my_book_shop_app.repositories.BookRepository;
import com.example.my_book_shop_app.repositories.RatingRepository;
import com.example.my_book_shop_app.repositories.ReviewLikeRepository;
import com.example.my_book_shop_app.repositories.ReviewRepository;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.services.BooksRatingAndPopulatityService;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.book.rating.RatingEntity;
import com.example.my_book_shop_app.struct.book.review.BookReviewEntity;
import com.example.my_book_shop_app.struct.book.review.BookReviewLikeEntity;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RatingServiceTests {

    private final BooksRatingAndPopulatityService ratingService;
    private final BookService bookService;

    private static final double RATING = 2;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private RatingRepository ratingRepository;

    @MockBean
    private ReviewLikeRepository reviewLikeRepository;

    @MockBean
    private ReviewRepository reviewRepository;

    @Autowired
    public RatingServiceTests(BooksRatingAndPopulatityService ratingService, BookService bookService) {
        this.ratingService = ratingService;
        this.bookService = bookService;
    }

    @Test
    void getPageOfPopularBooksTest() {
        List<Book> popularBooks = Collections.singletonList(new Book());
        int offset = 1;
        int limit = 5;
        Mockito.doReturn(popularBooks).when(bookRepository).getPageOfPopularBooks(RATING, offset, limit);

        assertEquals(popularBooks, ratingService.getPageOfPopularBooksBySql(offset, limit));
        Mockito.verify(bookRepository, Mockito.times(1)).getPageOfPopularBooks(RATING, offset, limit);
    }

    @Test
    void getCountOfPopularBooksTest() {
        Mockito.doReturn(Collections.singletonList(new Book())).when(bookRepository).getPopularBooks(RATING);

        assertEquals(1, ratingService.getCountOfPopularBooks());
        Mockito.verify(bookRepository, Mockito.times(1)).getPopularBooks(RATING);
    }

    @Test
    void getPageOfRecommendedBooksTest() {
        Page<Book> books = new PageImpl<>(Collections.singletonList(new Book()));
        Mockito.doReturn(books).when(bookRepository).findAll(Mockito.any(PageRequest.class));

        assertEquals(books, bookService.getPageOfRecommendedBooks(1,5));
        Mockito.verify(bookRepository, Mockito.times(1)).findAll(Mockito.any(PageRequest.class));
    }

    @Test
    void addRatingToBook() {
        Book book = new Book();
        UserEntity user = new UserEntity();
        String slug = "slug";
        Mockito.doReturn(book).when(bookRepository).findBookBySlug(slug);
        Mockito.doReturn(null).when(ratingRepository).findRatingEntityByBookAndUser(book, user);

        assertTrue(ratingService.addRatingToBook(slug, user, 3));
        Mockito.verify(ratingRepository, Mockito.times(1)).save(Mockito.any(RatingEntity.class));
    }

    @Test
    void updateRatingToBook() {
        Book book = new Book();
        UserEntity user = new UserEntity();
        String slug = "slug";
        RatingEntity rating = new RatingEntity();
        Mockito.doReturn(book).when(bookRepository).findBookBySlug(slug);
        Mockito.doReturn(rating).when(ratingRepository).findRatingEntityByBookAndUser(book, user);

        assertTrue(ratingService.addRatingToBook(slug, user, 3));
        Mockito.verify(ratingRepository, Mockito.times(1)).save(rating);
    }

    @Test
    void addBookReviewBySlugTest() {
        String slug = "slug";
        Mockito.doReturn(new Book()).when(bookRepository).findBookBySlug(slug);

        ratingService.addBookReviewBySlug(slug, new UserEntity(), "text");

        Mockito.verify(reviewRepository, Mockito.times(1)).save(Mockito.any(BookReviewEntity.class));
    }

    @Test
    void addRatingToBookReviewTest() {
        Integer reviewId = 1;
        UserEntity user = new UserEntity();
        BookReviewEntity review = new BookReviewEntity();
        short value = (short) 1;
        Mockito.doReturn(review).when(reviewRepository).findBookReviewEntityById(reviewId);
        Mockito.doReturn(null).when(reviewLikeRepository).findByUserAndReviewEntityAndValue(user, review, value);

        ratingService.addRatingToBookReview(reviewId, user, value);

        Mockito.verify(reviewLikeRepository, Mockito.times(1)).save(Mockito.any(BookReviewLikeEntity.class));
        Mockito.verify(reviewLikeRepository, Mockito.times(0)).delete(Mockito.any(BookReviewLikeEntity.class));
    }

    @Test
    void deleteRatingToBookReviewTest() {
        Integer reviewId = 1;
        UserEntity user = new UserEntity();
        BookReviewEntity review = new BookReviewEntity();
        short value = (short) 1;
        BookReviewLikeEntity existLike = new BookReviewLikeEntity();
        Mockito.doReturn(review).when(reviewRepository).findBookReviewEntityById(reviewId);
        Mockito.doReturn(existLike).when(reviewLikeRepository).findByUserAndReviewEntityAndValue(user, review, value);

        ratingService.addRatingToBookReview(reviewId, user, value);

        Mockito.verify(reviewLikeRepository, Mockito.times(1)).delete(existLike);
        Mockito.verify(reviewLikeRepository, Mockito.times(0)).save(Mockito.any(BookReviewLikeEntity.class));
    }
}
