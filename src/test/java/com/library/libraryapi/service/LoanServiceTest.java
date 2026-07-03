package com.library.libraryapi.service;

import com.library.libraryapi.dto.LoanRequestDTO;
import com.library.libraryapi.dto.LoanResponseDTO;
import com.library.libraryapi.exception.BusinessException;
import com.library.libraryapi.model.Book;
import com.library.libraryapi.model.Loan;
import com.library.libraryapi.model.Member;
import com.library.libraryapi.model.enums.LoanStatus;
import com.library.libraryapi.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookService bookService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private LoanService loanService;

    private Book book;
    private Member member;
    private LoanRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("9780134685991")
                .totalCopies(2)
                .availableCopies(1)
                .build();

        member = Member.builder()
                .id(1L)
                .name("Maria Silva")
                .email("maria@email.com")
                .build();

        requestDTO = new LoanRequestDTO();
        requestDTO.setBookId(1L);
        requestDTO.setMemberId(1L);
    }

    @Test
    void deveRealizarEmprestimoComSucesso() {
        when(bookService.getBookOrThrow(1L)).thenReturn(book);
        when(memberService.getMemberOrThrow(1L)).thenReturn(member);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan loan = invocation.getArgument(0);
            loan.setId(1L);
            return loan;
        });

        LoanResponseDTO response = loanService.borrow(requestDTO);

        assertThat(response.getStatus()).isEqualTo(LoanStatus.ATIVO);
        assertThat(book.getAvailableCopies()).isEqualTo(0);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void naoDeveEmprestarLivroSemCopiaDisponivel() {
        book.setAvailableCopies(0);
        when(bookService.getBookOrThrow(1L)).thenReturn(book);
        when(memberService.getMemberOrThrow(1L)).thenReturn(member);

        assertThatThrownBy(() -> loanService.borrow(requestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Não há cópias disponíveis");

        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void deveRegistrarDevolucaoENaoPermitirDevolucaoDuplicada() {
        Loan loan = Loan.builder()
                .id(1L)
                .book(book)
                .member(member)
                .loanDate(LocalDate.now().minusDays(5))
                .dueDate(LocalDate.now().plusDays(9))
                .status(LoanStatus.ATIVO)
                .build();

        when(loanRepository.findById(1L)).thenReturn(java.util.Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        LoanResponseDTO response = loanService.returnBook(1L);

        assertThat(response.getStatus()).isEqualTo(LoanStatus.DEVOLVIDO);
        assertThat(book.getAvailableCopies()).isEqualTo(2);

        // Segunda tentativa de devolução deve falhar
        assertThatThrownBy(() -> loanService.returnBook(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já foi devolvido");
    }
}
