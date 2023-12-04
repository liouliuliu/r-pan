package com.liuhf.pan.web.validator;

import com.liuhf.pan.core.constants.RPanConstants;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * @author: lhf
 * @date: 2023/12/4 22:27
 * @description 统一的参数校验器
 */
@SpringBootConfiguration
@Log4j2
public class WebValidatorConfig {

    private static final String DAIL_FAST_KEY = "hibernate.validator.fail_fast";
    
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(){
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(rPanValidator());
        log.info("The hibernate validator is loaded successfully!");
        return postProcessor;
    }

    /**
     * 构造项目的方法参数校验器
     */
    private Validator rPanValidator() {
        ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty(DAIL_FAST_KEY, RPanConstants.TRUE_STR)
                .buildValidatorFactory();
        return factory.getValidator();
    }
}
