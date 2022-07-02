package library.implementation

import library.implementation.utils.Book
import java.util.concurrent.CompletableFuture

interface ILibWrapper {
    fun write(key: String, value: ByteArray): CompletableFuture<Unit>

    fun read(key: String): CompletableFuture<ByteArray?>
}
