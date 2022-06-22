package com.example.my_book_shop_app.unit_tests.security_tests;

import com.example.my_book_shop_app.exceptions.UserAlreadyExistException;
import com.example.my_book_shop_app.repositories.UserContactRepository;
import com.example.my_book_shop_app.repositories.UserRepository;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.security.RegistrationForm;
import com.example.my_book_shop_app.struct.user.UserContactEntity;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BookstoreUserRegisterTests {

    private final BookstoreUserRegister userRegister;
    private RegistrationForm registrationForm;

    @MockBean
    private UserRepository bookstoreUserRepositoryMock;

    @MockBean
    private UserContactRepository userContactRepositoryMock;

    @Autowired
    public BookstoreUserRegisterTests(BookstoreUserRegister userRegister) {
        this.userRegister = userRegister;
    }

    @BeforeEach
    void setUp() {
        registrationForm = new RegistrationForm();
        registrationForm.setEmail("test@mail.org");
        registrationForm.setName("Tester");
        registrationForm.setPassword("iddqd");
        registrationForm.setPhoneNumber("9031232323");
    }

    @AfterEach
    void tearDown() {
        registrationForm = null;
    }

    @Test
    void registerNewUser() throws UserAlreadyExistException {
        userRegister.registerNewUser(registrationForm);

        Mockito.verify(bookstoreUserRepositoryMock, Mockito.times(1))
                .save(Mockito.any(UserEntity.class));
        Mockito.verify(userContactRepositoryMock, Mockito.times(1))
                .save(Mockito.any(UserContactEntity.class));
    }

    @Test
    void registerNewUserFail(){
        Mockito.doReturn(new UserContactEntity())
                .when(userContactRepositoryMock)
                .findUserContactEntityByContact(registrationForm.getEmail());

        assertThrows(UserAlreadyExistException.class, () -> userRegister.registerNewUser(registrationForm));
        Mockito.verify(bookstoreUserRepositoryMock, Mockito.times(0))
                .save(Mockito.any(UserEntity.class));
        Mockito.verify(userContactRepositoryMock, Mockito.times(0))
                .save(Mockito.any(UserContactEntity.class));
    }
}
