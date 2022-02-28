package com.example.my_book_shop_app.data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "postponed_books")
public class PostponedBook extends FilteredBook {
}
