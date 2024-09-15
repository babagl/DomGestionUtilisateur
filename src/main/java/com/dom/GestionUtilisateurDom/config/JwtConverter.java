package com.dom.gestionutilisateurdom.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken>{
    private final JwtGrantedAuthoritiesConverter jwtAuthenticationConverter = new JwtGrantedAuthoritiesConverter();
    private final JwtConverterProperties jwtConverterProperties;

    public JwtConverter(JwtConverterProperties jwtConverterProperties) {
        this.jwtConverterProperties = jwtConverterProperties;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtAuthenticationConverter.convert(jwt).stream(),
                extractRessourcesRoles(jwt).stream()
        ).collect(Collectors.toSet());
         return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimsName(jwt));
    }

    private Collection<? extends GrantedAuthority> extractRessourcesRoles(Jwt jwt) {

        Map<String , Object> resourceAccess = jwt.getClaim("ressources_access");
        log.info("Ressources access: {}", resourceAccess);
        Map<String , Object> resource;
        Collection<String> resourceRoles;
        if (resourceAccess == null
                || (resource = (Map<String, Object>) resourceAccess.get(jwtConverterProperties.getResourceID())) == null
                || (resourceRoles = (Collection<String>) resource.get("roles")) == null){
            return Set.of();
        }
        return resourceRoles.stream().map(role -> new SimpleGrantedAuthority("ROLE_"+role))
                .collect(Collectors.toSet());
    }

    private String getPrincipalClaimsName(Jwt jwt) {
        String claimsName = JwtClaimNames.SUB;
        log.info("############Claims name"+claimsName);
        if (jwtConverterProperties.getPrincipalAttribute()!= null){
            claimsName = jwtConverterProperties.getPrincipalAttribute();
        }
        return jwt.getClaim(claimsName);
    }


}