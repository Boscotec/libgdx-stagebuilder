package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.ImageModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

public class ImageBuilder extends ActorBuilder {


    public ImageBuilder(AssetsInterface assets, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        super(assets, resolutionHelper, localizationService);
    }

    @Override
    public Actor build(BaseModel model) {
        ImageModel imageModel = (ImageModel) model;
        Image image;
        if (imageModel.getTextureSrc() != null) {
            image = createFromTexture(imageModel);
        } else {
            image = createFromTextureAtlas(imageModel);
        }

        normalizeModelSize(imageModel,
                image.getDrawable().getMinWidth(),
                image.getDrawable().getMinHeight());


        setBasicProperties(model, image);

        if (ImageModel.TYPE_BACKGROUND.equals(imageModel.getType())) {
            updateBackgroundImagePosition(image);
        }

        return image;
    }

    private void updateBackgroundImagePosition(Image image) {
        Vector2 selectedResolution = assets.findBestResolution();
        Vector2 backGroundSize = resolutionHelper.calculateBackgroundSize(selectedResolution.x, selectedResolution.y);
        Vector2 backGroundPosition = resolutionHelper.calculateBackgroundPosition(image.getWidth(), image.getHeight());
        Vector2 gameAreaPosition = resolutionHelper.getGameAreaPosition();
          /*
  		 * stage root position is always set to gameAreaPosition.
  		 * Since the bg image is also inside the root group, bg image position should be updated.
		 */
        image.setPosition(backGroundPosition.x - gameAreaPosition.x, backGroundPosition.y - gameAreaPosition.y);
        image.setSize(backGroundSize.x, backGroundSize.y);
    }

    private Image createFromTexture(ImageModel imageModel) {
        if(imageModel.getNinepatch()){
            NinePatchDrawable ninePatchDrawable = new NinePatchDrawable();
            NinePatch patch = new NinePatch(new TextureRegion(assets.getTexture(getLocalizedString(imageModel.getTextureSrc()))),
                    imageModel.getNinepatchOffset(), imageModel.getNinepatchOffset(), imageModel.getNinepatchOffset(), imageModel.getNinepatchOffset());
            ninePatchDrawable.setPatch(patch);
            return new Image(patch);
        }else{
            TextureRegion textureRegion = new TextureRegion(assets.getTexture(getLocalizedString(imageModel.getTextureSrc())));
            return new Image(textureRegion);
        }
    }

    private Image createFromTextureAtlas(ImageModel imageModel) {
        if(imageModel.getNinepatch()){
            return new Image(createNinePatchDrawable(imageModel.getFrame(), assets.getTextureAtlas(imageModel.getAtlasName()), imageModel.getNinepatchOffset()));
        }else{
            TextureAtlas textureAtlas = assets.getTextureAtlas(imageModel.getAtlasName());
            TextureAtlas.AtlasRegion atlasRegion = textureAtlas.findRegion(getLocalizedString(imageModel.getFrame()));
            return new Image(atlasRegion);
        }
    }

    private NinePatchDrawable createNinePatchDrawable(String imageName, TextureAtlas textureAtlas ,int patchOffset) {
        NinePatchDrawable ninePatchDrawable = new NinePatchDrawable();
        NinePatch patch = new NinePatch(textureAtlas.findRegion(imageName), patchOffset, patchOffset, patchOffset, patchOffset);
        ninePatchDrawable.setPatch(patch);
        return ninePatchDrawable;
    }

}
