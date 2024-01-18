package com.github.admin.server.controller;

import com.github.admin.common.domain.Role;
import com.github.admin.common.request.RoleRequest;
import com.github.admin.server.service.RoleService;
import com.github.framework.core.Result;
import com.github.framework.core.page.DataPage;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Set;

@RestController
public class RoleController {

    @Resource
    private RoleService roleServiceImpl;

    @GetMapping("/findRoleByUserId/{userId}")
    Result<Boolean> findRoleByUserId(@PathVariable("userId") Long userId){
        return roleServiceImpl.findRoleByUserId(userId);
    }

    @GetMapping("/findRolePermissionsByUserId/{userId}")
    Result<Set<Role>> findRolePermissionsByUserId(@PathVariable("userId")Long userId){

        return roleServiceImpl.findRolePermissionsByUserId(userId);
    }


    @PostMapping("/findRoleByPage")
    Result<DataPage<Role>> findRoleByPage(@RequestBody RoleRequest roleRequest){
        return roleServiceImpl.findRoleByPage(roleRequest);
    }

    @PostMapping("/saveRole")
    Result saveRole(@RequestBody RoleRequest roleRequest){
        return roleServiceImpl.saveRole(roleRequest);
    }

    @GetMapping("/findRoleById/{id}")
    Result<Role> findRoleById(@PathVariable("id") Long id){
        return roleServiceImpl.findRoleById(id);
    }

    @PostMapping("/updateRole")
    Result updateRole(@RequestBody RoleRequest roleRequest){
        return roleServiceImpl.updateRole(roleRequest);
    }

    @PostMapping("/deleteRoleById/{id}")
    Result deleteRoleById(@PathVariable("id") Long id){
        return roleServiceImpl.deleteRoleById(id);
    }

    @PostMapping("/changeRoleStatus")
    Result changeRoleStatus(@RequestBody RoleRequest request){
        return roleServiceImpl.changeRoleStatus(request);
    }
}
