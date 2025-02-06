package com.rsetiapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val userEmail:String,
    val userName:String
)
