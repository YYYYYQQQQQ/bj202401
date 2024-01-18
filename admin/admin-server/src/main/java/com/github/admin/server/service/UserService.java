package com.github.admin.server.service;

import com.github.admin.common.domain.User;
import com.github.admin.common.request.UserRequest;
import com.github.framework.core.Result;
import com.github.framework.core.page.DataPage;

import java.util.List;

public interface UserService {

    /**
     * 根据账号查询用户
     * @param userName
     * @return
     */

    Result<User> findUserByUserName(String userName);


    /**
     * 查询用户列表
     * @param userRequest
     * @return
     */
    Result<DataPage<User>> findUserByPage(UserRequest userRequest);


    /**
     * 保存添加用户信息
     * @param request
     * @return
     */
    Result saveUser(UserRequest request);

    /**
     * 根据用户id查找用户信息
     * @param id
     * @return
     */
    Result<User> findUserById(Long id);

    /**
     * 修改用户信息
     * @param userRequest
     * @return
     */
    Result updateUser(UserRequest userRequest);


    /**
     * 根据用户id删除用户
     * @param id
     * @return
     */
    Result deleteUserById(Long id);

    /**
     * 批量启用用户权限
     * @param request
     * @return
     */
    Result changeUserStatus(UserRequest request);

    /**
     * 修改密码
     * @param request
     * @return
     */
    Result changePwd(UserRequest request);

    /**
     * 查询用户角色信息
     * @param id
     * @return
     */
    Result<User> roleAuth(Long id);


    /**
     * 修改用户角色信息
     * @param user
     * @return
     */
    Result changeUserRole(UserRequest user);

    /**
     * 根据角色查找用户列表
     * @param id
     * @return
     */
    Result<List<User>> findUsersByRoleId(Long id);
}
