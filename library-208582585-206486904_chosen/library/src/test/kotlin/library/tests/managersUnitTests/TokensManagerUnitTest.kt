package library.tests.managersUnitTests


import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import library.implementation.ITokensManager
import library.tests.modules.LibraryTestModule
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class TokensManagerUnitTest {
    private val injector = Guice.createInjector(LibraryTestModule())
    private val tokensManager = injector.getInstance<ITokensManager>()

    @Test
    fun `new token generated and stored successfully`() {
        // Arrange & Action
        val newToken = tokensManager.generateAndWriteNewToken("aUserName").join();
        // Assert
        Assertions.assertTrue(tokensManager.isTokenValid(newToken).join());
    }

    @Test
    fun `invalidate existing token test`() {
        // Arrange
        val tokenToInvalidate = tokensManager.generateAndWriteNewToken("aUserName").join();
        // Action
        tokensManager.invalidateToken(tokenToInvalidate).join();
        // Assert
        Assertions.assertFalse(tokensManager.isTokenValid(tokenToInvalidate).join());
    }

    @Test
    fun `non existing token is invalid`() {
        Assertions.assertFalse(tokensManager.isTokenValid("non existing token").join());
    }

    @Test
    fun `retrieving username of invalid token returns null`() {
        Assertions.assertNull(tokensManager.getUsernameByToken("anInvalidToken").join());
    }

    @Test
    fun `retrieving username of invalidated (was valid but invalidated) token returns null`() {
        // Arrange
        val tokenToInvalidate = tokensManager.generateAndWriteNewToken("aUserName").join();
        // Action
        tokensManager.invalidateToken(tokenToInvalidate).join();
        // Assert
        Assertions.assertNull(tokensManager.getUsernameByToken(tokenToInvalidate).join());
    }

    @Test
    fun `retrieving username of valid token returns correct username`() {
        // Arrange
        val userName = "aUserName"
        val aValidToken = tokensManager.generateAndWriteNewToken(userName).join();
        Assertions.assertEquals(userName, tokensManager.getUsernameByToken(aValidToken).join());
    }

    @Test
    fun `stores data of over 100 bytes correctly`() {
        // Arrange
        val userName = "This string is very looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong"
        val aValidToken = tokensManager.generateAndWriteNewToken(userName).join();
        val res = tokensManager.getUsernameByToken(aValidToken).join();
        Assertions.assertEquals(userName, res);
    }
}