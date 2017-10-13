package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.TextAreaModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

public class TextAreaBuilder extends ActorBuilder{

    public TextAreaBuilder(AssetsInterface assets, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        super(assets, resolutionHelper, localizationService);
    }

    @Override
    public Actor build(BaseModel model, Group parent) {
        TextAreaModel textAreaModel = (TextAreaModel)model;
        
        BitmapFont font = assets.getFont(textAreaModel.getFontName());
        Color fontColor = Color.valueOf(textAreaModel.getFontColor());
        
        TextureAtlas textureAtlas = assets.getTextureAtlas(textAreaModel.getAtlasName());

        NinePatchDrawable cursor = convertTextureRegionToNinePatchDrawable(
                textureAtlas.findRegion(textAreaModel.getCursorImageName()),
                textAreaModel.getCursorOffset(),
                textAreaModel.getCursorOffset(),
                textAreaModel.getCursorOffset(),
                textAreaModel.getCursorOffset());
        cursor.getPatch().setColor(fontColor);

        NinePatchDrawable selection = convertTextureRegionToNinePatchDrawable(
                textureAtlas.findRegion(textAreaModel.getSelectionImageName()),
                textAreaModel.getSelectionOffset(),
                textAreaModel.getSelectionOffset(),
                textAreaModel.getSelectionOffset(),
                textAreaModel.getSelectionOffset());

        NinePatchDrawable background = null;

        if(textAreaModel.getBackgroundImageName() != null){
            if (textAreaModel.isBackgroundUsingPatchSize()) {
                background = convertTextureRegionToNinePatchDrawable(
                        textureAtlas.findRegion(textAreaModel.getBackgroundImageName()),
                        textAreaModel.getBackgroundPatchSizeLeft(),
                        textAreaModel.getBackgroundPatchSizeRight(),
                        textAreaModel.getBackgroundPatchSizeTop(),
                        textAreaModel.getBackgroundPatchSizeBottom());
            } else {
                background = convertTextureRegionToNinePatchDrawable(
                        textureAtlas.findRegion(textAreaModel.getBackgroundImageName()),
                        textAreaModel.getBackgroundOffset(),
                        textAreaModel.getBackgroundOffset(),
                        textAreaModel.getBackgroundOffset(),
                        textAreaModel.getBackgroundOffset());
            }
            background.setLeftWidth(textAreaModel.getPadding());
            background.setRightWidth(textAreaModel.getPadding());
            background.setBottomHeight(textAreaModel.getPadding());
            background.setTopHeight(textAreaModel.getPadding());
        }


        TextFieldStyle textAreaStyle = new TextFieldStyle(font, fontColor, cursor, selection, background);
        TextArea textArea = new TextArea(getLocalizedString(textAreaModel.getText()), textAreaStyle);
        textArea.setPasswordMode(textAreaModel.isPassword());
        textArea.setPasswordCharacter(textAreaModel.getPasswordChar().charAt(0));
        if(textAreaModel.getHint() != null) textArea.setMessageText(getLocalizedString(textAreaModel.getHint()));
        normalizeModelSize(model, parent, model.getWidth(), model.getHeight());
        setBasicProperties(model, textArea);
        
        return textArea;
    }

    protected void updateDrawableSize( TextureRegionDrawable textureRegionDrawable){
        float sizeMultiplier = resolutionHelper.getSizeMultiplier();
        textureRegionDrawable.setMinWidth( textureRegionDrawable.getMinWidth() * sizeMultiplier);
        textureRegionDrawable.setMinHeight( textureRegionDrawable.getMinHeight() * sizeMultiplier);
    }
}
