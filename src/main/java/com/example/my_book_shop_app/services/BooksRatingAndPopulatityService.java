package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.data.RatingDto;
import com.example.my_book_shop_app.repositories.*;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.book.rating.RatingEntity;
import com.example.my_book_shop_app.struct.book.review.BookReviewEntity;
import com.example.my_book_shop_app.struct.book.review.BookReviewLikeEntity;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BooksRatingAndPopulatityService {

    private final BookRepository bookRepository;
    private final RatingRepository ratingRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final RecommendedBookRepository recommendedBookRepository;
    private static final double RATING = 2;

    @Autowired
    public BooksRatingAndPopulatityService(BookRepository bookRepository, RatingRepository ratingRepository, ReviewRepository reviewRepository, ReviewLikeRepository reviewLikeRepository, RecommendedBookRepository recommendedBookRepository) {
        this.bookRepository = bookRepository;
        this.ratingRepository = ratingRepository;
        this.reviewRepository = reviewRepository;
        this.reviewLikeRepository = reviewLikeRepository;
        this.recommendedBookRepository = recommendedBookRepository;
    }

    public List<Book> getPageOfPopularBooksBySql(Integer offset, Integer limit) {
        offset = offset * limit;
        return bookRepository.getPageOfPopularBooks(RATING, offset, limit);
    }

    public List<Book> getPageOfHighRatingBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.getPageOfHighRatingBooks(nextPage).getContent();
    }

    public List<Book> getPageOfRecommendedBooks(Integer userId, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return recommendedBookRepository.getAllRecommendedBooksByUserId(userId, nextPage).getContent();
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
            rating.setBook(book);
            rating.setUser(user);
        } else {
            rating.setRatingCount(value);
        }
        this.ratingRepository.save(rating);
        return true;
    }

    public void addRatingToBookReview(Integer reviewId, UserEntity user, short value) {
        BookReviewEntity review = reviewRepository.findBookReviewEntityById(reviewId);
        BookReviewLikeEntity existLike = reviewLikeRepository.findByUserAndReviewEntityAndValue(user, review, value);
        if (existLike == null) {
            BookReviewLikeEntity reviewLike = new BookReviewLikeEntity();
            reviewLike.setReviewEntity(review);
            reviewLike.setTime(LocalDateTime.now());
            reviewLike.setValue(value);
            reviewLike.setUser(user);
            this.reviewLikeRepository.save(reviewLike);
        } else {
            reviewLikeRepository.delete(existLike);
        }
    }

    public void addBookReviewBySlug(String slug, UserEntity user, String text) {
        BookReviewEntity reviewEntity = new BookReviewEntity();
        reviewEntity.setBook(bookRepository.findBookBySlug(slug));
        reviewEntity.setUser(user);
        reviewEntity.setTime(LocalDateTime.now());
        reviewEntity.setText(text);
        this.reviewRepository.save(reviewEntity);
    }

    public RatingDto getUserRating(int userId) {
        long likesCount = this.reviewLikeRepository.getLikesByUserId(userId);
        long dislikesCount = this.reviewLikeRepository.getDislikesByUserId(userId);
        long voicesCount;
        double ratingDouble;
        int rating;
        voicesCount = likesCount + dislikesCount;
        if (voicesCount > 0) {
            ratingDouble = (double) likesCount / voicesCount;
            if(ratingDouble < 0.2d) rating = 1;
            else if(ratingDouble < 0.4d) rating = 2;
            else if(ratingDouble < 0.6d) rating = 3;
            else if(ratingDouble < 0.8) rating = 4;
            else rating = 5;
            return new RatingDto(rating, (int) voicesCount);
        }
        return new RatingDto(0,0);
    }
}
