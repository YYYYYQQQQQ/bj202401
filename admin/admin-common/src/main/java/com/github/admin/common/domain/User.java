package com.github.admin.common.domain;

import lombok.Data;

public class User {
    private Long id;
    private String userName;
    private String nickName;
    private String password;
    private String salt;
    private String picture;
    private Integer sex;
    private String email;
    private String phone;
    private String remark;
    private Data createDate;
    private  Data updateDate;
    private Integer status;

}
