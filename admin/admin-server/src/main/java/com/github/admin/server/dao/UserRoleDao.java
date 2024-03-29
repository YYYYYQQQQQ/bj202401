package com.github.admin.server.dao;

import com.github.admin.common.domain.UserRole;

import java.util.List;

public interface UserRoleDao {


    List<UserRole> findUserRoleByUserId(Long userId);

    int deleteUserRoleByUserId(Long id);

    int inset(UserRole userRole);

    int deleteUserRoleByRoleId(Long id);

    List<UserRole> findUserRoleByRoleId(Long id);
}
