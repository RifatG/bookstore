package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.external_api.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Long> {

    ConfirmationCode findByCode(String code);
}
