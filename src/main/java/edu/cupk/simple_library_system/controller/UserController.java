package edu.cupk.simple_library_system.controller;

import edu.cupk.simple_library_system.common.ApiResponse;
import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.dto.AlterPasswordRequest;
import edu.cupk.simple_library_system.dto.LoginRequest;
import edu.cupk.simple_library_system.entity.User;
import edu.cupk.simple_library_system.repository.UserRepository;
import edu.cupk.simple_library_system.service.TokenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public UserController(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@RequestBody LoginRequest request) {
        Optional<User> user = userRepository.findByUserNameAndUserPasswordAndIsAdmin(
                request.getUsername(), request.getUserpassword(), request.getIsAdmin());
        if (user.isEmpty()) {
            return ApiResponse.fail("用户名或密码错误，或角色不匹配");
        }
        String token = tokenService.createToken(user.get().getUserId());
        return ApiResponse.ok("登录成功", Map.of("token", token));
    }

    @GetMapping("/info")
    public ApiResponse<User> info(@RequestParam String token) {
        Integer userId = tokenService.verify(token);
        if (userId == null) {
            return ApiResponse.fail("Token无效或已过期");
        }
        return userRepository.findById(userId)
                .map(u -> {
                    u.setUserPassword("******");
                    return ApiResponse.ok("获取成功", u);
                })
                .orElseGet(() -> ApiResponse.fail("用户不存在"));
    }

    @GetMapping("/logout")
    public ApiResponse<Void> logout(@RequestParam String token) {
        tokenService.remove(token);
        return ApiResponse.ok("登出成功", null);
    }

    @PostMapping("/register")
    public Integer register(@RequestParam String username, @RequestParam String password) {
        if (userRepository.existsByUserName(username)) {
            return 0;
        }
        User user = new User();
        user.setUserName(username);
        user.setUserPassword(password);
        user.setIsAdmin((byte) 0);
        userRepository.save(user);
        return 1;
    }

    @PostMapping("/alterPassword")
    public Integer alterPassword(@RequestBody AlterPasswordRequest request) {
        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        if (optionalUser.isEmpty()) {
            return 0;
        }
        User user = optionalUser.get();
        if (!Objects.equals(user.getUserName(), request.getUserName())
                || !Objects.equals(user.getIsAdmin(), request.getIsAdmin())
                || !Objects.equals(user.getUserPassword(), request.getOldPassword())) {
            return 0;
        }
        user.setUserPassword(request.getNewPassword());
        userRepository.save(user);
        return 1;
    }

    @PostMapping("/reader/alterPassword")
    public Integer readerAlterPassword(@RequestBody AlterPasswordRequest request) {
        return alterPassword(request);
    }

    @GetMapping("/getCount")
    public long getCount() {
        return userRepository.count();
    }

    @GetMapping("/queryUsers")
    public List<User> queryUsers() {
        List<User> list = userRepository.findAll();
        list.forEach(u -> u.setUserPassword("******"));
        return list;
    }

    @GetMapping("/queryUsersByPage")
    public PageResponse<User> queryUsersByPage(@RequestParam int page,
                                               @RequestParam int limit,
                                               @RequestParam(required = false) String username) {
        Page<User> result;
        if (username == null || username.isBlank()) {
            result = userRepository.findAll(PageRequest.of(Math.max(page - 1, 0), limit));
        } else {
            result = userRepository.findByUserNameContaining(username, PageRequest.of(Math.max(page - 1, 0), limit));
        }
        result.getContent().forEach(u -> u.setUserPassword("******"));
        return PageResponse.success(result.getTotalElements(), result.getContent());
    }

    @PostMapping("/addUser")
    public Integer addUser(@RequestBody User user) {
        if (userRepository.existsByUserName(user.getUserName())) {
            return 0;
        }
        userRepository.save(user);
        return 1;
    }

    @DeleteMapping("/deleteUser")
    public Integer deleteUser(@RequestBody User user) {
        if (user.getUserId() == null || !userRepository.existsById(user.getUserId())) {
            return 0;
        }
        userRepository.deleteById(user.getUserId());
        return 1;
    }

    @DeleteMapping("/deleteUsers")
    public Integer deleteUsers(@RequestBody List<User> users) {
        int count = 0;
        for (User user : users) {
            if (user.getUserId() != null && userRepository.existsById(user.getUserId())) {
                userRepository.deleteById(user.getUserId());
                count++;
            }
        }
        return count;
    }

    @PutMapping("/updateUser")
    public Integer updateUser(@RequestBody User user) {
        if (user.getUserId() == null || !userRepository.existsById(user.getUserId())) {
            return 0;
        }
        userRepository.save(user);
        return 1;
    }
}
