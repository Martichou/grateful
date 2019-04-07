package me.martichou.be.grateful.data.model

import java.util.*

class DatabaseObject internal constructor (
    public val date: Date?,
    public val cryptedData: String?
): Serializable {
    constructor() : this(null, null)
}
