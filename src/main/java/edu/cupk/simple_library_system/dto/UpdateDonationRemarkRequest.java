package edu.cupk.simple_library_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateDonationRemarkRequest {
    @JsonProperty("staffremark")
    private String staffRemark;

    public String getStaffRemark() {
        return staffRemark;
    }

    public void setStaffRemark(String staffRemark) {
        this.staffRemark = staffRemark;
    }
}
