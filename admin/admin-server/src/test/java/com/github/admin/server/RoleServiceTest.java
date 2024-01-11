package com.github.admin.server;

import com.github.admin.server.service.RoleService;
import com.github.framework.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
public class RoleServiceTest {

    @Resource
    private RoleService RoleserviceImpl;


    @Test
    public void _根据用户id查询用户对应菜单(){

        Result<Boolean> result = RoleserviceImpl.findRoleByUserId(1L);

        log.info("查询用户数据返回code = {},message = {}",result.getCode(),result.getMessage());
    }

}
