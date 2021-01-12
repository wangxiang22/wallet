package wallet.service;

import com.xinlian.common.enums.RechargeOperTypeEnum;
import com.xinlian.common.utils.Base64Utils;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.common.utils.RSAEncrypt;
import com.xinlian.member.biz.trcusdt.TrcUsdtConfig;
import com.xinlian.member.server.controller.handler.MaleChainCallbackHandler;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import wallet.service.base.BaseServiceTest;

import java.util.Date;

public class TrcUsdtServiceTest extends BaseServiceTest {

    @Autowired
    private TrcUsdtConfig trcUsdtConfig;


    public static void main(String[] args)throws Exception {
        Date clearDate = DateFormatUtil.addDate(new Date(),-1);
        String clearDay = DateFormatUtil.get(7,clearDate);
        System.err.println(clearDay);
    }



    /**
     * 公钥解密
     * @throws Exception
     */
    @Test
    public void publicEncryptData()throws Exception{
        String publicKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC/inxVDKZYZ0l6871lF7s/6hxfSCm5UvebKXCKjH8wOXoguk41JvYiYiaGB2k8XcrG206+jWraRMRiq9Hu6XctfHV0IDdviin+Yu1wWROlOg2RBaL1HB+ldxdJ2XnWIMhnGXnwsi/rVhRUV+0RCAkp7kul1lm7ChIQeCmb8P2Qp33k1o14FfiOMRGI35D+1qXrW4TuGpGyHG/j3ihFRopo2aDTd6pFkNr4qzVfsy7dawYOZiwtfC/vgJ01renY7etFSuGKusbBiPYMD7YOZSKWhR7BZ2qmH1kt1UdsNirF6G8cpQ/KNJec/vi3H61ZUprjDoUMQSKYyTQJuPeVmQZRAgMBAAECggEAYx2NcxFFru9bxcUFF5/tJIjHv8J+3MiDGoaaFwfW9SR4/yCJQdX0fhxeJPMG0NcUDE0F9XMGfQYivZB95Oxo4F/MA5h8Nzq+ukWE6qMdmF1vEcFh7wZrldsHcQ6ZjmWOblNvl08NnvD/Ohn5sdt/qnQGLz25VyFB8cXMekZ9X/F27EgU/VnmQ5YJGaEpe/lj/J/Fjh/67LqMepRyYYT6/ZD+V9Bc3qa4hwumtrqVpgx8StUHgpR6yEzBf9GXxbXrYzbprrS86w8gaIVypSSQjRv0YKzDstYJ1dOfaC5o7UNKlKA8RqgW56smQGQ1TZJhcj+hxmO1zyivlH91OeyThQKBgQDhjukwo8jjzLODFCIotv6QAuh6dPpHF6eBJQn/z0Luj6I+GdU3M1VaW9GCRfJCH4RftBZ8XNmbcdA5ym9vcJ5IPPs8yYdj9b9P7l4ED69dzHyEVVMvgNsClXoiO6n7bTChfALJzuJ1Gq8jTSKc4G4eitOzIVxwB21IbYAj3SHt3wKBgQDZZEUN29nFBcJGYDC0rDhwFcI32W3DfamPjhRiByDLvbV681ENz/7U+LOzfYH+6CSEQLYLgTj4ebyqAqISVLOqDy+MEr8X0ths/1d+idLuh1BrCqdxiYQa1yaDhw6xr3OC1da1IzaOGGcdUbF5Q+zs0OFntt8UbEamZvFZp54xzwKBgQDeVxtywOCT/QKvITeS3uAMYXF81LapQZ3yZep3aX1W3+tUP+63XTizu3LIcd6eY6b/F+xaZsRywDw721sBhQfhuYsGnrmBRYtyZUvzwOpjEeFkKR+44dIBuAsOl7W1jCq15CJzgCYmvUkLCUwnS+wA22RXXXlbTVbiYF5B/AcoDQKBgE9ngpmXTina+qU+CUxVA6BdMj24G5GmWsLmwKc1t1L0Dk/EhDypVolv9iN/mnYDUSNFh7kYEDkiLjljENPlQGLDfTiKuGX5S7g0ZD8CFJuvrT4L/DFKa4hqQdUVBdb9IRIPDYfIPusUGsXoywxiNS4i9dIuR7lr+zpYq5t10CCdAoGAAyZvqf8wJ9yov5BbOmH9LqrymDiG8MiAvBGSjzFsPeM+h08MFZwWiAoYuUP590d0bZVkFuWfUFLqHEAEbs5rMBiDYxbUv8u2i1g+rMmctpXST4qQz/d6eF02u5bAGyiZ1joxFhT5TTc3jIxoFfw03QpBVeQfygp5zRl4wEG1evU=";
        String encryptData = "OyWVDlH92csB37vcrNpTroyH9Hs4gAmTa6ZH9fdxMI7rhH2zsepJmeryAqbOQX/Z8PMd/7G48b6ztsvkZlaB9XFxSps0v10dqyYcZp/bMj/7kwV3Oz4d5qors84mdbR4lIOfTo/0rA6lWBXNpUG4Rnx99CsxsqlXcVXbzo3phqhqGpcNMUBqGqsct+q47p8Q+nILV3xrMYV/XteG6iE8q6hlrdmIERvhzIGkK5v3WME+CEyh8EdCLUS72H2QaIeczkTPS0VBjF6WDfU32UDQH6Iq4ZFRdCcUrBMKYQH1OwoayIu8p5Qziezo8kaf8wxfi+wyRAiIyOMCMz0aEuU+0w==";
        byte [] bvy = RSAEncrypt.decryptByPublicKey(Base64Utils.decode(encryptData),publicKey);
        System.err.println(new String(bvy));
    }

    /**
     * 私钥解密
     * @throws Exception
     */
    @Test
    public void privateEncryptData()throws Exception{
        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC/inxVDKZYZ0l6871lF7s/6hxfSCm5UvebKXCKjH8wOXoguk41JvYiYiaGB2k8XcrG206+jWraRMRiq9Hu6XctfHV0IDdviin+Yu1wWROlOg2RBaL1HB+ldxdJ2XnWIMhnGXnwsi/rVhRUV+0RCAkp7kul1lm7ChIQeCmb8P2Qp33k1o14FfiOMRGI35D+1qXrW4TuGpGyHG/j3ihFRopo2aDTd6pFkNr4qzVfsy7dawYOZiwtfC/vgJ01renY7etFSuGKusbBiPYMD7YOZSKWhR7BZ2qmH1kt1UdsNirF6G8cpQ/KNJec/vi3H61ZUprjDoUMQSKYyTQJuPeVmQZRAgMBAAECggEAYx2NcxFFru9bxcUFF5/tJIjHv8J+3MiDGoaaFwfW9SR4/yCJQdX0fhxeJPMG0NcUDE0F9XMGfQYivZB95Oxo4F/MA5h8Nzq+ukWE6qMdmF1vEcFh7wZrldsHcQ6ZjmWOblNvl08NnvD/Ohn5sdt/qnQGLz25VyFB8cXMekZ9X/F27EgU/VnmQ5YJGaEpe/lj/J/Fjh/67LqMepRyYYT6/ZD+V9Bc3qa4hwumtrqVpgx8StUHgpR6yEzBf9GXxbXrYzbprrS86w8gaIVypSSQjRv0YKzDstYJ1dOfaC5o7UNKlKA8RqgW56smQGQ1TZJhcj+hxmO1zyivlH91OeyThQKBgQDhjukwo8jjzLODFCIotv6QAuh6dPpHF6eBJQn/z0Luj6I+GdU3M1VaW9GCRfJCH4RftBZ8XNmbcdA5ym9vcJ5IPPs8yYdj9b9P7l4ED69dzHyEVVMvgNsClXoiO6n7bTChfALJzuJ1Gq8jTSKc4G4eitOzIVxwB21IbYAj3SHt3wKBgQDZZEUN29nFBcJGYDC0rDhwFcI32W3DfamPjhRiByDLvbV681ENz/7U+LOzfYH+6CSEQLYLgTj4ebyqAqISVLOqDy+MEr8X0ths/1d+idLuh1BrCqdxiYQa1yaDhw6xr3OC1da1IzaOGGcdUbF5Q+zs0OFntt8UbEamZvFZp54xzwKBgQDeVxtywOCT/QKvITeS3uAMYXF81LapQZ3yZep3aX1W3+tUP+63XTizu3LIcd6eY6b/F+xaZsRywDw721sBhQfhuYsGnrmBRYtyZUvzwOpjEeFkKR+44dIBuAsOl7W1jCq15CJzgCYmvUkLCUwnS+wA22RXXXlbTVbiYF5B/AcoDQKBgE9ngpmXTina+qU+CUxVA6BdMj24G5GmWsLmwKc1t1L0Dk/EhDypVolv9iN/mnYDUSNFh7kYEDkiLjljENPlQGLDfTiKuGX5S7g0ZD8CFJuvrT4L/DFKa4hqQdUVBdb9IRIPDYfIPusUGsXoywxiNS4i9dIuR7lr+zpYq5t10CCdAoGAAyZvqf8wJ9yov5BbOmH9LqrymDiG8MiAvBGSjzFsPeM+h08MFZwWiAoYuUP590d0bZVkFuWfUFLqHEAEbs5rMBiDYxbUv8u2i1g+rMmctpXST4qQz/d6eF02u5bAGyiZ1joxFhT5TTc3jIxoFfw03QpBVeQfygp5zRl4wEG1evU=";
        String encryptData = "U+6R81pWr6+Xtrbom6CLblnx0iY8i3riH3RMbXWBA+/C1OedRZWGz8HZ4kWoXKZyP3FfE2MWXRLmKgMX9Ymz4qho0n17WXYfBHANNElc8+GONaxBdgkfYxHPcMkd7v8DRuSrFd2gevTc1jU0/ZA6FNCPpRPfC4POh8FzJ7h/CwuaMlMhIRwqHxgU7QMlFnwRYjT9ZcxSey08aB5gX/S+0bB2b6ehKqj3nxCJaW4F+nR1QLyXcKsEgp1NAEypcc6iy5rWzymp/DKOgcnnqKImVEHZ5g27HYXP2xqmsnObMX5zSTrv0hcFFiq3Z0u+vfyKray2iQo7YqLWaoSZ10vPUQ==";
        byte [] bvy = RSAEncrypt.decryptByPrivateKey(Base64Utils.decode(encryptData),privateKey);
        System.err.println(new String(bvy));
    }

    /**
     * 私钥解密2
     * @throws Exception
     */
    @Test
    public void privateEncryptData2()throws Exception{
        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC/inxVDKZYZ0l6871lF7s/6hxfSCm5UvebKXCKjH8wOXoguk41JvYiYiaGB2k8XcrG206+jWraRMRiq9Hu6XctfHV0IDdviin+Yu1wWROlOg2RBaL1HB+ldxdJ2XnWIMhnGXnwsi/rVhRUV+0RCAkp7kul1lm7ChIQeCmb8P2Qp33k1o14FfiOMRGI35D+1qXrW4TuGpGyHG/j3ihFRopo2aDTd6pFkNr4qzVfsy7dawYOZiwtfC/vgJ01renY7etFSuGKusbBiPYMD7YOZSKWhR7BZ2qmH1kt1UdsNirF6G8cpQ/KNJec/vi3H61ZUprjDoUMQSKYyTQJuPeVmQZRAgMBAAECggEAYx2NcxFFru9bxcUFF5/tJIjHv8J+3MiDGoaaFwfW9SR4/yCJQdX0fhxeJPMG0NcUDE0F9XMGfQYivZB95Oxo4F/MA5h8Nzq+ukWE6qMdmF1vEcFh7wZrldsHcQ6ZjmWOblNvl08NnvD/Ohn5sdt/qnQGLz25VyFB8cXMekZ9X/F27EgU/VnmQ5YJGaEpe/lj/J/Fjh/67LqMepRyYYT6/ZD+V9Bc3qa4hwumtrqVpgx8StUHgpR6yEzBf9GXxbXrYzbprrS86w8gaIVypSSQjRv0YKzDstYJ1dOfaC5o7UNKlKA8RqgW56smQGQ1TZJhcj+hxmO1zyivlH91OeyThQKBgQDhjukwo8jjzLODFCIotv6QAuh6dPpHF6eBJQn/z0Luj6I+GdU3M1VaW9GCRfJCH4RftBZ8XNmbcdA5ym9vcJ5IPPs8yYdj9b9P7l4ED69dzHyEVVMvgNsClXoiO6n7bTChfALJzuJ1Gq8jTSKc4G4eitOzIVxwB21IbYAj3SHt3wKBgQDZZEUN29nFBcJGYDC0rDhwFcI32W3DfamPjhRiByDLvbV681ENz/7U+LOzfYH+6CSEQLYLgTj4ebyqAqISVLOqDy+MEr8X0ths/1d+idLuh1BrCqdxiYQa1yaDhw6xr3OC1da1IzaOGGcdUbF5Q+zs0OFntt8UbEamZvFZp54xzwKBgQDeVxtywOCT/QKvITeS3uAMYXF81LapQZ3yZep3aX1W3+tUP+63XTizu3LIcd6eY6b/F+xaZsRywDw721sBhQfhuYsGnrmBRYtyZUvzwOpjEeFkKR+44dIBuAsOl7W1jCq15CJzgCYmvUkLCUwnS+wA22RXXXlbTVbiYF5B/AcoDQKBgE9ngpmXTina+qU+CUxVA6BdMj24G5GmWsLmwKc1t1L0Dk/EhDypVolv9iN/mnYDUSNFh7kYEDkiLjljENPlQGLDfTiKuGX5S7g0ZD8CFJuvrT4L/DFKa4hqQdUVBdb9IRIPDYfIPusUGsXoywxiNS4i9dIuR7lr+zpYq5t10CCdAoGAAyZvqf8wJ9yov5BbOmH9LqrymDiG8MiAvBGSjzFsPeM+h08MFZwWiAoYuUP590d0bZVkFuWfUFLqHEAEbs5rMBiDYxbUv8u2i1g+rMmctpXST4qQz/d6eF02u5bAGyiZ1joxFhT5TTc3jIxoFfw03QpBVeQfygp5zRl4wEG1evU=";
        String encryptData = "OyWVDlH92csB37vcrNpTroyH9Hs4gAmTa6ZH9fdxMI7rhH2zsepJmeryAqbOQX/Z8PMd/7G48b6ztsvkZlaB9XFxSps0v10dqyYcZp/bMj/7kwV3Oz4d5qors84mdbR4lIOfTo/0rA6lWBXNpUG4Rnx99CsxsqlXcVXbzo3phqhqGpcNMUBqGqsct+q47p8Q+nILV3xrMYV/XteG6iE8q6hlrdmIERvhzIGkK5v3WME+CEyh8EdCLUS72H2QaIeczkTPS0VBjF6WDfU32UDQH6Iq4ZFRdCcUrBMKYQH1OwoayIu8p5Qziezo8kaf8wxfi+wyRAiIyOMCMz0aEuU+0w==";
        byte [] bvy = RSAEncrypt.decryptByPrivateKey(Base64Utils.decode(encryptData),privateKey);
        System.err.println(new String(bvy));
    }



    /**
     * 公钥加密 - 私钥解密
     * @throws Exception
     */
    public static void checkPulickPrivatekey()throws Exception{
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv4p8VQymWGdJevO9ZRe7P+ocX0gpuVL3mylwiox/MDl6ILpONSb2ImImhgdpPF3KxttOvo1q2kTEYqvR7ul3LXx1dCA3b4op/mLtcFkTpToNkQWi9RwfpXcXSdl51iDIZxl58LIv61YUVFftEQgJKe5LpdZZuwoSEHgpm/D9kKd95NaNeBX4jjERiN+Q/tal61uE7hqRshxv494oRUaKaNmg03eqRZDa+Ks1X7Mu3WsGDmYsLXwv74CdNa3p2O3rRUrhirrGwYj2DA+2DmUiloUewWdqph9ZLdVHbDYqxehvHKUPyjSXnP74tx+tWVKa4w6FDEEimMk0Cbj3lZkGUQIDAQAB";
        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC/inxVDKZYZ0l6871lF7s/6hxfSCm5UvebKXCKjH8wOXoguk41JvYiYiaGB2k8XcrG206+jWraRMRiq9Hu6XctfHV0IDdviin+Yu1wWROlOg2RBaL1HB+ldxdJ2XnWIMhnGXnwsi/rVhRUV+0RCAkp7kul1lm7ChIQeCmb8P2Qp33k1o14FfiOMRGI35D+1qXrW4TuGpGyHG/j3ihFRopo2aDTd6pFkNr4qzVfsy7dawYOZiwtfC/vgJ01renY7etFSuGKusbBiPYMD7YOZSKWhR7BZ2qmH1kt1UdsNirF6G8cpQ/KNJec/vi3H61ZUprjDoUMQSKYyTQJuPeVmQZRAgMBAAECggEAYx2NcxFFru9bxcUFF5/tJIjHv8J+3MiDGoaaFwfW9SR4/yCJQdX0fhxeJPMG0NcUDE0F9XMGfQYivZB95Oxo4F/MA5h8Nzq+ukWE6qMdmF1vEcFh7wZrldsHcQ6ZjmWOblNvl08NnvD/Ohn5sdt/qnQGLz25VyFB8cXMekZ9X/F27EgU/VnmQ5YJGaEpe/lj/J/Fjh/67LqMepRyYYT6/ZD+V9Bc3qa4hwumtrqVpgx8StUHgpR6yEzBf9GXxbXrYzbprrS86w8gaIVypSSQjRv0YKzDstYJ1dOfaC5o7UNKlKA8RqgW56smQGQ1TZJhcj+hxmO1zyivlH91OeyThQKBgQDhjukwo8jjzLODFCIotv6QAuh6dPpHF6eBJQn/z0Luj6I+GdU3M1VaW9GCRfJCH4RftBZ8XNmbcdA5ym9vcJ5IPPs8yYdj9b9P7l4ED69dzHyEVVMvgNsClXoiO6n7bTChfALJzuJ1Gq8jTSKc4G4eitOzIVxwB21IbYAj3SHt3wKBgQDZZEUN29nFBcJGYDC0rDhwFcI32W3DfamPjhRiByDLvbV681ENz/7U+LOzfYH+6CSEQLYLgTj4ebyqAqISVLOqDy+MEr8X0ths/1d+idLuh1BrCqdxiYQa1yaDhw6xr3OC1da1IzaOGGcdUbF5Q+zs0OFntt8UbEamZvFZp54xzwKBgQDeVxtywOCT/QKvITeS3uAMYXF81LapQZ3yZep3aX1W3+tUP+63XTizu3LIcd6eY6b/F+xaZsRywDw721sBhQfhuYsGnrmBRYtyZUvzwOpjEeFkKR+44dIBuAsOl7W1jCq15CJzgCYmvUkLCUwnS+wA22RXXXlbTVbiYF5B/AcoDQKBgE9ngpmXTina+qU+CUxVA6BdMj24G5GmWsLmwKc1t1L0Dk/EhDypVolv9iN/mnYDUSNFh7kYEDkiLjljENPlQGLDfTiKuGX5S7g0ZD8CFJuvrT4L/DFKa4hqQdUVBdb9IRIPDYfIPusUGsXoywxiNS4i9dIuR7lr+zpYq5t10CCdAoGAAyZvqf8wJ9yov5BbOmH9LqrymDiG8MiAvBGSjzFsPeM+h08MFZwWiAoYuUP590d0bZVkFuWfUFLqHEAEbs5rMBiDYxbUv8u2i1g+rMmctpXST4qQz/d6eF02u5bAGyiZ1joxFhT5TTc3jIxoFfw03QpBVeQfygp5zRl4wEG1evU=";
        String data = "U+6R81pWr6+Xtrbom6CLblnx0iY8i3riH3RMbXWBA+/C1OedRZWGz8HZ4kWoXKZyP3FfE2MWXRLmKgMX9Ymz4qho0n17WXYfBHANNElc8+GONaxBdgkfYxHPcMkd7v8DRuSrFd2gevTc1jU0/ZA6FNCPpRPfC4POh8FzJ7h/CwuaMlMhIRwqHxgU7QMlFnwRYjT9ZcxSey08aB5gX/S+0bB2b6ehKqj3nxCJaW4F+nR1QLyXcKsEgp1NAEypcc6iy5rWzymp/DKOgcnnqKImVEHZ5g27HYXP2xqmsnObMX5zSTrv0hcFFiq3Z0u+vfyKray2iQo7YqLWaoSZ10vPUQ==";
        byte [] encryptByte = RSAEncrypt.encryptByPublicKey(data.getBytes(),publicKey);
        String encryptData = Base64Utils.encode(encryptByte);
        byte [] bvy = RSAEncrypt.decryptByPrivateKey(Base64Utils.decode(encryptData),privateKey);
        System.err.println("解密后字符串:" + new String(bvy));
    }


    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void doRequestTest(){
        String url = "http://47.244.209.218:8558/v1/extract";
        HttpHeaders headers =new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String jsonString = "{\"data\":\"CMLHBq01ylyAXD85tkPsSrGheWWeYeWkxoLHFjFJzKZv8OLb7yoiuUGeSkamiazlUp1IQ/1aAcXZOpHO6m7LHP7HNk/eiwYNZRxDA0FDEn0rg4Qr9MTpodktfvsAhzh2FSjUFuyrh8tVre40w1oge3jkQ3p9EzSgHKn+PCF+URsz+T3dVTBeIEhaVEpBfp9CedlKiTf3ryP17f05u0nLAcJsMxYfay56NbgKhOPapmQBB0uJWDKd+WwLDxzJnyEzGV92d34msYEDRQ/sBhS3sEFo6WUscszPPJgaqIrHj33KeSAjEryTk5eBjz/A8CpCPqaONthdW6px3KWPReEhzQ==\"}";
        HttpEntity requestEntity = new HttpEntity(jsonString, headers);
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
