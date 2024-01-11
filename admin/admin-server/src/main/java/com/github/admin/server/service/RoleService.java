package com.github.admin.server.service;

import com.github.framework.core.Result;

public interface RoleService {

    Result<Boolean> findRoleByUserId(Long userId);
}
