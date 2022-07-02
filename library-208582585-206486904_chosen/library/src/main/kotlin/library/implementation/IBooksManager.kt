package library.implementation

import library.implementation.utils.Book
import java.util.concurrent.CompletableFuture

interface IBooksManager {
    /**
     * Get the [Book] with the given book [id] from the database.
     *
     * @param id the id of the wanted book.
     *
     * @return The searched [Book] if found.
     * @return `null` if the book is not found in the database.
     */
    fun getBookById(id: String): CompletableFuture<Book?>

    /**
     * Store the [book] in the database.
     *
     * @param book the book to be stored in the database.
     */
    fun writeBookToStorage(book: Book): CompletableFuture<Unit>

    /**
     * Get the [BookId] with the given insert [order] from the database.
     *
     * @param order the wanted order.
     *
     * @return The searched [Book] if found.
     * @return `null` if the book is not found in the database.
     */
    fun getBookIdByOrder(order: Int): CompletableFuture<String?>

    /**
     * Reduces the [Book.copiesAmount] by 1.
     *
     * @param bookId the id of the book to return.
     *
     * @throws IllegalArgumentException if [bookId] is not a pre-added book to the catalog, or if there are not any
     * available copies in the library.
     */
    fun loanBook(bookId: String): CompletableFuture<Unit>

    /**
     * Increases the [Book.copiesAmount] by 1.
     *
     * @param bookId the id of the book to return.
     *
     * @throws IllegalArgumentException if [bookId] is not a pre-added book to the catalog.
     */
    fun returnBook(bookId: String): CompletableFuture<Unit>
}