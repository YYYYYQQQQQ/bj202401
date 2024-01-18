package com.github.admin.server;

import com.github.admin.common.domain.Role;
import com.github.admin.server.dao.RoleDao;
import com.github.admin.server.service.RoleService;
import com.github.framework.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
public class RoleServiceTest {

    @Resource
    private RoleService RoleserviceImpl;
    @Resource
    private RoleDao roleDao;

    @Test
    public void _根据用户id查询用户对应菜单(){

        List<Role> allRoleList = roleDao.findRoleByRoleIds(null).stream()
                .filter(role -> role.getStatus() == 1)
                .collect(Collectors.toList());

        Result<Set<Role>> result = RoleserviceImpl.findRolePermissionsByUserId(1L);

        log.error(allRoleList.toString());
        log.info("查询用户数据返回code = {},message = {},data = {}",result.getCode(),result.getMessage(),result.getData());
    }

}
