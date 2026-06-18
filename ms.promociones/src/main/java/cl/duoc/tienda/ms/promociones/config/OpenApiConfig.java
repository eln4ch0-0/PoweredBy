package cl.duoc.tienda.ms.promociones.config;

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
    public OpenAPI promocionesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS-Promociones — PoweredBy")
                        .version("1.0.0")
                        .description("Microservicio de promociones y descuentos. Permite crear " +
                                "promociones globales o específicas por juego, con descuentos por " +
                                "porcentaje o monto fijo, y aplicarlas a precios durante la compra.")
                        .contact(new Contact()
                                .name("Equipo PoweredBy")
                                .url("https://github.com/eln4ch0-0/PoweredBy")))
                .servers(List.of(
                        new Server().url("http://localhost:8086").description("Servidor local")
                ));
    }
}