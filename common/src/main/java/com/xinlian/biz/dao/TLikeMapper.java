package com.xinlian.biz.dao;

import com.xinlian.biz.model.TLike;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Component;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wx
 * @since 2020-03-27
 */
@Component
public interface TLikeMapper extends BaseMapper<TLike> {

    TLike queryLike(int uid,int nid);
    Integer insertLike1(int uid,int nid);
    Integer insertLike(int uid,int nid);
    Integer updateLikeStatus(int uid,int nid);
    Integer updateLike(int uid,int nid);
}
