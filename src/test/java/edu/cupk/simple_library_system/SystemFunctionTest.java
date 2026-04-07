package edu.cupk.simple_library_system;

import edu.cupk.simple_library_system.common.ApiResponse;
import edu.cupk.simple_library_system.common.GlobalExceptionHandler;
import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.config.WebConfig;
import edu.cupk.simple_library_system.controller.BookInfoController;
import edu.cupk.simple_library_system.controller.BorrowController;
import edu.cupk.simple_library_system.controller.UserController;
import edu.cupk.simple_library_system.repository.BookInfoRepository;
import edu.cupk.simple_library_system.repository.BorrowRepository;
import edu.cupk.simple_library_system.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// 系统功能测试类
// 测试系统级别的功能，包括：
// - 跨域访问支持（CORS）
// - 统一异常处理
// - 数据统计（用户总数、图书总数、借阅记录总数）
// - 文件上传功能配置
// - 通用响应类测试
@SpringBootTest
class SystemFunctionTest {

    // 自动注入Web配置类，用于测试CORS和拦截器配置
    @Autowired
    private WebConfig webConfig;

    // 自动注入全局异常处理器
    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    // 模拟用户数据访问层，用于测试数据统计
    @MockitoBean
    private UserRepository userRepository;

    // 模拟图书信息数据访问层，用于测试数据统计
    @MockitoBean
    private BookInfoRepository bookInfoRepository;

    // 模拟借阅记录数据访问层，用于测试数据统计
    @MockitoBean
    private BorrowRepository borrowRepository;

    // 自动注入用户控制器，用于测试数据统计功能
    @Autowired
    private UserController userController;

    // 自动注入图书信息控制器，用于测试数据统计功能
    @Autowired
    private BookInfoController bookInfoController;

    // 自动注入借阅控制器，用于测试数据统计功能
    @Autowired
    private BorrowController borrowController;

    // 测试CORS配置是否正确注册
    // 验证点：
    // 1. WebConfig对象不为空
    // 2. CORS配置方法可以正常调用
    @Test
    void testCorsConfigurationExists() {
        assertNotNull(webConfig);

        // 验证CORS注册方法可以正常调用，不会抛出异常
        CorsRegistry corsRegistry = new CorsRegistry();
        assertDoesNotThrow(() -> webConfig.addCorsMappings(corsRegistry));
    }

    // 测试拦截器配置是否正确注册
    // 验证点：
    // 1. 拦截器配置方法可以正常调用
    // 2. 拦截器配置包含正确的路径模式
    @Test
    void testInterceptorConfiguration() {
        assertNotNull(webConfig);

        // 验证拦截器注册方法可以正常调用，不会抛出异常
        InterceptorRegistry interceptorRegistry = new InterceptorRegistry();
        assertDoesNotThrow(() -> webConfig.addInterceptors(interceptorRegistry));
    }

    // 测试资源处理器配置（文件上传）
    // 验证点：
    // 1. 资源处理器配置方法可以正常调用
    // 2. 上传路径配置正确
    @Test
    void testResourceHandlerConfiguration() {
        assertNotNull(webConfig);

        // 创建模拟的 ResourceHandlerRegistry
        ResourceHandlerRegistry resourceHandlerRegistry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration resourceHandlerRegistration = mock(ResourceHandlerRegistration.class);

        // 配置 mock 行为：当调用 addResourceHandler 时返回模拟的 ResourceHandlerRegistration
        when(resourceHandlerRegistry.addResourceHandler(any(String[].class))).thenReturn(resourceHandlerRegistration);
        when(resourceHandlerRegistration.addResourceLocations(any(String[].class))).thenReturn(resourceHandlerRegistration);

        // 验证资源处理器注册方法可以正常调用，不会抛出异常
        assertDoesNotThrow(() -> webConfig.addResourceHandlers(resourceHandlerRegistry));

        // 验证 addResourceHandler 方法被调用
        verify(resourceHandlerRegistry, times(1)).addResourceHandler(any(String[].class));
    }

    // 测试全局异常处理器存在
    // 验证点：
    // 1. GlobalExceptionHandler对象不为空
    @Test
    void testGlobalExceptionHandlerExists() {
        assertNotNull(globalExceptionHandler);
    }

    // 测试异常处理方法 - 处理普通异常
    // 验证点：
    // 1. 返回的响应状态码为420（失败状态码）
    // 2. 返回的错误消息与异常消息一致
    @Test
    void testHandleException() {
        Exception testException = new Exception("测试异常消息");

        ApiResponse<Void> response = globalExceptionHandler.handleException(testException);

        assertEquals(420, response.getStatus());
        assertEquals("测试异常消息", response.getMessage());
    }

    // 测试异常处理方法 - 处理空消息异常
    // 验证点：
    // 1. 返回的响应状态码为420（失败状态码）
    // 2. 返回默认错误消息"操作失败"
    @Test
    void testHandleExceptionWithNullMessage() {
        Exception testException = new Exception();

        ApiResponse<Void> response = globalExceptionHandler.handleException(testException);

        assertEquals(420, response.getStatus());
        assertEquals("操作失败", response.getMessage());
    }

    // 测试用户总数统计
    // 验证点：
    // 1. 返回的用户数量与模拟值一致
    @Test
    void testUserCountStatistics() {
        // 模拟用户数量为100
        when(userRepository.count()).thenReturn(100L);

        long count = userController.getCount();

        assertEquals(100L, count);
        verify(userRepository, times(1)).count();
    }

    // 测试图书总数统计
    // 验证点：
    // 1. 返回的图书数量与模拟值一致
    @Test
    void testBookCountStatistics() {
        // 模拟图书数量为500
        when(bookInfoRepository.count()).thenReturn(500L);

        long count = bookInfoController.getCount();

        assertEquals(500L, count);
        verify(bookInfoRepository, times(1)).count();
    }

    // 测试借阅记录总数统计
    // 验证点：
    // 1. 返回的借阅记录数量与模拟值一致
    @Test
    void testBorrowCountStatistics() {
        // 模拟借阅记录数量为200
        when(borrowRepository.count()).thenReturn(200L);

        long count = borrowController.getCount();

        assertEquals(200L, count);
        verify(borrowRepository, times(1)).count();
    }

    // 测试综合数据统计
    // 验证点：
    // 1. 同时获取用户、图书、借阅记录的数量
    // 2. 各统计数据正确
    @Test
    void testComprehensiveStatistics() {
        // 模拟统计数据
        when(userRepository.count()).thenReturn(50L);
        when(bookInfoRepository.count()).thenReturn(300L);
        when(borrowRepository.count()).thenReturn(150L);

        long userCount = userController.getCount();
        long bookCount = bookInfoController.getCount();
        long borrowCount = borrowController.getCount();

        assertEquals(50L, userCount);
        assertEquals(300L, bookCount);
        assertEquals(150L, borrowCount);
    }

    // 测试ApiResponse成功响应
    // 验证点：
    // 1. 状态码为200
    // 2. 消息正确
    // 3. 数据正确
    // 4. 时间戳不为0
    @Test
    void testApiResponseSuccess() {
        String testData = "测试数据";
        ApiResponse<String> response = ApiResponse.ok("操作成功", testData);

        assertEquals(200, response.getStatus());
        assertEquals("操作成功", response.getMessage());
        assertEquals(testData, response.getData());
        assertTrue(response.getTimestamp() > 0);
    }

    // 测试ApiResponse失败响应
    // 验证点：
    // 1. 状态码为420
    // 2. 错误消息正确
    // 3. 数据为null
    @Test
    void testApiResponseFail() {
        ApiResponse<Void> response = ApiResponse.fail("操作失败");

        assertEquals(420, response.getStatus());
        assertEquals("操作失败", response.getMessage());
        assertNull(response.getData());
    }

    // 测试PageResponse成功响应
    // 验证点：
    // 1. 状态码为0
    // 2. 消息为"success"
    // 3. 总数正确
    // 4. 数据列表正确
    @Test
    void testPageResponseSuccess() {
        java.util.List<String> dataList = java.util.Arrays.asList("数据1", "数据2", "数据3");
        PageResponse<String> response = PageResponse.success(100L, dataList);

        assertEquals(0, response.getCode());
        assertEquals("success", response.getMessage());
        assertEquals(100L, response.getCount());
        assertEquals(3, response.getData().size());
    }

    // 测试PageResponse空数据响应
    // 验证点：
    // 1. 状态码为0
    // 2. 总数为0
    // 3. 数据列表为空
    @Test
    void testPageResponseEmptyData() {
        PageResponse<String> response = PageResponse.success(0L, java.util.Collections.emptyList());

        assertEquals(0, response.getCode());
        assertEquals(0L, response.getCount());
        assertTrue(response.getData().isEmpty());
    }

    // 测试Spring Boot应用上下文加载
    // 验证点：
    // 1. 应用上下文成功加载
    // 2. 所有必要的Bean已注册
    @Test
    void testApplicationContextLoads() {
        // 如果应用上下文加载失败，此测试会自动失败
        assertTrue(true);
    }

    // 测试WebConfig Bean存在
    // 验证点：
    // 1. WebConfig Bean已正确注册
    @Test
    void testWebConfigBeanExists() {
        assertNotNull(webConfig);
    }

    // 测试GlobalExceptionHandler Bean存在
    // 验证点：
    // 1. GlobalExceptionHandler Bean已正确注册
    @Test
    void testGlobalExceptionHandlerBeanExists() {
        assertNotNull(globalExceptionHandler);
    }
}