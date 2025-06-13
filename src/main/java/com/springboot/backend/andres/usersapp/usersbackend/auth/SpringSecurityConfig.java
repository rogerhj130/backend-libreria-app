package com.springboot.backend.andres.usersapp.usersbackend.auth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.springboot.backend.andres.usersapp.usersbackend.auth.filter.JwtAuthenticationFilter;
import com.springboot.backend.andres.usersapp.usersbackend.auth.filter.JwtValidationFilter;

@Configuration
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http.authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/page/{page}","/api/users/me").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/users/me").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/ventas/enviar-pdf").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/vistas/count").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/vistas").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/stripe/create-checkout-session").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/stripe/webhook").permitAll()
    

                .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAnyAuthority("USERS_VIEW", "ADMIN_PANEL_ACCESS") // Agregué ADMIN_PANEL_ACCESS como un comodín para administradores
                // Para crear un usuario, necesitas el permiso "USERS_CREATE"
                .requestMatchers(HttpMethod.POST, "/api/users").hasAnyAuthority("USERS_CREATE_VIEW", "ADMIN_PANEL_ACCESS")
                 // Para actualizar un usuario, necesitas el permiso "USERS_UPDATE"
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasAnyAuthority("USERS_UPDATE", "ADMIN_PANEL_ACCESS")
                  // Para eliminar un usuario, necesitas el permiso "USERS_DELETE"
                 .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasAnyAuthority("USERS_DELETE", "ADMIN_PANEL_ACCESS")


                 
                .requestMatchers(HttpMethod.GET,"/api/users/me").hasAnyAuthority("ME_VIEW", "ADMIN_PANEL_ACCESS")
                 .requestMatchers(HttpMethod.GET, "/api/permissions", "/api/permissions/page/{page}").hasAnyAuthority("PERMISO_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.GET, "/api/permissions/{id}").hasAnyAuthority("PERMISO_VIEW", "ADMIN_PANEL_ACCESS")

                 // Rutas públicas (accesibles sin autenticación)
                .requestMatchers(HttpMethod.GET, "/api/ventas", "/api/ventas/page/{page}").hasAnyAuthority("VENTAS_VIEW", "ADMIN_PANEL_ACCESS")
              //  .requestMatchers(HttpMethod.GET, "/api/compras", "/api/compras/page/{page}").permitAll()
               // .requestMatchers(HttpMethod.GET, "/api/traspasos/page/{page}").permitAll()

                // USUARIOS
               // .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/page/{page}").hasAnyRole("USER", "ADMIN")
               // .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAnyRole("USER", "ADMIN")
               // .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
               // .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasRole("ADMIN")
               
                
                // CLIENTES
               // .requestMatchers(HttpMethod.GET, "/api/clientes", "/api/clientes/page/{page}").hasAnyRole("USER", "ADMIN")
               // .requestMatchers(HttpMethod.GET, "/api/clientes/{id}").hasAnyRole("USER", "ADMIN")
               // .requestMatchers(HttpMethod.POST, "/api/clientes").hasAnyRole("USER", "ADMIN")
               // .requestMatchers(HttpMethod.PUT, "/api/clientes/{id}").hasAnyRole("USER", "ADMIN")
            
               

                .requestMatchers(HttpMethod.GET, "/api/clientes", "/api/clientes/{id}").hasAnyAuthority("CLIENTS_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.POST, "/api/clientes").hasAnyAuthority("CLIENTS_CREATE_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.PUT, "/api/clientes/{id}").hasAnyAuthority("CLIENTS_UPDATE", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.DELETE, "/api/clientes/{id}").hasAnyAuthority("CLIENTS_DELETE", "ADMIN_PANEL_ACCESS")


                // EMPLEADOS
                .requestMatchers(HttpMethod.GET, "/api/empleados").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/empleados/{id}").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/empleados").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/empleados/{id}").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/empleados/{id}").hasAnyRole("USER", "ADMIN")

                // LABORATORIOS
                .requestMatchers(HttpMethod.GET, "/api/marcas").hasAnyAuthority("MARCA_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.GET, "/api/marcas/{id}").hasAnyAuthority("MARCA_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.POST, "/api/marcas").hasAnyAuthority("MARCA_CREATE_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.PUT, "/api/marcas/{id}").hasAnyAuthority("MARCA_UPDATE", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.DELETE, "/api/marcas/{id}").hasAnyAuthority("MARCA_DELETE", "ADMIN_PANEL_ACCESS")

                // PROVEEDORES
                .requestMatchers(HttpMethod.GET, "/api/proveedores").hasAnyAuthority("PROVEEDOR_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.GET, "/api/proveedores/{id}").hasAnyAuthority("PROVEEDOR_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.POST, "/api/proveedores").hasAnyAuthority("PROVEEDOR_CREATE_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.PUT, "/api/proveedores/{id}").hasAnyAuthority("PROVEEDOR_UPDATE", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.DELETE, "/api/proveedores/{id}").hasAnyAuthority("PROVEEDOR_DELETE", "ADMIN_PANEL_ACCESS")

                // MEDICAMENTOS
                .requestMatchers(HttpMethod.GET, "/api/materiales").hasAnyAuthority("PRODUCTS_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.GET, "/api/materiales/{id}").hasAnyAuthority("PRODUCTS_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.POST, "/api/materiales").hasAnyAuthority("PRODUCTS_CREATE_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.PUT, "/api/materiales/{id}").hasAnyAuthority("PRODUCTS_UPDATE", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.DELETE, "/api/materiales/{id}").hasAnyAuthority("PRODUCTS_DELETE", "ADMIN_PANEL_ACCESS")


                // VENTAS
                .requestMatchers(HttpMethod.GET, "/api/ventas").hasAnyAuthority("VENTAS_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.GET, "/api/ventas/{id}").hasAnyAuthority("VENTAS_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.POST, "/api/ventas").hasAnyAuthority("VENTAS_CREATE_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.DELETE, "/api/ventas/{id}").hasAnyAuthority("VENTAS_DELETE", "ADMIN_PANEL_ACCESS")

                .requestMatchers(HttpMethod.POST, "/api/ventas/usuario/ventas").hasAnyAuthority("REPORTE_INVENTARIO_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.POST, "/api/ventas/usuario/ventas/rango").hasAnyAuthority("REPORTE_RANGO_VIEW", "ADMIN_PANEL_ACCESS")


                // COMPRAS
                .requestMatchers(HttpMethod.GET, "/api/compras").hasAnyAuthority("COMPRAS_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.GET, "/api/compras/{id}").hasAnyAuthority("COMPRAS_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.POST, "/api/compras").hasAnyAuthority("COMPRAS_CREATE_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.DELETE, "/api/compras/{id}").hasAnyAuthority("COMPRAS_DELETE", "ADMIN_PANEL_ACCESS")

                // ALMACENES
                .requestMatchers(HttpMethod.GET, "/api/almacenes").hasAnyAuthority("ALMACEN_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.GET, "/api/almacenes/{id}").hasAnyAuthority("ALMACEN_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.POST, "/api/almacenes").hasAnyAuthority("ALMACEN_CREATE_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.PUT, "/api/almacenes/{id}").hasAnyAuthority("ALMACEN_UPDATE", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.DELETE, "/api/almacenes/{id}").hasAnyAuthority("ALMACEN_DELETE", "ADMIN_PANEL_ACCESS")
                
                .requestMatchers(HttpMethod.GET, "/api/almacenes/listar-medicamentos/{id}").hasAnyAuthority("ALMACEN_LIST_PROD_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.POST, "/api/almacenes/traspasar").hasAnyAuthority("TRASPASAR_CREATE", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.GET, "/api/almacenes/medicamentos/almacen1").hasAnyAuthority("ALMACEN1_VIEW", "ADMIN_PANEL_ACCESS")
                
                // TRASPASOS
                .requestMatchers(HttpMethod.GET, "/api/almacenes/traspasar").hasAnyAuthority("TRASPASAR_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.GET, "/api/almacenes/traspasar/{id}").hasAnyAuthority("TRASPASAR_VIEW", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.POST, "/api/almacenes/traspasar").hasAnyAuthority("TRASPASAR_CREATE", "ADMIN_PANEL_ACCESS")
                .requestMatchers(HttpMethod.DELETE, "/api/traspasar/{id}").hasAnyAuthority("TRASPASAR_DELETE", "ADMIN_PANEL_ACCESS")
                 .requestMatchers(HttpMethod.GET, "/api/almacenes/ajustar").hasAnyAuthority("AJUSTAR_VIEW", "ADMIN_PANEL_ACCESS") // <-- agrega esto

                 .requestMatchers(HttpMethod.POST, "/api/almacenes/ajustar/{id}").hasAnyAuthority("AJUSTAR_CREATE", "ADMIN_PANEL_ACCESS") 


                // DASHBOARD
                .requestMatchers(HttpMethod.GET, "/api/dashboard/total-stock").hasAnyAuthority("TOTAL_STOCK_VIEW", "ADMIN_PANEL_ACCESS") 
                .requestMatchers(HttpMethod.GET, "/stock-por-almacen/{almacenId}").hasAnyAuthority("STOCK_ALMACEN_VIEW", "ADMIN_PANEL_ACCESS") 

                .requestMatchers(HttpMethod.GET, "/api/dashboard/ventas-diarias").hasAnyAuthority("VENTA_DIARIA_VIEW", "ADMIN_PANEL_ACCESS") 

                .requestMatchers(HttpMethod.GET, "/api/dashboard/ventas-semanales").hasAnyAuthority("VENTA_SEM_VIEW", "ADMIN_PANEL_ACCESS") 

                .requestMatchers(HttpMethod.GET, "/api/dashboard/ventas-mensuales").hasAnyAuthority("VENTA_MEN_VIEW", "ADMIN_PANEL_ACCESS") 
                .requestMatchers(HttpMethod.POST, "/api/dashboard/ventas-especifica").hasAnyAuthority("VENTA_ESPECI_CREATE", "ADMIN_PANEL_ACCESS") 
                .requestMatchers(HttpMethod.GET, "/api/dashboard/medicamentos-mas-vendidos-semana").hasAnyAuthority("MAS_VENDIDO_VIEW", "ADMIN_PANEL_ACCESS") 




                // View Roles (GET /api/roles, GET /api/roles/with-permissions, GET /api/roles/{id})
    .requestMatchers(HttpMethod.GET, "/api/roles", "/api/roles/{id}").hasAnyAuthority("ROLES_VIEW", "ADMIN_PANEL_ACCESS")

    .requestMatchers(HttpMethod.GET, "/api/roles/page").hasAnyAuthority("ROLES_VIEW", "ADMIN_PANEL_ACCESS")

    .requestMatchers(HttpMethod.GET,"/api/roles/with-permissions").hasAnyAuthority("ROLE_WITH_VIEW","ADMIN_PANEL_ACCESS")
    // Create Role (POST /api/roles)
    .requestMatchers(HttpMethod.POST, "/api/roles").hasAnyAuthority("ROLES_CREATE", "ADMIN_PANEL_ACCESS")

    // Assign Role Permissions to User (POST /api/roles/assign-role-permissions)
    // This is very sensitive, likely only for admins or specialized user managers
    .requestMatchers(HttpMethod.POST, "/api/roles/assign-role").hasAnyAuthority("USER_ROLE_ASSIGN", "ADMIN_PANEL_ACCESS")

     .requestMatchers(HttpMethod.PUT, "/api/roles/update-roles").hasAnyAuthority("USER_ROLE_ASSIGN_UPDA", "ADMIN_PANEL_ACCESS")

     .requestMatchers(HttpMethod.PUT, "/api/roles/update-user-roles").hasAnyAuthority("USER_ROLE_ASSIGN", "ADMIN_PANEL_ACCESS")
    // Update Role (PUT /api/roles/{id})
    .requestMatchers(HttpMethod.PUT, "/api/roles/{id}").hasAnyAuthority("ROLES_UPDATE", "ADMIN_PANEL_ACCESS")

    // Delete Role (DELETE /api/roles/{id})
    .requestMatchers(HttpMethod.DELETE, "/api/roles/{id}").hasAnyAuthority("ROLES_DELETE", "ADMIN_PANEL_ACCESS")
                
    
    
    
    
    .requestMatchers(HttpMethod.GET, "/api/permissions", "/api/permissions/page/{page}", "/api/permissions/{id}").hasAnyAuthority("PERMISSIONS_VIEW", "ADMIN_PANEL_ACCESS")

    // Create Permission (POST /api/permissions)
    .requestMatchers(HttpMethod.POST, "/api/permissions").hasAnyAuthority("PERMISSIONS_CREATE", "ADMIN_PANEL_ACCESS")

    // Assign Permissions to Role (POST /api/permissions/assign-permissions)
    // This is highly sensitive, allowing modification of what roles can do.
    .requestMatchers(HttpMethod.POST, "/api/permissions/assign-permissions").hasAnyAuthority("ROLE_PERMISSION_ASSIGN", "ADMIN_PANEL_ACCESS")

    // Update Permission (PUT /api/permissions/{id})
    .requestMatchers(HttpMethod.PUT, "/api/permissions/{id}").hasAnyAuthority("PERMISSIONS_UPDATE", "ADMIN_PANEL_ACCESS")

    // Delete Permission (DELETE /api/permissions/{id})
    .requestMatchers(HttpMethod.DELETE, "/api/permissions/{id}").hasAnyAuthority("PERMISSIONS_DELETE", "ADMIN_PANEL_ACCESS")
    
    



    .requestMatchers(HttpMethod.GET, "/api/traspasos", "/api/traspasos/page/{page}", "/api/traspasos/{id}").hasAnyAuthority("TRASPASOS_VIEW", "ADMIN_PANEL_ACCESS")

    // Crear Traspaso (POST /api/traspasos)
    .requestMatchers(HttpMethod.POST, "/api/traspasos").hasAnyAuthority("TRASPASOS_CREATE", "ADMIN_PANEL_ACCESS")

    // Actualizar Traspaso (PUT /api/traspasos/{id})
    .requestMatchers(HttpMethod.PUT, "/api/traspasos/{id}").hasAnyAuthority("TRASPASOS_UPDATE", "ADMIN_PANEL_ACCESS")

    // Eliminar Traspaso (DELETE /api/traspasos/{id})
    .requestMatchers(HttpMethod.DELETE, "/api/traspasos/{id}").hasAnyAuthority("TRASPASOS_DELETE", "ADMIN_PANEL_ACCESS")
    
    
    
    // DETALLES DE VENTAS //no es necesario esta por si acaso
                
                  .requestMatchers(HttpMethod.GET, "/api/ventas").hasAnyRole("USER", "ADMIN")
                  .requestMatchers(HttpMethod.GET,
                  "/api/ventas/{ventaId}/{medicamentoId}").hasAnyRole("USER", "ADMIN")
                  .requestMatchers(HttpMethod.POST, "/api/ventas").hasAnyRole("USER", "ADMIN")
                  .requestMatchers(HttpMethod.DELETE,
                  "/api/venta/{ventaId}/{medicamentoId}").hasAnyRole("USER", "ADMIN")
                 

                // Cualquier otra solicitud debe estar autenticada
                .anyRequest().authenticated())
                .cors(cors -> cors.configurationSource(configurationSource()))
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationManager()))
                .csrf(config -> config.disable())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }


//.anyRequest().authenticated())
  //              .cors(cors -> cors.configurationSource(configurationSource()))
    //            .addFilter(new JwtAuthenticationFilter(authenticationManager()))
      //          .addFilter(new JwtValidationFilter(authenticationManager()))
        //        .csrf(config -> config.disable())
          //      .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            //    .build();
   // }

    @Bean
    CorsConfigurationSource configurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedOrigins(Arrays.asList("https://classy-snickerdoodle-9f37c8.netlify.app"));
        config.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<CorsFilter>(
                new CorsFilter(this.configurationSource()));
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }
}
