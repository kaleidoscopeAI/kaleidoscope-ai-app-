package com.example.kaleidoscopeai.renderer

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.kaleidoscopeai.core.Node
import com.example.kaleidoscopeai.core.Bond
import com.example.kaleidoscopeai.core.Vec3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.absoluteValue

class CubeRenderer(
    private val context: Context,
    private val nodesProvider: () -> List<Node>,
    private val bondsProvider: () -> List<Bond>,
    private val coroutineScope: CoroutineScope
) : GLSurfaceView.Renderer {
    
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private var angleX = 30f
    private var angleY = 45f
    private var distance = 10f
    
    // OpenGL resources
    private var vertexShaderProgram = 0
    private var positionHandle = 0
    private var colorHandle = 0
    private var mvpMatrixHandle = 0
    
    // Buffers for rendering
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var colorBuffer: FloatBuffer
    
    // Context management
    private var isContextReady = false
    private var surfaceWidth = 0
    private var surfaceHeight = 0
    
    // Vertex shader source
    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        attribute vec4 vColor;
        varying vec4 fragColor;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            gl_PointSize = 8.0;
            fragColor = vColor;
        }
    """.trimIndent()
    
    // Fragment shader source
    private val fragmentShaderCode = """
        precision mediump float;
        varying vec4 fragColor;
        void main() {
            gl_FragColor = fragColor;
        }
    """.trimIndent()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        try {
            Log.d("CubeRenderer", "Surface created, initializing OpenGL context")
            
            // Set clear color
            glClearColor(0.1f, 0.1f, 0.15f, 1.0f)
            glEnable(GL_DEPTH_TEST)
            glEnable(GL_BLEND)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            
            // Initialize shader program
            initializeShaders()
            
            // Initialize buffers
            initializeBuffers()
            
            isContextReady = true
            Log.d("CubeRenderer", "OpenGL context initialized successfully")
            
        } catch (e: Exception) {
            Log.e("CubeRenderer", "Error initializing OpenGL context", e)
            isContextReady = false
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        try {
            Log.d("CubeRenderer", "Surface changed: ${width}x${height}")
            
            surfaceWidth = width
            surfaceHeight = height
            
            glViewport(0, 0, width, height)
            
            val ratio = width.toFloat() / height
            Matrix.perspectiveM(projectionMatrix, 0, 45f, ratio, 0.1f, 100f)
            updateViewMatrix()
            
        } catch (e: Exception) {
            Log.e("CubeRenderer", "Error handling surface change", e)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        if (!isContextReady) {
            Log.w("CubeRenderer", "OpenGL context not ready, skipping frame")
            return
        }
        
        try {
            // Clear buffers
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
            
            // Use shader program
            glUseProgram(vertexShaderProgram)
            
            // Set up matrices
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.rotateM(modelMatrix, 0, angleX, 1f, 0f, 0f)
            Matrix.rotateM(modelMatrix, 0, angleY, 0f, 1f, 0f)
            Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)
            
            // Pass matrix to shader
            glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
            
            // Draw scene
            drawBonds()
            drawNodes()
            
            // Rotate for animation
            angleY += 0.5f
            if (angleY >= 360f) angleY -= 360f
            
        } catch (e: Exception) {
            Log.e("CubeRenderer", "Error drawing frame", e)
        }
    }
    
    private fun initializeShaders() {
        val vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)
        
        vertexShaderProgram = glCreateProgram()
        glAttachShader(vertexShaderProgram, vertexShader)
        glAttachShader(vertexShaderProgram, fragmentShader)
        glLinkProgram(vertexShaderProgram)
        
        // Check linking status
        val linkStatus = IntArray(1)
        glGetProgramiv(vertexShaderProgram, GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] != GL_TRUE) {
            val error = glGetProgramInfoLog(vertexShaderProgram)
            Log.e("CubeRenderer", "Shader program linking failed: $error")
            throw RuntimeException("Shader program linking failed")
        }
        
        // Get handles to shader variables
        positionHandle = glGetAttribLocation(vertexShaderProgram, "vPosition")
        colorHandle = glGetAttribLocation(vertexShaderProgram, "vColor")
        mvpMatrixHandle = glGetUniformLocation(vertexShaderProgram, "uMVPMatrix")
        
        // Clean up individual shaders
        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
    }
    
    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = glCreateShader(type)
        glShaderSource(shader, shaderCode)
        glCompileShader(shader)
        
        // Check compilation status
        val compileStatus = IntArray(1)
        glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] != GL_TRUE) {
            val error = glGetShaderInfoLog(shader)
            Log.e("CubeRenderer", "Shader compilation failed: $error")
            throw RuntimeException("Shader compilation failed")
        }
        
        return shader
    }
    
    private fun initializeBuffers() {
        // Initialize buffers with reasonable capacity
        val vertexCapacity = 1000 * 3 * 4 // 1000 vertices, 3 coordinates, 4 bytes per float
        val colorCapacity = 1000 * 4 * 4 // 1000 colors, 4 components, 4 bytes per float
        
        vertexBuffer = ByteBuffer.allocateDirect(vertexCapacity)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            
        colorBuffer = ByteBuffer.allocateDirect(colorCapacity)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
    }

    private fun updateViewMatrix() {
        val eyeX = distance * cos(Math.toRadians(angleY.toDouble())).toFloat() * cos(Math.toRadians(angleX.toDouble())).toFloat()
        val eyeY = distance * sin(Math.toRadians(angleX.toDouble())).toFloat()
        val eyeZ = distance * sin(Math.toRadians(angleY.toDouble())).toFloat() * cos(Math.toRadians(angleX.toDouble())).toFloat()
        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, 0f, 0f, 0f, 0f, 1f, 0f)
    }

    private fun drawNodes() {
        val nodes = nodesProvider()
        if (nodes.isEmpty()) return
        
        vertexBuffer.clear()
        colorBuffer.clear()
        
        // Prepare vertex and color data
        for (node in nodes) {
            // Add vertex position
            vertexBuffer.put(node.position.x)
            vertexBuffer.put(node.position.y)
            vertexBuffer.put(node.position.z)
            
            // Add vertex color based on node state
            val color = getNodeColor(node)
            colorBuffer.put(color[0])
            colorBuffer.put(color[1])
            colorBuffer.put(color[2])
            colorBuffer.put(color[3])
        }
        
        vertexBuffer.position(0)
        colorBuffer.position(0)
        
        // Enable vertex attributes
        glEnableVertexAttribArray(positionHandle)
        glEnableVertexAttribArray(colorHandle)
        
        // Set vertex data
        glVertexAttribPointer(positionHandle, 3, GL_FLOAT, false, 0, vertexBuffer)
        glVertexAttribPointer(colorHandle, 4, GL_FLOAT, false, 0, colorBuffer)
        
        // Draw points
        glDrawArrays(GL_POINTS, 0, nodes.size)
        
        // Disable vertex attributes
        glDisableVertexAttribArray(positionHandle)
        glDisableVertexAttribArray(colorHandle)
    }

    private fun drawBonds() {
        val bonds = bondsProvider().filter { !it.isBroken() }
        if (bonds.isEmpty()) return
        
        vertexBuffer.clear()
        colorBuffer.clear()
        
        // Prepare line vertex and color data
        for (bond in bonds) {
            // Add line start vertex
            vertexBuffer.put(bond.nodeA.position.x)
            vertexBuffer.put(bond.nodeA.position.y)
            vertexBuffer.put(bond.nodeA.position.z)
            
            // Add line end vertex
            vertexBuffer.put(bond.nodeB.position.x)
            vertexBuffer.put(bond.nodeB.position.y)
            vertexBuffer.put(bond.nodeB.position.z)
            
            // Add colors for both vertices
            val color = tensionColor(bond.tension)
            repeat(2) { // Same color for both vertices of the line
                colorBuffer.put(color[0])
                colorBuffer.put(color[1])
                colorBuffer.put(color[2])
                colorBuffer.put(color[3])
            }
        }
        
        vertexBuffer.position(0)
        colorBuffer.position(0)
        
        // Enable vertex attributes
        glEnableVertexAttribArray(positionHandle)
        glEnableVertexAttribArray(colorHandle)
        
        // Set vertex data
        glVertexAttribPointer(positionHandle, 3, GL_FLOAT, false, 0, vertexBuffer)
        glVertexAttribPointer(colorHandle, 4, GL_FLOAT, false, 0, colorBuffer)
        
        // Draw lines
        glDrawArrays(GL_LINES, 0, bonds.size * 2)
        
        // Disable vertex attributes
        glDisableVertexAttribArray(positionHandle)
        glDisableVertexAttribArray(colorHandle)
    }
    
    private fun getNodeColor(node: Node): FloatArray {
        return if (node.fixed) {
            // Fixed nodes are red
            floatArrayOf(1.0f, 0.3f, 0.3f, 1.0f)
        } else {
            // Color based on energy level
            val energy = node.energy.coerceIn(0f, 1f)
            floatArrayOf(
                0.3f + energy * 0.7f, // Red component
                0.8f * energy,        // Green component
                0.3f + (1f - energy) * 0.5f, // Blue component
                0.8f
            )
        }
    }

    private fun tensionColor(tension: Float): FloatArray {
        // Blue for compression, red for tension
        val normalizedTension = (tension / 10f).coerceIn(-1f, 1f)
        return when {
            normalizedTension > 0 -> { // Tension (red)
                val intensity = normalizedTension * 0.8f + 0.2f
                floatArrayOf(intensity, 0.2f, 0.2f, 0.7f)
            }
            normalizedTension < 0 -> { // Compression (blue)
                val intensity = normalizedTension.absoluteValue * 0.8f + 0.2f
                floatArrayOf(0.2f, 0.2f, intensity, 0.7f)
            }
            else -> { // Neutral (white)
                floatArrayOf(0.5f, 0.5f, 0.5f, 0.5f)
            }
        }
    }
    
    // Public methods for camera control
    fun setRotation(x: Float, y: Float) {
        angleX = x
        angleY = y
        updateViewMatrix()
    }
    
    fun setDistance(dist: Float) {
        distance = dist.coerceIn(1f, 50f)
        updateViewMatrix()
    }
    
    // Cleanup method to be called when renderer is destroyed
    fun cleanup() {
        if (isContextReady && vertexShaderProgram != 0) {
            glDeleteProgram(vertexShaderProgram)
            vertexShaderProgram = 0
        }
        isContextReady = false
    }
}