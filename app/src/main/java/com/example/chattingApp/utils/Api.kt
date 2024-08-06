package com.example.chattingApp.utils

enum class Api(private val url: String) {
    PNS_BASE_URL("https://d526e7a7-848f-46fd-8b82-5d832ee3c74c-00-2k6otxeyn5tz3.worf.replit.dev/"),
    TIME_OUT("9");

    fun getValue(): String{
        return url
    }
}
