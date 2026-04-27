package edu.cupk.simple_library_system.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_donation")
public class BookDonation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donationId")
    @JsonProperty("donationid")
    private Integer donationId;

    @Column(name = "donorName", nullable = false)
    @JsonProperty("donorname")
    private String donorName;

    @Column(name = "donorPhone", nullable = false)
    @JsonProperty("donorphone")
    private String donorPhone;

    @Column(name = "donorEmail")
    @JsonProperty("donoremail")
    private String donorEmail;

    @Column(name = "bookIsbn", nullable = false)
    @JsonProperty("bookisbn")
    private String bookIsbn;

    @Column(name = "bookName")
    @JsonProperty("bookname")
    private String bookName;

    @Column(name = "bookAuthor")
    @JsonProperty("bookauthor")
    private String bookAuthor;

    @Column(name = "bookPublisher")
    @JsonProperty("bookpublisher")
    private String bookPublisher;

    @Column(name = "status", nullable = false)
    @JsonProperty("status")
    private Byte status = 0;

    @Column(name = "applyTime", nullable = false)
    @JsonProperty("applytime")
    private LocalDateTime applyTime;

    @Column(name = "contactTime")
    @JsonProperty("contacttime")
    private LocalDateTime contactTime;

    @Column(name = "receiveTime")
    @JsonProperty("receivetime")
    private LocalDateTime receiveTime;

    @Column(name = "donorRemark", columnDefinition = "TEXT")
    @JsonProperty("donorremark")
    private String donorRemark;

    @Column(name = "staffRemark", columnDefinition = "TEXT")
    @JsonProperty("staffremark")
    private String staffRemark;

    public Integer getDonationId() {
        return donationId;
    }

    public void setDonationId(Integer donationId) {
        this.donationId = donationId;
    }

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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public LocalDateTime getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(LocalDateTime applyTime) {
        this.applyTime = applyTime;
    }

    public LocalDateTime getContactTime() {
        return contactTime;
    }

    public void setContactTime(LocalDateTime contactTime) {
        this.contactTime = contactTime;
    }

    public LocalDateTime getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(LocalDateTime receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getDonorRemark() {
        return donorRemark;
    }

    public void setDonorRemark(String donorRemark) {
        this.donorRemark = donorRemark;
    }

    public String getStaffRemark() {
        return staffRemark;
    }

    public void setStaffRemark(String staffRemark) {
        this.staffRemark = staffRemark;
    }
}
