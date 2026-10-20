package apartamentos.demo;

import jakarta.activation.DataSource;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;


@Configuration
public class ProjectConfig implements WebMvcConfigurer{
    //los soguientes metodos son para incorporar el tema de internalización en el proyecto
    
    // localResolver se utiliza para crear una sesión de cambio de idioma
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:8080");
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public LocaleResolver localeResolver(){
        var slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.getDefault());
        slr.setLocaleAttributeName("session.current.locale");
        slr.setTimeZoneAttributeName("session.current.timezone");
        return slr;
    }
    
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor(){
        var lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registro){
        registro.addInterceptor(localeChangeInterceptor());
    }
    
    @Bean("messageSource")
    public MessageSource messageSource(){
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/registro/nuevo").setViewName("/registro/nuevo");
    }
    

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((request) -> request
                .requestMatchers("/","/index","/errores/**","/usuario/guardar2",
                        "/pruebas/**","/reportes/**",
                        "/registro/**","/js/**","/webjars/**","/css/**",
                        "/menu/**")
                        .permitAll()
                .requestMatchers(
                        "/agua/nuevo","/agua/guardar", "/agua/eliminar/**","/agua/modificar/**", "/agua/descargar","/agua/abrirLink/**","/agua/interactuarPagina/**","/agua/pagar/**","/agua/descargarMontos","/agua/actualizarFechaVencimieno/**",
                        "/apartamento/nuevo","/apartamento/guardar", "/apartamento/eliminar/**","/apartamento/modificar/**",
                        "/encargado/nuevo","/encargado/guardar", "/encargado/eliminar/**","/encargado/modificar/**",
                        "/luz/nuevo","/luz/guardar", "/luz/eliminar/**","/luz/modificar/**","/luz/descargar","/luz/abrirLink/**","/luz/interactuarPagina/**","/luz/pagar/**","/luz/descargarMontos", "/luz/ActualizarFechaVencimiento/**",
                        "/montosAgua/nuevo","/montosAgua/guardar", "/montosAgua/eliminar/**","/montosAgua/modificar/**", "/montosAgua/query",
                        "/montosLuz/nuevo","/montosLuz/guardar", "/montosLuz/eliminar/**","/montosLuz/modificar/**","/montosLuz/query",
                        "/rol/nuevo","/rol/guardar", "/rol/eliminar/**","/rol/guardar/**",
                        "/usuario/nuevo","/usuario/guardar", "/usuario/eliminar/**","/usuario/modificar/**","/usuario/guardar2","/usuario/guardar3"
                ).hasRole("ADMIN")
                .requestMatchers("/agua/listado",
                        "/apartamento/listado",
                        "/encargado/listado",
                        "/luz/listado",
                        "/montosAgua/listado",
                        "/montosLuz/listado",
                        "/usuario/listado",
                        "/index"
                        )
                .hasRole("USER")
                )
                .formLogin((form) -> form
                .loginPage("/login")
                .defaultSuccessUrl("/index")
                .permitAll())
                .logout((logout) -> logout.permitAll());
        return http.build();
    }
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
    
    
}