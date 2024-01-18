package com.github.admin.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.admin.common.domain.*;
import com.github.admin.common.enums.AdminErrorMsgEnum;
import com.github.admin.common.request.RoleRequest;
import com.github.admin.server.dao.*;
import com.github.admin.server.service.RoleService;
import com.github.framework.core.Result;
import com.github.framework.core.exception.Ex;
import com.github.framework.core.page.DataPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Resource
    private UserDao userDao;
    @Resource
    private UserRoleDao userRoleDao;
    @Resource
    private RoleDao roleDao;
    @Resource
    private RoleMenuDao roleMenuDao;
    @Resource
    private MenuDao menuDao;

    @Override
    public Result<Boolean> findRoleByUserId(Long userId) {

        if (userId == null){
            log.error("查询用户对应角色是否授权userId为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }

        User user = userDao.findUserByPrimaryKey(userId);
        if(user == null){
            log.error("查询userId = {}对应用户不存在",userId);
            return Result.fail(AdminErrorMsgEnum.DATA_IS_NOT_EXIST);
        }
        if(user.getStatus() != 1){
            log.error("当前userId = {}对应用户已停用",userId);
            return Result.fail(AdminErrorMsgEnum.USER_ACCOUNT_HAS_BEEN_FROZEN);
        }

        List<UserRole> userRoleList = userRoleDao .findUserRoleByUserId(userId);
        if(CollectionUtils.isEmpty(userRoleList)){
            log.error("当前userId = {}没有对应角色",userId);
            return Result.fail(AdminErrorMsgEnum.DATA_IS_NOT_EXIST);
        }
        //从查询出来的用户角色集合中通过STREAM流对角色ID收集
        List<Long> roleIds = userRoleList.stream().
                map(userRole -> userRole.getRoleId()).
                collect(Collectors.toList());

        //查询角色集合，然后过滤状态启用的角色
        List<Role> roleList = roleDao.findRoleByRoleIds(roleIds)
                .stream().filter(role -> role.getStatus() == 1)
                .collect(Collectors.toList());

        if(CollectionUtils.isEmpty(roleList)){
            log.error("当前userId = {}没有对应角色",userId);
            return Result.fail(AdminErrorMsgEnum.DATA_IS_NOT_EXIST);
        }

        List<RoleMenu> roleMenuList = roleMenuDao.findRoleMenuByRoleIds(roleIds);

        if(CollectionUtils.isEmpty(roleMenuList)){
            log.error("当前userId = {}查询角色菜单数据不存在",userId);
            return Result.fail(AdminErrorMsgEnum.DATA_IS_NOT_EXIST);
        }

        List<Long> menuIdList = roleMenuList.stream().
                map(roleMenu -> roleMenu.getMenuId()).collect(Collectors.toList());

        List<Menu> menuList = menuDao.findMenuByMenuIds(menuIdList).
                stream().filter(menu -> menu.getStatus() == 1).collect(Collectors.toList());

        if(CollectionUtils.isEmpty(menuList)){
            log.error("当前userId = {}查询菜单数据不存在",userId);
            return Result.fail(AdminErrorMsgEnum.DATA_IS_NOT_EXIST);
        }

        log.info("当前userId = {}查询角色授权成功",userId);

        return Result.ok(Boolean.TRUE);
    }

    @Override
    public Result<Set<Role>> findRolePermissionsByUserId(Long userId) {
        if (userId == null){
            log.error("查询角色授权userId参数为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }

        User user = userDao.findUserByPrimaryKey(userId);
        if(user == null){
            log.error("查询角色授权对应用户数据为空，userId = {}",userId);
            return Result.fail(AdminErrorMsgEnum.DATA_IS_NOT_EXIST);
        }
        if(user.getStatus() != 1){
            log.error("查询角色授权对应用户已停用，userId = {}",userId);
            return Result.fail(AdminErrorMsgEnum.USER_ACCOUNT_HAS_BEEN_FROZEN);
        }

        List<UserRole> userRoleList = userRoleDao.findUserRoleByUserId(userId);
        if(CollectionUtils.isEmpty(userRoleList)){
            log.error("查询角色授权用户角色数据为空，userId = {}");
            return Result.fail(AdminErrorMsgEnum.DATA_IS_NOT_EXIST);
        }

        List<Long> roleIds = userRoleList.stream().map(userRole -> userRole.getRoleId())
                .collect(Collectors.toList());
        List<Role> roleList = roleDao.findRoleByRoleIds(roleIds);
        if(CollectionUtils.isEmpty(roleList)){
            log.error("查询角色授权对应角色为空，userId = {}",userId);
            return Result.fail(AdminErrorMsgEnum.DATA_IS_NOT_EXIST);
        }

        for(Role role:roleList){
            Long roleId = role.getId();
            List<RoleMenu> roleMenuList = roleMenuDao.findRoleMenuByRoleIds(Arrays.asList(roleId));
            if(CollectionUtils.isEmpty(roleMenuList)){
                log.error("查询角色授权没有对应角色菜单,roleId = {}",roleId);
                continue;
            }
            List<Long> menuIds = roleMenuList.stream().
                    map(roleMenu -> roleMenu.getMenuId()).collect(Collectors.toList());

            List<Menu> menuList = menuDao.findMenuByMenuIds(menuIds).stream()
                    .filter(menu -> menu.getStatus() == 1).collect(Collectors.toList());
            Set<Menu> menuSet = menuList.stream().collect(Collectors.toSet());
            role.setMenus(menuSet);
        }

        Set<Role> roleSet = roleList.stream().collect(Collectors.toSet());
        log.error("查询角色授权成功，userId = {}",userId);

        return Result.ok(roleSet);
    }

    @Override
    public Result<DataPage<Role>> findRoleByPage(RoleRequest roleRequest) {

        if(roleRequest == null){
            log.error("查询角色分页请求对象为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }

        int pageNo = roleRequest.getPageNo();
        int pageSize = roleRequest.getPageSize();
        DataPage<Role> dataPage = new DataPage<>(pageNo, pageSize);
        if (StringUtils.isBlank(roleRequest.getOrderByColumn()) || StringUtils.isBlank(roleRequest.getAsc())) {
            roleRequest.setOrderByColumn("create_date");
            roleRequest.setAsc("desc");
        }
        //通过java反射机制把对象转化为map
        Map<String, Object> map = BeanUtil.beanToMap(roleRequest);
        map.put("startIndex", dataPage.getStartIndex());
        map.put("offset", dataPage.getEndIndex());

        long totalCount = roleDao.findRoleByPageCount(map);
        List<Role> dataList = new ArrayList<>();
        dataList = roleDao.findRoleByPageList(map);
        dataPage.setTotalCount(totalCount);
        dataPage.setDataList(dataList);
        return Result.ok(dataPage);
    }


    @Override
    public Result saveRole(RoleRequest request) {
        if (request == null) {
            log.error("添加角色请求参数对象为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (StringUtils.isBlank(request.getTitle())) {
            log.error("添加角色名称为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (StringUtils.isBlank(request.getName())) {
            log.error("添加角色标识名称为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }

        String title = request.getTitle();
        String name = request.getName();
        Integer status = request.getStatus();

        if (title.length() != StringUtils.deleteWhitespace(title).length()) {
            log.error("添加角色名称存在空格");
            return Result.fail(AdminErrorMsgEnum.ROLE_TITLE_HAS_INCLUDE_BLANK);
        }

        title = StringUtils.deleteWhitespace(title);

        Role existRole = roleDao.findRoleByTitle(title);
        if (existRole != null) {
            log.error("添加角色当前角色名称已存在,title = {}", title);
            return Result.fail(AdminErrorMsgEnum.ROLE_TITLE_IS_EXIST);
        }

        Role role = new Role();
        BeanUtils.copyProperties(request,role);
        Date date = new Date();
        role.setCreateDate(date);
        role.setUpdateDate(date);
        role.setTitle(title);
        int row = roleDao.insert(role);
        //添加成功返回row == 1,其他数据都是失败
        if (row != 1) {
            log.error("添加角色操作失败");
            return Result.fail(AdminErrorMsgEnum.OPERATION_FAIL);
        }
        return Result.ok();
    }


    @Override
    public Result<Role> findRoleById(Long id) {

        if(id == null){
            log.error("查询角色信息id = {}角色id为空", id);
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }

        Role role = roleDao.findRoleById(id);
        if(role == null){
            log.error("查询角色信息不存在");
            return Result.fail(AdminErrorMsgEnum.DATA_IS_NOT_EXIST);
        }

        return Result.ok(role);
    }

    @Transactional
    @Override
    public Result updateRole(RoleRequest roleRequest) {
        if (roleRequest == null) {
            log.error("更新角色请求参数对象为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (roleRequest.getId() == null) {
            log.error("更新角色请求参数id为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }

        if (StringUtils.isBlank(roleRequest.getName())) {
            log.error("更新角色标识名称为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }

        if (StringUtils.isBlank(roleRequest.getTitle())) {
            log.error("更新角色名称为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        String title = StringUtils.deleteWhitespace(roleRequest.getTitle());
        if (roleRequest.getTitle().length() != title.length()) {
            log.error("更新角色名称存在空格,");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }

        Long id = roleRequest.getId();
        Role existRole = roleDao.findRoleById(roleRequest.getId());
        if (existRole == null) {
            log.error("更新角色操作对应角色数据不存在,id = {}", roleRequest.getId());
            return Result.fail(AdminErrorMsgEnum.OPERATION_FAIL);
        }

        existRole = roleDao.findRoleByTitle(title);
        if (existRole != null && existRole.getId() != id) {
            log.error("更新用户操作当前角色名称已存在,title = {}", roleRequest.getTitle());
            return Result.fail(AdminErrorMsgEnum.ROLE_TITLE_IS_EXIST);
        }

        Role role = new Role();
        BeanUtils.copyProperties(roleRequest, role);
        role.setUpdateDate(new Date());
        role.setTitle(StringUtils.deleteWhitespace(role.getTitle()));
        log.error(role.toString());
        int row = roleDao.update(role);
        //添加成功返回row == 1,其他数据都是失败
        if (row != 1) {
            log.error("编辑角色操作失败");
            throw Ex.business(AdminErrorMsgEnum.OPERATION_FAIL);
        }


        return Result.ok();
    }

    @Transactional
    @Override
    public Result deleteRoleById(Long id) {
        if (id == null) {
            log.error("更新用户请求参数为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        Role existRole = roleDao.findRoleById(id);
        if (existRole == null) {
            log.error("删除角色操作对应角色数据不存在,id = {}", id);
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }

        if(existRole.getId() == 1){
            log.error("该角色为管理员，无权限删除");
            return Result.fail(AdminErrorMsgEnum.HAVE_NO_AUTHORITY);
        }

        int row1 = roleDao.deleteRoleById(id);
        if(row1 != 1){
            log.error("删除角色操作失败");
            throw Ex.business(AdminErrorMsgEnum.OPERATION_FAIL);
        }
        int row2 = userRoleDao.deleteUserRoleByRoleId(id);
        int row3 = roleMenuDao.deleteRoleMenuByRoleId(id);

        return Result.ok();
    }

    @Transactional
    @Override
    public Result changeRoleStatus(RoleRequest request) {
        if (request == null) {
            log.error("角色授权请求参数对象为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (CollectionUtils.isEmpty(request.getIds())) {
            log.error("角色授权请求参数ids为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (request.getStatus() == null) {
            log.error("角色授权请求参数status为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        Integer status = request.getStatus();
        for (Long id : request.getIds()) {
            Role role = new Role();
            role.setId(id);
            role.setStatus(status);
            role.setUpdateDate(new Date());
            int row = roleDao.update(role);
            if (row != 1) {
                log.error("角色授权roleId = {}修改状态失败", id);
                throw Ex.business(AdminErrorMsgEnum.OPERATION_FAIL);
            }
        }
        return Result.ok();
    }
}
