package sfa.product_service.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class UserAuthorizationInterceptor implements HandlerInterceptor {

    private final RestTemplate restTemplate;
    @Value("${validate.token.url}")
    public String validatorUrl;
    private static final Logger logger = Logger.getLogger(UserAuthorizationInterceptor.class.getName());

    @Autowired
    public UserAuthorizationInterceptor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            UserAuthorization userAuthorization = method.getAnnotation(UserAuthorization.class);
            if(userAuthorization != null){
                String authorizationHeader = request.getHeader("Authorization");
                if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }
                String token = authorizationHeader.substring(7);
                if(token.isEmpty()){
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }
                if(!validateToken(token)){
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }
                return true;
            }
        }
        return true;
    }

    public boolean validateToken(String token){
        String validateTokenUrl = validatorUrl + token;
        try {
            restTemplate.getForEntity(validateTokenUrl, Void.class);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred in UserAuthorizationInterceptor due to invalid token ", e);
            return false;
        }
        return true;
    }
}