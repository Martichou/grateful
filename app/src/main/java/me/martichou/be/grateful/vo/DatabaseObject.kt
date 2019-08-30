package me.martichou.be.grateful.vo

import java.util.*

data class DatabaseObject(
        public val date: Date?,
        public val cryptedData: String?
) {
    constructor() : this(null, null)
}