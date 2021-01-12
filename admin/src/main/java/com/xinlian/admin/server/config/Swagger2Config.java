package com.xinlian.admin.server.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class Swagger2Config {

	@Autowired
	private Environment env;
	
	@Bean
	public Docket createRestApi() {
		List<Parameter> list = new ArrayList<Parameter>();
       /* ParameterBuilder country = new ParameterBuilder();
        country.name("login").description("用户名").modelRef(new ModelRef("string")).parameterType("header")
                .required(true).defaultValue("paipaideli").build();*/

        ParameterBuilder token = new ParameterBuilder();
        token.name("Authorization").description("token，登陆成功后返回").modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();
        list.add(token.build());

		ParameterBuilder deviceNumber = new ParameterBuilder();
		deviceNumber.name("DeviceNumber").description("deviceNumber，设备号").modelRef(new ModelRef("string")).parameterType("header")
				.required(false).build();
		list.add(deviceNumber.build());

        boolean enableSwagger = Boolean.valueOf(env.getProperty("swagger2.enableSwagger"));
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.enable(enableSwagger)
				.select()
				//.apis(RequestHandlerSelectors.basePackage("com.paipaidl"))
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) 
				.paths(PathSelectors.any())
				.build()
				.globalOperationParameters(list);
	}
	
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("钱包后台admin文档")
				.description("test")
				.termsOfServiceUrl("")
				.version("1.0")
				.build();
	}

	
}
