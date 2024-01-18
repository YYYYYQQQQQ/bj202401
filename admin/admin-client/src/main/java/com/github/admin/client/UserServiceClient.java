package com.github.admin.client;

import com.github.admin.common.domain.User;
import com.github.admin.common.request.UserRequest;
import com.github.framework.core.Result;
import com.github.framework.core.page.DataPage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FeignClient(name = "admin-server",url = "127.0.0.1:8082")
public interface UserServiceClient {

    @GetMapping("/findUserByUserName")
    Result<User> findUserByUserName(@RequestParam("userName") String userName);

    @PostMapping("/findUserByPage")
    Result<DataPage<User>> findUserByPage(@RequestBody UserRequest userRequest);

    @PostMapping("/saveUser")
    Result saveUser(@RequestBody UserRequest request);

    @GetMapping("/findUserById/{id}")
    Result<User> findUserById(@PathVariable("id") Long id);


    @PostMapping("/updateUser")
    Result updateUser(@RequestBody UserRequest userRequest);

    @GetMapping("/deleteUserById/{id}")
    Result<User> deleteUserById(@PathVariable("id") Long id);

    @PostMapping("/changeUserStatus")
    Result changeUserStatus(@RequestBody UserRequest request);

    @PostMapping("/changePwd")
    Result changePwd(@RequestBody UserRequest request);

    @GetMapping("/roleAuth/{id}")
    Result<User> roleAuth(@PathVariable("id") Long id);

    @PostMapping("/changeRoleAuth")
    Result changeRoleAuth(@RequestBody UserRequest request);

    @PostMapping("/changeUserRole")
    Result changeUserRole(@RequestBody UserRequest user);

    @GetMapping("/findUsersByRoleId/{id}")
    Result<List<User>> findUsersByRoleId(@PathVariable("id") Long id);
}
