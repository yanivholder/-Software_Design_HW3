package il.ac.technion.cs.softwaredesign

import dev.misfitlabs.kotlinguice4.KotlinModule
import Library

class MessagingClientModule: KotlinModule() {
    override fun configure() {
//        install(LibraryModule())
        bind<Library>().to<LibraryFake>()

        bind<MessagingClientFactory>().to<MessagingClientFactoryImpl>()
    }
}