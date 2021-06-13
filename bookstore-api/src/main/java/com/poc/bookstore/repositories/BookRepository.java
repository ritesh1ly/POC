package com.poc.bookstore.repositories;

import org.springframework.data.repository.CrudRepository;

import com.poc.bookstore.entity.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

}