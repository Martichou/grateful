package me.martichou.be.grateful.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Notes(
        @ColumnInfo(name = "title")
        val title: String,

        @ColumnInfo(name = "content")
        val content: String,

        @ColumnInfo(name = "image")
        val image: String,

        @ColumnInfo(name = "date")
        val date: String,

        @ColumnInfo(name = "dateToSearch")
        val dateToSearch: String,

        @ColumnInfo(name = "location")
        val location: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}