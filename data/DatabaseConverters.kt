package com.example.kaleidoscopeai.data

import androidx.room.TypeConverter
import com.example.kaleidoscopeai.core.Vec3
import org.json.JSONObject
import org.json.JSONArray

class DatabaseConverters {
    
    @TypeConverter
    fun fromVec3(vec3: Vec3?): String? {
        return vec3?.let {
            JSONObject().apply {
                put("x", it.x)
                put("y", it.y)
                put("z", it.z)
            }.toString()
        }
    }
    
    @TypeConverter
    fun toVec3(json: String?): Vec3? {
        return json?.let {
            try {
                val jsonObject = JSONObject(it)
                Vec3(
                    jsonObject.getDouble("x").toFloat(),
                    jsonObject.getDouble("y").toFloat(),
                    jsonObject.getDouble("z").toFloat()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
    
    @TypeConverter
    fun fromFloatArray(array: FloatArray?): String? {
        return array?.let {
            JSONArray(it.toTypedArray()).toString()
        }
    }
    
    @TypeConverter
    fun toFloatArray(json: String?): FloatArray? {
        return json?.let {
            try {
                val jsonArray = JSONArray(it)
                FloatArray(jsonArray.length()) { i ->
                    jsonArray.getDouble(i).toFloat()
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    @TypeConverter
    fun fromStringMap(map: Map<String, String>?): String? {
        return map?.let {
            JSONObject(it).toString()
        }
    }
    
    @TypeConverter
    fun toStringMap(json: String?): Map<String, String>? {
        return json?.let {
            try {
                val jsonObject = JSONObject(it)
                val map = mutableMapOf<String, String>()
                jsonObject.keys().forEach { key ->
                    map[key] = jsonObject.getString(key)
                }
                map
            } catch (e: Exception) {
                null
            }
        }
    }
}