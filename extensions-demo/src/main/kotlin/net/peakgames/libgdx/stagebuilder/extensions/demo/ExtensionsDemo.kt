package net.peakgames.libgdx.stagebuilder.extensions.demo

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import net.peakgames.libgdx.stagebuilder.core.AbstractGame
import net.peakgames.libgdx.stagebuilder.core.demo.DemoLocalizationService

class ExtensionsDemo : AbstractGame() {
    override fun getSupportedResolutions() = mutableListOf(Vector2(1280f, 720f))
    override fun getLocalizationService() = DemoLocalizationService()

    override fun create() {
        assetsInterface.addAssetConfiguration("demo", "UI.atlas", TextureAtlas::class.java)
        assetsInterface.addAssetConfiguration("demo", "future.fnt", BitmapFont::class.java)
        assetsInterface.addAssetConfiguration("demo", "future_thin.fnt", BitmapFont::class.java)
        assetsInterface.loadAssetsSync("demo")
        screen = DemoScreen(this)
    }
}