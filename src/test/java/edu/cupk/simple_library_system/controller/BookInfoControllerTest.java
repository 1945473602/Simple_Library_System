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

@SpringBootTest
class BookInfoControllerTest {

    @Autowired
    private BookInfoController bookInfoController;

    @MockitoBean
    private BookInfoRepository bookInfoRepository;

    @MockitoBean
    private BookTypeRepository bookTypeRepository;

    private BookInfo testBookInfo;
    private BookType testBookType;

    @BeforeEach
    void setUp() {
        testBookType = new BookType();
        testBookType.setBookTypeId(1);
        testBookType.setBookTypeName("计算机科学");
        testBookType.setBookTypeDesc("计算机相关书籍");

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

    @Test
    void testGetCount() {
        when(bookInfoRepository.count()).thenReturn(100L);

        long count = bookInfoController.getCount();

        assertEquals(100L, count, "图书数量必须是100");
    }

    @Test
    void testQueryBookInfos() {
        List<BookInfo> bookInfos = new ArrayList<>();
        bookInfos.add(testBookInfo);

        when(bookInfoRepository.findAll()).thenReturn(bookInfos);
        when(bookTypeRepository.findById(1)).thenReturn(Optional.of(testBookType));

        List<BookInfoView> result = bookInfoController.queryBookInfos();

        assertEquals(1, result.size(), "图书列表大小必须是1");
        assertEquals("Java编程思想", result.get(0).getBookName(), "图书名称必须是'Java编程思想'");
        assertEquals("计算机科学", result.get(0).getBookTypeName(), "图书类型名称必须是'计算机科学'");
    }

    @Test
    void testQueryBookInfosByPage() {
        List<BookInfo> bookInfos = new ArrayList<>();
        bookInfos.add(testBookInfo);
        Page<BookInfo> page = new PageImpl<>(bookInfos);

        when(bookInfoRepository.search(null, null, null, PageRequest.of(0, 10))).thenReturn(page);
        when(bookTypeRepository.findById(1)).thenReturn(Optional.of(testBookType));

        PageResponse<BookInfoView> result = bookInfoController.queryBookInfosByPage(1, 10, null, null, null);

        assertEquals(1, result.getCount(), "图书总数必须是1");
        assertEquals(1, result.getData().size(), "图书列表大小必须是1");
        assertEquals("计算机科学", result.getData().get(0).getBookTypeName(), "图书类型名称必须是'计算机科学'");
    }

    @Test
    void testQueryBookInfosByPage_WithBookName() {
        List<BookInfo> bookInfos = new ArrayList<>();
        bookInfos.add(testBookInfo);
        Page<BookInfo> page = new PageImpl<>(bookInfos);

        when(bookInfoRepository.search("Java", null, null, PageRequest.of(0, 10))).thenReturn(page);
        when(bookTypeRepository.findById(1)).thenReturn(Optional.of(testBookType));

        PageResponse<BookInfoView> result = bookInfoController.queryBookInfosByPage(1, 10, "Java", null, null);

        assertEquals(1, result.getCount(), "过滤后图书总数必须是1");
        assertEquals("Java编程思想", result.getData().get(0).getBookName(), "图书名称必须是'Java编程思想'");
    }

    @Test
    void testQueryBookInfosByPage_WithBookAuthor() {
        List<BookInfo> bookInfos = new ArrayList<>();
        bookInfos.add(testBookInfo);
        Page<BookInfo> page = new PageImpl<>(bookInfos);

        when(bookInfoRepository.search(null, "Bruce", null, PageRequest.of(0, 10))).thenReturn(page);
        when(bookTypeRepository.findById(1)).thenReturn(Optional.of(testBookType));

        PageResponse<BookInfoView> result = bookInfoController.queryBookInfosByPage(1, 10, null, "Bruce", null);

        assertEquals(1, result.getCount(), "过滤后图书总数必须是1");
        assertEquals("Bruce Eckel", result.getData().get(0).getBookAuthor(), "图书作者必须是'Bruce Eckel'");
    }

    @Test
    void testQueryBookInfosByPage_WithBookTypeId() {
        List<BookInfo> bookInfos = new ArrayList<>();
        bookInfos.add(testBookInfo);
        Page<BookInfo> page = new PageImpl<>(bookInfos);

        when(bookInfoRepository.search(null, null, 1, PageRequest.of(0, 10))).thenReturn(page);
        when(bookTypeRepository.findById(1)).thenReturn(Optional.of(testBookType));

        PageResponse<BookInfoView> result = bookInfoController.queryBookInfosByPage(1, 10, null, null, 1);

        assertEquals(1, result.getCount(), "过滤后图书总数必须是1");
        assertEquals(1, result.getData().get(0).getBookTypeId(), "图书类型ID必须是1");
    }

    @Test
    void testQueryBookInfosByPage_WithMultipleConditions() {
        List<BookInfo> bookInfos = new ArrayList<>();
        bookInfos.add(testBookInfo);
        Page<BookInfo> page = new PageImpl<>(bookInfos);

        when(bookInfoRepository.search("Java", "Bruce", 1, PageRequest.of(0, 10))).thenReturn(page);
        when(bookTypeRepository.findById(1)).thenReturn(Optional.of(testBookType));

        PageResponse<BookInfoView> result = bookInfoController.queryBookInfosByPage(1, 10, "Java", "Bruce", 1);

        assertEquals(1, result.getCount(), "多条件过滤后图书总数必须是1");
    }

    @Test
    void testAddBookInfo_Success() {
        BookInfo newBook = new BookInfo();
        newBook.setBookName("Effective Java");
        newBook.setBookAuthor("Joshua Bloch");
        newBook.setBookPrice(new BigDecimal("85.00"));
        newBook.setBookTypeId(1);
        newBook.setBookDesc("Java最佳实践");

        when(bookInfoRepository.save(any(BookInfo.class))).thenReturn(newBook);

        Integer result = bookInfoController.addBookInfo(newBook);

        assertEquals(1, result, "添加图书成功必须返回1");
    }

    @Test
    void testAddBookInfo_WithBorrowedStatus() {
        BookInfo newBook = new BookInfo();
        newBook.setBookName("Effective Java");
        newBook.setBookAuthor("Joshua Bloch");
        newBook.setBookPrice(new BigDecimal("85.00"));
        newBook.setBookTypeId(1);
        newBook.setBookDesc("Java最佳实践");
        newBook.setIsBorrowed((byte) 1);

        when(bookInfoRepository.save(any(BookInfo.class))).thenReturn(newBook);

        Integer result = bookInfoController.addBookInfo(newBook);

        assertEquals(1, result, "添加已借阅状态图书成功必须返回1");
    }

    @Test
    void testAddBookInfo_IsBorrowedDefaultToZero() {
        BookInfo newBook = new BookInfo();
        newBook.setBookName("Spring实战");
        newBook.setBookAuthor("Craig Walls");

        when(bookInfoRepository.save(any(BookInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Integer result = bookInfoController.addBookInfo(newBook);

        assertEquals(1, result, "添加图书成功必须返回1");
        assertEquals((byte) 0, newBook.getIsBorrowed(), "isBorrowed为null时必须默认设为0");
    }

    @Test
    void testDeleteBookInfo_Success() {
        BookInfo bookToDelete = new BookInfo();
        bookToDelete.setBookId(1);

        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(testBookInfo));
        doNothing().when(bookInfoRepository).deleteById(1);

        Integer result = bookInfoController.deleteBookInfo(bookToDelete);

        assertEquals(1, result, "删除图书成功必须返回1");
        verify(bookInfoRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteBookInfo_NullId() {
        BookInfo bookToDelete = new BookInfo();
        bookToDelete.setBookId(null);

        Integer result = bookInfoController.deleteBookInfo(bookToDelete);

        assertEquals(0, result, "删除空ID图书必须返回0");
    }

    @Test
    void testDeleteBookInfo_NotFound() {
        BookInfo bookToDelete = new BookInfo();
        bookToDelete.setBookId(999);

        when(bookInfoRepository.findById(999)).thenReturn(Optional.empty());

        Integer result = bookInfoController.deleteBookInfo(bookToDelete);

        assertEquals(0, result, "删除不存在图书必须返回0");
    }

    @Test
    void testDeleteBookInfo_AlreadyBorrowed() {
        BookInfo bookToDelete = new BookInfo();
        bookToDelete.setBookId(1);

        BookInfo borrowedBook = new BookInfo();
        borrowedBook.setBookId(1);
        borrowedBook.setBookName("Java编程思想");
        borrowedBook.setIsBorrowed((byte) 1);

        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(borrowedBook));

        Integer result = bookInfoController.deleteBookInfo(bookToDelete);

        assertEquals(0, result, "删除已借阅图书必须返回0");
        verify(bookInfoRepository, never()).deleteById(anyInt());
    }

    @Test
    void testDeleteBookInfos() {
        List<BookInfo> booksToDelete = new ArrayList<>();

        BookInfo book1 = new BookInfo();
        book1.setBookId(1);
        book1.setIsBorrowed((byte) 0);
        booksToDelete.add(book1);

        BookInfo book2 = new BookInfo();
        book2.setBookId(2);
        book2.setIsBorrowed((byte) 1);
        booksToDelete.add(book2);

        BookInfo book3 = new BookInfo();
        book3.setBookId(3);
        book3.setIsBorrowed((byte) 0);
        booksToDelete.add(book3);

        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(book1));
        when(bookInfoRepository.findById(2)).thenReturn(Optional.of(book2));
        when(bookInfoRepository.findById(3)).thenReturn(Optional.of(book3));
        doNothing().when(bookInfoRepository).deleteById(anyInt());

        Integer result = bookInfoController.deleteBookInfos(booksToDelete);

        assertEquals(2, result, "批量删除图书成功数量必须是2（两本未借阅，一本已借阅）");
        verify(bookInfoRepository, times(1)).deleteById(1);
        verify(bookInfoRepository, never()).deleteById(2);
        verify(bookInfoRepository, times(1)).deleteById(3);
    }

    @Test
    void testDeleteBookInfos_WithNullId() {
        List<BookInfo> booksToDelete = new ArrayList<>();

        BookInfo book1 = new BookInfo();
        book1.setBookId(null);
        booksToDelete.add(book1);

        BookInfo book2 = new BookInfo();
        book2.setBookId(1);
        book2.setIsBorrowed((byte) 0);
        booksToDelete.add(book2);

        when(bookInfoRepository.findById(1)).thenReturn(Optional.of(book2));
        doNothing().when(bookInfoRepository).deleteById(1);

        Integer result = bookInfoController.deleteBookInfos(booksToDelete);

        assertEquals(1, result, "批量删除图书（含空ID）成功数量必须是1");
    }

    @Test
    void testUpdateBookInfo_Success() {
        BookInfo bookToUpdate = new BookInfo();
        bookToUpdate.setBookId(1);
        bookToUpdate.setBookName("Java编程思想（第5版）");
        bookToUpdate.setBookAuthor("Bruce Eckel");
        bookToUpdate.setBookPrice(new BigDecimal("128.00"));
        bookToUpdate.setBookTypeId(1);
        bookToUpdate.setBookDesc("更新后的描述");

        when(bookInfoRepository.existsById(1)).thenReturn(true);
        when(bookInfoRepository.save(any(BookInfo.class))).thenReturn(bookToUpdate);

        Integer result = bookInfoController.updateBookInfo(bookToUpdate);

        assertEquals(1, result, "更新图书成功必须返回1");
    }

    @Test
    void testUpdateBookInfo_NullId() {
        BookInfo bookToUpdate = new BookInfo();
        bookToUpdate.setBookId(null);

        Integer result = bookInfoController.updateBookInfo(bookToUpdate);

        assertEquals(0, result, "更新空ID图书必须返回0");
    }

    @Test
    void testUpdateBookInfo_NotFound() {
        BookInfo bookToUpdate = new BookInfo();
        bookToUpdate.setBookId(999);

        when(bookInfoRepository.existsById(999)).thenReturn(false);

        Integer result = bookInfoController.updateBookInfo(bookToUpdate);

        assertEquals(0, result, "更新不存在图书必须返回0");
    }
}
