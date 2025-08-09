package com.kaleidoscopeai.ui

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.kaleidoscopeai.discovery.Molecule
import com.kaleidoscopeai.discovery.ProteinTarget
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.*

class ProteinRenderer(private val context: Context) : GLSurfaceView.Renderer {
    
    private lateinit var proteinModel: ProteinModel
    private lateinit var moleculeModel: MoleculeModel
    private lateinit var bondRenderer: BondRenderer
    
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    
    private var rotationX = 0f
    private var rotationY = 0f
    private var zoom = 1.0f
    
    private var currentProtein: ProteinTarget? = null
    private var currentMolecule: Molecule? = null
    private var showBindingSite = true
    
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set background color to dark blue
        GLES20.glClearColor(0.1f, 0.1f, 0.2f, 1.0f)
        
        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        
        // Initialize renderers
        proteinModel = ProteinModel()
        moleculeModel = MoleculeModel()
        bondRenderer = BondRenderer()
    }
    
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        
        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 50f)
    }
    
    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        
        // Set up view matrix
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 15f * zoom, 0f, 0f, 0f, 0f, 1f, 0f)
        
        // Apply rotations
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, rotationX, 1f, 0f, 0f)
        Matrix.rotateM(modelMatrix, 0, rotationY, 0f, 1f, 0f)
        
        // Calculate MVP matrix
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)
        
        // Render protein if available
        currentProtein?.let { protein ->
            proteinModel.draw(mvpMatrix, protein)
            
            if (showBindingSite) {
                renderBindingSite(mvpMatrix, protein)
            }
        }
        
        // Render molecule if available
        currentMolecule?.let { molecule ->
            moleculeModel.draw(mvpMatrix, molecule)
            renderMolecularBonds(mvpMatrix, molecule)
        }
        
        // Render interaction lines if both protein and molecule present
        if (currentProtein != null && currentMolecule != null) {
            renderInteractionLines(mvpMatrix)
        }
    }
    
    fun setRotation(x: Float, y: Float) {
        rotationX = x
        rotationY = y
    }
    
    fun setZoom(zoomLevel: Float) {
        zoom = zoomLevel.coerceIn(0.5f, 5.0f)
    }
    
    fun setProtein(protein: ProteinTarget) {
        currentProtein = protein
    }
    
    fun setMolecule(molecule: Molecule) {
        currentMolecule = molecule
    }
    
    fun toggleBindingSite() {
        showBindingSite = !showBindingSite
    }
    
    private fun renderBindingSite(mvpMatrix: FloatArray, protein: ProteinTarget) {
        // Render binding site as semi-transparent sphere
        val bindingSiteVertices = generateSphereVertices(
            radius = (protein.bindingSiteVolume / 1000.0).toFloat(),
            color = floatArrayOf(0.8f, 0.8f, 0.2f, 0.3f) // Yellow, semi-transparent
        )
        
        proteinModel.drawSphere(mvpMatrix, bindingSiteVertices)
    }
    
    private fun renderMolecularBonds(mvpMatrix: FloatArray, molecule: Molecule) {
        val bonds = parseMolecularBonds(molecule.smiles)
        bondRenderer.drawBonds(mvpMatrix, bonds)
    }
    
    private fun renderInteractionLines(mvpMatrix: FloatArray) {
        // Render dashed lines showing potential interactions
        val interactionLines = calculateInteractionLines()
        bondRenderer.drawInteractionLines(mvpMatrix, interactionLines)
    }
    
    private fun parseMolecularBonds(smiles: String): List<Bond> {
        val bonds = mutableListOf<Bond>()
        val atoms = extractAtomsFromSMILES(smiles)
        
        // Simple bond parsing (in real implementation, use proper SMILES parser)
        for (i in atoms.indices) {
            if (i < atoms.size - 1) {
                bonds.add(Bond(
                    atom1 = atoms[i],
                    atom2 = atoms[i + 1],
                    bondType = BondType.SINGLE
                ))
            }
        }
        
        return bonds
    }
    
    private fun extractAtomsFromSMILES(smiles: String): List<Atom> {
        val atoms = mutableListOf<Atom>()
        var x = 0f
        
        // Simplified atom extraction
        for (char in smiles) {
            when (char.uppercaseChar()) {
                'C' -> atoms.add(Atom(AtomType.CARBON, x, 0f, 0f))
                'N' -> atoms.add(Atom(AtomType.NITROGEN, x, 0f, 0f))
                'O' -> atoms.add(Atom(AtomType.OXYGEN, x, 0f, 0f))
                'S' -> atoms.add(Atom(AtomType.SULFUR, x, 0f, 0f))
            }
            x += 1.5f
        }
        
        return atoms
    }
    
    private fun generateSphereVertices(radius: Float, color: FloatArray): FloatArray {
        val vertices = mutableListOf<Float>()
        val latitudes = 20
        val longitudes = 20
        
        for (i in 0..latitudes) {
            val lat = PI * i / latitudes - PI / 2
            val y = sin(lat) * radius
            val r = cos(lat) * radius
            
            for (j in 0..longitudes) {
                val lon = 2 * PI * j / longitudes
                val x = cos(lon) * r
                val z = sin(lon) * r
                
                // Position
                vertices.add(x.toFloat())
                vertices.add(y.toFloat())
                vertices.add(z.toFloat())
                
                // Color
                vertices.addAll(color.toList())
            }
        }
        
        return vertices.toFloatArray()
    }
    
    private fun calculateInteractionLines(): List<InteractionLine> {
        val lines = mutableListOf<InteractionLine>()
        
        currentMolecule?.let { molecule ->
            currentProtein?.let { protein ->
                // Simulate interaction points
                val moleculeCenter = floatArrayOf(0f, 0f, 0f)
                val proteinCenter = floatArrayOf(2f, 1f, 0f)
                
                lines.add(InteractionLine(
                    start = moleculeCenter,
                    end = proteinCenter,
                    type = InteractionLineType.HYDROGEN_BOND,
                    strength = 0.8f
                ))
            }
        }
        
        return lines
    }
}

class ProteinModel {
    private val vertexShader = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        attribute vec4 vColor;
        varying vec4 fColor;
        
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            gl_PointSize = 10.0;
            fColor = vColor;
        }
    """.trimIndent()
    
    private val fragmentShader = """
        precision mediump float;
        varying vec4 fColor;
        
        void main() {
            gl_FragColor = fColor;
        }
    """.trimIndent()
    
    private var program: Int = 0
    private var positionHandle: Int = 0
    private var colorHandle: Int = 0
    private var mvpMatrixHandle: Int = 0
    
    init {
        program = createShaderProgram(vertexShader, fragmentShader)
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        colorHandle = GLES20.glGetAttribLocation(program, "vColor")
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
    }
    
    fun draw(mvpMatrix: FloatArray, protein: ProteinTarget) {
        GLES20.glUseProgram(program)
        
        // Generate protein representation
        val vertices = generateProteinVertices(protein)
        val vertexBuffer = createVertexBuffer(vertices)
        
        // Set up vertex attributes
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 28, vertexBuffer)
        
        vertexBuffer.position(3)
        GLES20.glEnableVertexAttribArray(colorHandle)
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 28, vertexBuffer)
        
        // Pass transformation matrix
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        
        // Draw protein as points
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertices.size / 7)
        
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
    }
    
    fun drawSphere(mvpMatrix: FloatArray, vertices: FloatArray) {
        GLES20.glUseProgram(program)
        
        val vertexBuffer = createVertexBuffer(vertices)
        
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 28, vertexBuffer)
        
        vertexBuffer.position(3)
        GLES20.glEnableVertexAttribArray(colorHandle)
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 28, vertexBuffer)
        
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertices.size / 7)
        
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
    }
    
    private fun generateProteinVertices(protein: ProteinTarget): FloatArray {
        val vertices = mutableListOf<Float>()
        
        // Generate simplified protein structure
        for (i in 0 until 50) {
            val angle = 2 * PI * i / 50
            val radius = 3f + sin(angle * 3).toFloat() * 0.5f
            
            val x = cos(angle).toFloat() * radius
            val y = sin(angle * 2).toFloat() * 2f
            val z = sin(angle).toFloat() * radius
            
            // Position
            vertices.add(x)
            vertices.add(y)
            vertices.add(z)
            
            // Color (protein = blue/green)
            vertices.add(0.2f)
            vertices.add(0.6f)
            vertices.add(0.8f)
            vertices.add(1.0f)
        }
        
        return vertices.toFloatArray()
    }
}

class MoleculeModel {
    private var program: Int = 0
    private var positionHandle: Int = 0
    private var colorHandle: Int = 0
    private var mvpMatrixHandle: Int = 0
    
    private val vertexShader = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        attribute vec4 vColor;
        varying vec4 fColor;
        
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            gl_PointSize = 15.0;
            fColor = vColor;
        }
    """.trimIndent()
    
    private val fragmentShader = """
        precision mediump float;
        varying vec4 fColor;
        
        void main() {
            gl_FragColor = fColor;
        }
    """.trimIndent()
    
    init {
        program = createShaderProgram(vertexShader, fragmentShader)
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        colorHandle = GLES20.glGetAttribLocation(program, "vColor")
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
    }
    
    fun draw(mvpMatrix: FloatArray, molecule: Molecule) {
        GLES20.glUseProgram(program)
        
        val vertices = generateMoleculeVertices(molecule)
        val vertexBuffer = createVertexBuffer(vertices)
        
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 28, vertexBuffer)
        
        vertexBuffer.position(3)
        GLES20.glEnableVertexAttribArray(colorHandle)
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 28, vertexBuffer)
        
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertices.size / 7)
        
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
    }
    
    private fun generateMoleculeVertices(molecule: Molecule): FloatArray {
        val vertices = mutableListOf<Float>()
        val atomCount = molecule.smiles.count { it.isLetter() }
        
        for (i in 0 until atomCount) {
            val angle = 2 * PI * i / atomCount
            val x = cos(angle).toFloat() * 2f
            val y = sin(angle).toFloat() * 2f
            val z = 0f
            
            // Position
            vertices.add(x)
            vertices.add(y)
            vertices.add(z)
            
            // Color based on atom type
            val atomType = molecule.smiles.getOrNull(i)?.uppercaseChar()
            when (atomType) {
                'C' -> {
                    vertices.add(0.3f); vertices.add(0.3f); vertices.add(0.3f); vertices.add(1.0f) // Gray
                }
                'N' -> {
                    vertices.add(0.2f); vertices.add(0.2f); vertices.add(1.0f); vertices.add(1.0f) // Blue
                }
                'O' -> {
                    vertices.add(1.0f); vertices.add(0.2f); vertices.add(0.2f); vertices.add(1.0f) // Red
                }
                else -> {
                    vertices.add(0.8f); vertices.add(0.8f); vertices.add(0.2f); vertices.add(1.0f) // Yellow
                }
            }
        }
        
        return vertices.toFloatArray()
    }
}

class BondRenderer {
    private var program: Int = 0
    
    fun drawBonds(mvpMatrix: FloatArray, bonds: List<Bond>) {
        // Implementation for bond rendering
    }
    
    fun drawInteractionLines(mvpMatrix: FloatArray, lines: List<InteractionLine>) {
        // Implementation for interaction line rendering
    }
}

// Utility functions
private fun createShaderProgram(vertexShaderCode: String, fragmentShaderCode: String): Int {
    val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
    val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
    
    return GLES20.glCreateProgram().also { program ->
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
    }
}

private fun loadShader(type: Int, shaderCode: String): Int {
    return GLES20.glCreateShader(type).also { shader ->
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
    }
}

private fun createVertexBuffer(vertices: FloatArray): FloatBuffer {
    return ByteBuffer.allocateDirect(vertices.size * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(vertices)
            position(0)
        }
    }
}

// Data classes
data class Atom(
    val type: AtomType,
    val x: Float,
    val y: Float,
    val z: Float
)

data class Bond(
    val atom1: Atom,
    val atom2: Atom,
    val bondType: BondType
)

data class InteractionLine(
    val start: FloatArray,
    val end: FloatArray,
    val type: InteractionLineType,
    val strength: Float
)

enum class AtomType { CARBON, NITROGEN, OXYGEN, SULFUR, PHOSPHORUS }
enum class BondType { SINGLE, DOUBLE, TRIPLE, AROMATIC }
enum class InteractionLineType { HYDROGEN_BOND, HYDROPHOBIC, ELECTROSTATIC }
