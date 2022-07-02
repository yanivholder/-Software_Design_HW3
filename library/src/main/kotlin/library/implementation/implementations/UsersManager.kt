package library.implementation.implementations

import com.google.gson.Gson
import com.google.inject.Inject
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import library.implementation.IBasicStorageManager
import library.implementation.IUsersManager
import library.implementation.utils.AppUser
import library.implementation.utils.UsersStorage
import java.util.concurrent.CompletableFuture

class UsersManager @Inject constructor(@UsersStorage private val usersStorage: CompletableFuture<SecureStorage>, private val basicStorageManager: IBasicStorageManager) : IUsersManager {
    private val gsonInstance = Gson();

    /**
     * Get the [AppUser] of [username] from the database.
     *
     * @param username the username of the wanted user.
     *
     * @return [AppUser] that contains the data of the associated user if exists.
     * @return `null` if the user is not found in the database.
     */
    override fun getUserByUsername(username: String): CompletableFuture<AppUser?> {
        return basicStorageManager.readData(usersStorage, username.toByteArray())
            .thenApply {
                if(it == null) {
                    null
                } else {
                    val appUserJsonString = String(bytes = it)
                    gsonInstance.fromJson(appUserJsonString, AppUser::class.java);
                }
            }
    }

    /**
     * Store the [appUser] in the database.
     *
     * @param appUser the user to be stored in the database.
     */
    override fun writeUserToStorage(appUser: AppUser): CompletableFuture<Unit> {
        val appUserJsonStringAsBytesArray = gsonInstance.toJson(appUser).toByteArray();
        return basicStorageManager.writeData(usersStorage, key = appUser.user.username.toByteArray(), appUserJsonStringAsBytesArray)
    }
}