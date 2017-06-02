package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.SelectBoxModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;
import net.peakgames.libgdx.stagebuilder.core.util.GdxUtils;

public class SelectBoxBuilder extends ActorBuilder {

    public static final Color DEFAULT_COLOR = Color.BLACK;
    public static final String DELIMITER = ";";
    public static int DEFAULT_PADDING_LEFT = 5;
    public static int DEFAULT_PADDING_RIGHT = 5;
    public static int DEFAULT_PADDING_TOP = 5;
    public static int DEFAULT_PADDING_BOTTOM = 5;

    public SelectBoxBuilder(AssetsInterface assets, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        super(assets, resolutionHelper, localizationService);
    }

    @Override
    public Actor build(BaseModel model, Group parent) {
        float positionMultiplier = resolutionHelper.getPositionMultiplier();
        DEFAULT_PADDING_RIGHT = (int) (DEFAULT_PADDING_RIGHT * positionMultiplier);
        DEFAULT_PADDING_LEFT = (int) (DEFAULT_PADDING_LEFT * positionMultiplier);
        SelectBoxModel selectBoxModel = (SelectBoxModel)model;
        selectBoxModel.setPaddingLeft((int) (selectBoxModel.getPaddingLeft() * positionMultiplier));
        selectBoxModel.setPaddingRight((int) (selectBoxModel.getPaddingRight() * positionMultiplier));
        selectBoxModel.setPaddingTop((int) (selectBoxModel.getPaddingTop() * positionMultiplier));
        selectBoxModel.setPaddingBottom((int) (selectBoxModel.getPaddingBottom() * positionMultiplier));

        TextureAtlas textureAtlas = assets.getTextureAtlas(selectBoxModel.getAtlasName());

        TextureRegionDrawable hScroll = new TextureRegionDrawable(textureAtlas.findRegion(selectBoxModel.getSelection()));
        TextureRegionDrawable hScrollKnob = new TextureRegionDrawable(textureAtlas.findRegion(selectBoxModel.getSelection()));
        TextureRegionDrawable vScroll = new TextureRegionDrawable(textureAtlas.findRegion(selectBoxModel.getSelection()));
        TextureRegionDrawable vScrollKnob = new TextureRegionDrawable(textureAtlas.findRegion(selectBoxModel.getSelection()));

        TextureRegionDrawable selection = new TextureRegionDrawable(textureAtlas.findRegion(selectBoxModel.getSelection()));

        TextureRegion selectionBackgroundTextureRegion = textureAtlas.findRegion(selectBoxModel.getSelectionBackground());

        NinePatchDrawable selectionBackgroundDrawable = convertTextureRegionToNinePatchDrawable(
                selectionBackgroundTextureRegion,
                selectBoxModel.getSelectionBackgroundNinePatchSizeLeft(),
                selectBoxModel.getSelectionBackgroundNinePatchSizeRight(),
                selectBoxModel.getSelectionBackgroundNinePatchSizeTop(),
                selectBoxModel.getSelectionBackgroundNinePatchSizeBottom()
        );

        TextureRegion backgroundTextureRegion = textureAtlas.findRegion(selectBoxModel.getBackground());
        NinePatchDrawable backgroundDrawable = convertTextureRegionToNinePatchDrawable(
                backgroundTextureRegion,
                selectBoxModel.getBackgroundNinePatchSizeLeft(),
                selectBoxModel.getBackgroundNinePatchSizeRight(),
                selectBoxModel.getBackgroundNinePatchSizeTop(),
                selectBoxModel.getBackgroundNinePatchSizeBottom()
        );

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle(backgroundDrawable, hScroll, hScrollKnob, vScroll, vScrollKnob);


        BitmapFont font = assets.getFont(selectBoxModel.getFontName());
        Color fontColor = selectBoxModel.getFontColor()==null ? DEFAULT_COLOR : Color.valueOf(selectBoxModel.getFontColor());
        Color fontColorSelected = selectBoxModel.getFontColorSelected()==null ? DEFAULT_COLOR : Color.valueOf(selectBoxModel.getFontColorSelected());
        Color fontColorUnselected = selectBoxModel.getFontColorUnselected()==null ? DEFAULT_COLOR : Color.valueOf(selectBoxModel.getFontColorUnselected());

        selection.setLeftWidth(selectBoxModel.getPaddingLeft()==0 ? DEFAULT_PADDING_LEFT * positionMultiplier : selectBoxModel.getPaddingLeft());
        selection.setRightWidth(selectBoxModel.getPaddingRight() == 0 ? DEFAULT_PADDING_RIGHT * positionMultiplier : selectBoxModel.getPaddingRight());
        selection.setTopHeight(selectBoxModel.getPaddingTop()==0 ? DEFAULT_PADDING_TOP * positionMultiplier : selectBoxModel.getPaddingTop());
        selection.setBottomHeight(selectBoxModel.getPaddingBottom() == 0 ? DEFAULT_PADDING_BOTTOM * positionMultiplier : selectBoxModel.getPaddingBottom());

        String[] values = new String[0];
        String filterValues =  getLocalizedString(selectBoxModel.getValue());
        if (selectBoxModel.getValue() != null && !filterValues.isEmpty()) {
            values = filterValues.split(DELIMITER);
        }
        
        autoScaleFont(font, values, selectBoxModel.getMaxTextWidth() * positionMultiplier);

        com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle listStyle = new com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle(font, fontColorSelected, fontColorUnselected, selection);

        selectionBackgroundDrawable.setLeftWidth(selectBoxModel.getPaddingLeft());
        selectionBackgroundDrawable.setRightWidth(selectBoxModel.getPaddingRight());
        SelectBox.SelectBoxStyle style = new SelectBox.SelectBoxStyle(font, fontColor, selectionBackgroundDrawable, scrollPaneStyle, listStyle);


        SelectBox selectBox = new SelectBox(style);
        selectBox.setItems(values);
        selectBox.setName(selectBoxModel.getName());
        selectBox.getScrollPane().setScrollingDisabled(selectBoxModel.isHorizontalScrollDisabled(), selectBoxModel.isVerticalScrollDisabled());

        selectBox.setBounds(selectBoxModel.getX(), selectBoxModel.getY(), selectionBackgroundDrawable.getPatch().getTotalWidth(), selectionBackgroundDrawable.getPatch().getTotalHeight());

        normalizeModelSize(selectBoxModel, selectionBackgroundDrawable.getPatch().getTotalWidth(), selectionBackgroundDrawable.getPatch().getTotalHeight());
        setBasicProperties(selectBoxModel, selectBox);

        return selectBox;
    }

    private void autoScaleFont(BitmapFont font, String[] values, float maxWidth) {
        if (maxWidth <= 0) {
            return;
        }
        float max = 0;
        for (String value : values) {
            float textWidth = GdxUtils.getTextWidth(value, font);
            if (textWidth > max) {
                max = textWidth;
            }
        }
        if (max > maxWidth) {
            font.getData().setScale(font.getScaleX() * (maxWidth/max));
        }
    }

}
