package com.github.admin.server.dao;

import com.github.admin.common.domain.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RoleDao {

    List<Role> findRoleByPageList(Map<String, Object> map);

    long findRoleByPageCount(Map<String, Object> map);


    List<Role> findRoleByRoleIds(@Param("roleIds") List<Long> roleIds);

    Role findRoleByTitle(String title);

    int insert(Role role);

    Role findRoleById(Long id);

    int update(Role role);

    int deleteRoleById(Long id);
}
