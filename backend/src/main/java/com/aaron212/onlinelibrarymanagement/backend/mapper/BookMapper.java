package com.aaron212.onlinelibrarymanagement.backend.mapper;

import com.aaron212.onlinelibrarymanagement.backend.dto.BookStatisticsDto;
import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.BookAuthor;
import com.aaron212.onlinelibrarymanagement.backend.model.BookPublisher;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public static final BookMapper INSTANCE = new BookMapper();

    public BookStatisticsDto toBookStatisticsDto(Book book, Long borrowCount) {
        if (book == null) {
            return null;
        }

        List<String> authors = book.getAuthors() != null
                ? book.getAuthors().stream()
                        .map(BookAuthor::getAuthor)
                        .map(author -> author.getName())
                        .collect(Collectors.toList())
                : List.of();

        List<String> publishers = book.getPublishers() != null
                ? book.getPublishers().stream()
                        .map(BookPublisher::getPublisher)
                        .map(publisher -> publisher.getName())
                        .collect(Collectors.toList())
                : List.of();

        return new BookStatisticsDto(book.getId(), book.getTitle(), book.getIsbn(), authors, publishers, borrowCount);
    }
}
