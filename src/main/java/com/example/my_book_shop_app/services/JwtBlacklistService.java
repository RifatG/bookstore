package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.JwtBlacklistRepository;
import com.example.my_book_shop_app.struct.jwt.JwtBlackList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JwtBlacklistService {

    private final JwtBlacklistRepository jwtBlacklistRepository;

    @Autowired
    public JwtBlacklistService(JwtBlacklistRepository jwtBlacklistRepository) {
        this.jwtBlacklistRepository = jwtBlacklistRepository;
    }

    public void addToBlacklist(String token) {
        JwtBlackList tokenEntity = new JwtBlackList();
        tokenEntity.setToken(token);
        jwtBlacklistRepository.save(tokenEntity);
    }

    public boolean isTokenInBlacklist(String token) {
        Optional<JwtBlackList> jwtBlackList = jwtBlacklistRepository.findByToken(token);
        return jwtBlackList.isPresent();
    }
}
