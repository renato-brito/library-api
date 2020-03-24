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

// TESTE DE INTEGRAÇÃO

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

        // verficação
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retonar false quando não existir um livro na base com o isbn informado")
    public void returnFalseWhenIsbnDoesExists() {
        // cenário
        String isbn ="123";

        // execução
        boolean exists = repository.existsByIsbn(isbn);

        // verficação
        Assertions.assertThat(exists).isFalse();
    }

    public Book createNewBook() {
        return Book.builder()
                .author("Laura")
                .title("As aventuras")
                .isbn("123")
                .build();
    }
}
