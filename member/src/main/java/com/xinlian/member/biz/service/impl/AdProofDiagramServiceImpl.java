package com.xinlian.member.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.dao.AdProofDiagramMapper;
import com.xinlian.biz.model.AdProofDiagramModel;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.utils.Base64Utils;
import com.xinlian.common.utils.EncryptionUtil;
import com.xinlian.common.utils.RSAEncrypt;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.AdProofDiagramService;
import com.xinlian.member.server.vo.AdProofDiagramVo;
import com.xinlian.member.server.vo.AdProofDiagramVoConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Song
 * @date 2020-05-16 19:46
 * @description
 */
@Service
public class AdProofDiagramServiceImpl implements AdProofDiagramService {

    @Autowired
    private AdProofDiagramMapper adProofDiagramMapper;
    @Autowired
    private RedisClient redisClient;
    @Value("${swaggerAuth}")
    private String swaggerAuth;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;

    @Override
    public AdProofDiagramVo getRandomOneAd()throws Exception {
        String redisKey = RedisConstant.APP_REDIS_PREFIX + "RANDOM_ONE_AD";
        AdProofDiagramVo adProofDiagramVo = redisClient.get(redisKey);
        if(null==adProofDiagramVo){
            AdProofDiagramModel adProofDiagramModel = adProofDiagramMapper.getRandomOneAd();
            adProofDiagramVo = new AdProofDiagramVoConvertor().convert(adProofDiagramModel);
            redisClient.set(redisKey,adProofDiagramVo,6 * 60);
        }
        return adProofDiagramVo;
    }

    @Override
    public String doDesEncrypt(String data) throws Exception{
        return EncryptionUtil.encryptDES(data, EncryptionUtil.ENCRYPT_PWD);
    }

    @Override
    public String doRsaEncrypt(String data) throws Exception{
        String privateKey = "";
        if(null!=swaggerAuth && "gray".equals(swaggerAuth)){
            privateKey = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.APP_RSA_GRAY_PRIVATE_KEY.getBelongsSystemCode());
        }else{
            privateKey = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.APP_RSA_PRIVATE_KEY.getBelongsSystemCode());
        }
        byte [] bvy = RSAEncrypt.encryptByPrivateKey(data.getBytes(), privateKey);
        return Base64Utils.encode(bvy);
    }

    @Override
    public <T> T doDesDecryption(String stayDecode,Class<T> clazz)throws Exception{
        String reqData = EncryptionUtil.decryptDES(stayDecode, EncryptionUtil.DES_DECRYPT_PWD);
        return JSONObject.parseObject(reqData, clazz);
    }
}
