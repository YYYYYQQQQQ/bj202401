package com.github.admin.api.controller;

import com.github.admin.client.UserServiceClient;
import com.github.admin.common.domain.Role;
import com.github.admin.common.domain.User;
import com.github.admin.common.group.AddUserGroup;
import com.github.admin.common.group.UpdatePwdGroup;
import com.github.admin.common.group.UpdateUserGroup;
import com.github.admin.common.request.UserRequest;
import com.github.framework.core.Result;
import com.github.framework.core.page.DataPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@Slf4j
public class UserController {

    @Resource
    private UserServiceClient userServiceClient;

    @GetMapping("/main/system/user/index")
    @RequiresPermissions("system:user:index")
    public String index(Model model, UserRequest userRequest){

        Result<DataPage<User>> result = userServiceClient.findUserByPage(userRequest);
        List<User> list = new ArrayList<User>();
        DataPage<User> dataPage = new DataPage<>();
        if(result.isSuccess()){
            list = result.getData().getDataList();
            dataPage = result.getData();
            model.addAttribute("list",list);
            model.addAttribute("page",dataPage);
        }
        return "manager/user/index";
    }

    @GetMapping("/system/user/add")
    @RequiresPermissions("system:user:add")
    public String add(){
        return "manager/user/add";
    }


    @PostMapping("/system/user/save")
    @RequiresPermissions("system:user:add")
    @ResponseBody
    public Result add(@Validated(value = AddUserGroup.class) UserRequest request){


        return userServiceClient.saveUser(request);
    }

    @GetMapping("/system/user/edit/{id}")
    @RequiresPermissions("system:role:edit")
    public String edit(@PathVariable("id")Long id, Model model){
        User user = new User();
        Result<User> result = userServiceClient.findUserById(id);
        if(result.isSuccess()){
            user = result.getData();
            model.addAttribute("user",user);
        }
        return "manager/user/edit";
    }


    @PostMapping("/system/user/edit")
    @RequiresPermissions("system:role:edit")
    @ResponseBody
    public Result update(@Validated(value = UpdateUserGroup.class) UserRequest userRequest){
        return userServiceClient.updateUser(userRequest);
    }

    @GetMapping("/system/user/detail/{id}")
    @RequiresPermissions("system:role:detail")
    public String detail(@PathVariable("id")Long id, Model model){
        User user = new User();
        Result<User> result = userServiceClient.findUserById(id);
        if(result.isSuccess()){
            user = result.getData();
            model.addAttribute("user",user);
        }
        return "manager/user/detail";
    }


    @GetMapping("/system/user/delete/{id}")
    @RequiresPermissions("system:user:detail")
    @ResponseBody
    public Result delete(@PathVariable("id") Long id){
        return userServiceClient.deleteUserById(id);
    }

    @PostMapping("/system/user/status/start")
    @RequiresPermissions("system:user:status")
    @ResponseBody
    public Result start(@RequestParam(value = "ids",required = false) List<Long> ids){
        UserRequest request = new UserRequest();
        request.setStatus(1);
        request.setIds(ids);
        System.out.println("ids:"+ ids);
        return userServiceClient.changeUserStatus(request);
    }

    @PostMapping("/system/user/status/stop")
    @RequiresPermissions("system:user:status")
    @ResponseBody
    public Result stop(@RequestParam(value = "ids",required = false) List<Long> ids){
        UserRequest request = new UserRequest();
        request.setStatus(2);
        request.setIds(ids);
        System.out.println("ids:"+ ids);
        return userServiceClient.changeUserStatus(request);
    }

    @GetMapping("/system/user/pwd")
    @RequiresPermissions("system:user:pwd")
    public String pwd(@RequestParam(value = "ids",required = false) Long ids,Model model){
        model.addAttribute("id",ids);
        return "manager/user/pwd";
    }

    @PostMapping("/system/user/pwd")
    @RequiresPermissions("system:user:pwd")
    @ResponseBody
    public Result pwd(@Validated(value = UpdatePwdGroup.class) UserRequest request){
        return userServiceClient.changePwd(request);
    }

    @GetMapping("/system/user/role")
    @RequiresPermissions("system:user:role")
    public String roleAuth(@RequestParam(value = "ids",required = false) Long id,Model model){
        Result<User> result = userServiceClient.roleAuth(id);
        List<Role> list = new ArrayList<Role>();
        Set<Role> roleSet = new HashSet<Role>();
        if(result.isSuccess()){
            list = result.getData().getList();
            roleSet = result.getData().getAuthSet();
        }
        log.error(list.toString());
        model.addAttribute("id",id);
        model.addAttribute("list", list);
        model.addAttribute("authRoles", roleSet);
        return "manager/user/role";
    }

    @PostMapping("/system/user/role")
    @RequiresPermissions("system:user:role")
    @ResponseBody
    public Result roleAuth(UserRequest userRequest){
        return userServiceClient.changeUserRole(userRequest);
    }

}
