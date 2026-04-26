package edu.cupk.simple_library_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class DonationDetailView {
    @JsonProperty("donationid")
    private Integer donationId;

    @JsonProperty("donorname")
    private String donorName;

    @JsonProperty("donorphone")
    private String donorPhone;

    @JsonProperty("donoremail")
    private String donorEmail;

    @JsonProperty("bookisbn")
    private String bookIsbn;

    @JsonProperty("bookname")
    private String bookName;

    @JsonProperty("bookauthor")
    private String bookAuthor;

    @JsonProperty("bookpublisher")
    private String bookPublisher;

    @JsonProperty("status")
    private Byte status;

    @JsonProperty("statustext")
    private String statusText;

    @JsonProperty("applytime")
    private LocalDateTime applyTime;

    @JsonProperty("contacttime")
    private LocalDateTime contactTime;

    @JsonProperty("receivetime")
    private LocalDateTime receiveTime;

    @JsonProperty("donorremark")
    private String donorRemark;

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

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
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
