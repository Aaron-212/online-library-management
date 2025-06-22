package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.BookPublisher;
import com.aaron212.onlinelibrarymanagement.backend.model.Publisher;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookPublisherRepository extends JpaRepository<BookPublisher, Long> {
    List<BookPublisher> findByBook(Book book);

    List<BookPublisher> findByPublisher(Publisher publisher);

    boolean existsByBookAndPublisher(Book book, Publisher publisher);

    void deleteByBook(Book book);
}
