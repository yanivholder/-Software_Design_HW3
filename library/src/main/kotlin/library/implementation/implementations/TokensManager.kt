package library.implementation.implementations

import com.google.gson.Gson
import com.google.inject.Inject
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import library.implementation.IBasicStorageManager
import library.implementation.ITokensManager
import library.implementation.utils.TokensStorage
import java.util.*
import java.util.concurrent.CompletableFuture

class TokensManager @Inject constructor(@TokensStorage private val tokensStorage: CompletableFuture<SecureStorage>, private val basicStorageManager: IBasicStorageManager) : ITokensManager {
    private val gsonInstance = Gson();
    private val validPrefix = "valid_"
    private val invalidValue = "invalid"

    /**
     * @return a new [token] with a valid format.
     */
    override fun generateAndWriteNewToken(userName: String): CompletableFuture<String> {
        val newToken = UUID.randomUUID().toString();
        val tokenStatusAndUsernameStringJsonStringAsBytesArray = gsonInstance.toJson((validPrefix+userName)).toByteArray();
        return basicStorageManager.writeData(tokensStorage, newToken.toByteArray(), tokenStatusAndUsernameStringJsonStringAsBytesArray)
            .thenApply {
                newToken;
            }
    }

    /**
     * Invalidate [token] in the database.
     *
     * @param token the token to be invalidated.
     */
    override fun invalidateToken(token: String?): CompletableFuture<Unit> {
        if (token == null) {
            return CompletableFuture.completedFuture(Unit);
        }
        val tokenStatusAndUsernameStringJsonStringAsBytesArray = gsonInstance.toJson((invalidValue)).toByteArray();
        return basicStorageManager.writeData(tokensStorage, token.toByteArray(), tokenStatusAndUsernameStringJsonStringAsBytesArray)
    }

    /**
     * Checks validity of [token].
     *
     * @param token the token to be checked.
     *
     * @return [true] if the token is valid, [false] if the token is invalid or not found
     */
    override fun isTokenValid(token: String) : CompletableFuture<Boolean> {
        return basicStorageManager.readData(tokensStorage, token.toByteArray())
            .thenApply {
                if(it == null) {
                    false
                } else {
                    val readenDataJsonString = String(bytes = it)
                    val readenData = gsonInstance.fromJson(readenDataJsonString, String::class.java);
                    !readenData.equals(invalidValue)
                }
            }
    }

    /**
     * Get the [username] associated to the given token from the database.
     *
     * @param token the token to be searched.
     *
     * @return [String] that contains the username of the user associated with the given token if exists.
     * @return `null` if the token is invalid.
     */
    override fun getUsernameByToken(token: String): CompletableFuture<String?> {
        return basicStorageManager.readData(tokensStorage, token.toByteArray())
            .thenApply {
                if(it == null) {
                    null
                } else {
                    val usernameJsonString = String(bytes = it)
                    val retrievedValue = gsonInstance.fromJson(usernameJsonString, String::class.java);
                    if(retrievedValue.equals(invalidValue)) {
                        null
                    } else {
                        retrievedValue.drop(validPrefix.length)
                    }
                }
            }

    }
}