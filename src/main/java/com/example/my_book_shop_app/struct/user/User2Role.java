package com.example.my_book_shop_app.struct.user;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user2roles")
public class User2Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "SERIAL NOT NULL")
    private int id;

    @Column(name = "user_id", columnDefinition = "INT NOT NULL")
    private int userId;

    @Column(name = "role_id", columnDefinition = "INT NOT NULL")
    private int roleId;
}
