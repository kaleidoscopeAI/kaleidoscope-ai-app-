package com.example.kaleidoscopeai.core

import org.junit.Assert.assertEquals
import org.junit.Test

class Vec3Test {
    @Test
    fun testAddition() {
        val a = Vec3(1f, 2f, 3f)
        val b = Vec3(4f, 5f, 6f)
        val result = a + b
        assertEquals(Vec3(5f, 7f, 9f), result)
    }

    @Test
    fun testSubtraction() {
        val a = Vec3(5f, 7f, 9f)
        val b = Vec3(1f, 2f, 3f)
        val result = a - b
        assertEquals(Vec3(4f, 5f, 6f), result)
    }

    @Test
    fun testLength() {
        val a = Vec3(3f, 4f, 0f)
        assertEquals(5f, a.length(), 1e-6f)
    }

    @Test
    fun testNormalize() {
        val a = Vec3(0f, 3f, 4f)
        val n = a.normalize()
        assertEquals(1f, n.length(), 1e-6f)
    }
}
