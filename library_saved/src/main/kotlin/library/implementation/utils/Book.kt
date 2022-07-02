package library.implementation.utils


/**
 * A class holding a single user's information in the system.
 *
 * @property id An id supplied to this book. This must be unique across all books in the system.
 * @property description A human-readable description of the book with unlimited length.
 * @property copiesAmount number of copies that will be available in the library of this book.
 */
data class Book(val id: String, val description: String, var copiesAmount: Int)
