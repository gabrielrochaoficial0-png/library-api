package com.library.libraryapi.service;

import com.library.libraryapi.dto.LoanRequestDTO;
import com.library.libraryapi.dto.LoanResponseDTO;
import com.library.libraryapi.exception.BusinessException;
import com.library.libraryapi.exception.ResourceNotFoundException;
import com.library.libraryapi.model.Book;
import com.library.libraryapi.model.Loan;
import com.library.libraryapi.model.Member;
import com.library.libraryapi.model.enums.LoanStatus;
import com.library.libraryapi.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final int LOAN_PERIOD_DAYS = 14;

    private final LoanRepository loanRepository;
    private final BookService bookService;
    private final MemberService memberService;

    public LoanResponseDTO borrow(LoanRequestDTO dto) {
        Book book = bookService.getBookOrThrow(dto.getBookId());
        Member member = memberService.getMemberOrThrow(dto.getMemberId());

        if (!book.hasAvailableCopy()) {
            throw new BusinessException("Não há cópias disponíveis do livro \"" + book.getTitle() + "\"");
        }

        book.decrementAvailableCopy();

        Loan loan = Loan.builder()
                .book(book)
                .member(member)
                .loanDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(LOAN_PERIOD_DAYS))
                .status(LoanStatus.ATIVO)
                .build();

        return toResponseDTO(loanRepository.save(loan));
    }

    public LoanResponseDTO returnBook(Long loanId) {
        Loan loan = getLoanOrThrow(loanId);

        if (loan.getStatus() == LoanStatus.DEVOLVIDO) {
            throw new BusinessException("Este empréstimo já foi devolvido");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.DEVOLVIDO);
        loan.getBook().incrementAvailableCopy();

        return toResponseDTO(loanRepository.save(loan));
    }

    public List<LoanResponseDTO> findAll() {
        return loanRepository.findAll().stream()
                .map(this::toResponseDTOWithRefresh)
                .toList();
    }

    public LoanResponseDTO findById(Long id) {
        return toResponseDTOWithRefresh(getLoanOrThrow(id));
    }

    public List<LoanResponseDTO> findByMember(Long memberId) {
        memberService.getMemberOrThrow(memberId);
        return loanRepository.findByMemberId(memberId).stream()
                .map(this::toResponseDTOWithRefresh)
                .toList();
    }

    private Loan getLoanOrThrow(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado com id " + id));
    }

    /**
     * Atualiza o status para ATRASADO quando aplicável, sem persistir,
     * apenas para refletir corretamente na resposta.
     */
    private LoanResponseDTO toResponseDTOWithRefresh(Loan loan) {
        LoanStatus effectiveStatus = loan.getStatus();
        if (effectiveStatus == LoanStatus.ATIVO && loan.getDueDate().isBefore(LocalDate.now())) {
            effectiveStatus = LoanStatus.ATRASADO;
        }

        return LoanResponseDTO.builder()
                .id(loan.getId())
                .bookTitle(loan.getBook().getTitle())
                .memberName(loan.getMember().getName())
                .loanDate(loan.getLoanDate())
                .dueDate(loan.getDueDate())
                .returnDate(loan.getReturnDate())
                .status(effectiveStatus)
                .build();
    }

    private LoanResponseDTO toResponseDTO(Loan loan) {
        return toResponseDTOWithRefresh(loan);
    }
}
