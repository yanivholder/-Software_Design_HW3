package il.ac.technion.cs.softwaredesign

import Library
import java.util.concurrent.CompletableFuture
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import javax.inject.Inject
import kotlin.system.exitProcess

fun deserialize(byteArray: ByteArray): Any {
    val bais = ByteArrayInputStream(byteArray)
    val ois = ObjectInputStream(bais)
    return ois.readObject()
}

fun serialize(value: Any): ByteArray {
    val baos = ByteArrayOutputStream()
    val oos = ObjectOutputStream(baos)
    oos.writeObject(value)
    return baos.toByteArray()
}

/**
 * A message sent by [fromUser].
 * [id] should be unique over all the messages of users of a MessagingClientFactory.
 */
data class Message(val id: String, val fromUser: String, val message: String) : java.io.Serializable

typealias Inbox = Map<String, List<Message>>
typealias MutableInbox = MutableMap<String, List<Message>>
const val inboxSuffix = "-inbox"
const val IDCounter = "ID-counter"


/**
 * This is a class implementing messaging between users
 */
class MessagingClient constructor(
    var library: Library,
    val username: String,
    private val password: String,
    var loggedList: MutableList<String>
    ) : java.io.Serializable {

    private fun amILoggedIn(): Boolean = this.loggedList.contains(this.username)

    /**
     * Login with a given password. A successfully logged-in user is considered "online". If the user is already
     * logged in, this is a no-op.
     *
     * @throws IllegalArgumentException If the password was wrong (according to the factory that created the instance)
     */
    fun login(password: String): CompletableFuture<Unit> {
        if (password != this.password) throw IllegalArgumentException()
        if (!this.amILoggedIn()) {
            this.loggedList.add(this.username)
        }
        return CompletableFuture.completedFuture(Unit)
    }

    /**
     * Log out of the system. After logging out, a user is no longer considered online.
     *
     * @throws IllegalArgumentException If the user was not previously logged in.
     */
    fun logout(): CompletableFuture<Unit> {
        if (!this.amILoggedIn()) throw IllegalArgumentException()

        this.loggedList.remove(this.username)
        return CompletableFuture.completedFuture(Unit)
    }

    /**
     * Get online (logged in) users.
     *
     * @throws PermissionException If the user is not logged in.
     * @return A list of usernames which are currently online.
     */
    fun onlineUsers(): CompletableFuture<List<String>> {
        if (!this.amILoggedIn()) throw PermissionException()

        return CompletableFuture.completedFuture(
            this.loggedList.toList()
        )
    }

    /**
     * Get messages currently in your inbox from other users.
     *
     * @return A mapping from usernames to lists of messages (conversations), sorted by time of sending.
     * @throws PermissionException If the user is not logged in.
     */
    fun inbox(): CompletableFuture<Inbox> {
        if (!this.amILoggedIn()) throw PermissionException()

        return this.library.read("${this.username}$inboxSuffix")
            .thenApply { res ->
                if (res == null) exitProcess(1) // BUG
                else {
                    (deserialize(res) as MutableInbox).toMap()
                }
            }
    }

    /**
     * Send a message to a username [toUsername].
     *
     * @throws PermissionException If the user is not logged in.
     * @throws IllegalArgumentException If the target user does not exist, or message contains more than 120 characters.
     */
    fun sendMessage(toUsername: String, message: String): CompletableFuture<Unit> {
        if (!this.amILoggedIn()) throw PermissionException()
        if (message.length > 120) throw IllegalArgumentException()

        return this.library.read("$toUsername$inboxSuffix")
            .thenCompose { serializedInbox ->
                if (serializedInbox == null) throw IllegalArgumentException()

                val mutableInbox = deserialize(serializedInbox) as MutableInbox
                val mutableInboxFromUser = if (mutableInbox.containsKey(this.username)) {
                    mutableInbox[this.username]!!.toMutableList()
                } else {
                    mutableListOf()
                }
                this.library.read(IDCounter).thenCompose { serializedCounter ->
                    if (serializedCounter == null) exitProcess(1) // BUG

                    val counter = deserialize(serializedCounter) as Int
                    mutableInboxFromUser.add(Message(
                        id = counter.toString(),
                        fromUser = this.username,
                        message = message
                    ))
                    mutableInbox[this.username] = mutableInboxFromUser.toList()
                    this.library.write(key = IDCounter, value = serialize(counter + 1))
                        .thenCompose { this.library.write(key = "$toUsername$inboxSuffix", value = serialize(mutableInbox)) }
                }
            }
    }

    /**
     * Delete a message from your inbox.
     *
     * @throws PermissionException If the user is not logged in.
     * @throws IllegalArgumentException If a message with the given [id] does not exist
     */
    fun deleteMessage(id: String): CompletableFuture<Unit> {
        if (!this.amILoggedIn()) throw PermissionException()

        return this.library.read("${this.username}$inboxSuffix")
            .thenCompose { serializedInbox ->
                if (serializedInbox == null) throw IllegalArgumentException()

                val mutableInbox = deserialize(serializedInbox) as MutableInbox
                var newMutableInbox = mutableInbox.mapValues {
                        v -> v.value.filter { message -> message.id != id }
                }.toMutableMap()
                newMutableInbox = newMutableInbox.filter { entry -> entry.value.isNotEmpty() }.toMutableMap()
                // Check if a message with the given id erased
                if (mutableInbox == newMutableInbox) throw IllegalArgumentException()
                this.library.write(
                    key = "${this.username}$inboxSuffix",
                    value = serialize(newMutableInbox)
                )
            }
    }
}

/**
 * A factory for creating messaging clients that can send messages to each other.
 */
interface MessagingClientFactory {
    /**
     * Get an instance of a [MessagingClient] for a given username and password.
     * You can assume that:
     * 1. different clients will have different usernames.
     * 2. calling get for the first time creates a user with [username] and [password].
     * 3. calling get for an existing client (not the first time) is called with the right password.
     *
     * About persistence:
     * All inboxes of clients should be persistent.
     * Note: restart == a new instance is created and the previous one is not used.
     * When MessagingClientFactory restarts all users should be logged off.
     * When a MessagingClient restarts (another instance is created with [MessagingClientFactory]'s [get]), only the
     *  specific user is logged off.
     */
    fun get(username: String, password: String): CompletableFuture<MessagingClient>
}

class MessagingClientFactoryImpl @Inject constructor(private val library: Library) : MessagingClientFactory {
    private val loggedList: MutableList<String> = mutableListOf()
    init {
        // Making sure there is a persistent ID counter
        this.library.read(IDCounter).thenCompose { serializedCounter ->
            if (serializedCounter == null) this.library.write(IDCounter, serialize(0))
            else CompletableFuture.completedFuture(Unit)
        }.get()
    }

    override fun get(username: String, password: String): CompletableFuture<MessagingClient> {
        return this.library.read(username)
            .thenCompose { res ->
                if (res != null) {
                    val client = deserialize(res) as MessagingClient
                    client.loggedList = this.loggedList
                    client.library = this.library
                    // Making sure the current user is logged off
                    this.loggedList.remove(client.username)
                    CompletableFuture.completedFuture(client)
                } else {
                    val client = MessagingClient(
                        library = this.library,
                        username = username,
                        password = password,
                        loggedList = this.loggedList
                    )
                    this.library.write(key = username, value = serialize(client))
                        .thenCompose {
                            this.library.write(
                                key = "$username$inboxSuffix",
                                value = serialize(mutableMapOf<String, List<Message>>())
                            )
                        }
                        .thenApply { client }
                }
            }
    }
}