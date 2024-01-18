package com.github.admin.common.request;

import com.github.admin.common.group.AddRoleGroup;
import com.github.admin.common.group.UpdateRoleGroup;
import com.github.framework.core.common.base.BaseRequest;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RoleRequest extends BaseRequest {

    @NotNull(message = "角色id不能为空",groups = {UpdateRoleGroup.class})
    private Long id;
    @NotBlank(message = "角色名称不能为空",groups = {AddRoleGroup.class, UpdateRoleGroup.class})
    private String title;
    @NotBlank(message = "标识名称不能为空",groups = {AddRoleGroup.class, UpdateRoleGroup.class})
    private String name;

    private Integer status;
    private String remark;

    private Long createBy;
    private Long updateBy;

    private List<Long> ids;


}
