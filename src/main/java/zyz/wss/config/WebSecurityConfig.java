package zyz.wss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@EnableWebSecurity
public class WebSecurityConfig {
    public static final String USERS_BY_USERNAME_QUERY = "select name,password,active "
            + "from user "
            + "where name = ?";
    public static final String AUTHORITIES_BY_USERNAME_QUERY = "select user.name, a.name "
            + "from user_authority ua "
            + "inner join user on ua.user_id = user.id "
            + "inner join authority a on ua.auth_id = a.id "
            + "where user.name = ?";

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcDaoImpl dao = new JdbcDaoImpl();
        dao.setUsersByUsernameQuery(USERS_BY_USERNAME_QUERY);
        dao.setAuthoritiesByUsernameQuery(AUTHORITIES_BY_USERNAME_QUERY);
//        dao.setEnableAuthorities(false);
        dao.setDataSource(dataSource);
        return dao;
/*        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withDefaultPasswordEncoder().username("admin").password("123456").roles("USER").build());
        return manager;*/
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/reglogin", "/static/**", "/login", "/user/**").permitAll()
                .anyRequest().authenticated()
                .and().formLogin()
                .usernameParameter("nameOrEmail").loginPage("/reglogin").loginProcessingUrl("/login").defaultSuccessUrl("/")
                .and().logout(logout -> logout.logoutUrl("/user/logout"));
        return http.build();
    }

    @Bean
    public NewDaoAuthenticationProvider newDaoAuthenticationProvider(UserDetailsService userDetailsService) {
        NewDaoAuthenticationProvider provider = new NewDaoAuthenticationProvider();
        provider.setDecodePassword(true);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

}
