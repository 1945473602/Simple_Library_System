package edu.cupk.simple_library_system.repository;

import edu.cupk.simple_library_system.entity.BookType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookTypeRepository extends JpaRepository<BookType, Integer> {
    Page<BookType> findByBookTypeNameContaining(String bookTypeName, Pageable pageable);
}
