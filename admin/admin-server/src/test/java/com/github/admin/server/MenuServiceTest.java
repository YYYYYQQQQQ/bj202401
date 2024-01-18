package com.github.admin.server;

import com.github.admin.common.domain.Menu;
import com.github.admin.server.service.MenuService;
import com.github.framework.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.TreeMap;

@SpringBootTest
@Slf4j
public class MenuServiceTest {

    @Resource
    private MenuService menuServiceImpl;

    @Test
    public void _根据用户id查询菜单权限(){

        Result<TreeMap<Long, Menu>> result = menuServiceImpl.findMenuByUserId(1L);


        log.info("查询用户数据返回code = {},message = {},data = {}",result.getCode(),result.getMessage(),result.getData());
    }
}
