package com.example.kaleidoscopeai.util

import android.content.Context
import android.os.Environment
import com.example.kaleidoscopeai.data.NodeReflection
import java.io.File
import java.io.FileWriter

object ReflectionExporter {
    fun exportReflections(context: Context, reflections: List<NodeReflection>, fileName: String = "reflections.csv"): File {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        FileWriter(file).use { writer ->
            writer.append("id,nodeId,timestamp,reflectionText\n")
            reflections.forEach { r ->
                writer.append("${r.id},${r.nodeId},${r.timestamp},\"${r.reflectionText.replace("\"", "''")}\"\n")
            }
        }
        return file
    }
}
