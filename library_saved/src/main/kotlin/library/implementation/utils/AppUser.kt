package library.implementation.utils

data class AppUser(
    var user : User,
    val password : String,
    var token : String?,
)
