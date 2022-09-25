package com.example.my_book_shop_app.repositories;

import com.example.my_book_shop_app.struct.user.User2Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface User2RoleRepository extends JpaRepository<User2Role, Integer> {
}
