package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.dto.FavoriteCreateDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.FavoriteDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.FavoriteResponseDto;
import com.aaron212.onlinelibrarymanagement.backend.exception.BusinessLogicException;
import com.aaron212.onlinelibrarymanagement.backend.exception.ResourceNotFoundException;
import com.aaron212.onlinelibrarymanagement.backend.mapper.FavoriteMapper;
import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.Favorite;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import com.aaron212.onlinelibrarymanagement.backend.repository.BookRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.FavoriteRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final FavoriteMapper favoriteMapper;

    public FavoriteService(
            FavoriteRepository favoriteRepository,
            UserRepository userRepository,
            BookRepository bookRepository,
            FavoriteMapper favoriteMapper) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.favoriteMapper = favoriteMapper;
    }

    @Transactional(readOnly = true)
    public Page<FavoriteDto> getUserFavorites(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Page<Favorite> favorites = favoriteRepository.findByUserIdWithBookDetails(user.getId(), pageable);
        return favorites.map(favoriteMapper::toFavoriteDto);
    }

    @Transactional(readOnly = true)
    public Page<FavoriteResponseDto> getUserFavoritesSimple(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Page<Favorite> favorites = favoriteRepository.findByUserIdWithBookDetails(user.getId(), pageable);
        return favorites.map(favoriteMapper::toFavoriteResponseDto);
    }

    public FavoriteResponseDto addFavorite(String username, FavoriteCreateDto createDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Book book = bookRepository.findById(createDto.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", createDto.bookId()));
        
        // Check if already favorited
        if (favoriteRepository.existsByUserAndBook(user, book)) {
            throw new BusinessLogicException("Book is already in user's favorites");
        }
        
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setBook(book);
        
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return favoriteMapper.toFavoriteResponseDto(savedFavorite);
    }

    public void removeFavorite(String username, Long favoriteId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite", "id", favoriteId));
        
        // Ensure the favorite belongs to the requesting user
        if (!favorite.getUser().getId().equals(user.getId())) {
            throw new BusinessLogicException("Cannot remove favorite that doesn't belong to the user");
        }
        
        favoriteRepository.delete(favorite);
    }

    public void removeFavoriteByBook(String username, Long bookId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));
        
        Favorite favorite = favoriteRepository.findByUserAndBook(user, book)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite", "bookId", bookId));
        
        favoriteRepository.delete(favorite);
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(String username, Long bookId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return favoriteRepository.existsByUserIdAndBookId(user.getId(), bookId);
    }

    @Transactional(readOnly = true)
    public long getUserFavoritesCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return favoriteRepository.countByUserId(user.getId());
    }
}