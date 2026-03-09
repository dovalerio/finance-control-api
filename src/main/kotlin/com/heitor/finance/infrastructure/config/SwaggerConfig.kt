package com.heitor.finance.infrastructure.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SwaggerConfig : WebMvcConfigurer {

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addRedirectViewController(
            "/swagger-ui.html",
            "/webjars/swagger-ui/index.html?url=/api.yml&validatorUrl="
        )
    }
}
