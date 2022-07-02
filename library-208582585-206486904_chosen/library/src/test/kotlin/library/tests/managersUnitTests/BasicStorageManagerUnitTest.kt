package library.tests.managersUnitTests


import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import library.implementation.IBasicStorageManager
import library.tests.modules.BasicStorageTestModule
import org.junit.jupiter.api.*
import java.util.concurrent.CompletableFuture

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BasicStorageManagerUnitTest {
    private val injector = Guice.createInjector(BasicStorageTestModule())
    private val secureStorage = CompletableFuture.completedFuture(injector.getInstance<SecureStorage>())
    private val basicStorageManager = injector.getInstance<IBasicStorageManager>()

    @Test
    fun `stored data can be retrieved`() {
        // Arrange
        val key = "a test key".toByteArray();
        val value = "a value test".toByteArray();
        // Action
        basicStorageManager.writeData(secureStorage, key, value).get()
        // Assert
        Assertions.assertTrue(value.contentEquals(basicStorageManager.readData(secureStorage, key).get()));
    }

    @Test
    fun `override data for existing key`() {
        // Arrange
        val key = "a test key".toByteArray();
        val oldValue = "old value test".toByteArray();
        val newValue = "new value test".toByteArray();
        // Action
        basicStorageManager.writeData(secureStorage, key, oldValue).get();
        basicStorageManager.writeData(secureStorage, key, newValue).get();
        // Assert
        Assertions.assertTrue(newValue.contentEquals(basicStorageManager.readData(secureStorage, key).get()));
    }

    @Test
    fun `reading non existing key returns null`() {
        Assertions.assertNull(basicStorageManager.readData(secureStorage, "non existing key".toByteArray()).get());
    }

    @Test
    fun `data with more than 100 bytes stored and read successfully`() {
        // Arrange
        val key = "a test key".toByteArray();
        val over100BytesValue = "this string is very looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong".toByteArray();
        // Action
        basicStorageManager.writeData(secureStorage, key, over100BytesValue).get()
        // Assert
        Assertions.assertTrue(over100BytesValue.contentEquals(basicStorageManager.readData(secureStorage, key).get()));
    }
}