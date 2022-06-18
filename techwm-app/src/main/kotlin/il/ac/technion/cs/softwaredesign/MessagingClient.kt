package il.ac.technion.cs.softwaredesign

import java.util.concurrent.CompletableFuture

/**
 * A message sent by [fromUser].
 * [id] should be unique over all the messages of users of a MessagingClientFactory.
 */
data class Message(val id: String, val fromUser: String, val message: String)

typealias Inbox = Map<String, List<Message>>


/**
 * This is a class implementing messaging between users
 */
class MessagingClient {
    /**
     * Login with a given password. A successfully logged-in user is considered "online". If the user is already
     * logged in, this is a no-op.
     *
     * @throws IllegalArgumentException If the password was wrong (according to the factory that created the instance)
     */
    fun login(password: String): CompletableFuture<Unit> = TODO("Implement me!")

    /**
     * Log out of the system. After logging out, a user is no longer considered online.
     *
     * @throws IllegalArgumentException If the user was not previously logged in.
     */
    fun logout(): CompletableFuture<Unit> = TODO("Implement me!")

    /**
     * Get online (logged in) users.
     *
     * @throws PermissionException If the user is not logged in.
     * @return A list of usernames which are currently online.
     */
    fun onlineUsers(): CompletableFuture<List<String>> = TODO("Implement me!")

    /**
     * Get messages currently in your inbox from other users.
     *
     * @return A mapping from usernames to lists of messages (conversations), sorted by time of sending.
     * @throws PermissionException If the user is not logged in.
     */
    fun inbox(): CompletableFuture<Inbox> = TODO("Implement me!")

    /**
     * Send a message to a username [toUsername].
     *
     * @throws PermissionException If the user is not logged in.
     * @throws IllegalArgumentException If the target user does not exist, or message contains more than 120 characters.
     */
    fun sendMessage(toUsername: String, message: String): CompletableFuture<Unit> = TODO("Implement me!")

    /**
     * Delete a message from your inbox.
     *
     * @throws PermissionException If the user is not logged in.
     * @throws IllegalArgumentException If a message with the given [id] does not exist
     */
    fun deleteMessage(id: String): CompletableFuture<Unit> = TODO("Implement me!")
}

/**
 * A factory for creating messaging clients that can send messages to each other.
 */
interface MessagingClientFactory {
    /**
     * Get an instance of a [MessagingClient] for a given username and password.
     * You can assume that different users will have different usernames.
     */
    fun get(username: String, password: String): MessagingClient
}