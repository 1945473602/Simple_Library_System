package edu.cupk.simple_library_system.service;

import edu.cupk.simple_library_system.dto.*;
import edu.cupk.simple_library_system.entity.BookDonation;
import edu.cupk.simple_library_system.repository.BookDonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookDonationService {

    @Autowired
    private BookDonationRepository bookDonationRepository;

    @Transactional
    public Integer submitDonation(DonationSubmitRequest request) {
        BookDonation donation = new BookDonation();
        donation.setDonorName(request.getDonorName());
        donation.setDonorPhone(request.getDonorPhone());
        donation.setDonorEmail(request.getDonorEmail());
        donation.setBookIsbn(request.getBookIsbn());
        donation.setBookName(request.getBookName());
        donation.setBookAuthor(request.getBookAuthor());
        donation.setBookPublisher(request.getBookPublisher());
        donation.setDonorRemark(request.getDonorRemark());
        donation.setStatus((byte) 0);
        donation.setApplyTime(LocalDateTime.now());

        BookDonation saved = bookDonationRepository.save(donation);
        return saved.getDonationId();
    }

    public List<DonationRecordView> getRecordsByPhone(String phone) {
        List<BookDonation> donations = bookDonationRepository.findByDonorPhone(phone);
        return donations.stream()
                .map(this::convertToRecordView)
                .collect(Collectors.toList());
    }

    public Page<DonationDetailView> getAllDonations(Pageable pageable) {
        Page<BookDonation> donations = bookDonationRepository.findAll(pageable);
        return donations.map(this::convertToDetailView);
    }

    public Page<DonationDetailView> getDonationsByStatus(Byte status, Pageable pageable) {
        Page<BookDonation> donations = bookDonationRepository.findByStatus(status, pageable);
        return donations.map(this::convertToDetailView);
    }

    public DonationDetailView getDonationById(Integer id) {
        BookDonation donation = bookDonationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("捐赠申请不存在"));
        return convertToDetailView(donation);
    }

    @Transactional
    public void updateStatus(Integer id, Byte status) {
        BookDonation donation = bookDonationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("捐赠申请不存在"));

        donation.setStatus(status);

        if (status == 1) {
            donation.setContactTime(LocalDateTime.now());
        } else if (status == 2) {
            donation.setReceiveTime(LocalDateTime.now());
        }

        bookDonationRepository.save(donation);
    }

    @Transactional
    public void updateStaffRemark(Integer id, String remark) {
        BookDonation donation = bookDonationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("捐赠申请不存在"));
        donation.setStaffRemark(remark);
        bookDonationRepository.save(donation);
    }

    private DonationRecordView convertToRecordView(BookDonation donation) {
        DonationRecordView view = new DonationRecordView();
        view.setDonationId(donation.getDonationId());
        view.setBookIsbn(donation.getBookIsbn());
        view.setBookName(donation.getBookName());
        view.setBookAuthor(donation.getBookAuthor());
        view.setBookPublisher(donation.getBookPublisher());
        view.setStatus(donation.getStatus());
        view.setStatusText(getStatusText(donation.getStatus()));
        view.setApplyTime(donation.getApplyTime());
        view.setDonorRemark(donation.getDonorRemark());
        return view;
    }

    private DonationDetailView convertToDetailView(BookDonation donation) {
        DonationDetailView view = new DonationDetailView();
        view.setDonationId(donation.getDonationId());
        view.setDonorName(donation.getDonorName());
        view.setDonorPhone(donation.getDonorPhone());
        view.setDonorEmail(donation.getDonorEmail());
        view.setBookIsbn(donation.getBookIsbn());
        view.setBookName(donation.getBookName());
        view.setBookAuthor(donation.getBookAuthor());
        view.setBookPublisher(donation.getBookPublisher());
        view.setStatus(donation.getStatus());
        view.setStatusText(getStatusText(donation.getStatus()));
        view.setApplyTime(donation.getApplyTime());
        view.setContactTime(donation.getContactTime());
        view.setReceiveTime(donation.getReceiveTime());
        view.setDonorRemark(donation.getDonorRemark());
        view.setStaffRemark(donation.getStaffRemark());
        return view;
    }

    private String getStatusText(Byte status) {
        return switch (status) {
            case 0 -> "待处理";
            case 1 -> "待接收";
            case 2 -> "已入库";
            default -> "未知状态";
        };
    }
}
