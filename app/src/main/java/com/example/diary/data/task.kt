package com.example.diary.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.sql.Timestamp
import java.time.Instant.now
import java.time.LocalDateTime
import java.util.*

open class Task() : RealmObject() {

    var id: Int = 0

    var dateStart: Date = Date(1,1,1, 1,1,1)
    var dateFinish: Date =  Date(1,1,1, 1,1,1)
    var name: String = ""
    var description: String = ""
    constructor(id: Int,
                dateStart: Date,
                dateFinish: Date,
                name: String,
                description: String): this() {
        this.id = id
        this.dateStart = dateStart
        this.dateFinish = dateFinish
        this.name = name
        this.description = description


    }
}

