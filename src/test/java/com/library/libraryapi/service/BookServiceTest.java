package com.library.libraryapi.service;

import com.library.libraryapi.dto.BookRequestDTO;
import com.library.libraryapi.dto.BookResponseDTO;
import com.library.libraryapi.exception.BusinessException;
import com.library.libraryapi.exception.ResourceNotFoundException;
import com.library.libraryapi.model.Book;
import com.library.libraryapi.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private BookRequestDTO requestDTO;
    private Book book;

    @BeforeEach
    void setUp() {
        requestDTO = new BookRequestDTO();
        requestDTO.setTitle("Clean Code");
        requestDTO.setAuthor("Robert C. Martin");
        requestDTO.setIsbn("9780132350884");
        requestDTO.setTotalCopies(3);

        book = Book.builder()
                .id(1L)
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .totalCopies(3)
                .availableCopies(3)
                .build();
    }

    @Test
    void deveCriarUmLivroComSucesso() {
        when(bookRepository.existsByIsbn(requestDTO.getIsbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponseDTO response = bookService.create(requestDTO);

        assertThat(response.getTitle()).isEqualTo("Clean Code");
        assertThat(response.getAvailableCopies()).isEqualTo(3);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void naoDeveCriarLivroComIsbnDuplicado() {
        when(bookRepository.existsByIsbn(requestDTO.getIsbn())).thenReturn(true);

        assertThatThrownBy(() -> bookService.create(requestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe um livro cadastrado");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deveLancarExcecaoQuandoLivroNaoEncontrado() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Livro não encontrado");
    }

    @Test
    void naoDeveExcluirLivroComEmprestimosAtivos() {
        book.setAvailableCopies(1);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("empréstimos ativos");

        verify(bookRepository, never()).delete(any(Book.class));
    }
}
