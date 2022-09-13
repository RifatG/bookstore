package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.data.ResultDto;
import com.example.my_book_shop_app.exceptions.BookStoreApiWrongParameterException;
import com.example.my_book_shop_app.repositories.Book2UserRepository;
import com.example.my_book_shop_app.repositories.BookRepository;
import com.example.my_book_shop_app.struct.author.Author;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.book.links.Book2UserEntity;
import com.example.my_book_shop_app.struct.enums.Book2UserRelationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final Book2UserRepository book2UserRepository;

    private final ResourceStorage storage;

    private static final int CART_STATUS_ID = 2;
    private static final int PAID_STATUS_ID = 3;

    @Autowired
    public BookService(BookRepository bookRepository, Book2UserRepository book2UserRepository, ResourceStorage storage) {
        this.bookRepository = bookRepository;
        this.book2UserRepository = book2UserRepository;
        this.storage = storage;
    }

    public List<Book> getRecentBooksData() {
        Instant date = Instant.now().minus(Duration.ofDays(2 * 365));
        return bookRepository.findAllByPubDateGreaterThan(Date.from(date));
    }

    public List<Book> getBooksByAuthor(String authorName) {
        return bookRepository.findBooksByAuthorNameContaining(authorName);
    }

    public List<Book> getBooksByTitle(String bookTitle) throws BookStoreApiWrongParameterException {
        if(bookTitle.length()<=1) {
            throw new BookStoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            List<Book> data = bookRepository.findBooksByTitleContaining(bookTitle);
            if (!data.isEmpty()) {
                return data;
            } else {
                throw new BookStoreApiWrongParameterException("No data found with specified parameters...");
            }
        }
    }

    public List<Book> getBooksWithPriceBetween(int min, int max) {
        return bookRepository.findBooksByPriceBetween(min, max);
    }

    public List<Book> getBooksWithPrice(int price) {
        return bookRepository.findBooksByPriceIs(price);
    }

    public List<Book> getBookWithMaxDiscount() {
        return bookRepository.getBookWithMaxDiscount();
    }

    public List<Book> getBookWithMaxPrice() {
        return bookRepository.getBookWithMaxPrice();
    }

    public List<Book> getBestsellers() {
        return bookRepository.getBestsellers();
    }

    public Page<Book> getPageOfRecommendedBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findAll(nextPage);
    }

    public Page<Book> getPageOfPopularBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findAllByIsBestsellerEquals((byte) 1, nextPage);
    }

    public Page<Book> getPageOfRecentBooks(Integer offset, Integer limit) {
        Calendar max = new GregorianCalendar();
        Calendar min = new GregorianCalendar();
        min.set(Calendar.MONTH, max.get(Calendar.MONTH) - 1);
        Pageable nextPage = PageRequest.of(offset, limit, Sort.by("pubDate"));
        return bookRepository.findAllByPubDateBetween(min.getTime(), max.getTime(), nextPage);
    }

    public Page<Book> getPageOfBooksByDateRange(Date min, Date max, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit, Sort.by("pubDate"));
        return bookRepository.findAllByPubDateBetween(min, max, nextPage);
    }

    public Page<Book> getPageOfSearchResultBooks(String searchWord, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBooksByTitleContainingIgnoreCase(searchWord, nextPage);
    }

    public Page<Book> getPageOfBooksByAuthorId(Integer authorId, int offset, int limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return this.bookRepository.findAllByAuthorId(authorId, nextPage);
    }

    public Book getBookBySlug(String slug) {
        return this.bookRepository.findBookBySlug(slug);
    }

    public boolean isThereBookWithTitle(String title) {
        return this.bookRepository.existsBookByTitle(title);
    }

    public void updateBook(Book bookToUpdate) {
        this.bookRepository.save(bookToUpdate);
    }

    public List<Book> getBooksBySlugs(String[] cookieSlugs) {
        return this.bookRepository.findBooksBySlugIn(cookieSlugs);
    }

    public Book2UserEntity setBookAsPaid(Integer userId, Integer bookId) {
        if (!book2UserRepository.existsBook2UserEntityByUserIdAndBookId(userId, bookId)) {
            Book2UserEntity book2User = new Book2UserEntity();
            book2User.setBookId(bookId);
            book2User.setUserId(userId);
            book2User.setTime(LocalDateTime.now());
            book2User.setTypeId(PAID_STATUS_ID);
            return this.book2UserRepository.save(book2User);
        } else {
            Book2UserEntity book2User = this.book2UserRepository.findBook2UserEntityByUserIdAndBookId(userId, bookId);
            switch (book2User.getTypeId()) {
                case CART_STATUS_ID: {
                    book2User.setTypeId(Book2UserRelationType.PAID.getTypeId());
                    return this.book2UserRepository.save(book2User);
                }
                case PAID_STATUS_ID: {
                    return null;
                }
                default: return null;
            }
        }
    }

    public List<Book> getBooksOfUser(Integer userId) {
        return this.bookRepository.findBooksByUserId(userId);
    }

    public Book createNewBook(String title, String slug, String description, String image, int price, int discount, Author author) {
        Book book = new Book();
        book.setTitle(title);
        book.setDescription(description);
        book.setSlug(slug);
        book.setImage(image);
        book.setPrice(price);
        book.setDiscount((byte)discount);
        book.setPubDate(new Date());
        book.setAuthor(author);
        return bookRepository.save(book);
    }

    public void deleteBooks(List<Book> books) {
        books.forEach(this.bookRepository::delete);
    }
    public void deleteBook(Book book) {
        this.bookRepository.delete(book);
    }
    public void deleteBookById(int bookId) {
        Book book = bookRepository.findBookById(bookId);
        this.bookRepository.delete(book);
    }

    public ResultDto createNewBookWithValidation(String title, String description, String priceString, String discountString, MultipartFile image, Author author) throws IOException {
        if (title == null || title.equals("")) {
            return new ResultDto(false, "Description can't be empty");
        }
        if (description == null || description.equals("")) {
            return new ResultDto(false, "Description can't be empty");
        }
        int price;
        try {
            price = Integer.parseInt(priceString);
        } catch (NumberFormatException e) {
            return new ResultDto(false, "Old price must be digit");
        }
        int discount;
        try {
            discount = Integer.parseInt(discountString);
            if (discount < 0 || discount > 100) return new ResultDto(false, "Discount must be more than 0 and less than 100");
        } catch (NumberFormatException e) {
            return new ResultDto(false, "Discount must be digit");
        }
        String bookSlug = UUID.randomUUID().toString().substring(0, 10);
        String savePath = storage.saveNewBookImage(image, bookSlug);
        createNewBook(title, bookSlug, description, savePath, price, discount, author);
        return new ResultDto(true);
    }
}
