package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.dto.BookCopyCreateDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookCopyDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookCopyUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.exception.BusinessLogicException;
import com.aaron212.onlinelibrarymanagement.backend.exception.DuplicateResourceException;
import com.aaron212.onlinelibrarymanagement.backend.exception.ResourceNotFoundException;
import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;
import com.aaron212.onlinelibrarymanagement.backend.repository.BookCopyRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.BookRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    public BookCopyService(BookCopyRepository bookCopyRepository, BookRepository bookRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookRepository = bookRepository;
    }

    public BookCopyDto createBookCopy(BookCopyCreateDto createDto) {
        // Verify book exists
        Book book = bookRepository
                .findById(createDto.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", createDto.bookId()));

        // Check if barcode already exists
        if (bookCopyRepository.findByBarcode(createDto.barcode()).isPresent()) {
            throw new DuplicateResourceException("BookCopy", "barcode", createDto.barcode());
        }

        BookCopy copy = new BookCopy();
        copy.setBook(book);
        copy.setBarcode(createDto.barcode());
        copy.setStatus(createDto.status());
        copy.setPurchasePrice(createDto.purchasePrice());
        copy.setPurchaseTime(createDto.purchaseTime());

        BookCopy savedCopy = bookCopyRepository.save(copy);
        return convertToDto(savedCopy);
    }

    @Transactional(readOnly = true)
    public BookCopyDto getBookCopyById(Long id) {
        BookCopy copy =
                bookCopyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("BookCopy", "id", id));
        return convertToDto(copy);
    }

    @Transactional(readOnly = true)
    public List<BookCopyDto> getCopiesByBookId(Long bookId) {
        Book book =
                bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        List<BookCopy> copies = bookCopyRepository.findByBook(book);
        return copies.stream().map(this::convertToDto).toList();
    }

    public BookCopyDto updateBookCopy(Long id, BookCopyUpdateDto updateDto) {
        BookCopy copy =
                bookCopyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("BookCopy", "id", id));

        // Check if trying to update barcode and it conflicts with existing one
        if (updateDto.barcode() != null && !updateDto.barcode().equals(copy.getBarcode())) {
            Optional<BookCopy> existingCopy = bookCopyRepository.findByBarcode(updateDto.barcode());
            if (existingCopy.isPresent() && !existingCopy.get().getId().equals(id)) {
                throw new DuplicateResourceException("BookCopy", "barcode", updateDto.barcode());
            }
            copy.setBarcode(updateDto.barcode());
        }

        if (updateDto.status() != null) {
            copy.setStatus(updateDto.status());
        }

        if (updateDto.purchasePrice() != null) {
            copy.setPurchasePrice(updateDto.purchasePrice());
        }

        if (updateDto.purchaseTime() != null) {
            copy.setPurchaseTime(updateDto.purchaseTime());
        }

        if (updateDto.lastMaintenance() != null) {
            copy.setLastMaintenance(updateDto.lastMaintenance());
        }

        BookCopy savedCopy = bookCopyRepository.save(copy);
        return convertToDto(savedCopy);
    }

    public BookCopyDto updateBookCopyStatus(Long id, BookCopy.Status status) {
        BookCopy copy =
                bookCopyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("BookCopy", "id", id));

        // Validate status transition
        if (copy.getStatus() == BookCopy.Status.BORROWED && status == BookCopy.Status.AVAILABLE) {
            throw new BusinessLogicException(
                    "Cannot change status from BORROWED to AVAILABLE directly. Use return functionality instead.");
        }

        copy.setStatus(status);

        // Set maintenance time if changing to maintenance
        if (status == BookCopy.Status.MAINTENANCE) {
            copy.setLastMaintenance(LocalDateTime.now());
        }

        BookCopy savedCopy = bookCopyRepository.save(copy);
        return convertToDto(savedCopy);
    }

    public void deleteBookCopy(Long id) {
        BookCopy copy =
                bookCopyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("BookCopy", "id", id));

        // Check if copy is currently borrowed
        if (copy.getStatus() == BookCopy.Status.BORROWED) {
            throw new BusinessLogicException("Cannot delete a book copy that is currently borrowed");
        }

        bookCopyRepository.delete(copy);
    }

    @Transactional(readOnly = true)
    public List<BookCopyDto> getAllAvailableCopies() {
        List<BookCopy> copies = bookCopyRepository.findByStatus(BookCopy.Status.AVAILABLE);
        return copies.stream().map(this::convertToDto).toList();
    }

    @Transactional(readOnly = true)
    public Optional<BookCopy> findAvailableCopyForBook(Long bookId) {
        Book book =
                bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        return bookCopyRepository.findFirstByBookAndStatus(book, BookCopy.Status.AVAILABLE);
    }

    private BookCopyDto convertToDto(BookCopy copy) {
        return new BookCopyDto(
                copy.getId(),
                copy.getBook().getId(),
                copy.getBook().getTitle(),
                copy.getBook().getIsbn(),
                copy.getBarcode(),
                copy.getStatus(),
                copy.getPurchasePrice(),
                copy.getPurchaseTime(),
                copy.getLastMaintenance(),
                copy.getCreateTime(),
                copy.getUpdateTime());
    }
}
