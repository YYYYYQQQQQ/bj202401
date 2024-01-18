package com.github.admin.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.github.admin.common.domain.Role;
import com.github.admin.common.domain.User;
import com.github.admin.common.domain.UserRole;
import com.github.admin.common.enums.AdminErrorMsgEnum;
import com.github.admin.common.request.UserRequest;
import com.github.admin.common.utils.ShiroUtil;
import com.github.admin.server.dao.RoleDao;
import com.github.admin.server.dao.UserDao;
import com.github.admin.server.dao.UserRoleDao;
import com.github.admin.server.service.UserService;
import com.github.framework.core.Result;
import com.github.framework.core.exception.Ex;
import com.github.framework.core.page.DataPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserServiceImpl implements UserService {


    @Resource
    private UserDao userDao;

    @Resource
    private UserRoleDao userRoleDao;

    @Resource
    private RoleDao roleDao;

    @Override
    public Result<User> findUserByUserName(String userName) {
        if (StringUtils.isBlank(userName)) {
            log.error("查询用户userName参数为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }

        User user = userDao.findUserByUserName(userName);
        if (user == null) {
            log.error("根据userName = {}查询用户数据为空", userName);
            return Result.fail(AdminErrorMsgEnum.DATA_IS_NOT_EXIST);
        }

        return Result.ok(user);
    }

    @Override
    public Result<DataPage<User>> findUserByPage(UserRequest userRequest) {
        int pageNo = userRequest.getPageNo();
        int pageSize = userRequest.getPageSize();
        DataPage<User> dataPage = new DataPage<>(pageNo, pageSize);
        if (StringUtils.isBlank(userRequest.getOrderByColumn()) || StringUtils.isBlank(userRequest.getAsc())) {
            userRequest.setOrderByColumn("create_date");
            userRequest.setAsc("desc");
        }

        //通过java反射机制把对象转化为map
        Map<String, Object> map = BeanUtil.beanToMap(userRequest);
        map.put("startIndex", dataPage.getStartIndex());
        map.put("endIndex", dataPage.getEndIndex());

        long totalCount = userDao.findUserByPageCount(map);
        List<User> dataList = new ArrayList<>();
        dataList = userDao.findUserByPageList(map);
        dataPage.setTotalCount(totalCount);
        dataPage.setDataList(dataList);
        return Result.ok(dataPage);
    }

    @Override
    public Result saveUser(UserRequest request) {
        if (request == null) {
            log.error("添加用户请求参数对象为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (StringUtils.isBlank(request.getUserName())) {
            log.error("添加用户账号为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (StringUtils.isBlank(request.getPassword())) {
            log.error("添加用户密码为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (StringUtils.isBlank(request.getPassword())) {
            log.error("添加用户确认密码为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }

        String userName = request.getUserName();
        String password = request.getPassword();
        String confirmPwd = request.getConfirm();

        if (!StringUtils.equals(password, confirmPwd)) {
            log.error("添加用户密码和确认密码不一致，password = {},configPwrd = {}", password, confirmPwd);
            return Result.fail(AdminErrorMsgEnum.USER_PASSWORD_IS_NOT_SAME);
        }
        if (userName.length() != StringUtils.deleteWhitespace(userName).length()) {
            log.error("添加用户账号存在空格");
            return Result.fail(AdminErrorMsgEnum.USER_NAME_HAS_INCLUDE_BLANK);
        }
        userName = StringUtils.deleteWhitespace(userName);

        User existUser = userDao.findUserByUserName(userName);
        if (existUser != null) {
            log.error("添加用户当前账号已存在,userName = {}", userName);
            return Result.fail(AdminErrorMsgEnum.USER_NAME_IS_EXIST);
        }

        User user = new User();
        BeanUtils.copyProperties(request, user);
        Date date = new Date();
        user.setCreateDate(date);
        user.setUpdateDate(date);
        user.setUserName(userName);
        //获取加密盐
        String salt = ShiroUtil.getRandomSalt();
        //加密
        String pwd = ShiroUtil.encrypt(password, salt);
        user.setSalt(salt);
        user.setPassword(pwd);
        int row = userDao.insert(user);
        //添加成功返回row == 1,其他数据都是失败
        if (row != 1) {
            log.error("添加用户操作失败");
            return Result.fail(AdminErrorMsgEnum.OPERATION_FAIL);
        }


        return Result.ok();
    }

    @Override
    public Result<User> findUserById(Long id) {
        if (id == null) {
            log.error("查询用户id参数为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        User user = userDao.findUserByPrimaryKey(id);
        if (user == null) {
            log.error("查询用户id = {}数据不存在", id);
            return Result.fail(AdminErrorMsgEnum.DATA_IS_NOT_EXIST);
        }
        return Result.ok(user);
    }

    @Override
    public Result updateUser(UserRequest userRequest) {
        if (userRequest == null) {
            log.error("更新用户请求参数对象为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (userRequest.getId() == null) {
            log.error("更新用户请求参数id为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (StringUtils.isBlank(userRequest.getUserName())) {
            log.error("更新用户账号为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }

        if (StringUtils.isBlank(userRequest.getNickName())) {
            log.error("更新用户昵称为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        String userName = StringUtils.deleteWhitespace(userRequest.getUserName());
        if (userRequest.getUserName().length() != userName.length()) {
            log.error("更新用户名存在空格,");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        Long id = userRequest.getId();
        User existUser = userDao.findUserByPrimaryKey(userRequest.getId());
        if (existUser == null) {
            log.error("更新用户操作对应用户数据不存在,id = {}", userRequest.getId());
            return Result.fail(AdminErrorMsgEnum.OPERATION_FAIL);
        }

        existUser = userDao.findUserByUserName(userName);
        if (existUser != null && existUser.getId() != id) {
            log.error("更新用户操作当前用户名已存在,userName = {}", userRequest.getUserName());
            return Result.fail(AdminErrorMsgEnum.USER_NAME_IS_EXIST);
        }

        User user = new User();
        BeanUtils.copyProperties(userRequest, user);
        user.setUpdateDate(new Date());
        user.setUserName(StringUtils.deleteWhitespace(user.getUserName()));
        int row = userDao.update(user);
        //添加成功返回row == 1,其他数据都是失败
        if (row != 1) {
            log.error("编辑用户操作失败");
            return Result.fail(AdminErrorMsgEnum.OPERATION_FAIL);
        }

        return Result.ok();
    }

    @Transactional
    @Override
    public Result deleteUserById(Long id) {
        if (id == null) {
            log.error("更新用户请求参数为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        User existUser = userDao.findUserByPrimaryKey(id);
        if (existUser == null) {
            log.error("删除用户操作对应用户数据不存在,id = {}", id);
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }


        List<UserRole> list = userRoleDao.findUserRoleByUserId(id);
        if (!CollectionUtils.isEmpty(list)) {
            int row1;
            int row2;
            row1 = userDao.deleteUserById(id);
            row2 = userRoleDao.deleteUserRoleByUserId(id);
            //手动制造异常模拟删除时出现的错误
            //Integer i = 1 / 0;
            if (row1 != 1) {
                log.error("删除用户操作失败");
                throw Ex.business(AdminErrorMsgEnum.OPERATION_FAIL);
            }
            if (row2 != list.size()) {
                log.error("删除用户角色操作失败");
                throw Ex.business(AdminErrorMsgEnum.OPERATION_FAIL);
            }
        } else {
            int row1;
            row1 = userDao.deleteUserById(id);
            if (row1 != 1) {
                log.error("删除用户操作失败");
                throw Ex.business(AdminErrorMsgEnum.OPERATION_FAIL);
            }
        }
        return Result.ok();
    }

    @Transactional
    @Override
    public Result changeUserStatus(UserRequest request) {
        if (request == null) {
            log.error("更新用户请求参数为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (CollectionUtils.isEmpty(request.getIds())) {
            log.error("更新用户请求参数ids为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (request.getStatus() == null) {
            log.error("更新用户请求参数status为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        Integer status = request.getStatus();
        for (Long id : request.getIds()) {
            User user = new User();
            user.setId(id);
            user.setStatus(status);
            int row = userDao.update(user);
            if (row != 1) {
                log.error("更新用户userId = {}修改状态失败", id);
                throw Ex.business(AdminErrorMsgEnum.OPERATION_FAIL);
            }
        }
        return Result.ok();
    }

    @Transactional
    @Override
    public Result changePwd(UserRequest request) {
        if (request == null) {
            log.error("修改密码请求参数对象为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (request.getId() == null) {
            log.error("修改密码请求参数id为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (StringUtils.isBlank(request.getPassword())) {
            log.error("修改密码请求参数为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (StringUtils.isBlank(request.getConfirm())) {
            log.error("修改密码请求参数为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }

        String password = request.getPassword();
        String confirmPwd = request.getConfirm();
        if (!StringUtils.equals(password, confirmPwd)) {
            log.error("添加用户密码和确认密码不一致，password = {},configPwrd = {}", password, confirmPwd);
            return Result.fail(AdminErrorMsgEnum.USER_PASSWORD_IS_NOT_SAME);
        }

        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setId(request.getId());
        Date date = new Date();
        user.setUpdateDate(date);
        //获取加密盐
        String salt = ShiroUtil.getRandomSalt();
        //加密
        String pwd = ShiroUtil.encrypt(password, salt);
        user.setSalt(salt);
        user.setPassword(pwd);
        int row = userDao.update(user);
        if (row != 1) {
            log.error("修改密码userId = {}修改密码失败", user.getId());
            throw Ex.business(AdminErrorMsgEnum.OPERATION_FAIL);
        }
        return Result.ok();
    }


    @Override
    public Result<User> roleAuth(Long id) {
        if (id == null) {
            log.error("用户角色权限请求参数为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }

        User user = userDao.findUserByPrimaryKey(id);
        if (user == null) {
            log.error("用户角色授权id = {}对应用户不存在", id);
            return Result.fail(AdminErrorMsgEnum.DATA_IS_NOT_EXIST);
        }

        List<UserRole> userRoleList = userRoleDao.findUserRoleByUserId(id);
//        if(CollectionUtils.isEmpty(userRoleList)){
//            log.error("用户角色授权id = {}对应用户角色不存在", id);
//            return Result.fail(AdminErrorMsgEnum.DATA_IS_NOT_EXIST);
//        }

        List<Long> roleIds = new ArrayList<>();
        //查询用户对应的角色集合
        List<Role> roleList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userRoleList)) {
            roleIds = userRoleList.stream()
                    .map(userRole -> userRole.getRoleId())
                    .collect(Collectors.toList());
            roleList = roleDao.findRoleByRoleIds(roleIds);
        }

        Set<Role> sets = roleList.stream()
                .filter(role -> role.getStatus() == 1)
                .collect(Collectors.toSet());

        List<Role> allRoleList = roleDao.findRoleByRoleIds(null).stream()
                .filter(role -> role.getStatus() == 1)
                .collect(Collectors.toList());
        user.setAuthSet(sets);
        user.setList(allRoleList);
        return Result.ok(user);
    }

    @Transactional
    @Override
    public Result changeUserRole(UserRequest request) {
        if (request == null) {
            log.error("用户角色授权请求参数对象为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        if (request.getId() == null) {
            log.error("用户角色授权请求参数id为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        User user = userDao.findUserByPrimaryKey(request.getId());
        if(user == null){
            log.error("用户角色授权id = {}对应用户不存在",user.getId());
            return Result.fail(AdminErrorMsgEnum.DATA_IS_NOT_EXIST);
        }
        List<UserRole> roleList = userRoleDao.findUserRoleByUserId(request.getId());
        int row = userRoleDao.deleteUserRoleByUserId(request.getId());
        if(row != roleList.size()){
            log.error("修改用户角色信息userId = {}删除操作失败",request.getId());
            throw Ex.business(AdminErrorMsgEnum.OPERATION_FAIL);
        }
        if(request.getRoleId() != null){
            for(int i =0; i<request.getRoleId().size();i++){
                UserRole userRole = new UserRole();
                userRole.setUserId(request.getId());
                userRole.setRoleId(request.getRoleId().get(i));
                int row2 = userRoleDao.inset(userRole);
                if(row2 != 1){
                    log.error("修改用户角色信息userId = {},roleId = {}添加操作失败",request.getId(),request.getRoleId().get(i));
                    throw Ex.business(AdminErrorMsgEnum.OPERATION_FAIL);
                }
            }
        }
        return Result.ok();
    }


    @Override
    public Result<List<User>> findUsersByRoleId(Long id) {

        if (id == null) {
            log.error("根据角色查询用户列表请求参数为空");
            return Result.fail(AdminErrorMsgEnum.REQUEST_PARAM_IS_EMPTY);
        }
        Role role = roleDao.findRoleById(id);
        if (role == null) {
            log.error("根据角色查询用户列表id = {}对应角色不存在", id);
            return Result.fail(AdminErrorMsgEnum.DATA_IS_NOT_EXIST);
        }

        List<UserRole> userRoleList = userRoleDao.findUserRoleByRoleId(id);
        //查询角色对应的用户集合
        List<Long> userIds = new ArrayList<>();
        List<User> userList = new ArrayList<User>();
        if (!CollectionUtils.isEmpty(userRoleList)) {
            userIds = userRoleList.stream()
                    .map(userRole -> userRole.getUserId())
                    .collect(Collectors.toList());
            userList = userDao.findUserByUserIds(userIds);
        }
        return Result.ok(userList);
    }
}
