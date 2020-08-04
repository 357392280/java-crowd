package com.atguigu.crowd.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginVO implements java.io.Serializable{
    private static final long serialVersionUID=1L;

    private String username;

    private String email;

   private Integer id;
}