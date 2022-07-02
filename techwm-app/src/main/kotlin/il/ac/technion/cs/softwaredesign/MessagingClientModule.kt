package il.ac.technion.cs.softwaredesign

import dev.misfitlabs.kotlinguice4.KotlinModule
import library.implementation.ILibWrapper
import library.implementation.implementations.LibWrapper

class MessagingClientModule: KotlinModule() {
    override fun configure() {
//        install(LibraryModule())
        bind<ILibWrapper>().to<LibWrapper>()

        bind<MessagingClientFactory>().to<MessagingClientFactoryImpl>()
    }
}