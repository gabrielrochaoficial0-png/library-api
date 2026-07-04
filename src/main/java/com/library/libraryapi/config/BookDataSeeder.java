package com.library.libraryapi.config;

import com.library.libraryapi.model.Book;
import com.library.libraryapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookDataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;

    @Override
    public void run(String... args) {
        if (bookRepository.count() > 0) {
            return;
        }

        List<Book> books = List.of(
                Book.builder().title("Dom Casmurro").author("Machado de Assis").isbn("9788525406958").totalCopies(4).availableCopies(4).build(),
                Book.builder().title("1984").author("George Orwell").isbn("9788535914849").totalCopies(5).availableCopies(5).build(),
                Book.builder().title("O Senhor dos Anéis").author("J.R.R. Tolkien").isbn("9788595084759").totalCopies(3).availableCopies(3).build(),
                Book.builder().title("Harry Potter e a Pedra Filosofal").author("J.K. Rowling").isbn("9788532511010").totalCopies(6).availableCopies(6).build(),
                Book.builder().title("O Pequeno Príncipe").author("Antoine de Saint-Exupéry").isbn("9788574064997").totalCopies(4).availableCopies(4).build(),
                Book.builder().title("Cem Anos de Solidão").author("Gabriel García Márquez").isbn("9788501006769").totalCopies(3).availableCopies(3).build(),
                Book.builder().title("A Revolução dos Bichos").author("George Orwell").isbn("9788535909555").totalCopies(4).availableCopies(4).build(),
                Book.builder().title("Orgulho e Preconceito").author("Jane Austen").isbn("9788544001820").totalCopies(3).availableCopies(3).build(),
                Book.builder().title("O Hobbit").author("J.R.R. Tolkien").isbn("9788595084346").totalCopies(3).availableCopies(3).build(),
                Book.builder().title("Clean Code").author("Robert C. Martin").isbn("9780132350884").totalCopies(2).availableCopies(2).build(),
                Book.builder().title("O Alquimista").author("Paulo Coelho").isbn("9788576653246").totalCopies(5).availableCopies(5).build(),
                Book.builder().title("A Menina que Roubava Livros").author("Markus Zusak").isbn("9788598078159").totalCopies(3).availableCopies(3).build(),
                Book.builder().title("Neuromancer").author("William Gibson").isbn("9788576573412").totalCopies(2).availableCopies(2).build(),
                Book.builder().title("Duna").author("Frank Herbert").isbn("9788576572641").totalCopies(3).availableCopies(3).build(),
                Book.builder().title("Fahrenheit 451").author("Ray Bradbury").isbn("9788501014135").totalCopies(3).availableCopies(3).build()
        );

        bookRepository.saveAll(books);
    }
}