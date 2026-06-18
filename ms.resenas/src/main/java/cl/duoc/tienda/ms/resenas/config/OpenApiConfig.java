package cl.duoc.tienda.ms.resenas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public OpenAPI resenasOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS-Resenas — PoweredBy")
                        .version("1.0.0")
                        .description("Microservicio de reseñas y calificaciones. " +
                                "Valida con MS-Compras que el usuario posea el juego antes de permitir reseñarlo.")
                        .contact(new Contact()
                                .name("Equipo PoweredBy")
                                .url("https://github.com/eln4ch0-0/PoweredBy")))
                .servers(List.of(
                        new Server().url("http://localhost:8084").description("Servidor local")
                ));
    }
}