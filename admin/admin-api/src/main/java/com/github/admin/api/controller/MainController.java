package com.github.admin.api.controller;


import com.github.admin.client.MenuServiceClient;
import com.github.admin.common.domain.Menu;
import com.github.admin.common.domain.User;
import com.github.framework.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.TreeMap;

@Controller
@Slf4j
public class MainController {


    @Resource
    private MenuServiceClient menuServiceClient;

    @GetMapping("/main/index")
    public String index(){
        return "manager/main/index";
    }

    @GetMapping("/main")
    public String main(Model model){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        TreeMap<Long, Menu> treeMenu = new TreeMap<>();
        Long userId = user.getId();
        //获取角色对应后台菜单
        Result<TreeMap<Long,Menu>> result = menuServiceClient.findMenuByUserId(userId);
        if(result.isSuccess()){
            treeMenu = result.getData();
        }
        model.addAttribute("treeMenu",treeMenu);
        model.addAttribute("user",user);
        return "main";
    }


}
