package ru.maksmerfy.testappformelon.viewmodel

import com.google.gson.GsonBuilder
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.cio.*
import kotlinx.coroutines.runBlocking
import ru.maksmerfy.testappformelon.model.Shop
import java.lang.Exception

class Shops {
    var listofShop: List<Shop> = listOf()

    //Найдем магазин по айди
    fun findShopById(id: String): Shop{
        var shop:Shop = Shop()
        shop = listofShop.find { it -> it.id == id } ?: Shop()
        return shop
    }

    //заполним listofShop полученными данными от сервера
    fun fillShops(): String{
        var responseString : String = ""
        try {
            val response = runBlocking {
                loginUserOnServer()
            }
            if (response.status.value == 200) {
                val responseBody: String = runBlocking {
                    reeiveResponseBody(response)
                }
                val gson = GsonBuilder().create()
                listofShop = gson.fromJson(responseBody, Array<Shop>::class.java).toList()
                responseString = "Shops load success"
            }
            else {
                responseString = response.status.toString()
            }
        } catch (e: Exception) {
            responseString = e.message ?:"Can't load sops from server"}
        return responseString
    }

    suspend fun loginUserOnServer(): HttpResponse {
        val client = HttpClient()
        val response: HttpResponse = client.get("https://esb-dev.melonfashion.ru/api/3pl/etc/v1/stores") {
            headers.append ("Authorization", "Basic ZHBob25ldXNyOmxoVkFpa3U4cnM=")
        }
        client.close()
        return response
    }

    suspend fun reeiveResponseBody(response: HttpResponse) : String {
        return response.receive()
    }
}