package sfa.product_service.interceptor;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import sfa.product_service.AuthUtils.JwtHelper;
import sfa.product_service.constant.ApiErrorCodes;
import sfa.product_service.constant.UserRole;
import sfa.product_service.exception.InvalidInputException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class UserAuthorizationInterceptor implements HandlerInterceptor {

    private final JwtHelper jwtHelper;
    private static final Logger logger = Logger.getLogger(UserAuthorizationInterceptor.class.getName());
    public UserAuthorizationInterceptor(JwtHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        if(handler instanceof HandlerMethod handlerMethod){
            Method method = handlerMethod.getMethod();
            UserAuthorization userAuthorization = method.getAnnotation(UserAuthorization.class);
            if(userAuthorization != null){
                String authorizationHeader = request.getHeader("Authorization");
                UserRole[] allowedRoles = userAuthorization.allowedRoles();
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
                UserRole[] userRoles = getUserRole(token);
                if(userRoles != null){
                    if(!validateRole(allowedRoles, userRoles)){
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        return false;
                    }
                }
                return true;
            }
        }
        return true;
    }
    public boolean validateRole(UserRole[] allowedRoles, UserRole[] userRoles) {
        for (UserRole allowedRole : allowedRoles) {
           for(UserRole haveRole : userRoles){
               if(allowedRole == haveRole){
                   return true;
               }
           }
        }
        return false;
    }
    public boolean validateToken(String token){
        try {
            jwtHelper.validateOnlyToken(token);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred in UserAuthorizationInterceptor due to invalid token ", e);
            return false;
        }
        return true;
    }
    public UserRole[] getUserRole(String token) {
        try {
            return jwtHelper.getUserRolesFromToken(token).toArray(new UserRole[0]);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred in UserAuthorizationInterceptor due to invalid role or token ", e);
        }
        return null;
    }
}