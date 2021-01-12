package com.xinlian.biz.dao;

import com.xinlian.biz.model.AllCurrencyRes;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserBalanceMapper {
    List<AllCurrencyRes> selectUserBalance(Long userId);
}
