package com.library.libraryapi.service;

import com.library.libraryapi.dto.BookRequestDTO;
import com.library.libraryapi.dto.BookResponseDTO;
import com.library.libraryapi.exception.BusinessException;
import com.library.libraryapi.exception.ResourceNotFoundException;
import com.library.libraryapi.model.Book;
import com.library.libraryapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public BookResponseDTO create(BookRequestDTO dto) {
        if (bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new BusinessException("Já existe um livro cadastrado com o ISBN " + dto.getIsbn());
        }

        Book book = Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .isbn(dto.getIsbn())
                .totalCopies(dto.getTotalCopies())
                .availableCopies(dto.getTotalCopies())
                .build();

        return toResponseDTO(bookRepository.save(book));
    }

    public List<BookResponseDTO> findAll() {
        return bookRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public BookResponseDTO findById(Long id) {
        return toResponseDTO(getBookOrThrow(id));
    }

    public BookResponseDTO update(Long id, BookRequestDTO dto) {
        Book book = getBookOrThrow(id);

        int loanedCopies = book.getTotalCopies() - book.getAvailableCopies();
        if (dto.getTotalCopies() < loanedCopies) {
            throw new BusinessException(
                    "Não é possível reduzir o total de cópias abaixo da quantidade já emprestada (" + loanedCopies + ")");
        }

        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setAvailableCopies(dto.getTotalCopies() - loanedCopies);
        book.setTotalCopies(dto.getTotalCopies());

        return toResponseDTO(bookRepository.save(book));
    }

    public void delete(Long id) {
        Book book = getBookOrThrow(id);
        if (!book.getAvailableCopies().equals(book.getTotalCopies())) {
            throw new BusinessException("Não é possível excluir um livro que possui empréstimos ativos");
        }
        bookRepository.delete(book);
    }

    public Book getBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado com id " + id));
    }

    private BookResponseDTO toResponseDTO(Book book) {
        return BookResponseDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .build();
    }
}
