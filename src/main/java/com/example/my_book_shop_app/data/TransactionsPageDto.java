package com.example.my_book_shop_app.data;

import com.example.my_book_shop_app.struct.payments.BalanceTransactionEntity;
import lombok.Data;

import java.util.List;

@Data
public class TransactionsPageDto {

    private List<BalanceTransactionEntity> transactions;
    private Integer count;

    public TransactionsPageDto(List<BalanceTransactionEntity> transactions) {
        this.transactions = transactions;
        this.count = transactions.size();
    }
}
