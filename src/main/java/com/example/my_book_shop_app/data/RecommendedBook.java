package com.example.my_book_shop_app.data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "recommended_books")
public class RecommendedBook extends FilteredBook {
}
