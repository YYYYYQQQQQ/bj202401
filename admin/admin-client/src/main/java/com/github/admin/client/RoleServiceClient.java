package com.github.admin.client;

import com.github.admin.common.domain.Role;
import com.github.admin.common.request.RoleRequest;
import com.github.framework.core.Result;
import com.github.framework.core.page.DataPage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@FeignClient(value = "admin-server")
public interface RoleServiceClient {

    @GetMapping("/findRoleByUserId/{userId}")
    Result<Boolean> findRoleByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/findRolePermissionsByUserId/{userId}")
    Result<Set<Role>> findRolePermissionsByUserId(@PathVariable("userId")Long userId);

    @PostMapping("/findRoleByPage")
    Result<DataPage<Role>> findRoleByPage(@RequestBody RoleRequest roleRequest);

    @PostMapping("/saveRole")
    Result saveRole(@RequestBody RoleRequest roleRequest);

    @GetMapping("/findRoleById/{id}")
    Result<Role> findRoleById(@PathVariable("id") Long id);

    @PostMapping("/updateRole")
    Result updateRole(@RequestBody RoleRequest roleRequest);

    @PostMapping("/deleteRoleById/{id}")
    Result deleteRoleById(@PathVariable("id") Long id);

    @PostMapping("/changeRoleStatus")
    Result changeRoleStatus(@RequestBody RoleRequest request);
}
