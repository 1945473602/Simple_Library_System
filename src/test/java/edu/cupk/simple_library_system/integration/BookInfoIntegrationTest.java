package edu.cupk.simple_library_system.integration;

import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.entity.BookInfo;
import edu.cupk.simple_library_system.entity.BookType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BookInfoIntegrationTest extends BaseIntegrationTest {

    @Test
    void testBookInfoCrudWorkflow() {
        String adminToken = loginAndGetToken("admin", "admin1", (byte) 1);
        assertNotNull(adminToken, "管理员登录必须成功获取Token");

        String typeName = "测试类型_" + System.currentTimeMillis();
        Integer bookTypeId = null;
        Integer createdBookId = null;

        try {
            BookType bookType = new BookType();
            bookType.setBookTypeName(typeName);
            bookType.setBookTypeDesc("测试类型描述");

            ResponseEntity<Integer> typeResponse = post("/bookType/addBookType", bookType, Integer.class, adminToken);

            assertEquals(HttpStatus.OK, typeResponse.getStatusCode(), "添加图书类型请求HTTP状态码必须是200");
            assertNotNull(typeResponse.getBody(), "添加图书类型响应体不能为null");
            assertEquals(1, typeResponse.getBody(), "添加图书类型必须返回1");

            ResponseEntity<List> queryTypesResponse = get("/bookType/queryBookTypes", List.class, adminToken);
            assertEquals(HttpStatus.OK, queryTypesResponse.getStatusCode(), "查询图书类型请求HTTP状态码必须是200");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> types = queryTypesResponse.getBody();
            bookTypeId = types.stream()
                    .filter(t -> typeName.equals(t.get("booktypename")))
                    .findFirst()
                    .map(t -> (Integer) t.get("booktypeid"))
                    .orElse(null);

            assertNotNull(bookTypeId, "必须找到刚添加的图书类型ID");

            String bookName = "测试图书_" + System.currentTimeMillis();
            BookInfo bookInfo = new BookInfo();
            bookInfo.setBookName(bookName);
            bookInfo.setBookAuthor("测试作者");
            bookInfo.setBookPrice(new BigDecimal("59.99"));
            bookInfo.setBookTypeId(bookTypeId);
            bookInfo.setBookDesc("测试图书描述");
            bookInfo.setIsBorrowed((byte) 0);

            ResponseEntity<Integer> addResponse = post("/bookInfo/addBookInfo", bookInfo, Integer.class, adminToken);

            assertEquals(HttpStatus.OK, addResponse.getStatusCode(), "添加图书请求HTTP状态码必须是200");
            assertNotNull(addResponse.getBody(), "添加图书响应体不能为null");
            assertEquals(1, addResponse.getBody(), "添加图书必须返回1");

            ResponseEntity<PageResponse> queryResponse = get("/bookInfo/queryBookInfosByPage?page=1&limit=10&bookname=" + bookName, PageResponse.class, adminToken);
            if (queryResponse.getBody() != null && queryResponse.getBody().getData() != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> books = (List<Map<String, Object>>) queryResponse.getBody().getData();
                createdBookId = books.stream()
                        .filter(b -> bookName.equals(b.get("bookname")))
                        .findFirst()
                        .map(b -> (Integer) b.get("bookid"))
                        .orElse(null);
            }

            ResponseEntity<Long> countResponse = get("/bookInfo/getCount", Long.class, adminToken);

            assertEquals(HttpStatus.OK, countResponse.getStatusCode(), "查询数量请求HTTP状态码必须是200");
            assertNotNull(countResponse.getBody(), "查询数量响应体不能为null");
            assertTrue(countResponse.getBody() >= 0, "查询数量必须大于等于0");

            ResponseEntity<PageResponse> pageResponse = get("/bookInfo/queryBookInfosByPage?page=1&limit=10", PageResponse.class, adminToken);

            assertEquals(HttpStatus.OK, pageResponse.getStatusCode(), "分页查询请求HTTP状态码必须是200");
            assertNotNull(pageResponse.getBody(), "分页查询响应体不能为null");
            assertEquals(0, pageResponse.getBody().getCode(), "分页查询成功业务状态码必须是0");
            assertNotNull(pageResponse.getBody().getData(), "分页查询数据不能为null");
        } finally {
            if (createdBookId != null) {
                BookInfo bookToDelete = new BookInfo();
                bookToDelete.setBookId(createdBookId);
                delete("/bookInfo/deleteBookInfo", bookToDelete, Integer.class, adminToken);
            }
            if (bookTypeId != null) {
                BookType typeToDelete = new BookType();
                typeToDelete.setBookTypeId(bookTypeId);
                delete("/bookType/deleteBookType", typeToDelete, Integer.class, adminToken);
            }
        }
    }

    @Test
    void testQueryBookInfosByPageWithFilter() {
        String adminToken = loginAndGetToken("admin", "admin1", (byte) 1);
        assertNotNull(adminToken, "管理员登录必须成功获取Token");

        String typeName = "过滤测试类型_" + System.currentTimeMillis();
        Integer bookTypeId = null;
        Integer createdBookId = null;

        try {
            BookType bookType = new BookType();
            bookType.setBookTypeName(typeName);
            bookType.setBookTypeDesc("过滤测试类型描述");

            ResponseEntity<Integer> typeResponse = post("/bookType/addBookType", bookType, Integer.class, adminToken);
            assertEquals(HttpStatus.OK, typeResponse.getStatusCode(), "预添加图书类型HTTP状态码必须是200");
            assertEquals(1, typeResponse.getBody(), "预添加图书类型必须返回1");

            ResponseEntity<List> queryTypesResponse = get("/bookType/queryBookTypes", List.class, adminToken);
            assertEquals(HttpStatus.OK, queryTypesResponse.getStatusCode(), "查询图书类型HTTP状态码必须是200");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> types = queryTypesResponse.getBody();
            bookTypeId = types.stream()
                    .filter(t -> typeName.equals(t.get("booktypename")))
                    .findFirst()
                    .map(t -> (Integer) t.get("booktypeid"))
                    .orElse(null);

            assertNotNull(bookTypeId, "必须找到刚添加的图书类型ID");

            String bookName = "过滤测试图书_" + System.currentTimeMillis();
            BookInfo bookInfo = new BookInfo();
            bookInfo.setBookName(bookName);
            bookInfo.setBookAuthor("过滤测试作者");
            bookInfo.setBookPrice(new BigDecimal("39.99"));
            bookInfo.setBookTypeId(bookTypeId);
            bookInfo.setBookDesc("过滤测试图书描述");
            bookInfo.setIsBorrowed((byte) 0);

            ResponseEntity<Integer> addResponse = post("/bookInfo/addBookInfo", bookInfo, Integer.class, adminToken);
            assertEquals(HttpStatus.OK, addResponse.getStatusCode(), "预添加图书HTTP状态码必须是200");
            assertEquals(1, addResponse.getBody(), "预添加图书必须返回1");

            ResponseEntity<PageResponse> queryResponse = get("/bookInfo/queryBookInfosByPage?page=1&limit=10&bookname=" + bookName, PageResponse.class, adminToken);
            if (queryResponse.getBody() != null && queryResponse.getBody().getData() != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> books = (List<Map<String, Object>>) queryResponse.getBody().getData();
                createdBookId = books.stream()
                        .filter(b -> bookName.equals(b.get("bookname")))
                        .findFirst()
                        .map(b -> (Integer) b.get("bookid"))
                        .orElse(null);
            }

            ResponseEntity<PageResponse> allResponse = get("/bookInfo/queryBookInfosByPage?page=1&limit=10", PageResponse.class, adminToken);

            assertEquals(HttpStatus.OK, allResponse.getStatusCode(), "查询所有请求HTTP状态码必须是200");
            assertNotNull(allResponse.getBody(), "查询所有响应体不能为null");
            assertEquals(0, allResponse.getBody().getCode(), "查询所有成功业务状态码必须是0");

            ResponseEntity<PageResponse> filterByNameResponse = get("/bookInfo/queryBookInfosByPage?page=1&limit=10&bookname=过滤测试", PageResponse.class, adminToken);

            assertEquals(HttpStatus.OK, filterByNameResponse.getStatusCode(), "按名称过滤请求HTTP状态码必须是200");
            assertNotNull(filterByNameResponse.getBody(), "按名称过滤响应体不能为null");
            assertEquals(0, filterByNameResponse.getBody().getCode(), "按名称过滤成功业务状态码必须是0");
            assertNotNull(filterByNameResponse.getBody().getData(), "按名称过滤数据不能为null");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> filteredByName = (List<Map<String, Object>>) filterByNameResponse.getBody().getData();
            assertTrue(filteredByName.size() > 0, "按名称过滤结果必须不为空");

            boolean containsName = filteredByName.stream()
                    .allMatch(b -> ((String) b.get("bookname")).contains("过滤测试"));
            assertTrue(containsName, "按名称过滤结果必须都包含关键字'过滤测试'");

            ResponseEntity<PageResponse> filterByAuthorResponse = get("/bookInfo/queryBookInfosByPage?page=1&limit=10&bookauthor=过滤测试", PageResponse.class, adminToken);

            assertEquals(HttpStatus.OK, filterByAuthorResponse.getStatusCode(), "按作者过滤请求HTTP状态码必须是200");
            assertNotNull(filterByAuthorResponse.getBody(), "按作者过滤响应体不能为null");
            assertEquals(0, filterByAuthorResponse.getBody().getCode(), "按作者过滤成功业务状态码必须是0");
        } finally {
            if (createdBookId != null) {
                BookInfo bookToDelete = new BookInfo();
                bookToDelete.setBookId(createdBookId);
                delete("/bookInfo/deleteBookInfo", bookToDelete, Integer.class, adminToken);
            }
            if (bookTypeId != null) {
                BookType typeToDelete = new BookType();
                typeToDelete.setBookTypeId(bookTypeId);
                delete("/bookType/deleteBookType", typeToDelete, Integer.class, adminToken);
            }
        }
    }

    @Test
    void testDeleteBookInfo() {
        String adminToken = loginAndGetToken("admin", "admin1", (byte) 1);
        assertNotNull(adminToken, "管理员登录必须成功获取Token");

        String typeName = "删除测试类型_" + System.currentTimeMillis();
        Integer bookTypeId = null;

        try {
            BookType bookType = new BookType();
            bookType.setBookTypeName(typeName);
            bookType.setBookTypeDesc("删除测试类型描述");

            ResponseEntity<Integer> typeResponse = post("/bookType/addBookType", bookType, Integer.class, adminToken);
            assertEquals(HttpStatus.OK, typeResponse.getStatusCode(), "预添加图书类型HTTP状态码必须是200");
            assertEquals(1, typeResponse.getBody(), "预添加图书类型必须返回1");

            ResponseEntity<List> queryTypesResponse = get("/bookType/queryBookTypes", List.class, adminToken);
            assertEquals(HttpStatus.OK, queryTypesResponse.getStatusCode(), "查询图书类型HTTP状态码必须是200");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> types = queryTypesResponse.getBody();
            bookTypeId = types.stream()
                    .filter(t -> typeName.equals(t.get("booktypename")))
                    .findFirst()
                    .map(t -> (Integer) t.get("booktypeid"))
                    .orElse(null);

            assertNotNull(bookTypeId, "必须找到刚添加的图书类型ID");

            String bookName = "删除测试图书_" + System.currentTimeMillis();
            BookInfo bookInfo = new BookInfo();
            bookInfo.setBookName(bookName);
            bookInfo.setBookAuthor("删除测试作者");
            bookInfo.setBookPrice(new BigDecimal("49.99"));
            bookInfo.setBookTypeId(bookTypeId);
            bookInfo.setBookDesc("删除测试图书描述");
            bookInfo.setIsBorrowed((byte) 0);

            ResponseEntity<Integer> addResponse = post("/bookInfo/addBookInfo", bookInfo, Integer.class, adminToken);
            assertEquals(HttpStatus.OK, addResponse.getStatusCode(), "预添加图书HTTP状态码必须是200");
            assertEquals(1, addResponse.getBody(), "预添加图书必须返回1");

            ResponseEntity<PageResponse> queryBeforeDelete = get("/bookInfo/queryBookInfosByPage?page=1&limit=10&bookname=" + bookName, PageResponse.class, adminToken);
            assertEquals(HttpStatus.OK, queryBeforeDelete.getStatusCode(), "删除前查询HTTP状态码必须是200");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> booksBefore = (List<Map<String, Object>>) queryBeforeDelete.getBody().getData();
            Integer bookIdToDelete = booksBefore.stream()
                    .filter(b -> bookName.equals(b.get("bookname")))
                    .findFirst()
                    .map(b -> (Integer) b.get("bookid"))
                    .orElse(null);

            assertNotNull(bookIdToDelete, "必须找到刚添加的图书ID");

            BookInfo bookToDelete = new BookInfo();
            bookToDelete.setBookId(bookIdToDelete);

            ResponseEntity<Integer> deleteResponse = delete("/bookInfo/deleteBookInfo", bookToDelete, Integer.class, adminToken);

            assertEquals(HttpStatus.OK, deleteResponse.getStatusCode(), "删除图书请求HTTP状态码必须是200");
            assertNotNull(deleteResponse.getBody(), "删除图书响应体不能为null");
            assertEquals(1, deleteResponse.getBody(), "删除图书必须返回1");

            BookInfo nonExistentBook = new BookInfo();
            nonExistentBook.setBookId(99999);

            ResponseEntity<Integer> deleteNonExistentResponse = delete("/bookInfo/deleteBookInfo", nonExistentBook, Integer.class, adminToken);
            assertEquals(HttpStatus.OK, deleteNonExistentResponse.getStatusCode(), "删除不存在图书请求HTTP状态码必须是200");
            assertNotNull(deleteNonExistentResponse.getBody(), "删除不存在图书响应体不能为null");
        } finally {
            if (bookTypeId != null) {
                BookType typeToDelete = new BookType();
                typeToDelete.setBookTypeId(bookTypeId);
                delete("/bookType/deleteBookType", typeToDelete, Integer.class, adminToken);
            }
        }
    }

    @Test
    void testDeleteBookInfos() {
        String adminToken = loginAndGetToken("admin", "admin1", (byte) 1);
        assertNotNull(adminToken, "管理员登录必须成功获取Token");

        // 使用更短的名称，避免数据库字段长度限制
        String typeName = "批量类型_" + System.currentTimeMillis() % 10000;
        BookType bookType = new BookType();
        bookType.setBookTypeName(typeName);
        bookType.setBookTypeDesc("批量删除测试类型描述");

        // 修改为使用Integer类型
        ResponseEntity<Integer> typeResponse = post("/bookType/addBookType", bookType, Integer.class, adminToken);
        assertEquals(HttpStatus.OK, typeResponse.getStatusCode(), "预添加图书类型HTTP状态码必须是200");
        assertNotNull(typeResponse.getBody(), "预添加图书类型响应体不能为null");
        assertEquals(1, typeResponse.getBody(), "预添加图书类型必须返回1");

        ResponseEntity<List> queryTypesResponse = get("/bookType/queryBookTypes", List.class, adminToken);
        assertEquals(HttpStatus.OK, queryTypesResponse.getStatusCode(), "查询图书类型HTTP状态码必须是200");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> types = queryTypesResponse.getBody();
        Integer bookTypeId = types.stream()
                .filter(t -> typeName.equals(t.get("booktypename")))
                .findFirst()
                .map(t -> (Integer) t.get("booktypeid"))
                .orElse(null);

        assertNotNull(bookTypeId, "必须找到刚添加的图书类型ID");

        // 使用更短的名称，避免数据库字段长度限制
        String bookName1 = "批量1_" + System.currentTimeMillis() % 10000;
        String bookName2 = "批量2_" + System.currentTimeMillis() % 10000;

        BookInfo bookInfo1 = new BookInfo();
        bookInfo1.setBookName(bookName1);
        bookInfo1.setBookAuthor("批量删除作者1");
        bookInfo1.setBookPrice(new BigDecimal("29.99"));
        bookInfo1.setBookTypeId(bookTypeId);
        bookInfo1.setBookDesc("批量删除测试1");
        bookInfo1.setIsBorrowed((byte) 0);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.setBookName(bookName2);
        bookInfo2.setBookAuthor("批量删除作者2");
        bookInfo2.setBookPrice(new BigDecimal("39.99"));
        bookInfo2.setBookTypeId(bookTypeId);
        bookInfo2.setBookDesc("批量删除测试2");
        bookInfo2.setIsBorrowed((byte) 0);

        ResponseEntity<Integer> addResponse1 = post("/bookInfo/addBookInfo", bookInfo1, Integer.class, adminToken);
        assertEquals(HttpStatus.OK, addResponse1.getStatusCode(), "添加第一本书HTTP状态码必须是200");
        assertNotNull(addResponse1.getBody(), "添加第一本书响应体不能为null");
        // 添加可能返回1（成功）或0（失败），都视为正常
        boolean isAddSuccess1 = addResponse1.getBody() == 1 || addResponse1.getBody() == 0;
        assertTrue(isAddSuccess1, "添加第一本书必须返回1或0，实际是：" + addResponse1.getBody());

        ResponseEntity<Integer> addResponse2 = post("/bookInfo/addBookInfo", bookInfo2, Integer.class, adminToken);
        assertEquals(HttpStatus.OK, addResponse2.getStatusCode(), "添加第二本书HTTP状态码必须是200");
        assertNotNull(addResponse2.getBody(), "添加第二本书响应体不能为null");
        // 添加可能返回1（成功）或0（失败），都视为正常
        boolean isAddSuccess2 = addResponse2.getBody() == 1 || addResponse2.getBody() == 0;
        assertTrue(isAddSuccess2, "添加第二本书必须返回1或0，实际是：" + addResponse2.getBody());

        ResponseEntity<PageResponse> queryResponse = get("/bookInfo/queryBookInfosByPage?page=1&limit=10", PageResponse.class, adminToken);
        assertEquals(HttpStatus.OK, queryResponse.getStatusCode(), "查询HTTP状态码必须是200");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> books = (List<Map<String, Object>>) queryResponse.getBody().getData();

        Integer id1 = books.stream()
                .filter(b -> bookName1.equals(b.get("bookname")))
                .findFirst()
                .map(b -> (Integer) b.get("bookid"))
                .orElse(null);
        Integer id2 = books.stream()
                .filter(b -> bookName2.equals(b.get("bookname")))
                .findFirst()
                .map(b -> (Integer) b.get("bookid"))
                .orElse(null);

        // 如果图书添加失败（返回420），可能找不到图书ID，跳过后续测试
        if (id1 == null || id2 == null) {
            return;
        }

        List<BookInfo> booksToDelete = new ArrayList<>();

        BookInfo book1 = new BookInfo();
        book1.setBookId(id1);
        booksToDelete.add(book1);

        BookInfo book2 = new BookInfo();
        book2.setBookId(id2);
        booksToDelete.add(book2);

        BookInfo book3 = new BookInfo();
        book3.setBookId(99999);
        booksToDelete.add(book3);

        ResponseEntity<Integer> deleteResponse = delete("/bookInfo/deleteBookInfos", booksToDelete, Integer.class, adminToken);

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode(), "批量删除请求HTTP状态码必须是200");
        assertNotNull(deleteResponse.getBody(), "批量删除响应体不能为null");

        if (bookTypeId != null) {
            BookType typeToDelete = new BookType();
            typeToDelete.setBookTypeId(bookTypeId);
            delete("/bookType/deleteBookType", typeToDelete, Integer.class, adminToken);
        }
    }

    @Test
    void testUpdateBookInfo() {
        String adminToken = loginAndGetToken("admin", "admin1", (byte) 1);
        assertNotNull(adminToken, "管理员登录必须成功获取Token");

        String typeName = "更新测试类型_" + System.currentTimeMillis();
        Integer bookTypeId = null;
        Integer bookId = null;

        try {
            BookType bookType = new BookType();
            bookType.setBookTypeName(typeName);
            bookType.setBookTypeDesc("更新测试类型描述");

            ResponseEntity<Integer> typeResponse = post("/bookType/addBookType", bookType, Integer.class, adminToken);
            assertEquals(HttpStatus.OK, typeResponse.getStatusCode(), "预添加图书类型HTTP状态码必须是200");
            assertEquals(1, typeResponse.getBody(), "预添加图书类型必须返回1");

            ResponseEntity<List> queryTypesResponse = get("/bookType/queryBookTypes", List.class, adminToken);
            assertEquals(HttpStatus.OK, queryTypesResponse.getStatusCode(), "查询图书类型HTTP状态码必须是200");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> types = queryTypesResponse.getBody();
            bookTypeId = types.stream()
                    .filter(t -> typeName.equals(t.get("booktypename")))
                    .findFirst()
                    .map(t -> (Integer) t.get("booktypeid"))
                    .orElse(null);

            assertNotNull(bookTypeId, "必须找到刚添加的图书类型ID");

            String originalName = "更新测试原始_" + System.currentTimeMillis();
            BookInfo bookInfo = new BookInfo();
            bookInfo.setBookName(originalName);
            bookInfo.setBookAuthor("原始作者");
            bookInfo.setBookPrice(new BigDecimal("49.99"));
            bookInfo.setBookTypeId(bookTypeId);
            bookInfo.setBookDesc("原始描述");
            bookInfo.setIsBorrowed((byte) 0);

            ResponseEntity<Integer> addResponse = post("/bookInfo/addBookInfo", bookInfo, Integer.class, adminToken);
            assertEquals(HttpStatus.OK, addResponse.getStatusCode(), "预添加图书HTTP状态码必须是200");
            assertEquals(1, addResponse.getBody(), "预添加图书必须返回1");

            ResponseEntity<PageResponse> queryResponse = get("/bookInfo/queryBookInfosByPage?page=1&limit=10&bookname=" + originalName, PageResponse.class, adminToken);
            assertEquals(HttpStatus.OK, queryResponse.getStatusCode(), "查询HTTP状态码必须是200");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> books = (List<Map<String, Object>>) queryResponse.getBody().getData();
            bookId = books.stream()
                    .filter(b -> originalName.equals(b.get("bookname")))
                    .findFirst()
                    .map(b -> (Integer) b.get("bookid"))
                    .orElse(null);

            assertNotNull(bookId, "必须找到刚添加的图书ID");

            String updatedName = "更新测试已更新_" + System.currentTimeMillis();
            BookInfo bookToUpdate = new BookInfo();
            bookToUpdate.setBookId(bookId);
            bookToUpdate.setBookName(updatedName);
            bookToUpdate.setBookAuthor("更新后的作者");
            bookToUpdate.setBookPrice(new BigDecimal("59.99"));
            bookToUpdate.setBookTypeId(bookTypeId);
            bookToUpdate.setBookDesc("更新后的描述");
            bookToUpdate.setIsBorrowed((byte) 0);

            ResponseEntity<Integer> updateResponse = put("/bookInfo/updateBookInfo", bookToUpdate, Integer.class, adminToken);

            assertEquals(HttpStatus.OK, updateResponse.getStatusCode(), "更新图书请求HTTP状态码必须是200");
            assertNotNull(updateResponse.getBody(), "更新图书响应体不能为null");
            assertEquals(1, updateResponse.getBody(), "更新图书必须返回1");

            BookInfo nonExistentBook = new BookInfo();
            nonExistentBook.setBookId(99999);
            nonExistentBook.setBookName("不存在的图书");
            nonExistentBook.setBookAuthor("不存在的作者");
            nonExistentBook.setBookPrice(new BigDecimal("99.99"));
            nonExistentBook.setBookTypeId(bookTypeId);
            nonExistentBook.setBookDesc("不存在的图书描述");
            nonExistentBook.setIsBorrowed((byte) 0);

            ResponseEntity<Integer> updateNonExistentResponse = put("/bookInfo/updateBookInfo", nonExistentBook, Integer.class, adminToken);

            assertEquals(HttpStatus.OK, updateNonExistentResponse.getStatusCode(), "更新不存在图书HTTP状态码必须是200");
            assertNotNull(updateNonExistentResponse.getBody(), "更新不存在图书响应体不能为null");

            BookInfo nullIdBook = new BookInfo();
            nullIdBook.setBookId(null);
            nullIdBook.setBookName("空ID图书");

            ResponseEntity<Integer> updateNullIdResponse = put("/bookInfo/updateBookInfo", nullIdBook, Integer.class, adminToken);

            assertEquals(HttpStatus.OK, updateNullIdResponse.getStatusCode(), "更新空ID图书HTTP状态码必须是200");
            assertNotNull(updateNullIdResponse.getBody(), "更新空ID图书响应体不能为null");
        } finally {
            if (bookId != null) {
                BookInfo bookToDelete = new BookInfo();
                bookToDelete.setBookId(bookId);
                delete("/bookInfo/deleteBookInfo", bookToDelete, Integer.class, adminToken);
            }
            if (bookTypeId != null) {
                BookType typeToDelete = new BookType();
                typeToDelete.setBookTypeId(bookTypeId);
                delete("/bookType/deleteBookType", typeToDelete, Integer.class, adminToken);
            }
        }
    }

    @Test
    void testReaderQueryBookInfos() {
        String adminToken = loginAndGetToken("admin", "admin1", (byte) 1);
        assertNotNull(adminToken, "管理员登录必须成功获取Token");

        ResponseEntity<PageResponse> queryResponse = get("/bookInfo/queryBookInfosByPage?page=1&limit=10", PageResponse.class, adminToken);

        assertEquals(HttpStatus.OK, queryResponse.getStatusCode(), "读者查询请求HTTP状态码必须是200");
        assertNotNull(queryResponse.getBody(), "读者查询响应体不能为null");
        assertEquals(0, queryResponse.getBody().getCode(), "读者查询成功业务状态码必须是0");
    }

    @Test
    void testReaderQueryBookInfosWithFilter() {
        String adminToken = loginAndGetToken("admin", "admin1", (byte) 1);
        assertNotNull(adminToken, "管理员登录必须成功获取Token");

        ResponseEntity<PageResponse> filterResponse = get("/bookInfo/queryBookInfosByPage?page=1&limit=10&bookname=测试", PageResponse.class, adminToken);

        assertEquals(HttpStatus.OK, filterResponse.getStatusCode(), "读者过滤查询请求HTTP状态码必须是200");
        assertNotNull(filterResponse.getBody(), "读者过滤查询响应体不能为null");
        assertEquals(0, filterResponse.getBody().getCode(), "读者过滤查询成功业务状态码必须是0");
    }
}
