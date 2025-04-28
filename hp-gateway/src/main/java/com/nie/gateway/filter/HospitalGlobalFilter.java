package com.nie.gateway.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nie.gateway.config.FilterPath;
import com.nie.gateway.tools.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalGlobalFilter implements GlobalFilter, Ordered {
    private final FilterPath filterPath;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        String[] excludePaths = filterPath.getExcludePaths();
        if (checked(excludePaths, path)) {
            return chain.filter(exchange);
        }
//------------------------------------------------
        if (request.getMethod().equals("OPTIONS")) {
            return chain.filter(exchange);
        }

        String token = "";
        List<String> authorization = request.getHeaders().get("Authorization");
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.get(0);
            log.info("token :{}", token);
        }

        DecodedJWT decodedJWT = null;
        if (token != null && !token.isEmpty()) {
            decodedJWT = JwtUtil.verify(token);
            if (decodedJWT == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
//----------------------------------------------------
        String claimKey = "";
        String[] split = path.split("/");
        switch (split[1]) {
            case "admin":
                claimKey = "aId";
                break;
            case "doctor":
                claimKey = "dId";
                break;
            case "patient":
                claimKey = "pId";
                break;
            default:
                log.warn("未匹配到路径");
                break;
        }
        String id = decodedJWT.getClaim(claimKey).asString();
        log.info("HP-{} : {}", claimKey, id);
        ServerWebExchange webExchange = exchange
                .mutate()
                .request(builder -> builder.header("userid", id).header("usertype", split[1]))
                .build();

        return chain.filter(webExchange);

    }

    @Override
    public int getOrder() {
        return 0;
    }

    public boolean checked(String[] urls, String path) {
        for (String url : urls) {
            if (antPathMatcher.match(url, path)) {
                return true;
            }
        }
        return false;
    }
}
