package com.atguigu.crowd.service.api;

import com.atguigu.crowd.entity.po.MemberPO;
import com.ydgk.ssm.util.ResultEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface MemberService {
    MemberPO getMemberPOByLoginAcct(String loginacct);
    void saveMember(MemberPO memberPO);
}
