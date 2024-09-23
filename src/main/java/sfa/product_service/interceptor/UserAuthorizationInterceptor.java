package sfa.product_service.interceptor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import sfa.auth.AuthUtils.JwtHelper;
import sfa.auth.constant.ApiErrorCodes;
import sfa.auth.constant.UserRole;
import sfa.auth.exception.InvalidInputException;
import sfa.auth.implementation.UserServiceImpl;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class UserAuthorizationInterceptor implements HandlerInterceptor {

    private final JwtHelper jwtHelper;
    private final UserServiceImpl userServices;

    public UserAuthorizationInterceptor(JwtHelper jwtHelper, UserServiceImpl userServices) {
        this.jwtHelper = jwtHelper;
        this.userServices = userServices;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            UserAuthorization userAuthorization = method.getAnnotation(UserAuthorization.class);
            if(userAuthorization != null){
                UserRole[] allowedRoles = userAuthorization.allowedRoles();
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
                UserRole[] userRole = getUserRole(token);
                if(userRole != null){
                    if(validateRole(allowedRoles, userRole)) {
                        return true;
                    }
                }
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }
        return true;
    }

    public boolean validateRole(UserRole[] allowedRoles, UserRole[] userRoles) {
        for (UserRole allowedRole : allowedRoles) {
            for (UserRole userRole : userRoles) {
                if (allowedRole.equals(userRole)) {
                    return true; // At least one match found
                }
            }
        }
        return false; // No matches found
    }

    public boolean validateToken(String token){
        try {
            jwtHelper.validateOnlyToken(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    public UserRole[] getUserRole(String token) {
        try {
            String username = jwtHelper.getUsernameFromToken(token);
            List<UserRole> userRoleList = userServices.getAllRoleByMobileNo(Long.valueOf(username));
            if (userRoleList != null) {
                UserRole[] userRoles = new UserRole[20];
                for(int i=0; i<userRoleList.size(); i++){
                    userRoles[i] = userRoleList.get(i);
                }
                return userRoles;
            }
        } catch (Exception e) {
            throw new InvalidInputException(ApiErrorCodes.INVALID_USER_ROLE.getErrorCode(), ApiErrorCodes.INVALID_USER_ROLE.getErrorMessage());
        }
        return null;
    }


}