package com.xinlian.admin.service;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.admin.server.controller.handler.QueryUserLevelByUidsHandler;
import com.xinlian.admin.server.controller.handler.QueryUserLevelHandler;
import com.xinlian.admin.service.base.BaseServiceTest;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.AdminMenuModel;
import com.xinlian.biz.model.TUserAuth;
import com.xinlian.biz.model.gson.Children;
import com.xinlian.biz.model.gson.ChildrenList;
import com.xinlian.biz.model.gson.Root;
import com.xinlian.biz.model.next.NextUserInfoByUIdModel;
import com.xinlian.biz.model.next.NextUserInfoModel;
import net.bytebuddy.asm.Advice;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lt
 * @date 2020/09/17
 **/
public class QueryUserInfoTest extends BaseServiceTest {

    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private QueryUserLevelHandler queryUserLevelHandler;
    @Autowired
    private QueryUserLevelByUidsHandler queryUserLevelByUidsHandler;


    //1.根据身份证号码和手机号码等信息拿到一级链权人
    @Test
    public void getFirstLevel(){
        String authSn = "230407196901090127";
        NextUserInfoModel nextUserInfoModel = new NextUserInfoModel();
        nextUserInfoModel.setAuthSn(authSn);
        queryUserLevelHandler.getFirst(nextUserInfoModel);
    }


    @Test
    public void getFirstLevelByUids(){
        String authSn = "230407196901090127";
        NextUserInfoByUIdModel nextUserInfoByUIdModel = new NextUserInfoByUIdModel();
        nextUserInfoByUIdModel.setAuthSn(authSn);
        queryUserLevelByUidsHandler.getFirst(nextUserInfoByUIdModel);
    }

    @Test
    public void createUserLevelJson(){
        //循环查询添加到下面
        //1.拿到所有
        List<NextUserInfoByUIdModel> allLists = userInfoMapper.getAllUserNode();

        Root root = new Root();
        root.setName("宋伟");
        ChildrenList children = new ChildrenList();
        children.setName("宋伟:230407196901090127:105386");
        children.setChildren(this.doRecursion(allLists,105386L));
        ChildrenList children02 = new ChildrenList();
        children02.setName("宋伟:230407196901090127:643693");
        children02.setChildren(this.doRecursion(allLists,643693L));
        List<ChildrenList> lists = new ArrayList<>();
        lists.add(children);
        lists.add(children02);
        root.setChildren(lists);

        System.err.println(JSONObject.toJSON(root));
    }

    /**
     * 递归查找子级
     *
     * @param allLists 要查找的列表
     * @param parentUid 当前根uid
     * @return
     */
    private List<ChildrenList> doRecursion(List<NextUserInfoByUIdModel> allLists,Long parentUid){
        // 子级
        List<ChildrenList> childList = new ArrayList<>();
        for (NextUserInfoByUIdModel model : allLists) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (!StringUtils.isEmpty(model.getParentUid())) {
                if (model.getParentUid().intValue() == parentUid) {
                    ChildrenList childrenList = new ChildrenList();
                    String realName = null==model.getRealName()?"":model.getRealName();
                    String authSn = null==model.getAuthSn()?"":model.getAuthSn();
                    String mobile = null==model.getMobile()?"":model.getMobile();
                    String name = realName.concat(":").concat(authSn).concat(":").concat(model.getUid().toString()).concat(":").concat(mobile);
                    childrenList.setName(name);
                    childList.add(childrenList);
                }
            }
        }
        // 把子级的子级再循环一遍
        for (ChildrenList childrenList : childList) {// 没有url子菜单还有子菜单
            if (!StringUtils.isEmpty(childrenList.getName())) {
                // 递归
                String name = childrenList.getName();
                String [] array = name.split(":");
                childrenList.setChildren(doRecursion(allLists,Long.parseLong(array[2])));
            }
        } // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }


}
