package wallet.service;

import com.xinlian.biz.model.TPledgeMiningLog;
import com.xinlian.biz.utils.NodeVoyageUtil;
import com.xinlian.member.biz.jwt.util.EncryptionUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wallet.service.base.BaseServiceTest;

/**
 * @author Song
 * @date 2020-06-20 22:00
 * @description
 */
public class NodeServiceTest extends BaseServiceTest {

    @Autowired
    private NodeVoyageUtil nodeVoyageUtil;


    @Test
    public void testNode(){
        Long nodeId = 124l;
        boolean flag = nodeVoyageUtil.belongVoyageNode(nodeId);
        System.err.println(flag);
    }
}
