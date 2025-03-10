package org.wcs.myblog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wcs.myblog.dto.AuthorDTO;
import org.wcs.myblog.model.Author;
import org.wcs.myblog.service.AuthorService;

@RestController
@RequestMapping("/authors")
public class AuthorController {
  private final AuthorService authorService;

  public AuthorController(AuthorService authorService) {
    this.authorService = authorService;
  }

  @GetMapping
  public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
    List<AuthorDTO> authorDTOs = authorService.getAllAuthors();
    if (authorDTOs.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(authorDTOs);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
    AuthorDTO authorDTO = authorService.getAuthorById(id);
    if (authorDTO == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(authorDTO);
  }

  @PostMapping
  public ResponseEntity<AuthorDTO> createAuthor(@RequestBody Author author) {
    AuthorDTO savedAuthor = authorService.createAuthor(author);
    return ResponseEntity.status(201).body(savedAuthor);
  }

  @PutMapping("/{id}")
  public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable Long id, @RequestBody Author authorDetails) {
    AuthorDTO updatedAuthor = authorService.updateAuthor(id, authorDetails);
    if (updatedAuthor == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(updatedAuthor);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
    if (authorService.deleteAuthor(id)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
