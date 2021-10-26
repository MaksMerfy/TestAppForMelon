package ru.maksmerfy.testappformelon.model

data class Mall (   val id: String = "",
                    val name: String = "",
                    val fullName: String = "",
                    val city: City = City(),
                    val address: String = "")