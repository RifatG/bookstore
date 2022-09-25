package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.data.ResultDto;
import com.example.my_book_shop_app.exceptions.BookStoreApiWrongParameterException;
import com.example.my_book_shop_app.repositories.BookRepository;
import com.example.my_book_shop_app.struct.author.Author;
import com.example.my_book_shop_app.struct.book.Book;
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
import java.util.*;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final ResourceStorage storage;

    @Autowired
    public BookService(BookRepository bookRepository, ResourceStorage storage) {
        this.bookRepository = bookRepository;
        this.storage = storage;
    }

    public List<Book> getRecentBooksData() {
        Instant date = Instant.now().minus(Duration.ofDays(730));
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
    public Book getBookById(int id) {
        return this.bookRepository.findBookById(id);
    }

    public boolean isThereBookWithTitle(String title) {
        return this.bookRepository.existsBookByTitle(title);
    }
    public boolean isThereBookWithTitleIgnoreCase(String title) {
        return this.bookRepository.existsBookByTitleIgnoreCase(title);
    }

    public Book getBookByTitle(String title) {
        return this.bookRepository.findBookByTitle(title);
    }

    public Book getBookByTitleIgnoreCase(String title) {
        return this.bookRepository.findBookByTitle(title);
    }

    public void updateBook(Book bookToUpdate) {
        this.bookRepository.save(bookToUpdate);
    }

    public List<Book> getBooksBySlugs(String[] cookieSlugs) {
        return this.bookRepository.findBooksBySlugIn(cookieSlugs);
    }

    public Page<Book> getPageOfViewedBooks(Integer userId, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return this.bookRepository.findViewedBooks(userId, nextPage);
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
