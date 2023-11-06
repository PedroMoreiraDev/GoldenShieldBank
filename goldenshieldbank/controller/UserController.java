package com.goldenshieldbank.controller;

import com.goldenshieldbank.entity.dto.BankResponse;
import com.goldenshieldbank.entity.dto.UserRequest;
import com.goldenshieldbank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return userService.createAcount(userRequest);
    }

}
