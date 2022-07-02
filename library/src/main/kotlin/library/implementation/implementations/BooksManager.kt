package library.implementation.implementations

import com.google.gson.Gson
import com.google.inject.Inject
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import library.implementation.IBasicStorageManager
import library.implementation.IBooksManager
import library.implementation.utils.Book
import library.implementation.utils.BooksStorage
import java.util.concurrent.CompletableFuture
import java.util.function.BiFunction

class BooksManager @Inject constructor(@BooksStorage private val booksStorage: CompletableFuture<SecureStorage>, private val basicStorageManager: IBasicStorageManager) : IBooksManager {
    private val gsonInstance = Gson();
    private val counterKey = "counter"
    private val bookKeyPrefix = "book_"
    private val sortKeyPrefix = "sort_"

    /**
     * Get the [Book] with the given book [id] from the database.
     *
     * @param id the id of the wanted book.
     *
     * @return The searched [Book] if found.
     * @return `null` if the book is not found in the database.
     */
    override fun getBookById(id: String): CompletableFuture<Book?> {
        return basicStorageManager.readData(booksStorage, (bookKeyPrefix + id).toByteArray())
            .thenApply {
                if(it == null){
                    null
                } else {
                    val bookJsonString = String(bytes = it)
                    gsonInstance.fromJson(bookJsonString, Book::class.java);
                }
            }
    }

    /**
     * Store the [book] in the database.
     *
     * @param book the book to be stored in the database.
     */
    override fun writeBookToStorage(book: Book): CompletableFuture<Unit> {
        val bookJsonStringAsBytesArray = gsonInstance.toJson(book).toByteArray();
        val future1 = basicStorageManager.writeData(booksStorage, (bookKeyPrefix + book.id).toByteArray(), bookJsonStringAsBytesArray);

        val future2 = basicStorageManager.readData(booksStorage, counterKey.toByteArray())
            .thenCompose {
                var counter = 0;
                if(it != null) {
                    counter = gsonInstance.fromJson(String(bytes = it), Int::class.java);
                }
                val future2_1 = basicStorageManager.writeData(booksStorage, (sortKeyPrefix + counter).toByteArray(), book.id.toByteArray());
                val future2_2 = basicStorageManager.writeData(booksStorage, counterKey.toByteArray(), gsonInstance.toJson(counter + 1).toByteArray());
                future2_1.thenCombine(future2_2, BiFunction { _, _ -> })
            };

        return future1.thenCombine(future2, BiFunction { _, _ -> });
    }

    /**
     * Get the [BookId] with the given insert [order] from the database.
     *
     * @param order the wanted order.
     *
     * @return The searched [Book] if found.
     * @return `null` if the book is not found in the database.
     */
    override fun getBookIdByOrder(order: Int): CompletableFuture<String?> {
        return basicStorageManager.readData(booksStorage, (sortKeyPrefix + order).toByteArray())
            .thenApply {
                if (it == null) {
                    null;
                } else {
                    String(bytes = it);
                }
            }
    }

    /**
     * Reduces the [Book.copiesAmount] by 1.
     *
     * @param bookId the id of the book to return.
     *
     * @throws IllegalArgumentException if [bookId] is not a pre-added book to the catalog, or if there are not any
     * available copies in the library.
     */
    override fun loanBook(bookId: String): CompletableFuture<Unit> {
        return this.getBookById(bookId)
            .thenApply { bookData ->
                if(bookData == null || bookData.copiesAmount <=0) {
                    throw IllegalArgumentException()
                }
                bookData.copiesAmount--
                this.writeBookToStorage(bookData)
            }
    }

    /**
     * Increases the [Book.copiesAmount] by 1.
     *
     * @param bookId the id of the book to return.
     *
     * @throws IllegalArgumentException if [bookId] is not a pre-added book to the catalog.
     */
    override fun returnBook(bookId: String): CompletableFuture<Unit> {
        return this.getBookById(bookId)
            .thenApply { bookData ->
                if(bookData == null) {
                    throw IllegalArgumentException()
                }
                bookData.copiesAmount++
                this.writeBookToStorage(bookData)
            }
    }
}