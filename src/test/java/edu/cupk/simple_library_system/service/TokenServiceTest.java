package edu.cupk.simple_library_system.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "expireSeconds", 3600L);
    }

    @Test
    void testCreateToken_ReturnsNonNullToken() {
        String token = tokenService.createToken(1);

        assertNotNull(token, "创建Token不能返回null");
        assertFalse(token.isBlank(), "创建Token不能为空字符串");
    }

    @Test
    void testCreateToken_DifferentCallsReturnDifferentTokens() {
        String token1 = tokenService.createToken(1);
        String token2 = tokenService.createToken(1);

        assertNotEquals(token1, token2, "每次创建Token必须返回不同的值");
    }

    @Test
    void testVerify_ValidToken() {
        String token = tokenService.createToken(42);

        Integer userId = tokenService.verify(token);

        assertEquals(42, userId, "验证有效Token必须返回正确的userId");
    }

    @Test
    void testVerify_NullToken() {
        Integer userId = tokenService.verify(null);

        assertNull(userId, "验证null Token必须返回null");
    }

    @Test
    void testVerify_BlankToken() {
        Integer userId = tokenService.verify("   ");

        assertNull(userId, "验证空白Token必须返回null");
    }

    @Test
    void testVerify_InvalidToken() {
        Integer userId = tokenService.verify("nonexistent_token");

        assertNull(userId, "验证不存在的Token必须返回null");
    }

    @Test
    void testVerify_ExpiredToken() {
        ReflectionTestUtils.setField(tokenService, "expireSeconds", 0L);
        String token = tokenService.createToken(1);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Integer userId = tokenService.verify(token);

        assertNull(userId, "验证过期Token必须返回null");
    }

    @Test
    void testRemove_ValidToken() {
        String token = tokenService.createToken(1);
        assertNotNull(tokenService.verify(token), "删除前Token必须有效");

        tokenService.remove(token);

        assertNull(tokenService.verify(token), "删除后Token必须无效");
    }

    @Test
    void testRemove_NullToken() {
        assertDoesNotThrow(() -> tokenService.remove(null), "删除null Token不能抛异常");
    }

    @Test
    void testRemove_NonexistentToken() {
        assertDoesNotThrow(() -> tokenService.remove("nonexistent"), "删除不存在的Token不能抛异常");
    }

    @Test
    void testCreateAndVerify_MultipleUsers() {
        String token1 = tokenService.createToken(1);
        String token2 = tokenService.createToken(2);
        String token3 = tokenService.createToken(3);

        assertEquals(1, tokenService.verify(token1), "Token1必须对应userId=1");
        assertEquals(2, tokenService.verify(token2), "Token2必须对应userId=2");
        assertEquals(3, tokenService.verify(token3), "Token3必须对应userId=3");
    }
}
