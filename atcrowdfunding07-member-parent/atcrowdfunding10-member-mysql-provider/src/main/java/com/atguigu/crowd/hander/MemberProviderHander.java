package com.atguigu.crowd.hander;

import com.atguigu.crowd.entity.po.MemberPO;
import com.atguigu.crowd.entity.po.MemberPOExample;
import com.atguigu.crowd.mapper.MemberPOMapper;
import com.atguigu.crowd.service.api.MemberService;

import com.ydgk.ssm.constant.CrowdConstant;
import com.ydgk.ssm.util.ResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class MemberProviderHander {
    @Autowired
    private MemberService memberService;
     Logger logger= LoggerFactory.getLogger(MemberProviderHander.class);
    @RequestMapping("/get/memberpo/by/login/acct/remote")
    public ResultEntity<MemberPO> getMemberPOByLoginAcctRemote(@RequestParam("loginacct")String loginacct){
            ResultEntity<MemberPO> resultEntity=new ResultEntity<MemberPO>();
            resultEntity.setQueryData(memberService.getMemberPOByLoginAcct(loginacct));

        return resultEntity;

    }
    //自注册

    @RequestMapping("/save/member/remote")
    public ResultEntity<String> saveMember(@RequestBody MemberPO memberPO){
        logger.info(memberPO.toString());
        try {
            memberService.saveMember(memberPO);
            return ResultEntity.successWithoutData();

        }catch (Exception e){
           if (e instanceof DuplicateKeyException){
               return ResultEntity.failed(CrowdConstant.MESSAGE_ACCOUNT_NAME_ALREADY_IN_USER);
           }
            return ResultEntity.failed(e.getMessage());
        }

    }
}
