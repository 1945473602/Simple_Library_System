package edu.cupk.simple_library_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class UpdateDonationStatusRequest {
    @NotNull(message = "状态不能为空")
    @JsonProperty("status")
    private Byte status;

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}
