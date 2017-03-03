package net.peakgames.libgdx.stagebuilder.extensions.demo

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import net.peakgames.libgdx.stagebuilder.core.AbstractGame
import net.peakgames.libgdx.stagebuilder.core.demo.DemoLocalizationService
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService

class ExtensionsDemo : AbstractGame() {
    override fun getSupportedResolutions() = mutableListOf(Vector2(1280f, 720f))
    override fun getLocalizationService() = DemoLocalizationService()

    override fun create() {
        assetsInterface.addAssetConfiguration("common", "common.atlas", TextureAtlas::class.java)
        assetsInterface.loadAssetsSync("common")
        screen = DemoScreen(this)
    }
}