package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.TextFieldModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

public class TextFieldBuilder extends ActorBuilder{

    public TextFieldBuilder(AssetsInterface assets, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        super(assets, resolutionHelper, localizationService);
    }

    @Override
    public Actor build(BaseModel model, Group parent) {
        TextFieldModel textFieldModel = (TextFieldModel)model;
        
        BitmapFont font = assets.getFont(textFieldModel.getFontName());
        Color fontColor = Color.valueOf(textFieldModel.getFontColor());
        
        TextureAtlas textureAtlas = assets.getTextureAtlas(textFieldModel.getAtlasName());

        NinePatchDrawable cursor = convertTextureRegionToNinePatchDrawable(
                textureAtlas.findRegion(textFieldModel.getCursorImageName()),
                textFieldModel.getCursorOffset(),
                textFieldModel.getCursorOffset(),
                textFieldModel.getCursorOffset(),
                textFieldModel.getCursorOffset());
        cursor.getPatch().setColor(fontColor);

        NinePatchDrawable selection = convertTextureRegionToNinePatchDrawable(
                textureAtlas.findRegion(textFieldModel.getSelectionImageName()),
                textFieldModel.getSelectionOffset(),
                textFieldModel.getSelectionOffset(),
                textFieldModel.getSelectionOffset(),
                textFieldModel.getSelectionOffset());

        NinePatchDrawable background = null;

        if(textFieldModel.getBackgroundImageName() != null) {
            if (textFieldModel.isBackgroundUsingPatchSize()) {
                background = convertTextureRegionToNinePatchDrawable(
                        textureAtlas.findRegion(textFieldModel.getBackgroundImageName()),
                        textFieldModel.getBackgroundPatchSizeLeft(),
                        textFieldModel.getBackgroundPatchSizeRight(),
                        textFieldModel.getBackgroundPatchSizeTop(),
                        textFieldModel.getBackgroundPatchSizeBottom());
            } else {
                background = convertTextureRegionToNinePatchDrawable(
                        textureAtlas.findRegion(textFieldModel.getBackgroundImageName()),
                        textFieldModel.getBackgroundOffset(),
                        textFieldModel.getBackgroundOffset(),
                        textFieldModel.getBackgroundOffset(),
                        textFieldModel.getBackgroundOffset());
            }

			if(textFieldModel.getPadding() == 0) {
                addPaddings(background, textFieldModel.getLeftPadding(), textFieldModel.getRightPadding(), textFieldModel.getBottomPadding(), textFieldModel.getTopPadding());
            } else {
                addPaddings(background, textFieldModel.getPadding());
            }
            
        }
        
        TextFieldStyle textFieldStyle = new TextFieldStyle(font, fontColor, cursor, selection, background);
        TextField textField = new TextField(getLocalizedString(textFieldModel.getText()), textFieldStyle);
        textField.setPasswordMode(textFieldModel.isPassword());
        textField.setPasswordCharacter(textFieldModel.getPasswordChar().charAt(0));
        if(textFieldModel.getHint() != null) textField.setMessageText(getLocalizedString(textFieldModel.getHint()));
        normalizeModelSize(model, parent, model.getWidth(), model.getHeight());
        setBasicProperties(model, textField);

        textField.setAlignment(calculateAlignment(textFieldModel.getAlignment()));
        
        return textField;
    }

    private void addPaddings(NinePatchDrawable ninePatchDrawable, float padding) {
        addPaddings(ninePatchDrawable, padding, padding, padding, padding);
    }

    private void addPaddings(NinePatchDrawable ninePatchDrawable, float left, float right, float bottom, float top) {
        ninePatchDrawable.setLeftWidth(left * resolutionHelper.getPositionMultiplier());
        ninePatchDrawable.setRightWidth(right * resolutionHelper.getPositionMultiplier());
        ninePatchDrawable.setBottomHeight(bottom * resolutionHelper.getPositionMultiplier());
        ninePatchDrawable.setTopHeight(top * resolutionHelper.getPositionMultiplier());
    }

}
