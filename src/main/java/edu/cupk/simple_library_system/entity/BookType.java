package edu.cupk.simple_library_system.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "book_type")
public class BookType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookTypeId")
    @JsonProperty("booktypeid")
    private Integer bookTypeId;

    @Column(name = "bookTypeName", nullable = false, unique = true)
    @JsonProperty("booktypename")
    private String bookTypeName;

    @Column(name = "bookTypeDesc", nullable = false)
    @JsonProperty("booktypedesc")
    private String bookTypeDesc;

    public Integer getBookTypeId() {
        return bookTypeId;
    }

    public void setBookTypeId(Integer bookTypeId) {
        this.bookTypeId = bookTypeId;
    }

    public String getBookTypeName() {
        return bookTypeName;
    }

    public void setBookTypeName(String bookTypeName) {
        this.bookTypeName = bookTypeName;
    }

    public String getBookTypeDesc() {
        return bookTypeDesc;
    }

    public void setBookTypeDesc(String bookTypeDesc) {
        this.bookTypeDesc = bookTypeDesc;
    }
}
