package com.kaleidoscopeai.core

import android.content.Context
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.tensorflow.lite.Interpreter
import java.io.File

@RunWith(RobolectricTestRunner::class)
class DrugEngineTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockEsm2Interpreter: Interpreter

    @Mock
    private lateinit var mockMolformerInterpreter: Interpreter

    private lateinit var drugEngine: DrugEngine

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val file = mock(File::class.java)
        drugEngine = DrugEngine(mockContext)
        val esm2Field = DrugEngine::class.java.getDeclaredField("esm2Interpreter")
        esm2Field.isAccessible = true
        esm2Field.set(drugEngine, mockEsm2Interpreter)

        val molformerField = DrugEngine::class.java.getDeclaredField("molformerInterpreter")
        molformerField.isAccessible = true
        molformerField.set(drugEngine, mockMolformerInterpreter)
    }

    @Test
    fun testPredictProteinEmbedding() {
        val sequence = "ACDEF"
        val expectedEmbedding = FloatArray(1280) { 0.5f }

        org.mockito.Mockito.doAnswer { invocation ->
            val buffer = invocation.arguments[1] as java.nio.ByteBuffer
            buffer.asFloatBuffer().put(expectedEmbedding)
            null
        }.`when`(mockEsm2Interpreter).run(org.mockito.ArgumentMatchers.any(java.nio.ByteBuffer::class.java), org.mockito.ArgumentMatchers.any(java.nio.ByteBuffer::class.java))

        val embedding = drugEngine.predictProteinEmbedding(sequence)
        assert(embedding.contentEquals(expectedEmbedding))
    }

    @Test
    fun testGenerateHypothesis() {
        val smiles = "C1=CC=C(C=C1)C(=O)O"
        val expectedAffinity = 0.8f

        org.mockito.Mockito.doAnswer { invocation ->
            val buffer = invocation.arguments[1] as java.nio.ByteBuffer
            buffer.asFloatBuffer().put(expectedAffinity)
            null
        }.`when`(mockMolformerInterpreter).run(org.mockito.ArgumentMatchers.any(java.nio.ByteBuffer::class.java), org.mockito.ArgumentMatchers.any(java.nio.ByteBuffer::class.java))

        val hypothesis = drugEngine.generateHypothesis(smiles)
        assert(hypothesis.contains("0.80"))
    }

    @Test
    fun testClose() {
        drugEngine.close()
        org.mockito.Mockito.verify(mockEsm2Interpreter).close()
        org.mockito.Mockito.verify(mockMolformerInterpreter).close()
    }
}