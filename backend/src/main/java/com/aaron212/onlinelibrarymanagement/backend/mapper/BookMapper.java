package com.aaron212.onlinelibrarymanagement.backend.mapper;

import com.aaron212.onlinelibrarymanagement.backend.dto.BookCreateDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookDto;
import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.BookAuthor;
import com.aaron212.onlinelibrarymanagement.backend.model.BookPublisher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(target = "authors", source = "book", qualifiedByName = "mapAuthors")
    @Mapping(target = "publishers", source = "book", qualifiedByName = "mapPublishers")
    BookDto bookToBookDto(Book book);

    Book bookDtoToBook(BookDto bookDto);

    BookCreateDto bookToBookCreateDto(Book book);

    Book bookCreateDtoToBook(BookCreateDto bookCreateDto);
    
    @Named("mapAuthors")
    default List<String> mapAuthors(Book book) {
        if (book.getAuthors() == null) {
            return List.of();
        }
        return book.getAuthors().stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getName())
                .collect(Collectors.toList());
    }
    
    @Named("mapPublishers")
    default List<String> mapPublishers(Book book) {
        if (book.getPublishers() == null) {
            return List.of();
        }
        return book.getPublishers().stream()
                .map(bookPublisher -> bookPublisher.getPublisher().getName())
                .collect(Collectors.toList());
    }
}
