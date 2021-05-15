package com.ljw.calculatorapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey var uid: Int?,
    @ColumnInfo(name = "expression") var expression: String?,
    @ColumnInfo(name = "result") var result: String
)