package com.github.admin.server.controller;

import com.github.admin.common.domain.User;
import com.github.admin.server.service.UserService;
import com.github.framework.core.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class UserController {

    @Resource
    private UserService userServiceImpl;

    @GetMapping("/findUserByUserName")
    Result<User> findUserByUserName(@RequestParam("userName") String userName){

        return userServiceImpl.findUserByUserName(userName);
    }

}
