package edu.cupk.simple_library_system.controller;

import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.dto.BookInfoView;
import edu.cupk.simple_library_system.entity.BookInfo;
import edu.cupk.simple_library_system.entity.BookType;
import edu.cupk.simple_library_system.repository.BookInfoRepository;
import edu.cupk.simple_library_system.repository.BookTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// 图书信息管理控制器测试类
// 测试BookInfoController中的所有图书信息管理相关功能
// 包括：图书信息的增删改查、分页查询、多条件搜索、图书状态管理等
@SpringBootTest
class BookInfoControllerTest {

    // 自动注入被测试的控制器实例
    @Autowired
    private BookInfoController bookInfoController;

    // 模拟图书信息数据访问层，用于隔离数据库依赖
    @MockitoBean
    private BookInfoRepository bookInfoRepository;

    // 模拟图书类型数据访问层，用于隔离数据库依赖
    @MockitoBean
    private BookTypeRepository bookTypeRepository;

    // 测试用的图书信息实体对象
    private BookInfo testBookInfo;

    // 测试用的图书类型实体对象
    private BookType testBookType;

    // 每个测试方法执行前的初始化操作
    // 创建测试用的图书信息和图书类型数据
    @BeforeEach
    void setUp() {
        // 初始化测试图书类型数据
        testBookType = new BookType();
        testBookType.setBookTypeId(1);
        testBookType.setBookTypeName("计算机科学");
        testBookType.setBookTypeDesc("计算机相关书籍");

        // 初始化测试图书信息数据
        testBookInfo = new BookInfo();
        testBookInfo.setBookId(1);
        testBookInfo.setBookName("Java编程思想");
        testBookInfo.setBookAuthor("Bruce Eckel");
        testBookInfo.setBookPrice(new BigDecimal("108.00"));
        testBookInfo.setBookTypeId(1);
        testBookInfo.setBookDesc("经典的Java编程书籍");
        testBookInfo.setIsBorrowed((byte) 0);
        testBookInfo.setBookImg("/uploads/java.jpg");
    }

    // 测试获取图书总数
    // 验证点：
    // 1. 返回的图书数量与模拟值一致
    @Test
    void testGetCount() {
        // 模拟图书数量为100
        when(bookInfoRepository.count()).thenReturn(100L);

        long count = bookInfoController.getCount();

        assertEquals(100L, count);
    }

    // 测试查询所有图书信息
    // 验证点：
    // 1. 返回图书信息视图列表
    // 2. 图书类型名称正确转换
    @Test
    void testQueryBookInfos() {
        List<BookInfo> bookInfos = new ArrayList<>();
        bookInfos.add(testBookInfo);

        // 模拟查询所有图书
        when(bookInfoRepository.findAll()).thenReturn(bookInfos);
        // 模拟查询图书类型
        when(bookTypeRepository.findById(1)).thenReturn(Optional.of(testBookType));

        List<BookInfoView> result = bookInfoController.queryBookInfos();

        assertEquals(1, result.size());
        assertEquals("Java编程思想", result.get(0).getBookName());
        assertEquals("计算机科学", result.get(0).getBookTypeName());
    }

    // 测试分页查询图书（不带过滤条件）
    // 验证点：
    // 1. 返回的总记录数正确
    // 2. 返回的数据列表大小正确
    // 3. 图书类型名称正确转换
    @Test
    void testQueryBookInfosByPage() {
        List<BookInfo> bookInfos = new ArrayList<>();
        bookInfos.add(testBookInfo);
        Page<BookInfo> page = new PageImpl<>(bookInfos);

        // 模拟分页查询
        when(bookInfoRepository.search(null, null, null, PageRequest.of(0, 10))).thenReturn(page);
        // 模拟查询图书类型
        when(bookTypeRepository.findById(1)).thenReturn(Optional.of(testBookType));

        PageResponse<BookInfoView> result = bookInfoController.queryBookInfosByPage(1, 10, null, null, null);

        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().size());
        assertEquals("计算机科学", result.getData().get(0).getBookTypeName());
    }

    // 测试分页查询图书（带书名过滤条件）
    // 验证点：
    // 1. 返回的总记录数正确
    // 2. 搜索条件正确传递
    @Test
    void testQueryBookInfosByPage_WithBookName() {
        List<BookInfo> bookInfos = new ArrayList<>();
        bookInfos.add(testBookInfo);
        Page<BookInfo> page = new PageImpl<>(bookInfos);

        // 模拟按书名搜索
        when(bookInfoRepository.search("Java", null, null, PageRequest.of(0, 10))).thenReturn(page);
        when(bookTypeRepository.findById(1)).thenReturn(Optional.of(testBookType));

        PageResponse<BookInfoView> result = bookInfoController.queryBookInfosByPage(1, 10, "Java", null, null);

        assertEquals(1, result.getCount());
        assertEquals("Java编程思想", result.getData().get(0).getBookName());
    }

    // 测试分页查询图书（带作者过滤条件）
    // 验证点：
    // 1. 返回的总记录数正确
    // 2. 搜索条件正确传递
    @Test
    void testQueryBookInfosByPage_WithBookAuthor() {
        List<BookInfo> bookInfos = new ArrayList<>();
        bookInfos.add(testBookInfo);
        Page<BookInfo> page = new PageImpl<>(bookInfos);

        // 模拟按作者搜索
        when(bookInfoRepository.search(null, "Bruce", null, PageRequest.of(0, 10))).thenReturn(page);
        when(bookTypeRepository.findById(1)).thenReturn(Optional.of(testBookType));

        PageResponse<BookInfoView> result = bookInfoController.queryBookInfosByPage(1, 10, null, "Bruce", null);

        assertEquals(1, result.getCount());
        assertEquals("Bruce Eckel", result.getData().get(0).getBookAuthor());
    }

    // 测试分页查询图书（带类型过滤条件）
    // 验证点：
    // 1. 返回的总记录数正确
    // 2. 搜索条件正确传递
    @Test
    void testQueryBookInfosByPage_WithBookTypeId() {
        List<BookInfo> bookInfos = new ArrayList<>();
        bookInfos.add(testBookInfo);
        Page<BookInfo> page = new PageImpl<>(bookInfos);

        // 模拟按类型搜索
        when(bookInfoRepository.search(null, null, 1, PageRequest.of(0, 10))).thenReturn(page);
        when(bookTypeRepository.findById(1)).thenReturn(Optional.of(testBookType));

        PageResponse<BookInfoView> result = bookInfoController.queryBookInfosByPage(1, 10, null, null, 1);

        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().get(0).getBookTypeId());
    }

    // 测试分页查询图书（带多个过滤条件）
    // 验证点：
    // 1. 返回的总记录数正确
    // 2. 多个搜索条件同时生效
    @Test
    void testQueryBookInfosByPage_WithMultipleConditions() {
        List<BookInfo> bookInfos = new ArrayList<>();
        bookInfos.add(testBookInfo);
        Page<BookInfo> page = new PageImpl<>(bookInfos);

        // 模拟多条件搜索
        when(bookInfoRepository.search("Java", "Bruce", 1, PageRequest.of(0, 10))).thenReturn(page);
        when(bookTypeRepository.findById(1)).thenReturn(Optional.of(testBookType));

        PageResponse<BookInfoView> result = bookInfoController.queryBookInfosByPage(1, 10, "Java", "Bruce", 1);

        assertEquals(1, result.getCount());
    }

    // 测试添加图书信息成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 未设置借阅状态时，默认为未借阅（0）
    @Test
    void testAddBookInfo_Success() {
        BookInfo newBook = new BookInfo();
        newBook.setBookName("Effective Java");
        newBook.setBookAuthor("Joshua Bloch");
        newBook.setBookPrice(new BigDecimal("85.00"));
        newBook.setBookTypeId(1);
        newBook.setBookDesc("Java最佳实践");
        // 不设置isBorrowed，测试默认值

        // 模拟保存图书
        when(bookInfoRepository.save(any(BookInfo.class))).thenReturn(newBook);

        Integer result = bookInfoController.addBookInfo(newBook);

        assertEquals(1, result);
    }

    // 测试添加图书信息（已设置借阅状态）
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 保留设置的借阅状态
    @Test
    void testAddBookInfo_WithBorrowedStatus() {
        BookInfo newBook = new BookInfo();
        newBook.setBookName("Effective Java");
        newBook.setBookAuthor("Joshua Bloch");
        newBook.setBookPrice(new BigDecimal("85.00"));
        newBook.setBookTypeId(1);
        newBook.setBookDesc("Java最佳实践");
        newBook.setIsBorrowed((byte) 1);

        // 模拟保存图书
        when(bookInfoRepository.save(any(BookInfo.class))).thenReturn(newBook);

        Integer result = bookInfoController.addBookInfo(newBook);

        assertEquals(1, result);
    }

    // 测试删除图书成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 图书存在且未被借阅
    @Test
    void testDeleteBookInfo_Success() {
        BookInfo bookToDelete = new BookInfo();
        bookToDelete.setBookId(1);

        // 模拟图书存在且未被借阅
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));
        // 模拟删除图书
        doNothing().when(bookInfoRepository).deleteById(1);

        Integer result = bookInfoController.deleteBookInfo(bookToDelete);

        assertEquals(1, result);
        verify(bookInfoRepository, times(1)).deleteById(1);
    }

    // 测试删除图书失败场景 - 图书ID为空
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testDeleteBookInfo_NullId() {
        BookInfo bookToDelete = new BookInfo();
        bookToDelete.setBookId(null);

        Integer result = bookInfoController.deleteBookInfo(bookToDelete);

        assertEquals(0, result);
    }

    // 测试删除图书失败场景 - 图书不存在
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testDeleteBookInfo_NotFound() {
        BookInfo bookToDelete = new BookInfo();
        bookToDelete.setBookId(999);

        // 模拟图书不存在
        when(bookInfoRepository.findById(999)).thenReturn(Optional.empty());

        Integer result = bookInfoController.deleteBookInfo(bookToDelete);

        assertEquals(0, result);
    }

    // 测试删除图书失败场景 - 图书已被借阅
    // 验证点：
    // 1. 返回值为0（表示失败）
    // 2. 已被借阅的图书不能删除
    @Test
    void testDeleteBookInfo_AlreadyBorrowed() {
        BookInfo bookToDelete = new BookInfo();
        bookToDelete.setBookId(1);

        // 创建已被借阅的图书
        BookInfo borrowedBook = new BookInfo();
        borrowedBook.setBookId(1);
        borrowedBook.setBookName("Java编程思想");
        borrowedBook.setIsBorrowed((byte) 1);

        // 模拟图书存在但已被借阅
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(borrowedBook));

        Integer result = bookInfoController.deleteBookInfo(bookToDelete);

        assertEquals(0, result);
        verify(bookInfoRepository, never()).deleteById(anyInt());
    }

    // 测试批量删除图书
    // 验证点：
    // 1. 返回成功删除的图书数量
    // 2. 只删除未被借阅的图书
    @Test
    void testDeleteBookInfos() {
        List<BookInfo> booksToDelete = new ArrayList<>();

        // 第一条：未借阅，可以删除
        BookInfo book1 = new BookInfo();
        book1.setBookId(1);
        book1.setIsBorrowed((byte) 0);
        booksToDelete.add(book1);

        // 第二条：已借阅，不能删除
        BookInfo book2 = new BookInfo();
        book2.setBookId(2);
        book2.setIsBorrowed((byte) 1);
        booksToDelete.add(book2);

        // 第三条：未借阅，可以删除
        BookInfo book3 = new BookInfo();
        book3.setBookId(3);
        book3.setIsBorrowed((byte) 0);
        booksToDelete.add(book3);

        // 模拟查询图书状态
        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(book1));
        when(bookInfoRepository.findById(2)).thenReturn(Optional.of(book2));
        when(bookInfoRepository.findById(3)).thenReturn(Optional.of(book3));

        // 模拟删除操作
        doNothing().when(bookInfoRepository).deleteById(anyInt());

        Integer result = bookInfoController.deleteBookInfos(booksToDelete);

        assertEquals(2, result); // 只有2条未借阅的图书被删除
        verify(bookInfoRepository, times(1)).deleteById(1);
        verify(bookInfoRepository, never()).deleteById(2); // 已借阅的不删除
        verify(bookInfoRepository, times(1)).deleteById(3);
    }

    // 测试批量删除图书 - 包含ID为空的图书
    // 验证点：
    // 1. 跳过ID为空的图书
    // 2. 只删除有效的图书
    @Test
    void testDeleteBookInfos_WithNullId() {
        List<BookInfo> booksToDelete = new ArrayList<>();

        // 第一条：ID为空，跳过
        BookInfo book1 = new BookInfo();
        book1.setBookId(null);
        booksToDelete.add(book1);

        // 第二条：有效，可以删除
        BookInfo book2 = new BookInfo();
        book2.setBookId(1);
        book2.setIsBorrowed((byte) 0);
        booksToDelete.add(book2);

        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(book2));
        doNothing().when(bookInfoRepository).deleteById(1);

        Integer result = bookInfoController.deleteBookInfos(booksToDelete);

        assertEquals(1, result); // 只有1条有效图书被删除
    }

    // 测试更新图书信息成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 图书存在，可以正常更新
    @Test
    void testUpdateBookInfo_Success() {
        BookInfo bookToUpdate = new BookInfo();
        bookToUpdate.setBookId(1);
        bookToUpdate.setBookName("Java编程思想（第5版）");
        bookToUpdate.setBookAuthor("Bruce Eckel");
        bookToUpdate.setBookPrice(new BigDecimal("128.00"));
        bookToUpdate.setBookTypeId(1);
        bookToUpdate.setBookDesc("更新后的描述");

        // 模拟图书存在
        when(bookInfoRepository.existsById(1)).thenReturn(true);
        // 模拟保存图书
        when(bookInfoRepository.save(any(BookInfo.class))).thenReturn(bookToUpdate);

        Integer result = bookInfoController.updateBookInfo(bookToUpdate);

        assertEquals(1, result);
    }

    // 测试更新图书失败场景 - 图书ID为空
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testUpdateBookInfo_NullId() {
        BookInfo bookToUpdate = new BookInfo();
        bookToUpdate.setBookId(null);

        Integer result = bookInfoController.updateBookInfo(bookToUpdate);

        assertEquals(0, result);
    }

    // 测试更新图书失败场景 - 图书不存在
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testUpdateBookInfo_NotFound() {
        BookInfo bookToUpdate = new BookInfo();
        bookToUpdate.setBookId(999);

        // 模拟图书不存在
        when(bookInfoRepository.existsById(999)).thenReturn(false);

        Integer result = bookInfoController.updateBookInfo(bookToUpdate);

        assertEquals(0, result);
    }
}