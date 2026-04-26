package edu.cupk.simple_library_system.controller;

import edu.cupk.simple_library_system.dto.DonationRecordView;
import edu.cupk.simple_library_system.dto.DonationSubmitRequest;
import edu.cupk.simple_library_system.service.BookDonationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/donation")
public class BookDonationController {

    @Autowired
    private BookDonationService bookDonationService;

    @PostMapping("/submit")
    public Map<String, Object> submitDonation(@Valid @RequestBody DonationSubmitRequest request) {
        Integer donationId = bookDonationService.submitDonation(request);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "感谢您的捐赠！我们会尽快与您联系");
        response.put("data", Map.of("donationid", donationId));

        return response;
    }

    @GetMapping("/records")
    public Map<String, Object> getRecords(@RequestParam String phone) {
        List<DonationRecordView> records = bookDonationService.getRecordsByPhone(phone);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", records);

        return response;
    }
}
