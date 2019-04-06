package me.martichou.be.grateful.data.model

import java.util.*

data class DatabaseObject(
    val date: Date?,
    val cryptedData: String?
){
    constructor() : this(null, null)
}