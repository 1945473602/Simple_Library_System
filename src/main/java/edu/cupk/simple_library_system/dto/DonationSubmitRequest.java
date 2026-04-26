package edu.cupk.simple_library_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class DonationSubmitRequest {
    @NotBlank(message = "捐赠人姓名不能为空")
    @JsonProperty("donorname")
    private String donorName;

    @NotBlank(message = "联系电话不能为空")
    @JsonProperty("donorphone")
    private String donorPhone;

    @JsonProperty("donoremail")
    private String donorEmail;

    @NotBlank(message = "图书ISBN不能为空")
    @JsonProperty("bookisbn")
    private String bookIsbn;

    @JsonProperty("bookname")
    private String bookName;

    @JsonProperty("bookauthor")
    private String bookAuthor;

    @JsonProperty("bookpublisher")
    private String bookPublisher;

    @JsonProperty("donorremark")
    private String donorRemark;

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDonorPhone() {
        return donorPhone;
    }

    public void setDonorPhone(String donorPhone) {
        this.donorPhone = donorPhone;
    }

    public String getDonorEmail() {
        return donorEmail;
    }

    public void setDonorEmail(String donorEmail) {
        this.donorEmail = donorEmail;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookPublisher() {
        return bookPublisher;
    }

    public void setBookPublisher(String bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    public String getDonorRemark() {
        return donorRemark;
    }

    public void setDonorRemark(String donorRemark) {
        this.donorRemark = donorRemark;
    }
}
