package net.peakgames.libgdx.stagebuilder.core.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

public class Assets implements AssetsInterface {

    private StageBuilderFileHandleResolver fileHandleResolver;
    private final ResolutionHelper resolutionHelper;
    private AssetManager assetManager;
    private AssetLoader assetLoader;
    private Map<String, String> assetAliasMap;

    public Assets(StageBuilderFileHandleResolver fileHandleResolver, ResolutionHelper resolutionHelper) {
        this.fileHandleResolver = fileHandleResolver;
        this.resolutionHelper = resolutionHelper;
        this.assetManager = new AssetManager(fileHandleResolver);
        this.assetLoader = new AssetLoader(this.assetManager);
        this.assetAliasMap = new HashMap<String, String>();
    }

    @Override
    public BitmapFont getFont(String fontName) {
        BitmapFont bitmapFont = this.assetManager.get(getAssetName(fontName), BitmapFont.class);
        bitmapFont.getData().setScale(resolutionHelper.getSizeMultiplier(), resolutionHelper.getSizeMultiplier());
        return bitmapFont;
    }

    @Override
    public TextureAtlas getTextureAtlas(String atlasName) {
        return this.assetManager.get(getAssetName(atlasName), TextureAtlas.class);
    }

    @Override
    public Texture getTexture(String textureName) {
        return this.assetManager.get(getAssetName(textureName), Texture.class);
    }

    @Override
    public void addAssetConfiguration(String key, String fileName, Class<?> type) {
        this.assetLoader.addAssetConfiguration(key, fileName, type);
    }

    @Override
    public void addAssetConfiguration(String key, String fileName, String alias, Class<?> type) {
        assetAliasMap.put(alias, fileName);
        addAssetConfiguration(key, fileName, type);
    }

    @Override
    public void removeAssetConfiguration(String key) {
        this.assetLoader.removeAssetConfiguration(key);
    }

    @Override
    public void loadAssetsSync(String key) {
        this.assetLoader.loadAssetsSync(key);
    }

    @Override
    public void loadAssetsAsync(String key, AssetLoaderListener listener) {
        this.assetLoader.loadAssetsAsync(key, listener);
    }

    @Override
    public Map<String, List<AssetLoader.AssetConfig>> getAssetsConfiguration() {
        return this.assetLoader.getAssetsConfiguration();
    }

    @Override
    public void unloadAssets(String key) {
        this.assetLoader.unloadAssets(key, Collections.EMPTY_SET);
    }

    @Override
    public void unloadAssets(String key, Set<String> excludedSet) {
        this.assetLoader.unloadAssets(key, excludedSet);
    }

    @Override
    public AssetManager getAssetMAnager() {
        return this.assetManager;
    }

    @Override
    public Vector2 findBestResolution() {
        return this.fileHandleResolver.findBestResolution();
    }

    @Override
    public void resetAlreadyLoadedAssetsMap() {
         this.assetLoader.resetAlreadyLoadedAssetMap();
    }

    @Override
    public void clearAllAliases() {
        assetAliasMap.clear();
    }

    @Override
    public void removeAlias(String alias) {
        assetAliasMap.remove(alias);
    }

    private String getAssetName(String assetName) {
        String result = assetAliasMap.get(assetName);
        result = (result == null)? assetName : result;
        return result;
    }

}
