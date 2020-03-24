package com.poc.livraria.livraria.api.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String isbn) {
        super(isbn);
    }
}
