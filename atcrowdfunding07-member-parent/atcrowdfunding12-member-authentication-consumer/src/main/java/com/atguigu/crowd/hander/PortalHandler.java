package com.atguigu.crowd.hander;

import com.atguigu.crowd.api.MySQLRemoteService;
import com.atguigu.crowd.entity.vo.PortalTypeVO;
import com.ydgk.ssm.constant.CrowdConstant;
import com.ydgk.ssm.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class PortalHandler {
    @Autowired
    private MySQLRemoteService mySQLRemoteService;
    @RequestMapping("/")
    public String ShowPortallPage(Model model){
        //调用冤案错成接口
        ResultEntity<List<PortalTypeVO>> resultEntity =
                mySQLRemoteService.getPortalTypeProjectDataRemote();

        //2.获取数据
        String result=resultEntity.getOperationResult();
        if (ResultEntity.SUCCESS.equals(result)){
            List<PortalTypeVO> list = resultEntity.getQueryData();

            //4.存入模型
            model.addAttribute(CrowdConstant.ATTR_NAME_PROJECT_DATA,list);

        }

        return "portal";
    }
}
