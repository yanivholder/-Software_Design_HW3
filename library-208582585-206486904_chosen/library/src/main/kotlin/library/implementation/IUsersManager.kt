package library.implementation

import library.implementation.utils.AppUser
import java.util.concurrent.CompletableFuture

interface IUsersManager {
    /**
     * Get the [AppUser] of [username] from the database.
     *
     * @param username the username of the wanted user.
     *
     * @return [AppUser] that contains the data of the associated user if exists.
     * @return `null` if the user is not found in the database.
     */
    fun getUserByUsername(username: String): CompletableFuture<AppUser?>

    /**
     * Store the [appUser] in the database.
     *
     * @param appUser the user to be stored in the database.
     */
    fun writeUserToStorage(appUser: AppUser): CompletableFuture<Unit>
}