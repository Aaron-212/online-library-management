package com.aaron212.onlinelibrarymanagement.backend.service;


import com.aaron212.onlinelibrarymanagement.backend.model.Book;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BorrowService {

    private Map<Long, Book> bookDB = new HashMap<>();
    private Map<Long, List<Long>> reservationQueue = new HashMap<>();
    private Map<Long, Book.BorrowRecord> borrowRecords = new HashMap<>();

    public String borrowBook(Long userId, Long bookId) {
        Book book = bookDB.get(bookId);
        if (book == null || book.getStock() <= 0) {
            return "图书不可借";
        }
        book.setStock(book.getStock() - 1);
        borrowRecords.put(bookId, new Book.BorrowRecord(userId, bookId, LocalDate.now(), LocalDate.now().plusDays(30)));
        return "借书成功，应还日期：" + LocalDate.now().plusDays(30);
    }

    public String returnBook(Long userId, Long bookId) {
        Book.BorrowRecord record = borrowRecords.get(bookId);
        if (record == null || !record.getUserId().equals(userId)) {
            return "无借阅记录";
        }
        LocalDate now = LocalDate.now();
        boolean overdue = now.isAfter(record.getDueDate());
        bookDB.get(bookId).setStock(bookDB.get(bookId).getStock() + 1);
        borrowRecords.remove(bookId);

        // 处理预约通知
        List<Long> queue = reservationQueue.getOrDefault(bookId, new ArrayList<>());
        if (!queue.isEmpty()) {
            Long nextUserId = queue.remove(0);
            sendNotification(nextUserId, "您预约的图书《" + bookDB.get(bookId).getTitle() + "》已归还，请尽快前往借阅");
        }

        return overdue ? "还书成功（已逾期）" : "还书成功";
    }

    public String renewBook(Long userId, Long bookId) {
        Book.BorrowRecord record = borrowRecords.get(bookId);
        if (record == null || !record.getUserId().equals(userId)) {
            return "无续借权限";
        }
        if (reservationQueue.containsKey(bookId) && !reservationQueue.get(bookId).isEmpty()) {
            return "图书已被预约，无法续借";
        }
        record.getDueDate=(record.getDueDate().plusDays(15));
        return "续借成功，新还书日期：" + record.getDueDate();
    }

    public String reserveBook(Long userId, Long bookId) {
        reservationQueue.putIfAbsent(bookId, new ArrayList<>());
        reservationQueue.get(bookId).add(userId);
        return "预约成功，排队序号：" + reservationQueue.get(bookId).size();
    }

    private void sendNotification(Long userId, String message) {
        // 模拟发送通知
        System.out.println("向用户 " + userId + " 发送通知：" + message);
    }
}
