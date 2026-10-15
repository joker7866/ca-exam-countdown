package com.cacountdown.app.data.local

import androidx.room.TypeConverter
import com.cacountdown.app.data.local.entity.ActivityType
import com.cacountdown.app.data.local.entity.ScheduleUpdateStatus
import com.cacountdown.app.data.local.entity.SessionStatus
import com.cacountdown.app.domain.model.ExamGroup
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class Converters {

    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun toEpochDay(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun fromSecondOfDay(value: Int?): LocalTime? = value?.let { LocalTime.ofSecondOfDay(it.toLong()) }

    @TypeConverter
    fun toSecondOfDay(time: LocalTime?): Int? = time?.toSecondOfDay()

    @TypeConverter
    fun fromEpochMilli(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun toEpochMilli(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun fromGroupName(value: String?): ExamGroup? = value?.let { ExamGroup.valueOf(it) }

    @TypeConverter
    fun toGroupName(group: ExamGroup?): String? = group?.name

    @TypeConverter
    fun fromSessionStatusName(value: String?): SessionStatus? = value?.let { SessionStatus.valueOf(it) }

    @TypeConverter
    fun toSessionStatusName(status: SessionStatus?): String? = status?.name

    @TypeConverter
    fun fromScheduleUpdateStatusName(value: String?): ScheduleUpdateStatus? = value?.let { ScheduleUpdateStatus.valueOf(it) }

    @TypeConverter
    fun toScheduleUpdateStatusName(status: ScheduleUpdateStatus?): String? = status?.name

    @TypeConverter
    fun fromActivityTypeName(value: String?): ActivityType? = value?.let { ActivityType.valueOf(it) }

    @TypeConverter
    fun toActivityTypeName(type: ActivityType?): String? = type?.name
}
