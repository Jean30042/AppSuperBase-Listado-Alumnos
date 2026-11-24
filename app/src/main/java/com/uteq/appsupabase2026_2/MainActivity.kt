package com.uteq.appsupabase2026_2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.uteq.appsupabase2026_2.data.SupabaseManager
import com.uteq.appsupabase2026_2.models.Alumno
import com.uteq.appsupabase2026_2.utils.SupabaseErrorHandler
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var txtAlumnos: TextView
    private lateinit var progressMaterias: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtAlumnos = findViewById(R.id.txtAlumnos)
        progressMaterias = findViewById(R.id.progressMaterias)

        findViewById<Button>(R.id.btnConsultar).setOnClickListener {
            consultarAlumnos()
        }

        consultarAlumnos()
    }

    private fun obtenerApellido(alumno: Alumno): String {
        if (!alumno.apellidos.isNullOrBlank()) {
            return alumno.apellidos.trim()
        }
        val parts = alumno.nombres.trim().split("\\s+".toRegex())
        return when {
            parts.size >= 4 -> "${parts[2]} ${parts[3]}"
            parts.size == 3 -> "${parts[1]} ${parts[2]}"
            parts.size == 2 -> parts[1]
            else -> alumno.nombres
        }
    }

    private fun consultarAlumnos() {
        lifecycleScope.launch {
            progressMaterias.visibility = View.VISIBLE
            try {
                val alumnos = SupabaseManager.client
                    .from("alumnos")
                    .select()
                    .decodeList<Alumno>()

                // Eliminar duplicados y ordenar por apellido A-Z
                val alumnosOrdenados = alumnos
                    .distinctBy { Pair(it.nombres, it.correo) }
                    .sortedBy { obtenerApellido(it) }

                txtAlumnos.text = if (alumnosOrdenados.isEmpty()) {
                    "No se encontraron alumnos."
                } else {
                    alumnosOrdenados.joinToString("\n\n") { a ->
                        val nombreMostrar = if (!a.apellidos.isNullOrBlank()) {
                            "${a.nombres} ${a.apellidos}"
                        } else {
                            a.nombres
                        }
                        "Nombre: $nombreMostrar\n" +
                                "Paralelo: ${a.paralelo}\n" +
                                "Correo: ${a.correo ?: "Sin registrar"}\n" +
                                "Teléfono: ${a.telefono ?: "Sin registrar"}"
                    }
                }
            } catch (e: Exception) {
                SupabaseErrorHandler.show(this@MainActivity, e)
            } finally {
                progressMaterias.visibility = View.GONE
            }
        }
    }
}