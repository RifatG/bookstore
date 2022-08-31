package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.Book2UserRepository;
import com.example.my_book_shop_app.repositories.BookRepository;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.book.links.Book2UserEntity;
import com.example.my_book_shop_app.struct.enums.Book2UserRelationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserBooksService {

    private final BookRepository bookRepository;
    private final Book2UserRepository book2UserRepository;

    private static final int KEPT_STATUS_ID = 1;
    private static final int CART_STATUS_ID = 2;
    private static final int PAID_STATUS_ID = 3;

    @Autowired
    public UserBooksService(BookRepository bookRepository, Book2UserRepository book2UserRepository) {
        this.bookRepository = bookRepository;
        this.book2UserRepository = book2UserRepository;
    }

    private Book2UserEntity createBook2User(Integer userId, Integer bookId, int statusId) {
        Book2UserEntity book2User = new Book2UserEntity();
        book2User.setBookId(bookId);
        book2User.setUserId(userId);
        book2User.setTime(LocalDateTime.now());
        book2User.setTypeId(statusId);
        return this.book2UserRepository.save(book2User);
    }

    public Book2UserEntity setBookAsPaid(Integer userId, Integer bookId) {
        if (!book2UserRepository.existsBook2UserEntityByUserIdAndBookId(userId, bookId)) {
            return createBook2User(userId, bookId, PAID_STATUS_ID);
        } else {
            Book2UserEntity book2User = this.book2UserRepository.findBook2UserEntityByUserIdAndBookId(userId, bookId);
            if (book2User.getTypeId() == KEPT_STATUS_ID || book2User.getTypeId() == CART_STATUS_ID) {
                book2User.setTypeId(Book2UserRelationType.PAID.getTypeId());
                return this.book2UserRepository.save(book2User);
            } else return null;
        }
    }

    public Book2UserEntity setBookAsCart(Integer userId, Integer bookId) {
        if (!book2UserRepository.existsBook2UserEntityByUserIdAndBookId(userId, bookId)) {
            return createBook2User(userId, bookId, CART_STATUS_ID);
        } else {
            Book2UserEntity book2User = this.book2UserRepository.findBook2UserEntityByUserIdAndBookId(userId, bookId);
            if (book2User.getTypeId() == KEPT_STATUS_ID) {
                    book2User.setTypeId(Book2UserRelationType.CART.getTypeId());
                    return this.book2UserRepository.save(book2User);
            } else return null;
        }
    }

    public Book2UserEntity setBookAsKept(Integer userId, Integer bookId) {
        if (!book2UserRepository.existsBook2UserEntityByUserIdAndBookId(userId, bookId)) {
            return createBook2User(userId, bookId, KEPT_STATUS_ID);
        } else {
            Book2UserEntity book2User = this.book2UserRepository.findBook2UserEntityByUserIdAndBookId(userId, bookId);
            if (book2User.getTypeId() == CART_STATUS_ID) {
                    book2User.setTypeId(Book2UserRelationType.KEPT.getTypeId());
                    return this.book2UserRepository.save(book2User);
            } else return null;
        }
    }

    public List<Book> getBooksOfUser(Integer userId) {
        return this.bookRepository.findBooksByUserId(userId);
    }

    public List<Book> getBooksInCartOfUser(Integer userId) {
        return this.bookRepository.findBooksInCartByUserId(userId);
    }

    public List<Book> getBooksInKeptOfUser(Integer userId) {
        return this.bookRepository.findBooksInKeptByUserId(userId);
    }

    public void removeBookFromCart(Integer userId, Integer bookId) {
        Book2UserEntity book2User = this.book2UserRepository.findBook2UserEntityByUserIdAndBookId(userId, bookId);
        if(book2User != null && book2User.getTypeId() == 2) this.book2UserRepository.delete(book2User);
    }

    public void removeBookFromKept(Integer userId, Integer bookId) {
        Book2UserEntity book2User = this.book2UserRepository.findBook2UserEntityByUserIdAndBookId(userId, bookId);
        if(book2User != null && book2User.getTypeId() == 1) this.book2UserRepository.delete(book2User);
    }
}
