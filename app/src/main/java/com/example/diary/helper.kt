package com.example.diary

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

public fun ConvertToTimestampFromDateTime(dateTime: String) :Timestamp {
    val date = SimpleDateFormat("dd-MM-yyyy H:m").parse(dateTime)
    return Timestamp(date.time)
}
public fun getTimeFromTimeStamp(timestamp: Date): String {
    var hours: String
    var minutes: String
    if(timestamp.hours < 9) {
        hours = "0"+ timestamp.hours
    } else {
        hours = timestamp.hours.toString()
    }
    if(timestamp.minutes < 9) {
        minutes = "0"+ timestamp.minutes
    } else {
        minutes = timestamp.minutes.toString()
    }

    return "$hours:$minutes"
}
public fun getFullTime(startTime: Date, endTime: Date): String {
    return getTimeFromTimeStamp(startTime) +"-"+getTimeFromTimeStamp(endTime)
}
