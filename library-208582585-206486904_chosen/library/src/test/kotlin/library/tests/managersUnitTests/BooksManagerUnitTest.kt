package library.tests.managersUnitTests


import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import library.implementation.IBooksManager
import library.implementation.utils.Book
import library.tests.modules.LibraryTestModule
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BooksManagerUnitTest {
    private val injector = Guice.createInjector(LibraryTestModule())
    private val booksManager = injector.getInstance<IBooksManager>()

    @Test
    fun `stored book can be retrieved`() {
        // Arrange
        val book = Book("The Holy Quran", "This is the holy book of Muslim people", 1);
        // Action
        booksManager.writeBookToStorage(book).join();
        // Assert
        Assertions.assertEquals(book, booksManager.getBookById(book.id).join());
    }

    @Test
    fun `override data for existing book`() {
        // Arrange
        val bookId = "a book to override";
        val existingBookData = Book(bookId, "first description", 1);
        val newBookData = Book(bookId, "second description", 2);
        // Action
        booksManager.writeBookToStorage(existingBookData).join();
        booksManager.writeBookToStorage(newBookData).join();
        // Assert
        Assertions.assertEquals(newBookData, booksManager.getBookById(bookId).join());
    }

    @Test
    fun `reading non existing book returns null`() {
        Assertions.assertNull(booksManager.getBookById("non existing book id").join());
    }

    @Test
    fun `book with more than 100 bytes stored and read successfully`() {
        // Arrange
        val over100BytesString = "this string is very looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong";
        val bookWithMuchData = Book("A book name", over100BytesString, 1);
        // Action
        booksManager.writeBookToStorage(bookWithMuchData).join();
        // Assert
        Assertions.assertEquals(bookWithMuchData, booksManager.getBookById(bookWithMuchData.id).join());
    }

    @Test
    fun `return the correct ordered book`() {
        // Arrange
        val book0 = Book("The Holy Quran", "This is the holy book of Muslim people", 1);
        val book1 = Book("The Tanach", "This is the holy book of Jewish people", 1);
        val book2 = Book("The Bible", "This is the holy book of Christian people", 1);
        // Action
        booksManager.writeBookToStorage(book0).join();
        booksManager.writeBookToStorage(book1).join();
        booksManager.writeBookToStorage(book2).join();
        // Assert
        Assertions.assertEquals(book1.id, booksManager.getBookIdByOrder(1).join());
    }
}