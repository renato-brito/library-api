package com.poc.livraria.livraria.service;

import com.poc.livraria.livraria.api.exception.BusinessException;
import com.poc.livraria.livraria.model.entity.Book;
import com.poc.livraria.livraria.repository.BookRepository;
import com.poc.livraria.livraria.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        // cenário
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);

        Mockito.when(repository.save(book)).thenReturn(Book.builder()
                .id(11L)
                .isbn("123")
                .author("Alessandra")
                .title("As aventuras")
                .build());

        // execução
        Book savedBook = service.save(book);

        // verifcação
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo("123");
        Assertions.assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo("Alessandra");
    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWithDuplicatedISBN() {
        // cenário
        Book book = createValidBook();
        String mensagemErro = "Isbn já cadastrado";
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        // execução
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        // Verificações
        Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage(mensagemErro);

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por Id")
    public void getByIdTest() {
        // cenário
        Long id = 1L;

        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        // execução
        Optional<Book> foundBook = service.getById(id);

        // verificações
        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(id);
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por Id quando ele não existe na base")
    public void bookNotFoundByIdTest() {
        // cenário
        Long id = 1L;

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        // execução
        Optional<Book> book = service.getById(id);

        // verificação
        Assertions.assertThat(book.isPresent()).isFalse();

    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() {
        Book book = Book.builder().id(1L).build();

        // execução
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));

        // verificação
        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro o tentar deletar um livro inexistente")
    public void deleteInvalidBookTest() {
        Book book = new Book();

        // execução
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        // verificação
        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro o tentar atualizar um livro inexistente")
    public void updateInvalidBookTest() {
        Book book = new Book();

        // execução
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));

        // verificação
        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() {
        // cenário
        Long id = 1L;

        // livro a atualizar
        Book updatingBook = Book.builder().id(id).build();

        // simulação
        Book updatedBook = createValidBook();
        updatedBook.setId(id);

        Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);

        // execução
        Book book = service.update(updatingBook);

        // verificações
        Assertions.assertThat(book.getId()).isEqualTo(updatedBook.getId());
        Assertions.assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        Assertions.assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        Assertions.assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());

    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBooksTest() {
        // cenário
        Book book = createValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> list = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(list, pageRequest, 1);

        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        // execução
        Page<Book> result = service.find(book, pageRequest);

        // verificações
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(list);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    public Book createValidBook() {
        return Book.builder()
                .isbn("123")
                .author("Alessandra")
                .title("As aventuras")
                .build();
    }
}
