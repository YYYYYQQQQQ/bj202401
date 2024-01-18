package com.github.admin.api.controller;

import com.github.admin.client.RoleServiceClient;
import com.github.admin.client.UserServiceClient;
import com.github.admin.common.domain.Role;
import com.github.admin.common.domain.User;
import com.github.admin.common.group.AddRoleGroup;
import com.github.admin.common.group.UpdateUserGroup;
import com.github.admin.common.request.RoleRequest;
import com.github.framework.core.Result;
import com.github.framework.core.page.DataPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class RoleController {
    @Resource
    private RoleServiceClient roleServiceClient;
    @Resource
    private UserServiceClient userServiceClient;

    @GetMapping("/main/system/role/index")
    @RequiresPermissions("system:role:index")
    public String index(Model model, RoleRequest roleRequest){

        Result<DataPage<Role>> result = roleServiceClient.findRoleByPage(roleRequest);
        List<Role> list = new ArrayList<Role>();
        DataPage<Role> dataPage = new DataPage<>();
        if(result.isSuccess()){
            list = result.getData().getDataList();
            dataPage = result.getData();
            model.addAttribute("list",list);
            model.addAttribute("page",dataPage);
        }
        return "manager/role/index";
    }

    @GetMapping("/system/role/add")
    @RequiresPermissions("system:role:add")
    public String add(){
        return "manager/role/add";
    }

    @PostMapping("/system/role/save")
    @RequiresPermissions("system:role:add")
    @ResponseBody
    public Result add(@Validated(value = AddRoleGroup.class) RoleRequest roleRequest){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        roleRequest.setCreateBy(user.getId());
        roleRequest.setUpdateBy(user.getId());
        return roleServiceClient.saveRole(roleRequest);
    }





    @GetMapping("/system/role/edit/{id}")
    @RequiresPermissions("system:role:edit")
    public String edit(@PathVariable("id") Long id, Model model){
        Role role = new Role();
        Result<Role> result = roleServiceClient.findRoleById(id);
        if(result.isSuccess()){
            role = result.getData();
            model.addAttribute("role",role);
        }
        return "manager/role/edit";
    }

    @PostMapping("/system/role/edit")
    @RequiresPermissions("system:role:edit")
    @ResponseBody
    public Result update(@Validated(value = UpdateUserGroup.class) RoleRequest roleRequest){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        roleRequest.setUpdateBy(user.getId());
        return roleServiceClient.updateRole(roleRequest);
    }

    @GetMapping("/system/role/detail/{id}")
    @RequiresPermissions("system:role:detail")
    public String detail(@PathVariable("id")Long id, Model model){
        Role role = new Role();
        Result<Role> result = roleServiceClient.findRoleById(id);
        if(result.isSuccess()){
            role = result.getData();
            model.addAttribute("role",role);
        }
        return "manager/role/detail";
    }

    @GetMapping("/system/role/delete/{id}")
    @RequiresPermissions("system:role:detail")
    @ResponseBody
    public Result delete(@PathVariable("id") Long id){
        return roleServiceClient.deleteRoleById(id);
    }

    @GetMapping("/system/role/userList/{id}")
    @RequiresPermissions("system:role:edit")
    public String userList(@PathVariable(value = "id") Long id,Model model){
        Result<List<User>> result = userServiceClient.findUsersByRoleId(id);
        List<User> list = new ArrayList<>();
        if(result.isSuccess()){
            list = result.getData();
        }
        model.addAttribute("list",list);
        return "manager/role/userList";
    }

    @PostMapping("/system/role/status/enable")
    @RequiresPermissions("system:role:status")
    @ResponseBody
    public Result enable(@RequestParam(value = "ids",required = false) List<Long> ids){
        RoleRequest request = new RoleRequest();
        request.setStatus(1);
        request.setIds(ids);
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        request.setUpdateBy(user.getId());
        System.out.println("ids:"+ ids);
        return roleServiceClient.changeRoleStatus(request);
    }

    @PostMapping("/system/role/status/disable")
    @RequiresPermissions("system:role:status")
    @ResponseBody
    public Result disable(@RequestParam(value = "ids",required = false) List<Long> ids){
        RoleRequest request = new RoleRequest();
        request.setStatus(2);
        request.setIds(ids);
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        request.setUpdateBy(user.getId());
        System.out.println("ids:"+ ids);
        return roleServiceClient.changeRoleStatus(request);
    }
    @PostMapping("/system/role/status/delete")
    @RequiresPermissions("system:role:status")
    @ResponseBody
    public Result delete(@RequestParam(value = "ids",required = false) List<Long> ids){
        RoleRequest request = new RoleRequest();
        request.setStatus(3);
        request.setIds(ids);
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        request.setUpdateBy(user.getId());
        return roleServiceClient.changeRoleStatus(request);
    }



    @GetMapping("/system/role/auth")
    @RequiresPermissions("system:role:auth")
    public String auth(@RequestParam(value = "ids",required = false) Long id, Model model){
        model.addAttribute("id",id);
        return "manager/role/auth";
    }



}
