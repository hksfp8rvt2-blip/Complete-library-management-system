package com.library.management.repository;

import com.library.management.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Repository
public class LibraryRepository {
 private final JdbcTemplate jdbc;
 public LibraryRepository(JdbcTemplate jdbc){this.jdbc=jdbc;}

 public User login(String email,String password){
  List<User> x=jdbc.query("SELECT id,name,email,role,credits,penalty FROM users WHERE email=? AND password=?",
   (rs,n)->new User(rs.getInt("id"),rs.getString("name"),rs.getString("email"),rs.getString("role"),rs.getInt("credits"),rs.getDouble("penalty")),email,password);
  return x.isEmpty()?null:x.get(0);
 }
 public User getUser(int id){
  List<User> x=jdbc.query("SELECT id,name,email,role,credits,penalty FROM users WHERE id=?",
   (rs,n)->new User(rs.getInt("id"),rs.getString("name"),rs.getString("email"),rs.getString("role"),rs.getInt("credits"),rs.getDouble("penalty")),id);
  return x.isEmpty()?null:x.get(0);
 }
 public List<Book> books(){return jdbc.query("SELECT id,title,author,category,quantity,available_quantity FROM books ORDER BY id DESC",
  (rs,n)->new Book(rs.getInt("id"),rs.getString("title"),rs.getString("author"),rs.getString("category"),rs.getInt("quantity"),rs.getInt("available_quantity")));}
 public void addBook(String t,String a,String c,int q){jdbc.update("INSERT INTO books(title,author,category,quantity,available_quantity) VALUES(?,?,?,?,?)",t,a,c,q,q);}
 public void deleteBook(int id){jdbc.update("DELETE FROM books WHERE id=? AND available_quantity=quantity",id);}
 public List<User> students(){return jdbc.query("SELECT id,name,email,role,credits,penalty FROM users WHERE role='STUDENT' ORDER BY id DESC",
  (rs,n)->new User(rs.getInt("id"),rs.getString("name"),rs.getString("email"),rs.getString("role"),rs.getInt("credits"),rs.getDouble("penalty")));}
 public void addStudent(String n,String e,String p){jdbc.update("INSERT INTO users(name,email,password,role) VALUES(?,?,?,'STUDENT')",n,e,p);}
 public void deleteStudent(int id){jdbc.update("DELETE FROM users WHERE id=? AND role='STUDENT' AND NOT EXISTS(SELECT 1 FROM borrow_records WHERE student_id=? AND status='BORROWED')",id,id);}
 public boolean borrow(int sid,int bid){
  Integer av=jdbc.queryForObject("SELECT available_quantity FROM books WHERE id=?",Integer.class,bid);
  Integer active=jdbc.queryForObject("SELECT COUNT(*) FROM borrow_records WHERE student_id=? AND book_id=? AND status='BORROWED'",Integer.class,sid,bid);
  if(av==null||av<=0||active!=null&&active>0)return false;
  LocalDate d=LocalDate.now();
  jdbc.update("INSERT INTO borrow_records(student_id,book_id,borrow_date,due_date,status) VALUES(?,?,?,?, 'BORROWED')",sid,bid,d,d.plusDays(14));
  jdbc.update("UPDATE books SET available_quantity=available_quantity-1 WHERE id=?",bid); return true;
 }
 public boolean returnBook(int rid,int sid){
  List<Map<String,Object>> x=jdbc.queryForList("SELECT book_id,due_date FROM borrow_records WHERE id=? AND student_id=? AND status='BORROWED'",rid,sid);
  if(x.isEmpty())return false;
  int bid=((Number)x.get(0).get("book_id")).intValue();
  LocalDate due=((java.sql.Date)x.get(0).get("due_date")).toLocalDate(); LocalDate now=LocalDate.now();
  long late=Math.max(0,ChronoUnit.DAYS.between(due,now)); double pen=late*5.0; int credits=late==0?10:5;
  jdbc.update("UPDATE borrow_records SET return_date=?,status='RETURNED',credits_earned=?,penalty=? WHERE id=?",now,credits,pen,rid);
  jdbc.update("UPDATE users SET credits=credits+?,penalty=penalty+? WHERE id=?",credits,pen,sid);
  jdbc.update("UPDATE books SET available_quantity=available_quantity+1 WHERE id=?",bid); return true;
 }
 public List<BorrowRecord> records(int sid){return jdbc.query("SELECT r.id,r.student_id,r.book_id,b.title,r.borrow_date,r.due_date,r.return_date,r.status,r.credits_earned,r.penalty FROM borrow_records r JOIN books b ON b.id=r.book_id WHERE r.student_id=? ORDER BY r.id DESC",
  (rs,n)->new BorrowRecord(rs.getInt("id"),rs.getInt("student_id"),rs.getInt("book_id"),rs.getString("title"),rs.getString("borrow_date"),rs.getString("due_date"),rs.getString("return_date"),rs.getString("status"),rs.getInt("credits_earned"),rs.getDouble("penalty")),sid);}
 public Map<String,Object> analytics(){
  return Map.of("students",jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE role='STUDENT'",Integer.class),"books",jdbc.queryForObject("SELECT COALESCE(SUM(quantity),0) FROM books",Integer.class),"availableBooks",jdbc.queryForObject("SELECT COALESCE(SUM(available_quantity),0) FROM books",Integer.class),"borrowed",jdbc.queryForObject("SELECT COUNT(*) FROM borrow_records WHERE status='BORROWED'",Integer.class),"returned",jdbc.queryForObject("SELECT COUNT(*) FROM borrow_records WHERE status='RETURNED'",Integer.class),"penalties",jdbc.queryForObject("SELECT COALESCE(SUM(penalty),0) FROM borrow_records",Double.class));
 }
}
