package com.example.my_book_shop_app.controllers.admin;

import com.example.my_book_shop_app.data.ResultDto;
import com.example.my_book_shop_app.data.SearchWordDto;
import com.example.my_book_shop_app.data.request.ForAdmin.AdminElementChangePayload;
import com.example.my_book_shop_app.security.BookstoreUserRegister;
import com.example.my_book_shop_app.services.BookService;
import com.example.my_book_shop_app.services.UserService;
import com.example.my_book_shop_app.struct.book.Book;
import com.example.my_book_shop_app.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminUsersController {

    private final BookstoreUserRegister userRegister;
    private final UserService userService;
    private final BookService bookService;

    @Autowired
    public AdminUsersController(BookstoreUserRegister userRegister, UserService userService, BookService bookService) {
        this.userRegister = userRegister;
        this.userService = userService;
        this.bookService = bookService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("currentUser")
    public UserEntity currentUser() {
        return userRegister.getCurrentUser();
    }
    @ModelAttribute("currentRole")
    public String currentRole() {
        return userRegister.getCurrentUser().getRole().getName();
    }

    @ModelAttribute("booksData")
    public List<Book> popularBooksAttribute() {
        return bookService.getBooksOfUser(currentUser().getId());
    }

    @ModelAttribute("userList")
    public List<UserEntity> userList() {
        return userService.getAllUsers();
    }

    @GetMapping("/users")
    public String handelUsers(){
        return "users";
    }

    @GetMapping("/users/{userId}")
    public String handelUserBooks(@PathVariable(name = "userId") int userId, Model model){
        model.addAttribute("booksData", bookService.getBooksOfUser(userId));
        model.addAttribute("userId", userId);
        return "userBooks";
    }

    @PostMapping("/users/{userId}/books")
    @ResponseBody
    public ResultDto addBookToUser(@PathVariable(name = "userId") int userId, AdminElementChangePayload payload){
        String title = payload.getValue();
        if(bookService.isThereBookWithTitle(title)) {
            Book book = bookService.getBookByTitle(title);
            //todo add book to user after merge first part of diplom
            return new ResultDto(true);
        } else return new ResultDto(false, "There is no book with such title");
    }
}
