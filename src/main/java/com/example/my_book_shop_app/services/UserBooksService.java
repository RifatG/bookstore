package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.*;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.book.RecommendedBook;
import com.example.my_book_shop_app.struct.book.links.Book2UserEntity;
import com.example.my_book_shop_app.struct.book.viewed.ViewedBook2UserEntity;
import com.example.my_book_shop_app.struct.enums.Book2UserRelationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class UserBooksService {

    private final BookRepository bookRepository;
    private final Book2UserRepository book2UserRepository;
    private final ViewedBook2UserRepository viewedBook2UserRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final RecommendedBookRepository recommendedBookRepository;

    private static final int KEPT_STATUS_ID = 1;
    private static final int CART_STATUS_ID = 2;
    private static final int PAID_STATUS_ID = 3;
    private static final int ARCHIVED_STATUS_ID = 4;

    @Autowired
    public UserBooksService(BookRepository bookRepository, Book2UserRepository book2UserRepository, ViewedBook2UserRepository viewedBook2UserRepository, GenreRepository genreRepository, AuthorRepository authorRepository, RecommendedBookRepository recommendedBookRepository) {
        this.bookRepository = bookRepository;
        this.book2UserRepository = book2UserRepository;
        this.viewedBook2UserRepository = viewedBook2UserRepository;
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
        this.recommendedBookRepository = recommendedBookRepository;
    }

    private Book2UserEntity createBook2User(Integer userId, Integer bookId, int statusId) {
        Book2UserEntity book2User = new Book2UserEntity();
        book2User.setBookId(bookId);
        book2User.setUserId(userId);
        book2User.setTime(LocalDateTime.now());
        book2User.setTypeId(statusId);
        return this.book2UserRepository.save(book2User);
    }

    private ViewedBook2UserEntity createViewedBook2User(Integer userId, Integer bookId) {
        ViewedBook2UserEntity viewedBook = new ViewedBook2UserEntity();
        viewedBook.setBookId(bookId);
        viewedBook.setUserId(userId);
        viewedBook.setTime(LocalDateTime.now());
        return this.viewedBook2UserRepository.save(viewedBook);
    }

    public Book2UserEntity setBookAsPaid(Integer userId, Integer bookId) {
        if (!book2UserRepository.existsBook2UserEntityByUserIdAndBookId(userId, bookId)) {
            return createBook2User(userId, bookId, PAID_STATUS_ID);
        } else {
            Book2UserEntity book2User = this.book2UserRepository.findBook2UserEntityByUserIdAndBookId(userId, bookId);
            if (book2User.getTypeId() == KEPT_STATUS_ID || book2User.getTypeId() == CART_STATUS_ID) {
                addLikeBooksToRecommended(userId, bookId, false);
                book2User.setTypeId(Book2UserRelationType.PAID.getTypeId());
                return this.book2UserRepository.save(book2User);
            } else return null;
        }
    }

    public Book2UserEntity setBookAsArchived(Integer userId, Integer bookId) {
        if (!book2UserRepository.existsBook2UserEntityByUserIdAndBookId(userId, bookId)) {
            return createBook2User(userId, bookId, ARCHIVED_STATUS_ID);
        } else {
            Book2UserEntity book2User = this.book2UserRepository.findBook2UserEntityByUserIdAndBookId(userId, bookId);
            if (book2User.getTypeId() == PAID_STATUS_ID) {
                addLikeBooksToRecommended(userId, bookId, false);
                book2User.setTypeId(Book2UserRelationType.ARCHIVED.getTypeId());
                return this.book2UserRepository.save(book2User);
            } else return null;
        }
    }

    public Book2UserEntity setBookAsCart(Integer userId, Integer bookId) {
        if (!book2UserRepository.existsBook2UserEntityByUserIdAndBookId(userId, bookId)) {
            addLikeBooksToRecommended(userId, bookId, false);
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
            addLikeBooksToRecommended(userId, bookId, false);
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

    public ViewedBook2UserEntity setBookAsViewed(Integer userId, Integer bookId) {
        if (!viewedBook2UserRepository.existsViewedBook2UserEntityByUserIdAndBookId(userId, bookId)) {
            addLikeBooksToRecommended(userId, bookId, true);
            return createViewedBook2User(userId, bookId);
        } else {
            ViewedBook2UserEntity viewedBook2User = viewedBook2UserRepository.findViewedBook2UserEntityByUserIdAndBookId(userId, bookId);
            viewedBook2User.setTime(LocalDateTime.now());
            viewedBook2UserRepository.save(viewedBook2User);
            return viewedBook2User;
        }
    }

    private void addLikeBooksToRecommended(Integer userId, Integer bookId, boolean byViewed) {
        Random rand = new Random();
        bookRepository.findById(bookId).ifPresent(book -> {
            if (book.getGenreList() != null && !book.getGenreList().isEmpty()){
                int genreId = book.getGenreList().get(0).getId();
                boolean needToAdd = byViewed
                        ? genreRepository.getGenreIdsOfViewedBooksByUser(userId).contains(genreId)
                        : genreRepository.getGenreIdsByUserRelationsToBook(userId).contains(genreId);
                if (needToAdd) {
                    List<Book> bookList = genreRepository.getGenreEntityById(genreId).getBookList();
                    for (int i = 0; i < rand.nextInt(2) + 2; i++) {
                        addBookToRecommended(userId, bookList.get(rand.nextInt(bookList.size())));
                    }
                }
            }
            int authorId = book.getAuthor().getId();
            boolean needToAdd = byViewed
                    ? authorRepository.getAuthorIdsOfViewedBooksByUser(userId).contains(authorId)
                    :authorRepository.getAuthorIdsByUserRelationsToBook(userId).contains(authorId);
            if (needToAdd) {
                List<Book> bookList = authorRepository.getAuthorById(authorId).getBookList();
                for (int i = 0; i < rand.nextInt(2) + 2; i++) {
                    addBookToRecommended(userId, bookList.get(rand.nextInt(bookList.size())));
                }
            }
        });
    }

    private void addBookToRecommended(Integer userId, Book book) {
        if (!recommendedBookRepository.existsRecommendedBookByUserIdAndBook(userId, book)) {
            RecommendedBook recommendedBook = new RecommendedBook();
            recommendedBook.setBook(book);
            recommendedBook.setUserId(userId);
            recommendedBookRepository.save(recommendedBook);
        }
    }

    public boolean isBookPaidOrArchived(int userId, int bookId) {
        return book2UserRepository.existsBook2UserEntityByUserIdAndBookIdAndTypeId(userId, bookId, PAID_STATUS_ID)
                || book2UserRepository.existsBook2UserEntityByUserIdAndBookIdAndTypeId(userId, bookId, ARCHIVED_STATUS_ID);
    }

    public List<Book> getArchivedBooksOfUser(int userId) {
        return this.bookRepository.findBooksInArchiveByUserId(userId);
    }

    public long getKeptCount(int userId) {
        return this.book2UserRepository.getCountByUserIdAndTypeId(userId, KEPT_STATUS_ID);
    }

    public long getCartCount(int userId) {
        return this.book2UserRepository.getCountByUserIdAndTypeId(userId, CART_STATUS_ID);
    }

    public long getMyBooksCount(int userId) {
        return this.book2UserRepository.getCountByUserIdAndTypeId(userId, PAID_STATUS_ID) + this.book2UserRepository.getCountByUserIdAndTypeId(userId, ARCHIVED_STATUS_ID);
    }
}
