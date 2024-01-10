package com.github.admin.client;

import com.github.admin.common.domain.User;
import com.github.framework.core.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FeignClient(name = "admin-server",url = "127.0.0.1:8082")
public interface UserServiceClient {

    @GetMapping("/findUserByUserName")
    Result<User> findUserByUserName(@RequestParam("userName") String userName);


}