package com.library.management.model;
public record BorrowRecord(int id,int studentId,int bookId,String title,String borrowDate,String dueDate,String returnDate,String status,int creditsEarned,double penalty) {}
