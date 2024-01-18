package com.github.admin.server.controller;

import com.github.admin.common.domain.Menu;
import com.github.admin.server.service.MenuService;
import com.github.framework.core.Result;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.TreeMap;

@RestController
public class MenuController {

    @Resource
    private MenuService menuServiceImpl;

    @RequestMapping("/findMenuByUserId/{userId}")
    Result<TreeMap<Long, Menu>> findMenuByUserId(@PathVariable("userId") Long userId) {
        return menuServiceImpl.findMenuByUserId(userId);
    }


}
