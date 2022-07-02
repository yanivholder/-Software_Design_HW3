package il.ac.technion.cs.softwaredesign

import com.google.inject.Guice
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.misfitlabs.kotlinguice4.getInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class IntegrationalTest {
    private val injector = Guice.createInjector(MessagingClientModule())
    private var clientFactory = injector.getInstance<MessagingClientFactory>()

    @Test
    fun `Delete message works`() {
        val msg = "Hello, Patrick!"

        val spongebob = clientFactory.get("spongebob", "pass").get()
        val squidwid = clientFactory.get("squidwid", "pass").get()
        val patrick = clientFactory.get("patrick", "pass").get()
        spongebob.login("pass")
            .thenCompose { patrick.login("pass") }
            .thenCompose { spongebob.sendMessage("patrick", msg) }
            .thenCompose { patrick.inbox() }
            .thenAccept { inbox ->
                assertThat(inbox.size, equalTo(1))
                assertThat(inbox["spongebob"]!![0].message, equalTo(msg))
            }
            .thenCompose { squidwid.login("pass") }
            .thenCompose { squidwid.sendMessage("patrick", "squid") }
            .thenCompose { patrick.inbox() }
            .thenAccept { inbox ->
                assertThat(inbox.size, equalTo(2))
                assertThat(inbox["spongebob"]!![0].message, equalTo(msg))
                assertThat(inbox["squidwid"]!![0].message, equalTo("squid"))
            }
            .thenCompose { patrick.deleteMessage("0") }
            .thenCompose { patrick.inbox() }
            .thenAccept { inbox ->
                assertThat(inbox.size, equalTo(1))
                assertThat(inbox["squidwid"]!![0].message, equalTo("squid"))
            }
            .join()
    }

    @Test
    fun `A user cant send a message when not logged in`() {
        val msg = "Hello, Patrick!"

        val spongebob = clientFactory.get("spongebob", "pass").get()
        val patrick = clientFactory.get("patrick", "pass").get()
        assertThrows<PermissionException> {
            spongebob.sendMessage("patrick", msg)
        }
    }

    @Test
    fun `Online test`() {
        val spongebob = clientFactory.get("spongebob", "pass").get()
        val patrick = clientFactory.get("patrick", "pass").get()

        spongebob.login("pass")
            .thenCompose { spongebob.onlineUsers() }
            .thenApply { loggedList ->
                assertThat(loggedList.size, equalTo(1))
            }
            .thenCompose { patrick.login("pass") }
            .thenCompose { spongebob.onlineUsers() }
            .thenApply { loggedList ->
                assertThat(loggedList.size, equalTo(2))
            }
            .thenCompose { patrick.onlineUsers() }
            .thenApply { loggedList ->
                assertThat(loggedList.size, equalTo(2))
            }
            .thenCompose { patrick.logout() }
            .thenCompose { spongebob.onlineUsers() }
            .thenApply { loggedList ->
                assertThat(loggedList.size, equalTo(1))
            }
            .thenCompose { patrick.onlineUsers() }
            .thenApply { loggedList ->
                assertThat(loggedList.size, equalTo(1))
            }
    }

    @Test
    fun `Online restarts`() {
        var spongebob = clientFactory.get("spongebob", "pass").get()
        var patrick = clientFactory.get("patrick", "pass").get()

        spongebob.login("pass")
            .thenCompose { spongebob.onlineUsers() }
            .thenApply { loggedList ->
                assertThat(loggedList.size, equalTo(1))
            }
            .thenCompose { patrick.login("pass") }
            .thenCompose { spongebob.onlineUsers() }
            .thenApply { loggedList ->
                assertThat(loggedList.size, equalTo(2))
            }
            .thenCompose { clientFactory.get("patrick", "pass") }
            .thenApply { newPatrick ->
                patrick = newPatrick
            }
            .thenCompose { spongebob.onlineUsers() }
            .thenApply { loggedList ->
                assertThat(loggedList.size, equalTo(1))
            }
            .join()

        clientFactory = injector.getInstance<MessagingClientFactory>()

        spongebob = clientFactory.get("spongebob", "pass").get()
        patrick = clientFactory.get("patrick", "pass").get()

        spongebob.login("pass")
            .thenCompose { spongebob.onlineUsers() }
            .thenApply { loggedList ->
                assertThat(loggedList.size, equalTo(1))
            }
            .thenCompose { spongebob.login("pass") }
            .thenCompose { spongebob.onlineUsers() }
            .thenApply { loggedList ->
                assertThat(loggedList.size, equalTo(1))
            }
            .thenCompose { patrick.login("pass") }
            .thenCompose { spongebob.onlineUsers() }
            .thenApply { loggedList ->
                assertThat(loggedList.size, equalTo(2))
            }
            .thenCompose { clientFactory.get("patrick", "pass") }
            .thenCompose { spongebob.onlineUsers() }
            .thenApply { loggedList ->
                assertThat(loggedList.size, equalTo(1))
            }
            .join()
    }
}