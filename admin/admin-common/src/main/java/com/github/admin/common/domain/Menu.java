package com.github.admin.common.domain;

import lombok.Data;

public class Menu {
    private Long id;
    private String title;
    private Long pid;
    private String pids;
    private String url;
    private String perms;
    private String icon;
    private int type;
    private int sort;
    private String remark;
    private Data createDate;
    private Data updateDate;
    private Long createBy;
    private Long updateBy;
    private Integer status;

}
