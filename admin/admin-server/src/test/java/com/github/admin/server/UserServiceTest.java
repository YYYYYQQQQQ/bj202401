package com.github.admin.server;

import com.github.admin.common.request.UserRequest;
import com.github.admin.server.service.UserService;
import com.github.framework.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService UserServiceImpl;

    @Test
    public void _根据用户账号查询用户(){

        UserRequest user = new UserRequest();
        user.setId(15L);
        user.setPassword("123");
        user.setConfirm("123");
        Result result = UserServiceImpl.changePwd(user);

        log.info("查询用户数据返回code = {},message = {}",result.getCode(),result.getMessage());

    }

}
