package com.library.management.controller;
import com.library.management.model.User;
import com.library.management.repository.LibraryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LibraryController {
 private final LibraryRepository repo;
 public LibraryController(LibraryRepository repo){this.repo=repo;}
 @PostMapping("/auth/login") public ResponseEntity<?> login(@RequestBody Map<String,String> b){User u=repo.login(b.get("email"),b.get("password"));return u==null?ResponseEntity.status(401).body(Map.of("message","Invalid email or password")):ResponseEntity.ok(u);}
 @GetMapping("/user/{id}") public Object user(@PathVariable int id){return repo.getUser(id);}
 @GetMapping("/books") public Object books(){return repo.books();}
 @PostMapping("/books") public Object addBook(@RequestBody Map<String,Object> b){repo.addBook(String.valueOf(b.get("title")),String.valueOf(b.get("author")),String.valueOf(b.get("category")),Integer.parseInt(String.valueOf(b.get("quantity"))));return Map.of("message","Book added successfully");}
 @DeleteMapping("/books/{id}") public Object deleteBook(@PathVariable int id){repo.deleteBook(id);return Map.of("message","Book removed if it was not currently borrowed");}
 @GetMapping("/students") public Object students(){return repo.students();}
 @PostMapping("/students") public Object addStudent(@RequestBody Map<String,String> b){repo.addStudent(b.get("name"),b.get("email"),b.get("password"));return Map.of("message","Student added successfully");}
 @DeleteMapping("/students/{id}") public Object deleteStudent(@PathVariable int id){repo.deleteStudent(id);return Map.of("message","Student removed if they have no active borrow");}
 @PostMapping("/borrow") public ResponseEntity<?> borrow(@RequestBody Map<String,Object> b){boolean ok=repo.borrow(Integer.parseInt(String.valueOf(b.get("studentId"))),Integer.parseInt(String.valueOf(b.get("bookId"))));return ok?ResponseEntity.ok(Map.of("message","Book borrowed successfully")):ResponseEntity.badRequest().body(Map.of("message","Book unavailable or already borrowed by this student"));}
 @PostMapping("/return") public ResponseEntity<?> ret(@RequestBody Map<String,Object> b){boolean ok=repo.returnBook(Integer.parseInt(String.valueOf(b.get("recordId"))),Integer.parseInt(String.valueOf(b.get("studentId"))));return ok?ResponseEntity.ok(Map.of("message","Book returned successfully")):ResponseEntity.badRequest().body(Map.of("message","Return failed"));}
 @GetMapping("/student/{id}/records") public Object records(@PathVariable int id){return repo.records(id);}
 @GetMapping("/analytics") public Object analytics(){return repo.analytics();}
}
