/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.crs.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.cf.crs.config.config;

import com.cf.crs.common.constant.Constant;
import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

/**
 * Swagger配置
 *
 * @author Mark sunlightcs@gmail.com
 */
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class SwaggerConfig {

    @Bean(value = "restApi")
    public Docket restApi() {
        /**
         * 测试token用于swagger测试接口
         * @author leek
         * @description
         * @date 2019/8/16 10:20
         */
        List<Parameter> list = Arrays.asList(
                new ParameterBuilder()
                        .name(Constant.TOKEN_HEADER)
                        .defaultValue(Constant.TOKEN_TEST_VALUE)
                        .description("访问令牌")
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .build()
        );

        //可以添加多个header或参数
        return new Docket(DocumentationType.SWAGGER_2).globalOperationParameters(list)
                .groupName("api")
                .select()
                //这里采用包含注解的方式来确定要显示的接口
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build().apiInfo(restApiInfo());
    }

    private ApiInfo restApiInfo() {
        return new ApiInfoBuilder()
                .title("城管系统API接口文档")
                .description("城管系统 API文档")
                .contact(new Contact("城管系统", "", ""))
                .version("1.0")
                .build();
    }
}