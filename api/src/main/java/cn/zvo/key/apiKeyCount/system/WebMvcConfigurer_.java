package cn.zvo.key.apiKeyCount.system;


import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



/**
 * WebMvcConfigurer
 * @author 管雷鸣
 *
 */
@Configuration
public class WebMvcConfigurer_ implements WebMvcConfigurer {
	
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController( "/" ).setViewName("forward:/index.html" );
		registry.setOrder( Ordered.HIGHEST_PRECEDENCE );
	}
	
	@Bean
	public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
	    return factory -> {
	        if (factory instanceof TomcatServletWebServerFactory) {
	            ((TomcatServletWebServerFactory) factory).addConnectorCustomizers(connector -> {
	                connector.setMaxPostSize(100 * 1024 * 1024); // 设置为 100MB
	                //connector.setMaxPostSize(1024); // 设置为 100MB
	            });
	        }
	    };
	}
}