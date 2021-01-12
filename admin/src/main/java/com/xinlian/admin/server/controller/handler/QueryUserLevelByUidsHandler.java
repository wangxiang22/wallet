package com.xinlian.admin.server.controller.handler;

import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.next.NextUserInfoByUIdModel;
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
public class QueryUserLevelByUidsHandler {

    @Autowired
    private TUserInfoMapper userInfoMapper;



    public void getFirst(NextUserInfoByUIdModel nextUserInfoByUIdModel){
        // --
        int currentLevel = 1;
        //获取数据
        List<NextUserInfoByUIdModel> list = userInfoMapper.getFirstLevelUserUidByAuthSn(nextUserInfoByUIdModel);
        this.doHandle(list,currentLevel);
    }

    private void doHandle(List<NextUserInfoByUIdModel> list,int currentLevel){
        //插入db --
        splitInsertTempUser(list);
        //循环下一级 - 拿条件
        if(null==list || list.size()==0){
            System.err.println("条件数据为空!");
            return;
        }
        //当前身份号码放在添加到redis中
        List<NextUserInfoByUIdModel> getNextList = userInfoMapper.getNextLevelUserInfoByUidList(list);
        if(null==getNextList || 0==getNextList.size())return;
        currentLevel = currentLevel+1;
        for(NextUserInfoByUIdModel model : getNextList){
            model.setCurrentLevel(currentLevel);
        }
        doHandle(getNextList,currentLevel);
    }

    //拆分插入
    private void splitInsertTempUser(List<NextUserInfoByUIdModel> models){
        int num = models.size() % 1000 == 0 ? models.size() / 1000 : models.size() / 1000 + 1;
        List<List<NextUserInfoByUIdModel>> subLists = ListUtil.averageAssign(models, num);
        subLists.forEach(subList -> {
            userInfoMapper.batchInsertTempUserUids(subList);
        });
    }
}
