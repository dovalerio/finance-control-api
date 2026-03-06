package com.heitor.finance.domain.exception

sealed class DomainException(message: String) : RuntimeException(message)

class CategoryNotFoundException(id: Long) : DomainException("Category not found with id=$id")

class SubcategoryNotFoundException(id: Long) : DomainException("Subcategory not found with id=$id")

class EntryNotFoundException(id: Long) : DomainException("Entry not found with id=$id")

class InvalidPeriodException(message: String) : DomainException(message)

class CategoryAlreadyExistsException(name: String) : DomainException("Category already exists with name='$name'")
