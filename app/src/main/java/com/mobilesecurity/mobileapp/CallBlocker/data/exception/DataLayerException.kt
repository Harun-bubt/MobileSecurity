package com.mobilesecurity.mobileapp.CallBlocker.data.exception

class DataLayerException(message: String? = null) :
    Exception(message ?: "Something went wrong while interacting with the datasource")