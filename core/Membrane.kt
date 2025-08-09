package com.example.kaleidoscopeai.core

class Membrane(
    val nodes: List<Node>,
    val bonds: List<Bond>
) {
    fun totalEnergy(): Float = nodes.sumOf { it.energy.toDouble() }.toFloat()
    fun averageTension(): Float = bonds.map { it.tension }.average().toFloat()
    fun applyGlobalForce(force: Vec3) {
        nodes.filter { !it.fixed }.forEach { it.velocity = it.velocity + force }
    }
    fun boundaryNodes(): List<Node> = nodes.filter { it.fixed }
}
