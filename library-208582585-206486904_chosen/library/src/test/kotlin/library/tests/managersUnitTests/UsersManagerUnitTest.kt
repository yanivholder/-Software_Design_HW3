package library.tests.managersUnitTests


import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import library.implementation.IUsersManager
import library.implementation.utils.AppUser
import library.implementation.utils.User
import library.tests.modules.LibraryTestModule
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UsersManagerUnitTest {
    private val injector = Guice.createInjector(LibraryTestModule())
    private val usersManager = injector.getInstance<IUsersManager>()

    @Test
    fun `stored user can be retrieved`() {
        // Arrange
        val appUser = AppUser(User("Bukayo Saka", true, 24),"arsenal",null);
        // Action
        usersManager.writeUserToStorage(appUser).join();
        // Assert
        Assertions.assertEquals(appUser, usersManager.getUserByUsername(appUser.user.username).join());
    }

    @Test
    fun `override data for existing username`() {
        // Arrange
        val username = "Dusan Vlahovic";
        val existingAppUserData = AppUser(User(username, false, 20),"imNotJoiningArsenal","bearer ...");
        val newAppUserData = AppUser(User(username, true, 25),"imJoingJuventusInstead","1212");
        // Action
        usersManager.writeUserToStorage(existingAppUserData).join();
        usersManager.writeUserToStorage(newAppUserData).join();
        // Assert
        Assertions.assertEquals(newAppUserData, usersManager.getUserByUsername(username).join());
    }

    @Test
    fun `reading non existing user returns null`() {
        Assertions.assertNull(usersManager.getUserByUsername("Busquets").join());
    }

    @Test
    fun `appUser with more than 100 bytes stored and read successfully`() {
        // Arrange
        val over100BytesString = "Number of ballon d'ORes I have is 100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        val appUser = AppUser(User("Cristiano Ronaldo", true, 24), over100BytesString,null);
        // Action
        usersManager.writeUserToStorage(appUser).join();
        // Assert
        Assertions.assertEquals(appUser, usersManager.getUserByUsername(appUser.user.username).join());
    }
}