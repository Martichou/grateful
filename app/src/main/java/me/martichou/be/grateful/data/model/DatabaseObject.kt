package me.martichou.be.grateful.data.model

import java.util.*

data class DatabaseObject(
    public val date: Date?,
    public val cryptedData: String?
){
    constructor() : this(null, null)
}