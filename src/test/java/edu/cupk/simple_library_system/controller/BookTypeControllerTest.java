package edu.cupk.simple_library_system.controller;

import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.entity.BookType;
import edu.cupk.simple_library_system.repository.BookTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// 图书类型管理控制器测试类
// 测试BookTypeController中的所有图书类型管理相关功能
// 包括：图书类型的增删改查、分页查询、预设类型管理等
@SpringBootTest
class BookTypeControllerTest {

    // 自动注入被测试的控制器实例
    @Autowired
    private BookTypeController bookTypeController;

    // 模拟图书类型数据访问层，用于隔离数据库依赖
    @MockitoBean
    private BookTypeRepository bookTypeRepository;

    // 测试用的图书类型实体对象
    private BookType testBookType;

    // 预设的图书类型列表（计算机科学、历史、文学、科幻、小说、外语）
    private List<BookType> presetBookTypes;

    // 每个测试方法执行前的初始化操作
    // 创建测试用的图书类型数据
    @BeforeEach
    void setUp() {
        // 初始化测试图书类型数据
        testBookType = new BookType();
        testBookType.setBookTypeId(1);
        testBookType.setBookTypeName("计算机科学");
        testBookType.setBookTypeDesc("计算机相关书籍");

        // 初始化预设图书类型列表
        presetBookTypes = new ArrayList<>();
        presetBookTypes.add(createBookType(1, "计算机科学", "计算机相关书籍"));
        presetBookTypes.add(createBookType(2, "历史", "历史相关书籍"));
        presetBookTypes.add(createBookType(3, "文学", "文学相关书籍"));
        presetBookTypes.add(createBookType(4, "科幻", "科幻相关书籍"));
        presetBookTypes.add(createBookType(5, "小说", "小说相关书籍"));
        presetBookTypes.add(createBookType(6, "外语", "外语学习相关书籍"));
    }

    // 辅助方法：创建图书类型对象
    private BookType createBookType(Integer id, String name, String desc) {
        BookType type = new BookType();
        type.setBookTypeId(id);
        type.setBookTypeName(name);
        type.setBookTypeDesc(desc);
        return type;
    }

    // 测试获取图书类型总数
    // 验证点：
    // 1. 返回的图书类型数量与模拟值一致
    @Test
    void testGetCount() {
        // 模拟图书类型数量为6（预设类型数量）
        when(bookTypeRepository.count()).thenReturn(6L);

        long count = bookTypeController.getCount();

        assertEquals(6L, count);
    }

    // 测试查询所有图书类型
    // 验证点：
    // 1. 返回图书类型列表
    // 2. 列表中包含预设的6种图书类型
    @Test
    void testQueryBookTypes() {
        // 模拟查询所有图书类型
        when(bookTypeRepository.findAll()).thenReturn(presetBookTypes);

        List<BookType> result = bookTypeController.queryBookTypes();

        assertEquals(6, result.size());
        assertEquals("计算机科学", result.get(0).getBookTypeName());
        assertEquals("历史", result.get(1).getBookTypeName());
        assertEquals("文学", result.get(2).getBookTypeName());
        assertEquals("科幻", result.get(3).getBookTypeName());
        assertEquals("小说", result.get(4).getBookTypeName());
        assertEquals("外语", result.get(5).getBookTypeName());
    }

    // 测试读者查询所有图书类型
    // 验证点：
    // 1. 返回图书类型列表
    // 2. 功能与管理员查询相同
    @Test
    void testReaderQueryBookTypes() {
        // 模拟查询所有图书类型
        when(bookTypeRepository.findAll()).thenReturn(presetBookTypes);

        List<BookType> result = bookTypeController.readerQueryBookTypes();

        assertEquals(6, result.size());
        assertEquals("计算机科学", result.get(0).getBookTypeName());
    }

    // 测试分页查询图书类型（不带类型名称过滤条件）
    // 验证点：
    // 1. 返回的总记录数正确
    // 2. 返回的数据列表大小正确
    @Test
    void testQueryBookTypesByPage() {
        Page<BookType> page = new PageImpl<>(presetBookTypes);

        // 模拟分页查询
        when(bookTypeRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<BookType> result = bookTypeController.queryBookTypesByPage(1, 10, null);

        assertEquals(6, result.getCount());
        assertEquals(6, result.getData().size());
    }

    // 测试分页查询图书类型（带类型名称过滤条件）
    // 验证点：
    // 1. 返回的总记录数正确
    // 2. 搜索条件正确传递
    // 3. 支持模糊查询
    @Test
    void testQueryBookTypesByPage_WithBookTypeName() {
        List<BookType> filteredTypes = new ArrayList<>();
        filteredTypes.add(presetBookTypes.get(0)); // 计算机科学
        Page<BookType> page = new PageImpl<>(filteredTypes);

        // 模拟按类型名称模糊查询
        when(bookTypeRepository.findByBookTypeNameContaining("计算机", PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<BookType> result = bookTypeController.queryBookTypesByPage(1, 10, "计算机");

        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().size());
        assertEquals("计算机科学", result.getData().get(0).getBookTypeName());
    }

    // 测试分页查询图书类型（模糊匹配多个结果）
    // 验证点：
    // 1. 返回匹配的所有记录
    // 2. 模糊查询功能正常
    @Test
    void testQueryBookTypesByPage_WithFuzzySearch() {
        List<BookType> filteredTypes = new ArrayList<>();
        filteredTypes.add(presetBookTypes.get(3)); // 科幻
        filteredTypes.add(presetBookTypes.get(4)); // 小说
        Page<BookType> page = new PageImpl<>(filteredTypes);

        // 模拟模糊查询（匹配"小说"相关的类型）
        when(bookTypeRepository.findByBookTypeNameContaining("说", PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<BookType> result = bookTypeController.queryBookTypesByPage(1, 10, "说");

        assertEquals(2, result.getCount());
    }

    // 测试添加图书类型成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 图书类型被正确保存
    @Test
    void testAddBookType_Success() {
        BookType newType = new BookType();
        newType.setBookTypeName("艺术");
        newType.setBookTypeDesc("艺术相关书籍");

        // 模拟保存图书类型
        when(bookTypeRepository.save(any(BookType.class))).thenReturn(newType);

        Integer result = bookTypeController.addBookType(newType);

        assertEquals(1, result);
        verify(bookTypeRepository, times(1)).save(any(BookType.class));
    }

    // 测试添加预设图书类型
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 预设类型可以正常添加
    @Test
    void testAddBookType_PresetType() {
        BookType newType = new BookType();
        newType.setBookTypeName("哲学");
        newType.setBookTypeDesc("哲学相关书籍");

        when(bookTypeRepository.save(any(BookType.class))).thenReturn(newType);

        Integer result = bookTypeController.addBookType(newType);

        assertEquals(1, result);
    }

    // 测试删除图书类型成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 图书类型存在且被删除
    @Test
    void testDeleteBookType_Success() {
        BookType typeToDelete = new BookType();
        typeToDelete.setBookTypeId(1);

        // 模拟图书类型存在
        when(bookTypeRepository.existsById(1)).thenReturn(true);
        // 模拟删除图书类型
        doNothing().when(bookTypeRepository).deleteById(1);

        Integer result = bookTypeController.deleteBookType(typeToDelete);

        assertEquals(1, result);
        verify(bookTypeRepository, times(1)).deleteById(1);
    }

    // 测试删除图书类型失败场景 - 类型ID为空
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testDeleteBookType_NullId() {
        BookType typeToDelete = new BookType();
        typeToDelete.setBookTypeId(null);

        Integer result = bookTypeController.deleteBookType(typeToDelete);

        assertEquals(0, result);
    }

    // 测试删除图书类型失败场景 - 类型不存在
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testDeleteBookType_NotFound() {
        BookType typeToDelete = new BookType();
        typeToDelete.setBookTypeId(999);

        // 模拟图书类型不存在
        when(bookTypeRepository.existsById(999)).thenReturn(false);

        Integer result = bookTypeController.deleteBookType(typeToDelete);

        assertEquals(0, result);
    }

    // 测试批量删除图书类型
    // 验证点：
    // 1. 返回成功删除的图书类型数量
    // 2. 只删除存在的图书类型
    @Test
    void testDeleteBookTypes() {
        List<BookType> typesToDelete = new ArrayList<>();

        // 第一条：存在，可以删除
        BookType type1 = new BookType();
        type1.setBookTypeId(1);
        typesToDelete.add(type1);

        // 第二条：存在，可以删除
        BookType type2 = new BookType();
        type2.setBookTypeId(2);
        typesToDelete.add(type2);

        // 第三条：不存在，不能删除
        BookType type3 = new BookType();
        type3.setBookTypeId(999);
        typesToDelete.add(type3);

        // 模拟查询图书类型是否存在
        when(bookTypeRepository.existsById(1)).thenReturn(true);
        when(bookTypeRepository.existsById(2)).thenReturn(true);
        when(bookTypeRepository.existsById(999)).thenReturn(false);

        // 模拟删除操作
        doNothing().when(bookTypeRepository).deleteById(anyInt());

        Integer result = bookTypeController.deleteBookTypes(typesToDelete);

        assertEquals(2, result); // 只有2条存在的类型被删除
        verify(bookTypeRepository, times(1)).deleteById(1);
        verify(bookTypeRepository, times(1)).deleteById(2);
        verify(bookTypeRepository, never()).deleteById(999); // 不存在的不删除
    }

    // 测试批量删除图书类型 - 包含ID为空的类型
    // 验证点：
    // 1. 跳过ID为空的类型
    // 2. 只删除有效的类型
    @Test
    void testDeleteBookTypes_WithNullId() {
        List<BookType> typesToDelete = new ArrayList<>();

        // 第一条：ID为空，跳过
        BookType type1 = new BookType();
        type1.setBookTypeId(null);
        typesToDelete.add(type1);

        // 第二条：有效，可以删除
        BookType type2 = new BookType();
        type2.setBookTypeId(1);
        typesToDelete.add(type2);

        when(bookTypeRepository.existsById(1)).thenReturn(true);
        doNothing().when(bookTypeRepository).deleteById(1);

        Integer result = bookTypeController.deleteBookTypes(typesToDelete);

        assertEquals(1, result); // 只有1条有效类型被删除
    }

    // 测试更新图书类型成功场景
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 图书类型存在，可以正常更新
    @Test
    void testUpdateBookType_Success() {
        BookType typeToUpdate = new BookType();
        typeToUpdate.setBookTypeId(1);
        typeToUpdate.setBookTypeName("计算机科学与技术");
        typeToUpdate.setBookTypeDesc("更新后的描述");

        // 模拟图书类型存在
        when(bookTypeRepository.existsById(1)).thenReturn(true);
        // 模拟保存图书类型
        when(bookTypeRepository.save(any(BookType.class))).thenReturn(typeToUpdate);

        Integer result = bookTypeController.updateBookType(typeToUpdate);

        assertEquals(1, result);
        verify(bookTypeRepository, times(1)).save(any(BookType.class));
    }

    // 测试更新图书类型失败场景 - 类型ID为空
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testUpdateBookType_NullId() {
        BookType typeToUpdate = new BookType();
        typeToUpdate.setBookTypeId(null);

        Integer result = bookTypeController.updateBookType(typeToUpdate);

        assertEquals(0, result);
    }

    // 测试更新图书类型失败场景 - 类型不存在
    // 验证点：
    // 1. 返回值为0（表示失败）
    @Test
    void testUpdateBookType_NotFound() {
        BookType typeToUpdate = new BookType();
        typeToUpdate.setBookTypeId(999);

        // 模拟图书类型不存在
        when(bookTypeRepository.existsById(999)).thenReturn(false);

        Integer result = bookTypeController.updateBookType(typeToUpdate);

        assertEquals(0, result);
    }

    // 测试更新预设图书类型
    // 验证点：
    // 1. 返回值为1（表示成功）
    // 2. 预设类型可以正常更新
    @Test
    void testUpdateBookType_PresetType() {
        BookType typeToUpdate = new BookType();
        typeToUpdate.setBookTypeId(1);
        typeToUpdate.setBookTypeName("计算机科学");
        typeToUpdate.setBookTypeDesc("包含编程、算法、数据结构等");

        when(bookTypeRepository.existsById(1)).thenReturn(true);
        when(bookTypeRepository.save(any(BookType.class))).thenReturn(typeToUpdate);

        Integer result = bookTypeController.updateBookType(typeToUpdate);

        assertEquals(1, result);
    }
}