package library.implementation
import java.util.concurrent.CompletableFuture

interface Library : java.io.Serializable {
    fun write(key: String, value: ByteArray): CompletableFuture<Unit>

    fun read(key: String): CompletableFuture<ByteArray?>
}