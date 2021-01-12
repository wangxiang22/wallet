
package wallet.controller.base;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.MemberApplication;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MemberApplication.class)
public class BaseControllerTest {

    @Autowired
    protected WebApplicationContext wac;
    protected MockMvc mvc;
    @Before
    public void createMvc(){
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    public MvcResult postRequest(String url , String paramJsonStr)throws Exception{
        return mvc.perform(MockMvcRequestBuilders.post(url)
                .content(paramJsonStr)
                .contentType(MediaType.APPLICATION_JSON)
        ).
                andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    public <T> T postRequestToBaseResponse(String url , String paramJsonStr,Class<T> clazz)throws Exception{
        return JSONObject.parseObject(postRequest(url,paramJsonStr).getResponse().getContentAsString(),clazz);
    }

    public MvcResult getRequest(String url , String paramJsonStr)throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get(url)
                .param("requestBody",paramJsonStr)
                .contentType(MediaType.APPLICATION_JSON)
        ).
                andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
    public <T> T getRequestToBaseResponse(String url, String paramJsonStr,Class<T> clazz)throws Exception{
        return JSONObject.parseObject(getRequest(url,paramJsonStr).getResponse().getContentAsString(),clazz);
    }


}

