package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.Favorite;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * Fetches the current user's favourites together with the associated {@link com.aaron212.onlinelibrarymanagement.backend.model.Book}
     * and its authors and index category. We intentionally avoid fetching multiple bag collections (authors & publishers)
     * at the same time to prevent Hibernate's <i>MultipleBagFetchException</i> which is triggered when more than one
     * bag collection is fetched eagerly in a single query.
     * <p>
     * The publishers collection will be loaded lazily when accessed during mapping. Because the mapping happens inside the
     * service layer while the Hibernate session is still open (the method is annotated with {@code @Transactional}), this
     * does not lead to an <i>LazyInitializationException</i> but keeps the initial query lightweight and compatible with
     * pagination.
     */
    @Query(
            value = "SELECT DISTINCT f FROM Favorite f " + "LEFT JOIN FETCH f.book b "
                    + "LEFT JOIN FETCH b.authors ba "
                    + "LEFT JOIN FETCH ba.author "
                    + "LEFT JOIN FETCH b.indexCategory "
                    + "WHERE f.user.id = :userId",
            countQuery = "SELECT COUNT(f) FROM Favorite f WHERE f.user.id = :userId")
    Page<Favorite> findByUserIdWithBookDetails(@Param("userId") Long userId, Pageable pageable);

    boolean existsByUserAndBook(User user, Book book);

    Optional<Favorite> findByUserAndBook(User user, Book book);

    long countByUserId(Long userId);

    void deleteByUserAndBook(User user, Book book);

    @Query("SELECT COUNT(f) > 0 FROM Favorite f WHERE f.user.id = :userId AND f.book.id = :bookId")
    boolean existsByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);
}
