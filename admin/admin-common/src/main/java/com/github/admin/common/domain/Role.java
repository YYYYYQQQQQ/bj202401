package com.github.admin.common.domain;

import lombok.Data;

public class Role {
    private Long id;
    private String title;
    private String name;
    private String remark;
    private Data createDate;
    private Data updateDate;
    private Long createBy;
    private Long updateBy;
    private Integer status;
}
