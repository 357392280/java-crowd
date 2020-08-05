package com.atguigu.crowd.hander;

import com.atguigu.crowd.api.MySQLRemoteService;
import com.atguigu.crowd.api.RedisRemoteService;
import com.atguigu.crowd.config.ShortMessageProperrties;
import com.atguigu.crowd.entity.po.MemberPO;
import com.atguigu.crowd.entity.vo.MemberLoginVO;
import com.atguigu.crowd.entity.vo.MemberVO;
import com.ydgk.ssm.constant.CrowdConstant;
import com.ydgk.ssm.util.CrowdUtil;
import com.ydgk.ssm.util.ResultEntity;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Controller
public class MemberHander {
    @Autowired
    private ShortMessageProperrties shortMessageProperrties;
    @Autowired
    private MySQLRemoteService mySQLRemoteService;
    @Autowired
    private RedisRemoteService redisRemoteService;
    @RequestMapping("/auth/member/loginout")
    public String loginout(HttpSession session){
        session.invalidate();
        return "redirect:/";
        //return "redirect:htttp://www.crowd.com/";
    }

    @RequestMapping("/auth/do/member/login")
    private String login(@RequestParam("loginacct")String loginacct,
                         @RequestParam("userpswd")String userpswd,
                         ModelMap modelMap,
                         HttpSession session){
     //调用远程接口获取membervo对象
        ResultEntity<MemberPO> memberPOByLoginAcctRemote = mySQLRemoteService.getMemberPOByLoginAcctRemote(loginacct);
//
        if (ResultEntity.FAILED.equals(memberPOByLoginAcctRemote.getOperationResult())){
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,memberPOByLoginAcctRemote.getOperationMessage());
            return "login";
        }
        MemberPO memberPO=memberPOByLoginAcctRemote.getQueryData();
        if (memberPO==null){
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "login";
        }
        //比较用户输入的密码与mysql密码比较
        String userpswdDate=memberPO.getUserpswd();
        BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
        boolean matchesRessult = bCryptPasswordEncoder.matches(userpswd, userpswdDate);
        if (!matchesRessult){
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "login";
        }
        //创建membeerloginvo对象
        MemberLoginVO memberLoginVO = new MemberLoginVO(memberPO.getUsername(), memberPO.getEmail(), memberPO.getId());
        session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MESSAAGE,memberLoginVO);


        return "redirect:/auth/member/to/center/page";
    }

    @ResponseBody
    @RequestMapping("auth/member/send/short/message.json")
    public ResultEntity<String> sendMessage(@RequestParam("phoneNum")String phoneNum){

        ResultEntity<String> stringResultEntity = CrowdUtil.sendCodeByhortMessage(shortMessageProperrties.getHost(),
                shortMessageProperrties.getPath(),
                shortMessageProperrties.getMethod(),
                shortMessageProperrties.getAppCode(),
                phoneNum,
                shortMessageProperrties.getTemplateId());

        if (ResultEntity.SUCCESS.equals(stringResultEntity.getOperationResult())){
            String code=stringResultEntity.getQueryData();
            String key=CrowdConstant.REDIS_CODE_PREFIX+phoneNum;
            ResultEntity<String> saveCodeResultEntity=
                             redisRemoteService.setRedisKeyValueRemoteWithTimeout(key,code,15, TimeUnit.MINUTES);

            if (ResultEntity.SUCCESS.equals(saveCodeResultEntity.getOperationResult())){
                return ResultEntity.successWithoutData();
            } else {
                return saveCodeResultEntity;
            }
        }else {
            return stringResultEntity;
        }

    }
 @RequestMapping("/auth/do/member/register")
    public String register(MemberVO memberVO, ModelMap modelMap){
        //获取手机号
     String phoneNum=memberVO.getPhoneNum();
     System.out.println(phoneNum);
     //拼接redis的验证码key
     String key=CrowdConstant.REDIS_CODE_PREFIX+phoneNum;
     //从resiss中获取value1值
     ResultEntity<String> redisStringValueByKeyRemote = redisRemoteService.getRedisStringValueByKeyRemote(key);
     //检查是否有效
     String result=redisStringValueByKeyRemote.getOperationResult();

     if (ResultEntity.FAILED.equals(result)){
         modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,redisStringValueByKeyRemote.getOperationMessage());
         return "reg";
     }
     String rediscode=redisStringValueByKeyRemote.getQueryData();
     if (rediscode== null){
         modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_CODE_NOT_EXISTS);
         return "reeg";
     }
     //如果能从redis中处查询的验证码与一致
     String formCode=memberVO.getCode();
     if (Objects.equals(formCode,redisStringValueByKeyRemote)){
         modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_CODE_INVALID);
     }
     //如果验证码一致是删除验证码
      redisRemoteService.removeRedisKeyRemote(key);
     //密码加密
     BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
     String userpswdBeforeEncode=memberVO.getUserpswd();
     String encode = bCryptPasswordEncoder.encode(userpswdBeforeEncode);
     memberVO.setUserpswd(encode);
     //保存操作
     MemberPO memberPO=new MemberPO();
     BeanUtils.copyProperties(memberVO,memberPO);
     ResultEntity<String> savememberEntity=mySQLRemoteService.saveMember(memberPO);
     if (ResultEntity.FAILED.equals(savememberEntity.getOperationResult())){
         modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,savememberEntity.getOperationMessage());
         return "reg";
     }
return "redirect:/auth/member/to/login/page";

 }
}
