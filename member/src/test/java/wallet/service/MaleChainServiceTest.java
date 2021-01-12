package wallet.service;

import com.xinlian.biz.model.Address;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.RechargeOperTypeEnum;
import com.xinlian.common.utils.Base64Utils;
import com.xinlian.common.utils.RSAEncrypt;
import com.xinlian.member.biz.malechain.MaleChainConfig;
import com.xinlian.member.biz.service.MaleChainService;
import com.xinlian.member.server.controller.handler.MaleChainCallbackHandler;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import wallet.service.base.BaseServiceTest;

public class MaleChainServiceTest extends BaseServiceTest {

    @Autowired
    private MaleChainService maleChainService;
    @Autowired
    private MaleChainConfig maleChainConfig;


    public static void main(String[] args) throws Exception{
        System.err.println(System.getProperty("user.dir"));
        ClassPathResource cpr = new ClassPathResource("CATWallet每日资产统计数据模板.xlsx");
        System.err.println(ResourceUtils.getURL("classpath:").getPath());
    }
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    /**
     * 检验公私钥
     * @throws Exception
     */
    @Test
    public void checkPublicPrivate()throws Exception{
//        String tmp_requestBody = Base64Utils.encode(RSAEncrypt.encryptByPublicKey(stayDoEncrypt.getBytes(),
//                RSAEncrypt.getPublicKey(request.getEncryptKeyUrl())));
        String data = "{\"tx_hash\":\"0x04bfe214f打手电8b0a821e55676574ad3b568f7503aaa545f045a5736022f8c22a795\",\"bus的inessId\":430255,\"sta搭tus\":1,\"fee\":0}";
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtkIroBjbYdE8ugRLlTYELSMjofefSD0BSEq+XAIuii2rGcbGfQKqAtaWyqO58LuwWpqIzkuTPvMNsHLyYzLq5CEYjcI9baaSnfH1HHwZ261LHzl1F3gA63XCqccS34mxxdy58rP2ZRSuFMHJjD/RYuwPblgCARsvuCHzhogl20QT0wZvtOSREEgPYsX6pIcBjF9n08dxv6vOVKJ+91P1NY2O4cC7TYBaR/2dy2ZpkWAUkV6ysqlILao06bg+aoJ74J7nNRzCphSX3Y8b0UXs2jojP+MELP7bWBdI2onmbH2sFbcBWlAdbe8qlkypeKRDubk0Uomt4Sjhk6c/xXmGKwIDAQAB";
        byte [] encryptByte = RSAEncrypt.encryptByPublicKey(data.getBytes(),publicKey);
        String encryptData = Base64Utils.encode(encryptByte);
        System.err.println("加密后的字符串:"+encryptData);
        //String key = RSAEncrypt.getPublicKey()
        String stayEncode = "cxRxiX9966Z1JHDw3VaY1vOVfg86N98/qxpp62cyF9SA8eKnaLJ85kVuRH4F 1DOYiuH/6W0E9cCpBYuXHQ73qgqZUM0SC70q8yEd/2QYd0M3ooRetbdBSwxc T+kCw6SOrX9Cubp3nehvMutCL4GFdpM+M6ArVC16HajcHUgucqcNLnwBsehR kkEtm65AFDXScFGkuwKMskV0XTKYcZbP9ANh4MXfxWriK5g0kOCrUmVpTnmB Au/9h/TUGJ+0EolbvDjBtvbqL/kHXnMOXlHZKR2DxV+/eJb71a/rAoKPwThV 3KxUZr3eKwgS/pswL1YYn78Cm3kc5g1cTqgVo72Cdw==";
        //stayEncode = stayEncode.replace("\\\\","\\");
        String privateKey = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.APP_RSA_PRIVATE_KEY.getBelongsSystemCode());
        System.err.println("privateKey:" + privateKey);
        byte [] bvy = RSAEncrypt.decryptByPrivateKey(Base64Utils.decode(stayEncode),privateKey);//"MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC2QiugGNth0Ty6BEuVNgQtIyOh959IPQFISr5cAi6KLasZxsZ9AqoC1pbKo7nwu7BamojOS5M+8w2wcvJjMurkIRiNwj1tppKd8fUcfBnbrUsfOXUXeADrdcKpxxLfibHF3Lnys/ZlFK4UwcmMP9Fi7A9uWAIBGy+4IfOGiCXbRBPTBm+05JEQSA9ixfqkhwGMX2fTx3G/q85Uon73U/U1jY7hwLtNgFpH/Z3LZmmRYBSRXrKyqUgtqjTpuD5qgnvgnuc1HMKmFJfdjxvRRezaOiM/4wQs/ttYF0jaieZsfawVtwFaUB1t7yqWTKl4pEO5uTRSia3hKOGTpz/FeYYrAgMBAAECggEAf7yorrA9MbVgBk90WcNd8fSqyJ2wOQTonS4ldH7i911CifJSstLnLM4RKTzO2PXyh82/DDbToE7gzUYCs5vAHNtFRjVYsD0KjUBDNg+UX+54tA2lDynAG0aRhERN6fWrbq1gdcXDbtyrlUisZWtk6RvhRMjRvsy7CSZic2uAfMdukWVaPZYU/7iB2dZd8cfTUtC/FLeadOuzk9yrqvAvb1HAruhOK84DjLqLkBuGaJFOxDxHhR7HCTxP9/DnQW2vMulrdV6RLhUgRWkjEwrdegfR4OXl3WmHt96c4/OLxdrdWCfvYymZAbZJfe+PIrpAEMuH1CNUoROnZmjtpWxxYQKBgQDkJuyNN9r0dLWV4S051E7XJbpcrHOadKG19GbMQvXgd1KjeJFf03m9a/dI0WW8iCK4ouR/cRo3Rfffxq6je0/bXyZLDExL7x4vaN1zikxDUOfkUQRo2ErE3W2yuT50jHnRuwhJE7H4ttFvfnBd590mLlySJzyYk3aHp0HrnB0jzQKBgQDMgTVO4w1jJX7kUz8iGlBv/NDGAybmt7AAyp4x3ueqUKvzAmeqX7fUd5+7790xQqAlkhMjIT4G8I0HPabH+ik83w6iXSYf69SkXhSHGmB0yMMka/YiNl1s544G7CqEtJ3XQsbBT1RCv7m5pJLDoJlDhB5dA/q/cp0aQ1cEC/dJ1wKBgHfDPvU6VsNv6EoO89Chc+lYMqWnGOABTUnYS4X5uFvmBwkspxMoTYVzgVFGKiN3StKlH1EOq/ZL0jwzbUT/3AGJxu4qPHq3wJ6ea3DVLgdAFxowtSHcOBNzMLxapUBE5UWE6mqB3jBytynRs2Z36gy61XkbA6IL9nJrCB2Hy3wZAoGASsdEUuA7WPGxAJRgFXxBxXFQdddZNiwcs21jgyDa3TA6b9WFSotNkOddBT+kRgryvEo5rTLOl4MGTouAnMcgpR17IGunMSzuyFuObgh7FLkEgiDoE4KFjvjsuiNJONVCkh/cxXrYeL/ajcNb1yS2ZjW9VI3L9o1WXPAatWQavGkCgYAQboNO9M6+2/1E0OBKHAYlITLjIxC47y5XCMp3ARt2Bgzpy6+2XIyH2qoMvZQuWCsvVN7JK0RIyCNLE3pAnjHwEQda25dWVwej3NaKQ9u9s9Kb6pKhvVWFgwaf3+AG/HCmncDox4kgdDrHfDz/D+/4+d2Ymu6LM2SpMrEu1BJKhQ==");
        System.err.println("解密"+new String(bvy));
        byte [] bvy990 = RSAEncrypt.encryptByPrivateKey(data.getBytes(),privateKey);//"MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC2QiugGNth0Ty6BEuVNgQtIyOh959IPQFISr5cAi6KLasZxsZ9AqoC1pbKo7nwu7BamojOS5M+8w2wcvJjMurkIRiNwj1tppKd8fUcfBnbrUsfOXUXeADrdcKpxxLfibHF3Lnys/ZlFK4UwcmMP9Fi7A9uWAIBGy+4IfOGiCXbRBPTBm+05JEQSA9ixfqkhwGMX2fTx3G/q85Uon73U/U1jY7hwLtNgFpH/Z3LZmmRYBSRXrKyqUgtqjTpuD5qgnvgnuc1HMKmFJfdjxvRRezaOiM/4wQs/ttYF0jaieZsfawVtwFaUB1t7yqWTKl4pEO5uTRSia3hKOGTpz/FeYYrAgMBAAECggEAf7yorrA9MbVgBk90WcNd8fSqyJ2wOQTonS4ldH7i911CifJSstLnLM4RKTzO2PXyh82/DDbToE7gzUYCs5vAHNtFRjVYsD0KjUBDNg+UX+54tA2lDynAG0aRhERN6fWrbq1gdcXDbtyrlUisZWtk6RvhRMjRvsy7CSZic2uAfMdukWVaPZYU/7iB2dZd8cfTUtC/FLeadOuzk9yrqvAvb1HAruhOK84DjLqLkBuGaJFOxDxHhR7HCTxP9/DnQW2vMulrdV6RLhUgRWkjEwrdegfR4OXl3WmHt96c4/OLxdrdWCfvYymZAbZJfe+PIrpAEMuH1CNUoROnZmjtpWxxYQKBgQDkJuyNN9r0dLWV4S051E7XJbpcrHOadKG19GbMQvXgd1KjeJFf03m9a/dI0WW8iCK4ouR/cRo3Rfffxq6je0/bXyZLDExL7x4vaN1zikxDUOfkUQRo2ErE3W2yuT50jHnRuwhJE7H4ttFvfnBd590mLlySJzyYk3aHp0HrnB0jzQKBgQDMgTVO4w1jJX7kUz8iGlBv/NDGAybmt7AAyp4x3ueqUKvzAmeqX7fUd5+7790xQqAlkhMjIT4G8I0HPabH+ik83w6iXSYf69SkXhSHGmB0yMMka/YiNl1s544G7CqEtJ3XQsbBT1RCv7m5pJLDoJlDhB5dA/q/cp0aQ1cEC/dJ1wKBgHfDPvU6VsNv6EoO89Chc+lYMqWnGOABTUnYS4X5uFvmBwkspxMoTYVzgVFGKiN3StKlH1EOq/ZL0jwzbUT/3AGJxu4qPHq3wJ6ea3DVLgdAFxowtSHcOBNzMLxapUBE5UWE6mqB3jBytynRs2Z36gy61XkbA6IL9nJrCB2Hy3wZAoGASsdEUuA7WPGxAJRgFXxBxXFQdddZNiwcs21jgyDa3TA6b9WFSotNkOddBT+kRgryvEo5rTLOl4MGTouAnMcgpR17IGunMSzuyFuObgh7FLkEgiDoE4KFjvjsuiNJONVCkh/cxXrYeL/ajcNb1yS2ZjW9VI3L9o1WXPAatWQavGkCgYAQboNO9M6+2/1E0OBKHAYlITLjIxC47y5XCMp3ARt2Bgzpy6+2XIyH2qoMvZQuWCsvVN7JK0RIyCNLE3pAnjHwEQda25dWVwej3NaKQ9u9s9Kb6pKhvVWFgwaf3+AG/HCmncDox4kgdDrHfDz/D+/4+d2Ymu6LM2SpMrEu1BJKhQ==");
        System.err.println("私钥加密"+ Base64Utils.encode(bvy990));


    }

    /**
     * 私钥解密
     * @throws Exception
     */
    @Test
    public void privateEncryptData()throws Exception{
        //String encryptData = "xr7HBaN7waZYiTB7UgH7nEilmhci/+idwYqK0IIYx2EetecedS6MR/r7t8q+JtP/ZaRyr5QCswgu1V4bZ5M7nAXsMI+XyV7BFoaXLV3E2WcIBxVOS7ect9lBP2w/cIuWuy2SuDt2mj0qbFosRbf1tvYMwOLfzT0A0108Y6LOWxPAH+WmADlJDVeoSnBeDhbPaDJYCprY+bDHNk7hP/XpgGMYztqK6lArFhmRbeeSaFpCUrZD4QLMurPSLjbzgEU2cuI067+TyikjXQbTQm1YM03SPd9FWpEP7THrjObQd7yGerJH2GGRh3+jYfp3igioP/ZILQDbbBxU4YQkXNvZSQ==";
        String encryptData = "hnudPkFFy2YXGqp27lhqKBCaKC7RaY7hvjp8yuWNKkqboUSG3zugLlvz9rgslTt1IjzVH95+Hao4WqRC9cwZEMFyD1k5wHmy2GYU/+EN9iqrsnMVFBf2Edw33HmxYSr4MEPVwncmueMwEnRatOHWd7C/6FTRjOog3zLVJUzhBIaHgv/+SrHVOZjSxmpG6cgD1IZn7Xa71Y09feiXJ9HluqqCOM2czO48lB3vqXrJJIFkOjO/izGaWK4wVeLgv5lfC5sWXD0jyGxdWGLaCdQMcJXTj2hekOOPBnMDgNPyth9e4Pfc9+n60YYj6dFFhZKV+eDcz83T9/Mx6lPVXnF5Bw==";
        byte [] bvy = RSAEncrypt.decryptByPrivateKey(Base64Utils.decode(encryptData),RSAEncrypt.getPrivateKey(maleChainConfig.getKeyUrl()));
        System.err.println(new String(bvy));
    }

    /**
     * 公钥解密
     * @throws Exception
     */
    @Test
    public void publicEncryptData()throws Exception{
        String publicKey = "";
        String encryptData = "Jt5wihNm/Oj7GVj9DOrV4aUhDi5rNMDKK3HOxKmguYycd/0WB7+FLmDSCZiD2aa2Nieqbly1u2WxotPCNuhFeOf5W1HJwiNyFrYEVx7xxgcq86NpReRPI4G1rLuRZftyOCLVIVOpqjhjRb8JTeRs9gmO2V0bl5nx+a7GWIwug9L8jVRcCtzdYaMYq88ZnRSawu7aA5+5hiS7quttA7ccKtax3oba8ZoUO5Na/3ZwuQYn5fTPElkEZcN+tGVi/AA8yLVpuMVS3Cb1pOiIrObxvVEXA+pyW89phD3eLj+4LS+3CKSyuk156QAg/b6HXUo8RTfMPkSuHF6dqK0tZlW9eA==";
        byte [] bvy = RSAEncrypt.decryptByPublicKey(Base64Utils.decode(encryptData),publicKey);
        System.err.println(new String(bvy));
    }

    @Test
    public void createTest(){
        Address address = maleChainService.createMaleChainAddress(60);
        System.err.println(address.getAddress());
    }

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void doRequestTest(){
        String url = "http://47.244.209.218:8558/v1/extract";


        HttpHeaders headers =new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String jsonString = "{\"data\":\"CMLHBq01ylyAXD85tkPsSrGheWWeYeWkxoLHFjFJzKZv8OLb7yoiuUGeSkamiazlUp1IQ/1aAcXZOpHO6m7LHP7HNk/eiwYNZRxDA0FDEn0rg4Qr9MTpodktfvsAhzh2FSjUFuyrh8tVre40w1oge3jkQ3p9EzSgHKn+PCF+URsz+T3dVTBeIEhaVEpBfp9CedlKiTf3ryP17f05u0nLAcJsMxYfay56NbgKhOPapmQBB0uJWDKd+WwLDxzJnyEzGV92d34msYEDRQ/sBhS3sEFo6WUscszPPJgaqIrHj33KeSAjEryTk5eBjz/A8CpCPqaONthdW6px3KWPReEhzQ==\"}";

        HttpEntity requestEntity =new HttpEntity(jsonString, headers);

        //  执行HTTP请求


        String resData = restTemplate.postForObject(url, requestEntity, String.class);
        // 请求结束，返回结果
        System.err.println(resData);
    }
    @Autowired
    private MaleChainCallbackHandler maleChainCallbackHandler;
    @Test
    public void rechargeCallbackTest(){
        String data = "{\"tx_hash\":\"0x635cad124f124c28e7e6c1896396b06458cf1c6533301f68224f951759724330\",\"fee\":2867271000000000,\"address\":\"0x0C7A94b1885C7E3c22b0a8D1119cB479d9C62355\",\"tradeNumber\":4885380000}";
        maleChainCallbackHandler.doRechargeCallbackMethod(data, RechargeOperTypeEnum.MALE_CHAIN_RECHARGE.getOperType());
    }
}
