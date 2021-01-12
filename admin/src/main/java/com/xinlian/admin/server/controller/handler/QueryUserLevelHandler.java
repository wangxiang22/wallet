package com.xinlian.admin.server.controller.handler;

import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TUploadChainOwnerRecord;
import com.xinlian.biz.model.next.NextUserInfoModel;
import com.xinlian.common.utils.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lt
 * @date 2020/09/23
 **/
@Component
public class QueryUserLevelHandler {

    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private RedisClient redisClient;



    public void getFirst(NextUserInfoModel nextUserInfoModel){
        String redisKey = "ADMIN_LEVEL_REDIS_KEY";
        redisClient.pushSet(redisKey,nextUserInfoModel.getAuthSn());
        // --
        int currentLevel = 1;
        //获取数据
        List<NextUserInfoModel> list = userInfoMapper.getFirstLevelUserInfoByAuthSn(nextUserInfoModel);
        this.doHandle(list,redisKey,currentLevel);
    }

    private void doHandle(List<NextUserInfoModel> list,String redisKey,int currentLevel){
        //插入db --
        splitInsertTempUser(list);
        //循环下一级 - 拿条件
        List<NextUserInfoModel> newList = new ArrayList<>();
        for(NextUserInfoModel model : list){
            if(redisClient.isMemberSet(redisKey,model.getAuthSn())){
                continue;
            }
            redisClient.pushSet(redisKey,model.getAuthSn());
            newList.add(model);
        }
        if(newList.size()==0){
            System.err.println("条件数据为空!");
            return;
        }
        //当前身份号码放在添加到redis中
        List<NextUserInfoModel> getNextList = userInfoMapper.getNextLevelUserInfoByAuthSnList(newList);
        if(null==getNextList || 0==getNextList.size())return;
        currentLevel = currentLevel+1;
        for(NextUserInfoModel model : getNextList){
            model.setCurrentLevel(currentLevel);
        }
        doHandle(getNextList,redisKey,currentLevel);
    }

    //拆分插入
    private void splitInsertTempUser(List<NextUserInfoModel> models){
        int num = models.size() % 1000 == 0 ? models.size() / 1000 : models.size() / 1000 + 1;
        List<List<NextUserInfoModel>> subLists = ListUtil.averageAssign(models, num);
        subLists.forEach(subList -> {
            userInfoMapper.batchInsertTempUser(subList);
        });
    }
}
