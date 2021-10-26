package ru.maksmerfy.testappformelon.viewmodel

import ru.maksmerfy.testappformelon.model.Shop

//да странное название класса, но честно не знал как еще назвать, но как получилось
class CityMallBrand {
    val listOfCityMallBrand: MutableMap<String, MutableMap<String, MutableList<String>>> = mutableMapOf()

    fun fillListOfCityMallBrand(shops: List<Shop>){
        for (shop in shops){
            var valueOfKey: MutableMap<String, MutableList<String>> = mutableMapOf()
            var listOfBrand: MutableList<String> = mutableListOf<String>()

            if (listOfCityMallBrand.containsKey(shop.mall.city.name)) {
                valueOfKey = listOfCityMallBrand[shop.mall.city.name]!!
            }
            if (valueOfKey.containsKey(shop.mall.name)) {
                listOfBrand = valueOfKey.getValue(shop.mall.name)
            }
            listOfBrand.add(shop.brand.name)
            valueOfKey.set(shop.mall.name, listOfBrand)

            listOfCityMallBrand[shop.mall.city.name] = valueOfKey
        }
    }
}