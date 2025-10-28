package com.example.my_book_shop_app.services;

import java.time.Duration;
import java.util.List;
import com.example.my_book_shop_app.repositories.BookRepository;
import com.example.my_book_shop_app.struct.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CartCacheService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RedisTemplate<String, List<Book>> bookListRedisTemplate;

    private static final Duration CART_TTL = Duration.ofHours(24);

    public List<Book> getBooksInCartOfUser(Integer userId) {
        String cacheKey = "cart:user:" + userId;

        // Пробуем взять из кэша
        List<Book> cachedBooks = bookListRedisTemplate.opsForValue().get(cacheKey);
        if (cachedBooks != null) {
            return cachedBooks; // ✅ Возвращаем из кэша
        }

        // ❌ В кэше нет - загружаем из БД
        List<Book> freshBooks = bookRepository.findBooksInCartByUserId(userId);

        // Сохраняем в кэш
        bookListRedisTemplate.opsForValue().set(cacheKey, freshBooks, CART_TTL);

        return freshBooks;
    }

    // Очищаем кэш при изменении корзины
    public void evictCartCache(Integer userId) {
        bookListRedisTemplate.delete("cart:user:" + userId);
    }

    // Принудительно обновляем кэш
    public void refreshCartCache(Integer userId) {
        evictCartCache(userId);
        getBooksInCartOfUser(userId); // перезагрузит из БД и сохранит в кэш
    }
}
