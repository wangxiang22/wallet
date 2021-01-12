package com.xinlian.member.biz.springrunner;/*
package com.xinlian.wallet.biz.springrunner;

import com.paipaiprofi.common.utils.UniqueId;
import com.paipaiprofi.common.utils.UniqueNoUtil;
import com.paipaiprofi.member.biz.dao.SysNoConfigMapper;
import com.paipaiprofi.member.biz.model.SysNoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

*/
/**
 * 初始化 系统编号
 * 服务器ip 最后一段 作为系统编号
 *//*

@Component
@Order(1)
public class InitSysNoRunner implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(InitSysNoRunner.class);

    @Autowired
    private SysNoConfigMapper sysNoConfigMapper;

    @Autowired
    private Environment environment;

    private int port = 0;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        port = Integer.valueOf(environment.getProperty("server.port"));
        //创建 系统编号
        SysNoConfig sysNoConfig = initSysNoConfig();
        //注册 系统编号
        int sysNo = registerSysNo(sysNoConfig);
        //设置 系统编号
        UniqueId.setSystemNo(sysNo);
        UniqueNoUtil.setSystemNo(sysNo);
        logger.info("InitSysNoRunner注册系统编号sysNo==" + sysNo);
    }

    */
/**
     * 系统ip 最后一段 作为系统编号
     *//*

    private SysNoConfig initSysNoConfig() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        String ip = addr.getHostAddress();
        int sysNo = Integer.valueOf(ip.substring(ip.lastIndexOf(".") + 1));
        if(sysNo == 0){
            sysNo = 1;
        }
        SysNoConfig sysNoConfig = new SysNoConfig();
        sysNoConfig.setId((long)sysNo);
        sysNoConfig.setSysIp(ip);
        sysNoConfig.setSysNo(sysNo);
        sysNoConfig.setSys_port(port);
        sysNoConfig.setCreateTime(new Date());
        return sysNoConfig;
    }

    */
/**
     * 注册系统编号
     *//*

    private int registerSysNo(SysNoConfig sysNoConfig){
        while(true){
            try{
                SysNoConfig sameSysno = sysNoConfigMapper.selectById(sysNoConfig.getId());
                //系统编号 已存在
                if(sameSysno != null){
                    //是本系统注册的
                    if(sameSysno.getSysIp().equals(sysNoConfig.getSysIp()) &&
                            sameSysno.getSys_port()==port){
                        return sysNoConfig.getSysNo();
                    }
                    //已被其他系统注册 修正系统编号
                    correctSysNo(sysNoConfig);
                }
                //注册系统编号
                sysNoConfigMapper.insert(sysNoConfig);
                return sysNoConfig.getSysNo();
            }catch(Exception e){
                logger.error(e.getMessage(), e);
                //异常 修正编号
                correctSysNo(sysNoConfig);
            }
        }
    }

    */
/**
     * 修正
     *//*

    private void correctSysNo(SysNoConfig sysNoConfig){
        int sysNo = sysNoConfig.getSysNo();
        sysNo = sysNo + 1;
        if(sysNo > 255){
            sysNo = 1;
        }
        sysNoConfig.setId((long)sysNo);
        sysNoConfig.setSysNo(sysNo);
    }
}
*/
