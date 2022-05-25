package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.user.UserContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserContactRepository extends JpaRepository<UserContactEntity, Integer> {

    UserContactEntity findUserContactEntityByContact(String contact);
}
