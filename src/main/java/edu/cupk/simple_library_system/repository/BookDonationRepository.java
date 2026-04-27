package edu.cupk.simple_library_system.repository;

import edu.cupk.simple_library_system.entity.BookDonation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookDonationRepository extends JpaRepository<BookDonation, Integer> {

    List<BookDonation> findByDonorPhone(String donorPhone);

    Page<BookDonation> findByStatus(Byte status, Pageable pageable);
}
