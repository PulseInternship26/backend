package com.pulseinternship.bookstore.service;

import com.pulseinternship.bookstore.exception.BookNotFoundException;
import org.springframework.stereotype.Service;
import com.pulseinternship.bookstore.repository.BookRepo;
import com.pulseinternship.bookstore.model.dtos.BookSummaryResponseDto;
import com.pulseinternship.bookstore.model.dtos.BookResponseDto;
import java.util.List;
import com.pulseinternship.bookstore.model.dtos.BookRequestDto;
import com.pulseinternship.bookstore.model.entities.Book;

@Service
public class BookService {

    private final BookRepo bookRepo;

    public BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    public List<BookSummaryResponseDto> getAllBooks() {
        return bookRepo.findAll().stream()
                .map(book -> new BookSummaryResponseDto(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCategory(),
                        book.getPrice(),
                        book.getImageUrl()
                ))
                .toList();
    }

    public BookResponseDto getBookById(Long id) {
        return bookRepo.findById(id)
                .map(book -> new BookResponseDto(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCategory(),
                        book.getPrice(),
                        book.getDescription(),
                        book.getImageUrl()))
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));
    }


public BookResponseDto createBook(BookRequestDto bookRequestDto){
    Book book = new Book();
    book.setTitle(bookRequestDto.title());
    book.setAuthor(bookRequestDto.author());
    book.setCategory(bookRequestDto.category());
    book.setPrice(bookRequestDto.price());
    book.setDescription(bookRequestDto.description());
    book.setImageUrl(bookRequestDto.imageUrl());
    bookRepo.save(book);
    return new BookResponseDto(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getCategory(),
            book.getPrice(),
            book.getDescription(),
            book.getImageUrl()
    );
}


public BookResponseDto updateBook(Long id, BookRequestDto bookRequestDto) {
    Book book = bookRepo.findById(id)
            .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));

    book.setTitle(bookRequestDto.title());
    book.setAuthor(bookRequestDto.author());
    book.setCategory(bookRequestDto.category());
    book.setPrice(bookRequestDto.price());
    book.setDescription(bookRequestDto.description());
    book.setImageUrl(bookRequestDto.imageUrl());
    bookRepo.save(book);
    return new BookResponseDto(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getCategory(),
            book.getPrice(),
            book.getDescription(),
            book.getImageUrl()
    );
}
public void deleteBook(Long id) {
    Book book = bookRepo.findById(id)
            .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));
    bookRepo.delete(book);
}
}
