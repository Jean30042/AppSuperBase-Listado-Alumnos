package com.uteq.appsupabase2026_2.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Alumno(
    val id: Long,
    val nombres: String,
    val apellidos: String? = null,
    val paralelo: String,
    val correo: String? = null,
    val telefono: String? = null,
    val foto: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
)