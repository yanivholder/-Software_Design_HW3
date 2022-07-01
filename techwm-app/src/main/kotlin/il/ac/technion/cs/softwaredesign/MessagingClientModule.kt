package il.ac.technion.cs.softwaredesign

import Library
import dev.misfitlabs.kotlinguice4.KotlinModule

class MessagingClientModule: KotlinModule() {
    override fun configure() {
//        install(LibraryModule())
        bind<Library>().to<LibraryFake>()

        bind<MessagingClientFactory>().to<MessagingClientFactoryImpl>()
    }
}