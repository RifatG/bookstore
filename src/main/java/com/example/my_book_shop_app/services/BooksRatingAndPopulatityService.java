package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.BookRepository;
import com.example.my_book_shop_app.repositories.RatingRepository;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.book.links.Book2UserTypeEntity;
import com.example.my_book_shop_app.struct.book.rating.RatingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        return bookRepository.getPopularBooks(RATING, offset, limit);
    }

    public Page<Book> getPageOfPopularBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        List<Book> books = bookRepository.findAll();
        List<Integer> popularBookIdList = getPopularBookIdList(books);
        return bookRepository.findAllByIdIn(popularBookIdList, nextPage);
    }

    public Integer getCountOfPopularBooks() {
        List<Book> books = bookRepository.findAll();
        return getPopularBookIdList(books).size();
    }

    private List<Integer> getPopularBookIdList(List<Book> allBooks) {
        List<Integer> popularBookIdList = new ArrayList<>();
        allBooks.forEach(book -> {
            double popularity = 0;
            for (Book2UserTypeEntity relation: book.getUserRelations()) {
                switch (relation.getCode()) {
                    case "PAID" : {
                        popularity++;
                        break;
                    }
                    case "CART" : {
                        popularity += 0.7;
                        break;
                    }
                    case "KEPT" : {
                        popularity += 0.4;
                        break;
                    }
                    default: break;
                }
            }
            if (popularity >= RATING) popularBookIdList.add(book.getId());
        });
        return popularBookIdList;
    }

    public boolean addRatingToBook(String slug, Integer value) {
        RatingEntity rating = new RatingEntity();
        rating.setRatingCount(value);
        rating.setBook(this.bookRepository.findBookBySlug(slug));
        this.ratingRepository.save(rating);
        return true;
    }
}
