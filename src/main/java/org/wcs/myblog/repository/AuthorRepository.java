package org.wcs.myblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wcs.myblog.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
