package com.github.admin.server.service;

import com.github.admin.common.domain.Role;
import com.github.admin.common.request.RoleRequest;
import com.github.framework.core.Result;
import com.github.framework.core.page.DataPage;

import java.util.Set;

public interface RoleService {

    /**
     * 查询角色授权状态
     * @param userId
     * @return
     */
    Result<Boolean> findRoleByUserId(Long userId);

    /**
     * 查询角色授权信息
     * @param userId
     * @return
     */
    Result<Set<Role>> findRolePermissionsByUserId(Long userId);


    /**
     * 查询角色列表
     * @param roleRequest
     * @return
     */
    Result<DataPage<Role>> findRoleByPage(RoleRequest roleRequest);


    /**
     * 添加角色信息
     * @param roleRequest
     * @return
     */
    Result saveRole(RoleRequest roleRequest);

    /**
     * 查询角色信息
     * @param id
     * @return
     */
    Result<Role> findRoleById(Long id);

    /**
     * 修改角色信息
     * @param roleRequest
     * @return
     */
    Result updateRole(RoleRequest roleRequest);

    /**
     * 删除角色信息
     * @param id
     * @return
     */
    Result deleteRoleById(Long id);

    /**
     * 角色授权
     * @param request
     * @return
     */
    Result changeRoleStatus(RoleRequest request);
}
