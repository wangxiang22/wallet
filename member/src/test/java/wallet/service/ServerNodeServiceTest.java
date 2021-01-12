package wallet.service;

import com.xinlian.biz.model.TServerNode;
import com.xinlian.common.result.BizException;
import com.xinlian.member.biz.service.IServerNodeService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wallet.service.base.BaseServiceTest;

/**
 * @author Song
 * @date 2020-08-26 17:03
 * @description
 */
public class ServerNodeServiceTest extends BaseServiceTest {

    @Autowired
    private IServerNodeService serverNodeService;

    @Test
    public void testServerNode(){
        for (int i=7;i<123;i++){
            try {
                checkIsNewWorld(i);
            }catch (BizException e){
                System.err.println("节点:"+i);
            }
        }
    }

    private void checkIsNewWorld(int serverNodeId){
        //-----校验节点是否允许购买
        TServerNode serverNode = serverNodeService.getById(Long.parseLong(serverNodeId+""));
        if(serverNode == null){
            throw new BizException("获取当前用户节点出现异常");
        }
        String pids = serverNode.getParentIds();
        if(StringUtils.isBlank(pids)){
            throw new BizException("获取用户节点出现异常");
        }
        if(StringUtils.equals("0", pids)){//当前节点为一级节点
            //直接判断是否为新大陆
            if(serverNode.getId().longValue() == 7L){
                throw new BizException("当前节点暂未开放购买");
            }
        }else{
            //获取所有父节点列表
            String[] pidsArr = pids.split(",");
            if(pids.length() == 1){
                throw new BizException("节点结构出现异常");
            }
            //得到一级节点id
            Long pid = Long.parseLong(pidsArr[1]);
            //判断是否为新大陆
            if(pid.longValue() == 7L){
                throw new BizException("当前节点暂未开放购买");
            }
        }
    }
}
