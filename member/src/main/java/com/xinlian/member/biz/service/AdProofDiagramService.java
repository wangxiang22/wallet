package com.xinlian.member.biz.service;

import com.xinlian.member.server.vo.AdProofDiagramVo;

/**
 * <p>
 * 广告人机校验表 服务类
 * </p>
 *
 * @author WX
 * @since 2020-05-16
 */
public interface AdProofDiagramService {


    /**
     * 随机获取某个实体bean
     * @return
     */
    AdProofDiagramVo getRandomOneAd()throws Exception;

    /**
     * 执行DES加密
     * @param data
     * @return
     */
    String doDesEncrypt(String data) throws Exception;

    String doRsaEncrypt(String data) throws Exception;

    /**
     * 待解密字符串
     * @param stayDecode
     * @return
     */
    <T> T doDesDecryption(String stayDecode,Class<T> clazz)throws Exception;


}
