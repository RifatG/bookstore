package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.jwt.JwtBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlackList, Integer> {

    Optional<JwtBlackList> findByToken(String token);
}
