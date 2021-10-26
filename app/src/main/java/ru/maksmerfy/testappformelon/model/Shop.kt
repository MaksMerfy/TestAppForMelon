package ru.maksmerfy.testappformelon.model

data class Shop (val id: String = "",
                 val name: String = "",
                 val active : Boolean = false,
                 val brand: Brand = Brand(),
                 val mall: Mall = Mall(),
                 val typePrice: TypePrice = TypePrice(),
                 val mail: String = "",
                 val lastUpdated: String = "",
                 val phones: String = "")