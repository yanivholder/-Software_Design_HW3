package library.implementation

import java.util.*
import java.util.concurrent.CompletableFuture

interface ITokensManager {
    /**
     * @return a new [token] with a valid format.
     */
    fun generateAndWriteNewToken(userName: String): CompletableFuture<String>

    /**
     * Invalidate [token] in the database.
     *
     * @param token the token to be invalidated.
     */
    fun invalidateToken(token: String?): CompletableFuture<Unit>

    /**
     * Checks validity of [token].
     *
     * @param token the token to be checked.
     *
     * @return [true] if the token is valid, [false] if the token is invalid or not found
     */
    fun isTokenValid(token: String) : CompletableFuture<Boolean>

    /**
     * Get the [username] associated to the given token from the database.
     *
     * @param token the token to be searched.
     *
     * @return [String] that contains the username of the user associated with the given token if exists.
     * @return `null` if the token is invalid.
     */
    fun getUsernameByToken(token: String): CompletableFuture<String?>
}