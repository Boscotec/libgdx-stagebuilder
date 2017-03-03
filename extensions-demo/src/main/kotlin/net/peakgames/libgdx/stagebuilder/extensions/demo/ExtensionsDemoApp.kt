package net.peakgames.libgdx.stagebuilder.extensions.demo

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import net.peakgames.libgdx.stagebuilder.core.demo.StageBuilderDemo
import net.peakgames.libgdx.stagebuilder.core.keyboard.DummyKeyboardEventService

fun main(args: Array<String>) {
    val width = 1280
    val height = 800

    val config = LwjglApplicationConfiguration()
    config.width = width
    config.height = height

    val demo = ExtensionsDemo()
    demo.initialize(width, height)
    demo.setSoftKeyboardEventInterface(DummyKeyboardEventService())

    LwjglApplication(demo, config)
}