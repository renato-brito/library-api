package com.poc.livraria.livraria.api.model;

import com.poc.livraria.livraria.model.entity.Book;
import com.poc.livraria.livraria.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

// CLASSE COM FINALIDADE PARA TESTE DE INTEGRAÇÃO

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retonar true quando existir um livro na base com o isbn informado")
    public void returnTrueWhenIsbnExists() {
        // cenário
        String isbn ="123";
        Book book = createNewBook();
        entityManager.persist(book);

        // execução
        boolean exists = repository.existsByIsbn(isbn);

        // verificação
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retonar false quando não existir um livro na base com o isbn informado")
    public void returnFalseWhenIsbnDoesExists() {
        // cenário
        String isbn ="123";

        // execução
        boolean exists = repository.existsByIsbn(isbn);

        // verificação
        Assertions.assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void findByIdTest() {
        // cenário
        Book book = createNewBook();
        entityManager.persist(book);

        // execução
        Optional<Book> foundBook = repository.findById(book.getId());

        // verificação
        Assertions.assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        Book book = createNewBook();

        Book savedBook = repository.save(book);

        Assertions.assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() {
        Book book = createNewBook();
        entityManager.persist(book);

        Book foundBook = entityManager.find(Book.class, book.getId());
        repository.delete(foundBook);

        Book deleteBook = entityManager.find(Book.class, book.getId());
        Assertions.assertThat(deleteBook).isNull();
    }

    public Book createNewBook() {
        return Book.builder()
                .author("Laura")
                .title("As aventuras")
                .isbn("123")
                .build();
    }
}
