package com.example.phonepe.ui.navigation

object Screen {
    object Payment {
        const val route = "payment/{name}/{upiId}?qrData={qrData}"
        fun createRoute(name: String, upiId: String, qrData: String? = null): String {
            return if (qrData != null) {
                "payment/$name/$upiId?qrData=$qrData"
            } else {
                "payment/$name/$upiId"
            }
        }
    }
}

