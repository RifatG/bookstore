package com.example.my_book_shop_app.data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "recent_books")
public class RecentBook extends FilteredBook {
}
