package com.example.gestioninterim.models

class UserModel(
    val firstName: String? = null,
    val lastName: String? = null,
    val nationality: String? = null,
    val birthDate: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val password: String? = null,
    val city: String? = null,
    val cv: String? = null,
    val comments: String? = null,
    val role: String? = null,
    val companyName: String? = null,
    val address: String? = null,
    val links: String? = null,

) {
    // Constructeur sans argument requis par Firebase Database
    constructor() : this("", "", "", "", "", "", "", "", "", "")
}
