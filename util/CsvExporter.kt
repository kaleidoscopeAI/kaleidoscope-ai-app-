package com.example.kaleidoscopeai.util

import android.content.Context
import android.os.Environment
import com.example.kaleidoscopeai.core.Node
import com.example.kaleidoscopeai.core.Bond
import java.io.File
import java.io.FileWriter

object CsvExporter {
    fun exportNodes(context: Context, nodes: List<Node>, fileName: String = "nodes.csv"): File {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        FileWriter(file).use { writer ->
            writer.append("id,x,y,z,energy,tension,reflection\n")
            nodes.forEach { node ->
                writer.append("${node.id},${node.position.x},${node.position.y},${node.position.z},${node.energy},${node.tension},${node.reflection ?: ""}\n")
            }
        }
        return file
    }

    fun exportBonds(context: Context, bonds: List<Bond>, fileName: String = "bonds.csv"): File {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        FileWriter(file).use { writer ->
            writer.append("id,nodeA,nodeB,tension,validity\n")
            bonds.forEach { bond ->
                writer.append("${bond.id},${bond.nodeA.id},${bond.nodeB.id},${bond.tension},${bond.validity}\n")
            }
        }
        return file
    }
}
