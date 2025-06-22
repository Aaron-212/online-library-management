package com.aaron212.onlinelibrarymanagement.backend.mapper;

import com.aaron212.onlinelibrarymanagement.backend.dto.*;
import com.aaron212.onlinelibrarymanagement.backend.model.BookAuthor;
import com.aaron212.onlinelibrarymanagement.backend.model.BookPublisher;
import com.aaron212.onlinelibrarymanagement.backend.model.Favorite;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    public static final FavoriteMapper INSTANCE = new FavoriteMapper();

    public FavoriteDto toFavoriteDto(Favorite favorite) {
        if (favorite == null || favorite.getBook() == null) {
            return null;
        }

        var book = favorite.getBook();

        List<AuthorDto> authors = book.getAuthors() != null
                ? book.getAuthors().stream()
                        .map(BookAuthor::getAuthor)
                        .map(author -> new AuthorDto(author.getId(), author.getName()))
                        .collect(Collectors.toList())
                : List.of();

        List<PublisherDto> publishers = book.getPublishers() != null
                ? book.getPublishers().stream()
                        .map(BookPublisher::getPublisher)
                        .map(publisher -> new PublisherDto(publisher.getId(), publisher.getName()))
                        .collect(Collectors.toList())
                : List.of();

        IndexCategoryDto indexCategory = book.getIndexCategory() != null
                ? new IndexCategoryDto(
                        book.getIndexCategory().getId(), book.getIndexCategory().getName())
                : null;

        BookDto bookDto = new BookDto(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getLanguage(),
                null, // availableQuantity - not needed for favorites
                null, // totalQuantity - not needed for favorites
                book.getDescription(),
                authors,
                publishers,
                indexCategory,
                book.getCoverURL());

        return new FavoriteDto(favorite.getId(), bookDto, favorite.getCreateTime());
    }

    public FavoriteResponseDto toFavoriteResponseDto(Favorite favorite) {
        if (favorite == null || favorite.getBook() == null) {
            return null;
        }

        var book = favorite.getBook();

        return new FavoriteResponseDto(
                favorite.getId(),
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getCoverURL(),
                favorite.getCreateTime());
    }
}
