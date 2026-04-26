package edu.cupk.simple_library_system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cupk.simple_library_system.common.ApiResponse;
import edu.cupk.simple_library_system.entity.User;
import edu.cupk.simple_library_system.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public AdminInterceptor(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        if (userId == null) {
            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail("未登录")));
            return false;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getIsAdmin() == null || user.getIsAdmin() != 1) {
            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail("权限不足，需要管理员权限")));
            return false;
        }

        return true;
    }
}
