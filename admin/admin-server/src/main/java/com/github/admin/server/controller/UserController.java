package com.github.admin.server.controller;

import com.github.admin.common.domain.User;
import com.github.admin.common.request.UserRequest;
import com.github.admin.server.service.UserService;
import com.github.framework.core.Result;
import com.github.framework.core.page.DataPage;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class UserController {

    @Resource
    private UserService userServiceImpl;

    @GetMapping("/findUserByUserName")
    Result<User> findUserByUserName(@RequestParam("userName") String userName){
        return userServiceImpl.findUserByUserName(userName);
    }

    @PostMapping("/findUserByPage")
    Result<DataPage<User>> findUserByPage(@RequestBody UserRequest userRequest){
        return userServiceImpl.findUserByPage(userRequest);
    }

    @PostMapping("/saveUser")
    public Result saveUser(@RequestBody UserRequest request){
        return userServiceImpl.saveUser(request);
    }

    @GetMapping("/findUserById/{id}")
    Result<User> findUserById(@PathVariable("id") Long id){
        return userServiceImpl.findUserById(id);
    }

    @PostMapping("/updateUser")
    Result updateUser(@RequestBody UserRequest userRequest){
        return userServiceImpl.updateUser(userRequest);
    }

    @GetMapping("/deleteUserById/{id}")
    Result deleteUserById(@PathVariable("id") Long id){
        return userServiceImpl.deleteUserById(id);
    }

    @PostMapping("/changeUserStatus")
    Result changeUserStatus(@RequestBody UserRequest request){
        return userServiceImpl.changeUserStatus(request);
    }

    @PostMapping("/changePwd")
    Result changePwd(@RequestBody UserRequest request){
        return userServiceImpl.changePwd(request);
    }

    @GetMapping("/roleAuth/{id}")
    Result<User> roleAuth(@PathVariable("id") Long id){
        return userServiceImpl.roleAuth(id);
    }

    @PostMapping("/changeUserRole")
    Result changeUserRole(@RequestBody UserRequest user){
        return userServiceImpl.changeUserRole(user);
    }

    @GetMapping("/findUsersByRoleId/{id}")
    Result<List<User>> findUsersByRoleId(@PathVariable("id") Long id){
        return userServiceImpl.findUsersByRoleId(id);
    }
}
