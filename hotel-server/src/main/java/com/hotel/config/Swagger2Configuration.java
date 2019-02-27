package com.hotel.config;

import com.google.common.collect.Sets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author guangyong.yang
 * @date 2019-01-21
 */
@Configuration
@EnableSwagger2
public class Swagger2Configuration {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                //协议，http或https
                .protocols(Sets.newHashSet("http"))
                .apiInfo(apiInfo())
                .select()
                //一定要写对，会在这个路径下扫描controller定义
                .apis(RequestHandlerSelectors.basePackage("com.hotel.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("REST接口定义")
                .version("1.0")
                .description("旅馆RestFul接口文档")
                .build();
    }
}
