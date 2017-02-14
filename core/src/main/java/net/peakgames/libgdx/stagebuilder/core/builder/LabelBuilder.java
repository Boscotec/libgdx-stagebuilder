package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.LabelModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;
import net.peakgames.libgdx.stagebuilder.core.util.GdxUtils;

public class LabelBuilder extends ActorBuilder {

    public static final Color DEFAULT_LABEL_COLOR = Color.WHITE;

    public LabelBuilder(AssetsInterface assets, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        super(assets, resolutionHelper, localizationService);
    }

    @Override
    public Actor build(BaseModel model) {
        LabelModel labelModel = (LabelModel) model;
        Color color = labelModel.getFontColor() == null ? DEFAULT_LABEL_COLOR : Color.valueOf(labelModel.getFontColor());

        BitmapFont font = assets.getFont(labelModel.getFontName());
        font.getData().markupEnabled = true;
        Label.LabelStyle style = new Label.LabelStyle(font, color);
        String initialText = getLocalizedString(labelModel.getText()).replace("\\n", String.format("%n"));
        Label label = new Label(initialText, style);
        
        normalizeModelSize(labelModel, 0, 0);
        setBasicProperties(model, label);
        setAlignmentAndScaling(labelModel, label);

        if(((LabelModel) model).isShadow()) {
            ShadowLabel shadowLabel = new ShadowLabel(initialText, style, ((LabelModel) model).getShadowColor(), resolutionHelper.getPositionMultiplier());
            setBasicProperties(model, shadowLabel);
            shadowLabel.setName(label.getName());
            setAlignmentAndScaling(labelModel, shadowLabel);
            return shadowLabel;
        }
        
        return label;
    }

    private void setAlignmentAndScaling(LabelModel labelModel, Label label) {
        label.setAlignment(calculateAlignment(labelModel.getAlignment()));
        label.setWrap(labelModel.isWrap());
        if (labelModel.isFontAutoScale()) {
            autoScaleLabel(label);
        } else if (labelModel.getFontScale() != 1) {
            label.setFontScale(label.getStyle().font.getScaleX() * labelModel.getFontScale());
        } else if (labelModel.getLabelScale() != 0) {
            float scaleLabelWidth = labelModel.getLabelScale() * resolutionHelper.getPositionMultiplier();
            scaleLabel(label, scaleLabelWidth);
        }
    }

    private void autoScaleLabel(Label label) {
        scaleLabel(label, label.getWidth());
    }

    private static void scaleLabel(Label label, float labelWidth){

        float labelTextWidth = GdxUtils.getTextWidth(label) /label.getFontScaleX();
        float scaleDownFactor = labelWidth / labelTextWidth;
        if (labelTextWidth > labelWidth) {
            label.setFontScale(label.getStyle().font.getScaleX() * scaleDownFactor);
        }
    }
}
