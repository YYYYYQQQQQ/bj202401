package com.github.admin.common.request;

import com.github.admin.common.group.AddUserGroup;
import com.github.admin.common.group.UpdatePwdGroup;
import com.github.admin.common.group.UpdateUserGroup;
import com.github.framework.core.common.base.BaseRequest;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserRequest extends BaseRequest {


    @NotNull(message = "用户id不能为空",groups = {UpdateUserGroup.class, UpdatePwdGroup.class})
    private Long id;
    @NotBlank(message = "用户名不能为空",groups = {AddUserGroup.class, UpdateUserGroup.class})
    private String userName;
    @NotBlank(message = "用户昵称不能为空", groups = {AddUserGroup.class, UpdateUserGroup.class})
    private String nickName;
    @NotBlank(message = "密码不能为空", groups = {AddUserGroup.class, UpdatePwdGroup.class})
    private String password;
    @NotBlank(message = "确认密码不能为空", groups = {AddUserGroup.class, UpdatePwdGroup.class})
    private String confirm;

    private String phone;
    private String email;
    private Integer status;
    private Integer sex;
    private String remark;

    private List<Long> ids;


    private List<Long> roleId;


}
