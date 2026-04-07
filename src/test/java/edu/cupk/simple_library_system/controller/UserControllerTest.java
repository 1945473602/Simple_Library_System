package edu.cupk.simple_library_system.controller;

import edu.cupk.simple_library_system.common.ApiResponse;
import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.dto.AlterPasswordRequest;
import edu.cupk.simple_library_system.dto.LoginRequest;
import edu.cupk.simple_library_system.entity.User;
import edu.cupk.simple_library_system.repository.UserRepository;
import edu.cupk.simple_library_system.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// 用户管理控制器测试类
// 测试UserController中的所有用户管理相关功能
// 包括：登录、登出、注册、修改密码、用户信息查询、用户CRUD操作等
@SpringBootTest
class UserControllerTest {

    // 自动注入被测试的控制器实例
    @Autowired
    private UserController userController;

    // 模拟用户数据访问层，用于隔离数据库依赖
    @MockitoBean
    private UserRepository userRepository;

    // 模拟Token服务，用于隔离Redis缓存依赖
    @MockitoBean
    private TokenService tokenService;

    // 测试用的用户实体对象
    private User testUser;

    // 测试用的登录请求对象
    private LoginRequest loginRequest;

    // 每个测试方法执行前的初始化操作
    // 创建测试用的用户数据和登录请求数据
    @BeforeEach
    void setUp() {
        // 初始化测试用户数据
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUserName("testuser");
        testUser.setUserPassword("password");
        testUser.setIsAdmin((byte) 0);

        // 初始化登录请求数据
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setUserpassword("password");
        loginRequest.setIsAdmin((byte) 0);
    }

    // 测试用户登录成功场景
    // 验证点：
    // 1. 返回状态码为200
    // 2. 返回消息为"登录成功"
    // 3. 返回数据包含token信息
    @Test
    void testLogin_Success() {
        // 模拟用户存在且密码正确
        when(userRepository.findByUserNameAndUserPasswordAndIsAdmin(
                "testuser", "password", (byte) 0)).thenReturn(Optional.of(testUser));
        // 模拟生成token
        when(tokenService.createToken(1)).thenReturn("test-token");

        ApiResponse<?> response = userController.login(loginRequest);

        assertEquals(200, response.getStatus());
        assertEquals("登录成功", response.getMessage());
        assertNotNull(response.getData());
    }

    // 测试用户登录失败场景
    // 验证点：
    // 1. 返回状态码为420（业务错误码）
    // 2. 返回错误提示信息
    // 场景：用户名或密码错误，或角色不匹配
    @Test
    void testLogin_Failure() {
        // 模拟用户不存在（用户名或密码错误）
        when(userRepository.findByUserNameAndUserPasswordAndIsAdmin(
                "testuser", "password", (byte) 0)).thenReturn(Optional.empty());

        ApiResponse<?> response = userController.login(loginRequest);

        assertEquals(420, response.getStatus());
        assertEquals("用户名或密码错误，或角色不匹配", response.getMessage());
    }

    // 测试获取用户信息成功场景
    // 验证点：
    // 1. 返回状态码为200
    // 2. 返回消息为"获取成功"
    // 3. 返回的用户信息中密码被隐藏（显示为******）
    @Test
    void testInfo_Success() {
        // 模拟token验证成功，返回用户ID
        when(tokenService.verify("test-token")).thenReturn(1);
        // 模拟根据ID查询到用户
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        ApiResponse<?> response = userController.info("test-token");

        assertEquals(200, response.getStatus());
        assertEquals("获取成功", response.getMessage());
        User user = (User) response.getData();
        assertEquals("testuser", user.getUserName());
        assertEquals("******", user.getUserPassword());
    }

    // 测试获取用户信息失败场景 - Token无效
    // 验证点：
    // 1. 返回状态码为420
    // 2. 返回错误提示"Token无效或已过期"
    @Test
    void testInfo_TokenInvalid() {
        // 模拟token验证失败（token无效或已过期）
        when(tokenService.verify("invalid-token")).thenReturn(null);

        ApiResponse<?> response = userController.info("invalid-token");

        assertEquals(420, response.getStatus());
        assertEquals("Token无效或已过期", response.getMessage());
    }

    // 测试获取用户信息失败场景 - 用户不存在
    // 验证点：
    // 1. 返回状态码为420
    // 2. 返回错误提示"用户不存在"
    // 场景：token有效但用户已被删除
    @Test
    void testInfo_UserNotFound() {
        // 模拟token验证成功
        when(tokenService.verify("test-token")).thenReturn(1);
        // 模拟用户不存在（可能已被删除）
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ApiResponse<?> response = userController.info("test-token");

        assertEquals(420, response.getStatus());
        assertEquals("用户不存在", response.getMessage());
    }

    // 测试用户登出功能
    // 验证点：
    // 1. 返回状态码为200
    // 2. 返回消息为"登出成功"
    // 3. Token服务被调用移除token
    @Test
    void testLogout() {
        // 模拟token移除操作
        doNothing().when(tokenService).remove("test-token");

        ApiResponse<?> response = userController.logout("test-token");

        assertEquals(200, response.getStatus());
        assertEquals("登出成功", response.getMessage());
        verify(tokenService, times(1)).remove("test-token");
    }

    // 测试用户注册成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 场景：用户名不存在，可以正常注册
    @Test
    void testRegister_Success() {
        // 模拟用户名不存在
        when(userRepository.existsByUserName("newuser")).thenReturn(false);
        // 模拟保存用户成功
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Integer result = userController.register("newuser", "password");

        assertEquals(1, result);
    }

    // 测试用户注册失败场景 - 用户名已存在
    // 验证点：
    // 1. 返回值为0（表示失败）
    // 场景：用户名已被占用，无法重复注册
    @Test
    void testRegister_UserNameExists() {
        // 模拟用户名已存在
        when(userRepository.existsByUserName("testuser")).thenReturn(true);

        Integer result = userController.register("testuser", "password");

        assertEquals(0, result);
    }

    // 测试修改密码成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 场景：旧密码验证通过，新密码设置成功
    @Test
    void testAlterPassword_Success() {
        AlterPasswordRequest request = new AlterPasswordRequest();
        request.setUserId(1);
        request.setUserName("testuser");
        request.setIsAdmin((byte) 0);
        request.setOldPassword("password");
        request.setNewPassword("newpassword");

        // 模拟用户存在
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        // 模拟保存用户成功
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Integer result = userController.alterPassword(request);

        assertEquals(1, result);
    }

    // 测试修改密码失败场景 - 用户不存在
    // 验证点：
    // 1. 返回值为0（表示失败）
    // 场景：根据用户ID找不到用户
    @Test
    void testAlterPassword_UserNotFound() {
        AlterPasswordRequest request = new AlterPasswordRequest();
        request.setUserId(1);

        // 模拟用户不存在
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        Integer result = userController.alterPassword(request);

        assertEquals(0, result);
    }

    // 测试修改密码失败场景 - 凭证无效
    // 验证点：
    // 1. 返回值为0（表示失败）
    // 场景：用户名、角色或旧密码验证不通过
    @Test
    void testAlterPassword_InvalidCredentials() {
        AlterPasswordRequest request = new AlterPasswordRequest();
        request.setUserId(1);
        request.setUserName("wronguser");
        request.setIsAdmin((byte) 0);
        request.setOldPassword("wrongpassword");

        // 模拟用户存在但凭证不匹配
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        Integer result = userController.alterPassword(request);

        assertEquals(0, result);
    }

    // 测试获取用户总数
    // 验证点：
    // 1. 返回的用户数量与模拟值一致
    @Test
    void testGetCount() {
        // 模拟用户数量为10
        when(userRepository.count()).thenReturn(10L);

        long count = userController.getCount();

        assertEquals(10L, count);
    }

    // 测试查询所有用户
    // 验证点：
    // 1. 返回用户列表
    // 2. 用户密码被隐藏（显示为******）
    @Test
    void testQueryUsers() {
        List<User> users = new ArrayList<>();
        users.add(testUser);

        // 模拟查询所有用户
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userController.queryUsers();

        assertEquals(1, result.size());
        assertEquals("******", result.get(0).getUserPassword());
    }

    // 测试分页查询用户（不带用户名过滤条件）
    // 验证点：
    // 1. 返回的总记录数正确
    // 2. 返回的数据列表大小正确
    @Test
    void testQueryUsersByPage() {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        Page<User> page = new PageImpl<>(users);

        // 模拟分页查询
        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<User> result = userController.queryUsersByPage(1, 10, null);

        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().size());
    }

    // 测试分页查询用户（带用户名过滤条件）
    // 验证点：
    // 1. 返回的总记录数正确
    // 2. 返回的数据列表大小正确
    // 场景：根据用户名模糊查询
    @Test
    void testQueryUsersByPage_WithUsername() {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        Page<User> page = new PageImpl<>(users);

        // 模拟按用户名模糊查询
        when(userRepository.findByUserNameContaining("test", PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<User> result = userController.queryUsersByPage(1, 10, "test");

        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().size());
    }

    // 测试添加用户成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 场景：用户名不存在，可以正常添加
    @Test
    void testAddUser_Success() {
        // 模拟用户名不存在
        when(userRepository.existsByUserName("newuser")).thenReturn(false);
        // 模拟保存用户成功
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User newUser = new User();
        newUser.setUserName("newuser");
        newUser.setUserPassword("password");
        newUser.setIsAdmin((byte) 0);

        Integer result = userController.addUser(newUser);

        assertEquals(1, result);
    }

    // 测试添加用户失败场景 - 用户名已存在
    // 验证点：
    // 1. 返回值为0（表示失败）
    // 场景：用户名已被占用，无法重复添加
    @Test
    void testAddUser_UserNameExists() {
        // 模拟用户名已存在
        when(userRepository.existsByUserName("testuser")).thenReturn(true);

        User newUser = new User();
        newUser.setUserName("testuser");

        Integer result = userController.addUser(newUser);

        assertEquals(0, result);
    }

    // 测试删除用户成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 删除方法被调用一次
    // 场景：用户存在，可以正常删除
    @Test
    void testDeleteUser_Success() {
        User userToDelete = new User();
        userToDelete.setUserId(1);

        // 模拟用户存在
        when(userRepository.existsById(1)).thenReturn(true);
        // 模拟删除用户
        doNothing().when(userRepository).deleteById(1);

        Integer result = userController.deleteUser(userToDelete);

        assertEquals(1, result);
        verify(userRepository, times(1)).deleteById(1);
    }

    // 测试删除用户失败场景 - 用户不存在
    // 验证点：
    // 1. 返回值为0（表示失败）
    // 场景：用户ID不存在，无法删除
    @Test
    void testDeleteUser_UserNotFound() {
        User userToDelete = new User();
        userToDelete.setUserId(1);

        // 模拟用户不存在
        when(userRepository.existsById(1)).thenReturn(false);

        Integer result = userController.deleteUser(userToDelete);

        assertEquals(0, result);
    }

    // 测试批量删除用户
    // 验证点：
    // 1. 返回删除成功的用户数量
    // 2. 删除方法被调用正确次数
    // 场景：批量删除多个存在的用户
    @Test
    void testDeleteUsers() {
        List<User> usersToDelete = new ArrayList<>();
        User user1 = new User();
        user1.setUserId(1);
        User user2 = new User();
        user2.setUserId(2);
        usersToDelete.add(user1);
        usersToDelete.add(user2);

        // 模拟两个用户都存在
        when(userRepository.existsById(1)).thenReturn(true);
        when(userRepository.existsById(2)).thenReturn(true);
        // 模拟删除用户
        doNothing().when(userRepository).deleteById(anyInt());

        Integer result = userController.deleteUsers(usersToDelete);

        assertEquals(2, result);
        verify(userRepository, times(2)).deleteById(anyInt());
    }

    // 测试更新用户成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 场景：用户存在，可以正常更新
    @Test
    void testUpdateUser_Success() {
        User userToUpdate = new User();
        userToUpdate.setUserId(1);
        userToUpdate.setUserName("updateduser");

        // 模拟用户存在
        when(userRepository.existsById(1)).thenReturn(true);
        // 模拟保存用户成功
        when(userRepository.save(any(User.class))).thenReturn(userToUpdate);

        Integer result = userController.updateUser(userToUpdate);

        assertEquals(1, result);
    }

    // 测试更新用户失败场景 - 用户不存在
    // 验证点：
    // 1. 返回值为0（表示失败）
    // 场景：用户ID不存在，无法更新
    @Test
    void testUpdateUser_UserNotFound() {
        User userToUpdate = new User();
        userToUpdate.setUserId(1);

        // 模拟用户不存在
        when(userRepository.existsById(1)).thenReturn(false);

        Integer result = userController.updateUser(userToUpdate);

        assertEquals(0, result);
    }
}