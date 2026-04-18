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

@SpringBootTest
class SystemFunctionTest {

    @Autowired
    private WebConfig webConfig;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private BookInfoRepository bookInfoRepository;

    @MockitoBean
    private BorrowRepository borrowRepository;

    @Autowired
    private UserController userController;

    @Autowired
    private BookInfoController bookInfoController;

    @Autowired
    private BorrowController borrowController;

    @Test
    void testCorsConfigurationExists() {
        assertNotNull(webConfig, "WebConfig配置对象不能为null");

        CorsRegistry corsRegistry = new CorsRegistry();
        assertDoesNotThrow(() -> webConfig.addCorsMappings(corsRegistry), "CORS配置添加不应抛出异常");
    }

    @Test
    void testInterceptorConfiguration() {
        assertNotNull(webConfig, "WebConfig配置对象不能为null");

        InterceptorRegistry interceptorRegistry = new InterceptorRegistry();
        assertDoesNotThrow(() -> webConfig.addInterceptors(interceptorRegistry), "拦截器配置添加不应抛出异常");
    }

    @Test
    void testResourceHandlerConfiguration() {
        assertNotNull(webConfig, "WebConfig配置对象不能为null");

        ResourceHandlerRegistry resourceHandlerRegistry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration resourceHandlerRegistration = mock(ResourceHandlerRegistration.class);

        when(resourceHandlerRegistry.addResourceHandler(any(String[].class))).thenReturn(resourceHandlerRegistration);
        when(resourceHandlerRegistration.addResourceLocations(any(String[].class))).thenReturn(resourceHandlerRegistration);

        assertDoesNotThrow(() -> webConfig.addResourceHandlers(resourceHandlerRegistry), "资源处理器配置添加不应抛出异常");

        verify(resourceHandlerRegistry, times(1)).addResourceHandler(any(String[].class));
    }

    @Test
    void testGlobalExceptionHandlerExists() {
        assertNotNull(globalExceptionHandler, "全局异常处理器不能为null");
    }

    @Test
    void testHandleException() {
        Exception testException = new Exception("测试异常消息");

        ApiResponse<Void> response = globalExceptionHandler.handleException(testException);

        assertEquals(420, response.getStatus(), "异常处理状态码必须是420");
        assertEquals("测试异常消息", response.getMessage(), "异常消息必须是'测试异常消息'");
    }

    @Test
    void testHandleExceptionWithNullMessage() {
        Exception testException = new Exception();

        ApiResponse<Void> response = globalExceptionHandler.handleException(testException);

        assertEquals(420, response.getStatus(), "空消息异常处理状态码必须是420");
        assertEquals("操作失败", response.getMessage(), "空消息异常默认消息必须是'操作失败'");
    }

    @Test
    void testComprehensiveStatistics() {
        when(userRepository.count()).thenReturn(50L);
        when(bookInfoRepository.count()).thenReturn(300L);
        when(borrowRepository.count()).thenReturn(150L);

        long userCount = userController.getCount();
        long bookCount = bookInfoController.getCount();
        long borrowCount = borrowController.getCount();

        assertEquals(50L, userCount, "综合统计用户数量必须是50");
        assertEquals(300L, bookCount, "综合统计图书数量必须是300");
        assertEquals(150L, borrowCount, "综合统计借阅数量必须是150");
    }

    @Test
    void testApiResponseSuccess() {
        String testData = "测试数据";
        ApiResponse<String> response = ApiResponse.ok("操作成功", testData);

        assertEquals(200, response.getStatus(), "成功响应状态码必须是200");
        assertEquals("操作成功", response.getMessage(), "成功响应消息必须是'操作成功'");
        assertEquals(testData, response.getData(), "成功响应数据必须是'测试数据'");
        assertTrue(response.getTimestamp() > 0, "成功响应时间戳必须大于0");
    }

    @Test
    void testApiResponseFail() {
        ApiResponse<Void> response = ApiResponse.fail("操作失败");

        assertEquals(420, response.getStatus(), "失败响应状态码必须是420");
        assertEquals("操作失败", response.getMessage(), "失败响应消息必须是'操作失败'");
        assertNull(response.getData(), "失败响应数据必须为null");
    }

    @Test
    void testPageResponseSuccess() {
        java.util.List<String> dataList = java.util.Arrays.asList("数据1", "数据2", "数据3");
        PageResponse<String> response = PageResponse.success(100L, dataList);

        assertEquals(0, response.getCode(), "分页成功响应代码必须是0");
        assertEquals("success", response.getMessage(), "分页成功响应消息必须是'success'");
        assertEquals(100L, response.getCount(), "分页成功响应总数必须是100");
        assertEquals(3, response.getData().size(), "分页成功响应数据列表大小必须是3");
    }

    @Test
    void testPageResponseEmptyData() {
        PageResponse<String> response = PageResponse.success(0L, java.util.Collections.emptyList());

        assertEquals(0, response.getCode(), "空数据分页响应代码必须是0");
        assertEquals(0L, response.getCount(), "空数据分页响应总数必须是0");
        assertTrue(response.getData().isEmpty(), "空数据分页响应数据列表必须为空");
    }

    @Test
    void testWebConfigBeanExists() {
        assertNotNull(webConfig, "WebConfig Bean必须存在");
    }

    @Test
    void testGlobalExceptionHandlerBeanExists() {
        assertNotNull(globalExceptionHandler, "GlobalExceptionHandler Bean必须存在");
    }
}
