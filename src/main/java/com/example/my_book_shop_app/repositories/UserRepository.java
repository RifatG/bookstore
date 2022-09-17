package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findUserEntityByName(String username);

    UserEntity findUserEntityById(Integer id);

    List<UserEntity> findUserEntitiesByNameContainingIgnoreCase(String userName);
}
