package com.library.libraryapi.repository;

import com.library.libraryapi.model.Loan;
import com.library.libraryapi.model.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByMemberId(Long memberId);
    List<Loan> findByBookId(Long bookId);
    List<Loan> findByStatus(LoanStatus status);
    boolean existsByBookIdAndStatus(Long bookId, LoanStatus status);
}
