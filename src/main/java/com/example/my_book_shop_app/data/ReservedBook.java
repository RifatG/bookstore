package com.example.my_book_shop_app.data;


import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "reserved_books")
public class ReservedBook extends FilteredBook {
}
