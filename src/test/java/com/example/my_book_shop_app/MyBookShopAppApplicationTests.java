package com.example.my_book_shop_app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MyBookShopAppApplicationTests {

	private final MyBookShopAppApplication application;

	@Autowired
	MyBookShopAppApplicationTests(MyBookShopAppApplication application) {
		this.application = application;
	}

	@Test
	void contextLoads() {
		assertNotNull(application);
	}
}
