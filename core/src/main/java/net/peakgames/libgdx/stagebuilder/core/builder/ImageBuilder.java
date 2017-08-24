package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.ImageModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;
import net.peakgames.libgdx.stagebuilder.core.util.NinePatchUtils;

public class ImageBuilder extends ActorBuilder {

    private static final int MIN_VISIBLE_AREA_FOR_PATCH_REGION = 2;

    public ImageBuilder(AssetsInterface assets, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        super(assets, resolutionHelper, localizationService);
    }

    @Override
    public Actor build(BaseModel model, Group parent) {
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
        image.setWidth(backGroundSize.x);
        image.setHeight(backGroundSize.y);

        Vector2 backGroundPosition = resolutionHelper.calculateBackgroundPosition(image.getWidth(), image.getHeight());
        Vector2 gameAreaPosition = resolutionHelper.getGameAreaPosition();
          /*
  		 * stage root position is always set to gameAreaPosition.
  		 * Since the bg image is also inside the root group, bg image position should be updated.
		 */
        image.setPosition(backGroundPosition.x - gameAreaPosition.x, backGroundPosition.y - gameAreaPosition.y);
    }

    private Image createFromTexture(ImageModel imageModel) {
        if(imageModel.isNinepatch()){
            NinePatchDrawable ninePatchDrawable = new NinePatchDrawable();

            NinePatch patch;
            if (imageModel.getNinepatchOffset() == 0) {

                patch = new NinePatch(new TextureRegion(assets.getTexture(getLocalizedString(imageModel.getTextureSrc()))),
                        imageModel.getNinepatchOffsetLeft(), imageModel.getNinepatchOffsetRight(), imageModel.getNinepatchOffsetTop(), imageModel.getNinepatchOffsetBottom());
            } else {
                patch = new NinePatch(new TextureRegion(assets.getTexture(getLocalizedString(imageModel.getTextureSrc()))),
                        imageModel.getNinepatchOffset(), imageModel.getNinepatchOffset(), imageModel.getNinepatchOffset(), imageModel.getNinepatchOffset());
            }

            ninePatchDrawable.setPatch(patch);
            if(imageModel.getMinFilter() != null && imageModel.getMagFilter() != null) {
                patch.getTexture().setFilter(Texture.TextureFilter.valueOf(imageModel.getMinFilter()), Texture.TextureFilter.valueOf(imageModel.getMagFilter()));
            }
            
            if (imageModel.getTintColor() != null) {
                ninePatchDrawable = ninePatchDrawable.tint(Color.valueOf(imageModel.getTintColor()));
            }
            
            return new Image(ninePatchDrawable);
        }else{
            TextureRegion textureRegion = new TextureRegion(assets.getTexture(getLocalizedString(imageModel.getTextureSrc())));
            if(imageModel.getMinFilter() != null && imageModel.getMagFilter() != null) {
                textureRegion.getTexture().setFilter(Texture.TextureFilter.valueOf(imageModel.getMinFilter()), Texture.TextureFilter.valueOf(imageModel.getMagFilter()));
            }
            if (imageModel.isFlipX() || imageModel.isFlipY()) {
                textureRegion = new TextureRegion(textureRegion);
                textureRegion.flip(imageModel.isFlipX(), imageModel.isFlipY());
            }

            if (imageModel.getTintColor() != null) {
                SpriteDrawable spriteDrawable = new SpriteDrawable(new Sprite(textureRegion))
                        .tint(Color.valueOf(imageModel.getTintColor()));
                return new Image(spriteDrawable);
            }
            
            return new Image(textureRegion);
        }
    }

    private Image createFromTextureAtlas(ImageModel imageModel) {
        TextureAtlas textureAtlas = assets.getTextureAtlas(imageModel.getAtlasName());
        if(imageModel.isNinepatch()){
            if(imageModel.getNinepatchOffset() != 0) {
                imageModel.applyNinepatchValueForAllParts(imageModel.getNinepatchOffset());
            }
            normalizeNinePatchValues(imageModel, textureAtlas);
            NinePatchDrawable npDrawable = NinePatchUtils.createNinePatchDrawableFromAtlas(resolutionHelper, 
                    imageModel.getFrame(), textureAtlas, imageModel.getNinepatchOffsetLeft(), 
                    imageModel.getNinepatchOffsetRight(), imageModel.getNinepatchOffsetTop(), 
                    imageModel.getNinepatchOffsetBottom());

            if (imageModel.getTintColor() != null) {
                npDrawable = npDrawable.tint(Color.valueOf(imageModel.getTintColor()));
            }
            
            return new Image(npDrawable);
        }else{
            TextureAtlas.AtlasRegion atlasRegion = textureAtlas.findRegion(getLocalizedString(imageModel.getFrame()));
            if (imageModel.isFlipX() || imageModel.isFlipY()) {
                atlasRegion = new TextureAtlas.AtlasRegion(atlasRegion);
                atlasRegion.flip(imageModel.isFlipX(), imageModel.isFlipY());
            }

            if (imageModel.getTintColor() != null) {
                SpriteDrawable spriteDrawable = new SpriteDrawable(new Sprite(atlasRegion))
                        .tint(Color.valueOf(imageModel.getTintColor()));
                return new Image(spriteDrawable);
            }
            
            return new Image(atlasRegion);
        }
    }

    private void normalizeNinePatchValues(ImageModel imageModel, TextureAtlas textureAtlas) {
        TextureAtlas.AtlasRegion ninePatchRegion = textureAtlas.findRegion(imageModel.getFrame());

        float imageMultiplier = resolutionHelper.getPositionMultiplier() / resolutionHelper.getSizeMultiplier();

        imageModel.setNinepatchOffsetLeft((int) (imageModel.getNinepatchOffsetLeft() * imageMultiplier));
        imageModel.setNinepatchOffsetRight((int)(imageModel.getNinepatchOffsetRight() * imageMultiplier));
        imageModel.setNinepatchOffsetTop((int)(imageModel.getNinepatchOffsetTop() * imageMultiplier));
        imageModel.setNinepatchOffsetBottom((int)(imageModel.getNinepatchOffsetBottom() * imageMultiplier));
        normalizeHorizontalPatches(imageModel, ninePatchRegion);
        normalizeVerticalPatches(imageModel, ninePatchRegion);
    }

    private void normalizeHorizontalPatches(ImageModel imageModel, TextureAtlas.AtlasRegion ninePatchRegion) {
        int totalPatchWidth = imageModel.getNinepatchOffsetLeft() + imageModel.getNinepatchOffsetRight();
        if(totalPatchWidth >= ninePatchRegion.getRegionWidth()) {
            int usableAreaWidth = calculateMinUsableAreaSize(ninePatchRegion.getRegionWidth());
            imageModel.setNinepatchOffsetLeft(calculateNewPatchSize(usableAreaWidth, imageModel.getNinepatchOffsetLeft(), totalPatchWidth));
            imageModel.setNinepatchOffsetRight(calculateNewPatchSize(usableAreaWidth, imageModel.getNinepatchOffsetRight(), totalPatchWidth));
        }
    }

    private void normalizeVerticalPatches(ImageModel imageModel, TextureAtlas.AtlasRegion ninePatchRegion) {
        int totalPatchHeight = imageModel.getNinepatchOffsetTop() + imageModel.getNinepatchOffsetBottom();
        if(totalPatchHeight >= ninePatchRegion.getRegionHeight()) {
            int usableAreaHeight = calculateMinUsableAreaSize(ninePatchRegion.getRegionHeight());
            imageModel.setNinepatchOffsetTop(calculateNewPatchSize(usableAreaHeight, imageModel.getNinepatchOffsetTop(), totalPatchHeight ));
            imageModel.setNinepatchOffsetBottom(calculateNewPatchSize(usableAreaHeight, imageModel.getNinepatchOffsetBottom(), totalPatchHeight ));
        }
    }

    private static int calculateMinUsableAreaSize(int size) {
        return size - MIN_VISIBLE_AREA_FOR_PATCH_REGION;
    }

    private static int calculateNewPatchSize(int usableAreaSize, int patchSize, int totalPatchSize) {
        return (int)(usableAreaSize * (patchSize / (float)totalPatchSize));
    }
}
