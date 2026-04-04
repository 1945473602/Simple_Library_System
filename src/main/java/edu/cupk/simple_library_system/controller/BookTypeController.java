package edu.cupk.simple_library_system.controller;

import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.entity.BookType;
import edu.cupk.simple_library_system.repository.BookTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookType")
public class BookTypeController {
    private final BookTypeRepository bookTypeRepository;

    public BookTypeController(BookTypeRepository bookTypeRepository) {
        this.bookTypeRepository = bookTypeRepository;
    }

    @GetMapping("/getCount")
    public long getCount() {
        return bookTypeRepository.count();
    }

    @GetMapping("/queryBookTypes")
    public List<BookType> queryBookTypes() {
        return bookTypeRepository.findAll();
    }

    @GetMapping("/reader/queryBookTypes")
    public List<BookType> readerQueryBookTypes() {
        return bookTypeRepository.findAll();
    }

    @GetMapping("/queryBookTypesByPage")
    public PageResponse<BookType> queryBookTypesByPage(@RequestParam int page,
                                                       @RequestParam int limit,
                                                       @RequestParam(required = false) String booktypename) {
        Page<BookType> result;
        if (booktypename == null || booktypename.isBlank()) {
            result = bookTypeRepository.findAll(PageRequest.of(Math.max(page - 1, 0), limit));
        } else {
            result = bookTypeRepository.findByBookTypeNameContaining(booktypename, PageRequest.of(Math.max(page - 1, 0), limit));
        }
        return PageResponse.success(result.getTotalElements(), result.getContent());
    }

    @PostMapping("/addBookType")
    public Integer addBookType(@RequestBody BookType bookType) {
        bookTypeRepository.save(bookType);
        return 1;
    }

    @DeleteMapping("/deleteBookType")
    public Integer deleteBookType(@RequestBody BookType bookType) {
        if (bookType.getBookTypeId() == null || !bookTypeRepository.existsById(bookType.getBookTypeId())) {
            return 0;
        }
        bookTypeRepository.deleteById(bookType.getBookTypeId());
        return 1;
    }

    @DeleteMapping("/deleteBookTypes")
    public Integer deleteBookTypes(@RequestBody List<BookType> bookTypes) {
        int count = 0;
        for (BookType item : bookTypes) {
            if (item.getBookTypeId() != null && bookTypeRepository.existsById(item.getBookTypeId())) {
                bookTypeRepository.deleteById(item.getBookTypeId());
                count++;
            }
        }
        return count;
    }

    @PutMapping("/updateBookType")
    public Integer updateBookType(@RequestBody BookType bookType) {
        if (bookType.getBookTypeId() == null || !bookTypeRepository.existsById(bookType.getBookTypeId())) {
            return 0;
        }
        bookTypeRepository.save(bookType);
        return 1;
    }
}
