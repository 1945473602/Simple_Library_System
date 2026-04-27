package edu.cupk.simple_library_system.controller;

import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.dto.DonationDetailView;
import edu.cupk.simple_library_system.dto.UpdateDonationRemarkRequest;
import edu.cupk.simple_library_system.dto.UpdateDonationStatusRequest;
import edu.cupk.simple_library_system.service.BookDonationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/donation")
public class AdminDonationController {

    @Autowired
    private BookDonationService bookDonationService;

    @GetMapping("/list")
    public PageResponse<DonationDetailView> getDonationList(
            @RequestParam int page,
            @RequestParam int limit,
            @RequestParam(required = false) Byte status) {

        Page<DonationDetailView> result;
        if (status != null) {
            result = bookDonationService.getDonationsByStatus(status, PageRequest.of(Math.max(page - 1, 0), limit));
        } else {
            result = bookDonationService.getAllDonations(PageRequest.of(Math.max(page - 1, 0), limit));
        }

        return PageResponse.success(result.getTotalElements(), result.getContent());
    }

    @GetMapping("/{id}")
    public Map<String, Object> getDonationDetail(@PathVariable Integer id) {
        DonationDetailView detail = bookDonationService.getDonationById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", detail);

        return response;
    }

    @PutMapping("/{id}/status")
    public Map<String, Object> updateStatus(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateDonationStatusRequest request) {

        bookDonationService.updateStatus(id, request.getStatus());

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "状态更新成功");

        return response;
    }

    @PutMapping("/{id}/remark")
    public Map<String, Object> updateRemark(
            @PathVariable Integer id,
            @RequestBody UpdateDonationRemarkRequest request) {

        bookDonationService.updateStaffRemark(id, request.getStaffRemark());

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "备注更新成功");

        return response;
    }
}
