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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController userController;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private TokenService tokenService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUserName("testuser");
        testUser.setUserPassword("password");
        testUser.setIsAdmin((byte) 0);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setUserpassword("password");
        loginRequest.setIsAdmin((byte) 0);
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByUserNameAndUserPasswordAndIsAdmin(
                "testuser", "password", (byte) 0)).thenReturn(Optional.of(testUser));
        when(tokenService.createToken(1)).thenReturn("test-token");

        ApiResponse<?> response = userController.login(loginRequest);

        assertEquals(200, response.getStatus(), "登录成功业务状态码必须是200");
        assertEquals("登录成功", response.getMessage(), "登录成功消息必须是'登录成功'");
        assertNotNull(response.getData(), "登录成功响应数据不能为null");
    }

    @Test
    void testLogin_Failure() {
        when(userRepository.findByUserNameAndUserPasswordAndIsAdmin(
                "testuser", "password", (byte) 0)).thenReturn(Optional.empty());

        ApiResponse<?> response = userController.login(loginRequest);

        assertEquals(420, response.getStatus(), "登录失败业务状态码必须是420");
        assertEquals("用户名或密码错误，或角色不匹配", response.getMessage(), "登录失败消息必须是'用户名或密码错误，或角色不匹配'");
    }

    @Test
    void testInfo_Success() {
        when(tokenService.verify("test-token")).thenReturn(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        ApiResponse<?> response = userController.info("test-token");

        assertEquals(200, response.getStatus(), "获取用户信息成功业务状态码必须是200");
        assertEquals("获取成功", response.getMessage(), "获取用户信息成功消息必须是'获取成功'");
        assertNotNull(response.getData(), "获取用户信息响应数据不能为null");

        User user = (User) response.getData();
        assertEquals("testuser", user.getUserName(), "用户名必须是'testuser'");
        assertEquals("******", user.getUserPassword(), "密码必须被掩码显示为'******'");
    }

    @Test
    void testInfo_TokenInvalid() {
        when(tokenService.verify("invalid-token")).thenReturn(null);

        ApiResponse<?> response = userController.info("invalid-token");

        assertEquals(420, response.getStatus(), "无效token业务状态码必须是420");
        assertEquals("Token无效或已过期", response.getMessage(), "无效token消息必须是'Token无效或已过期'");
    }

    @Test
    void testInfo_UserNotFound() {
        when(tokenService.verify("test-token")).thenReturn(1);
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ApiResponse<?> response = userController.info("test-token");

        assertEquals(420, response.getStatus(), "用户不存在业务状态码必须是420");
        assertEquals("用户不存在", response.getMessage(), "用户不存在消息必须是'用户不存在'");
    }

    @Test
    void testLogout() {
        doNothing().when(tokenService).remove("test-token");

        ApiResponse<?> response = userController.logout("test-token");

        assertEquals(200, response.getStatus(), "登出成功业务状态码必须是200");
        assertEquals("登出成功", response.getMessage(), "登出成功消息必须是'登出成功'");
        verify(tokenService, times(1)).remove("test-token");
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsByUserName("newuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Integer result = userController.register(Map.of("username", "newuser", "password", "password"));

        assertEquals(1, result, "注册成功必须返回1");
    }

    @Test
    void testRegister_UserNameExists() {
        when(userRepository.existsByUserName("testuser")).thenReturn(true);

        Integer result = userController.register(Map.of("username", "testuser", "password", "password"));

        assertEquals(0, result, "用户名已存在必须返回0");
    }

    @Test
    void testRegister_NullUsername() {
        Integer result = userController.register(Map.of("password", "password"));

        assertEquals(0, result, "用户名为null必须返回0");
    }

    @Test
    void testRegister_NullPassword() {
        Integer result = userController.register(Map.of("username", "newuser"));

        assertEquals(0, result, "密码为null必须返回0");
    }

    @Test
    void testAlterPassword_Success() {
        AlterPasswordRequest request = new AlterPasswordRequest();
        request.setUserId(1);
        request.setUserName("testuser");
        request.setIsAdmin((byte) 0);
        request.setOldPassword("password");
        request.setNewPassword("newpassword");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Integer result = userController.alterPassword(request);

        assertEquals(1, result, "修改密码成功必须返回1");
    }

    @Test
    void testAlterPassword_UserNotFound() {
        AlterPasswordRequest request = new AlterPasswordRequest();
        request.setUserId(1);

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        Integer result = userController.alterPassword(request);

        assertEquals(0, result, "用户不存在修改密码必须返回0");
    }

    @Test
    void testAlterPassword_InvalidCredentials() {
        AlterPasswordRequest request = new AlterPasswordRequest();
        request.setUserId(1);
        request.setUserName("wronguser");
        request.setIsAdmin((byte) 0);
        request.setOldPassword("wrongpassword");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        Integer result = userController.alterPassword(request);

        assertEquals(0, result, "凭据无效修改密码必须返回0");
    }

    @Test
    void testReaderAlterPassword_Success() {
        AlterPasswordRequest request = new AlterPasswordRequest();
        request.setUserId(1);
        request.setUserName("testuser");
        request.setIsAdmin((byte) 0);
        request.setOldPassword("password");
        request.setNewPassword("newpassword");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Integer result = userController.readerAlterPassword(request);

        assertEquals(1, result, "读者修改密码成功必须返回1");
    }

    @Test
    void testReaderAlterPassword_UserNotFound() {
        AlterPasswordRequest request = new AlterPasswordRequest();
        request.setUserId(999);

        when(userRepository.findById(999)).thenReturn(Optional.empty());

        Integer result = userController.readerAlterPassword(request);

        assertEquals(0, result, "读者修改密码用户不存在必须返回0");
    }

    @Test
    void testGetCount() {
        when(userRepository.count()).thenReturn(10L);

        long count = userController.getCount();

        assertEquals(10L, count, "用户数量必须是10");
    }

    @Test
    void testQueryUsers() {
        List<User> users = new ArrayList<>();
        users.add(testUser);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userController.queryUsers();

        assertEquals(1, result.size(), "用户列表大小必须是1");
        assertEquals("******", result.get(0).getUserPassword(), "密码必须被掩码显示为'******'");
    }

    @Test
    void testQueryUsersByPage() {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        Page<User> page = new PageImpl<>(users);

        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<User> result = userController.queryUsersByPage(1, 10, null);

        assertEquals(1, result.getCount(), "用户总数必须是1");
        assertEquals(1, result.getData().size(), "用户列表大小必须是1");
    }

    @Test
    void testQueryUsersByPage_WithUsername() {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        Page<User> page = new PageImpl<>(users);

        when(userRepository.findByUserNameContaining("test", PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<User> result = userController.queryUsersByPage(1, 10, "test");

        assertEquals(1, result.getCount(), "过滤后用户总数必须是1");
        assertEquals(1, result.getData().size(), "过滤后用户列表大小必须是1");
    }

    @Test
    void testAddUser_Success() {
        when(userRepository.existsByUserName("newuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User newUser = new User();
        newUser.setUserName("newuser");
        newUser.setUserPassword("password");
        newUser.setIsAdmin((byte) 0);

        Integer result = userController.addUser(newUser);

        assertEquals(1, result, "添加用户成功必须返回1");
    }

    @Test
    void testAddUser_UserNameExists() {
        when(userRepository.existsByUserName("testuser")).thenReturn(true);

        User newUser = new User();
        newUser.setUserName("testuser");

        Integer result = userController.addUser(newUser);

        assertEquals(0, result, "用户名已存在添加用户必须返回0");
    }

    @Test
    void testDeleteUser_Success() {
        User userToDelete = new User();
        userToDelete.setUserId(1);

        when(userRepository.existsById(1)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1);

        Integer result = userController.deleteUser(userToDelete);

        assertEquals(1, result, "删除用户成功必须返回1");
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        User userToDelete = new User();
        userToDelete.setUserId(1);

        when(userRepository.existsById(1)).thenReturn(false);

        Integer result = userController.deleteUser(userToDelete);

        assertEquals(0, result, "用户不存在删除必须返回0");
    }

    @Test
    void testDeleteUsers() {
        List<User> usersToDelete = new ArrayList<>();
        User user1 = new User();
        user1.setUserId(1);
        User user2 = new User();
        user2.setUserId(2);
        usersToDelete.add(user1);
        usersToDelete.add(user2);

        when(userRepository.existsById(1)).thenReturn(true);
        when(userRepository.existsById(2)).thenReturn(true);
        doNothing().when(userRepository).deleteById(anyInt());

        Integer result = userController.deleteUsers(usersToDelete);

        assertEquals(2, result, "批量删除用户成功数量必须是2");
        verify(userRepository, times(2)).deleteById(anyInt());
    }

    @Test
    void testUpdateUser_Success() {
        User userToUpdate = new User();
        userToUpdate.setUserId(1);
        userToUpdate.setUserName("updateduser");

        when(userRepository.existsById(1)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(userToUpdate);

        Integer result = userController.updateUser(userToUpdate);

        assertEquals(1, result, "更新用户成功必须返回1");
    }

    @Test
    void testUpdateUser_UserNotFound() {
        User userToUpdate = new User();
        userToUpdate.setUserId(1);

        when(userRepository.existsById(1)).thenReturn(false);

        Integer result = userController.updateUser(userToUpdate);

        assertEquals(0, result, "用户不存在更新必须返回0");
    }
}
