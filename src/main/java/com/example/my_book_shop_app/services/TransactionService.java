package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.BalanceTransactionRepository;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.payments.BalanceTransactionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final BalanceTransactionRepository transactionRepository;

    @Autowired
    public TransactionService(BalanceTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void createPositiveTransaction(int userId, Double value) {
        BalanceTransactionEntity transactionEntity = new BalanceTransactionEntity();
        transactionEntity.setUserId(userId);
        transactionEntity.setTime(LocalDateTime.now());
        transactionEntity.setValue(value);
        transactionEntity.setDescription("Balance has been topped up");
        transactionRepository.save(transactionEntity);
    }

    public void createNegativeTransaction(int userId, Book book, Double value) {
        BalanceTransactionEntity transactionEntity = new BalanceTransactionEntity();
        transactionEntity.setUserId(userId);
        transactionEntity.setTime(LocalDateTime.now());
        transactionEntity.setValue(value);
        transactionEntity.setBookId(book.getId());
        transactionEntity.setDescription("Purchase of the book " + book.getTitle());
        transactionRepository.save(transactionEntity);
    }

    public Page<BalanceTransactionEntity> getPageOfTransactions(int offset, int limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return transactionRepository.findAll(nextPage);
    }
}
