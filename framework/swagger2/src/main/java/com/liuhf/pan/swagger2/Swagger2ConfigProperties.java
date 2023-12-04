package com.liuhf.pan.swagger2;

import com.liuhf.pan.core.constants.RPanConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author: lhf
 * @date: 2023/11/30 21:52
 * @description swagger2 配置属性实体
 */
@Data
@Component
@ConfigurationProperties(prefix = "swagger2")
public class Swagger2ConfigProperties {
    
    private boolean show =true;
    
    private String groupName = "r-pan";
    
    private String basePackage = RPanConstants.BASE_COMPONENT_SCAN_PATH;
    
    private String title = "r-pan-server";
    
    private String description = "r-pan-server";
    
    private String termsOfServiceUrl = "http://127.0.0.1:${server.port}";
    
    private String contactName = "rubin";
    
    private String contactUrl = "https://blog.rubinchu.com";
    
    private String contactEmail = "523005941@qq.com";
    
    private String version = "1.0";
}
