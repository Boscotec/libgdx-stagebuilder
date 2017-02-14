package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class ShadowLabel extends Label {

    private Label shadowLabel;
    private static final float shiftX = 2;
    private static final float shiftY = 2;
    private static float resolutionMult = 1;

    public ShadowLabel(CharSequence text, LabelStyle labelStyle, String shadowColorName) {
        this(text, labelStyle, Color.valueOf(shadowColorName));
        repositionShadowLabel();
    }

    public ShadowLabel(CharSequence text, LabelStyle labelStyle, Color color, float resolutionMult) {
        this(text, labelStyle, color);
        this.resolutionMult = resolutionMult;
        repositionShadowLabel();
    }

    public ShadowLabel(CharSequence text, LabelStyle labelStyle, String shadowColorName, float resolutionMult) {
        this(text, labelStyle, Color.valueOf(shadowColorName));
        this.resolutionMult = resolutionMult;
        repositionShadowLabel();
    }

    private void repositionShadowLabel() {
        float shadowX = getX() + (shiftX * resolutionMult);
        float shadowY = getY() - (shiftY * resolutionMult);
        shadowLabel.setPosition(shadowX, shadowY);
    }

    public ShadowLabel(CharSequence text, LabelStyle labelStyle, Color shadowColor) {
        super(text,labelStyle);
        LabelStyle shadowStyle = new LabelStyle(labelStyle.font, shadowColor);
        shadowLabel = new Label(text, shadowStyle);
    }

    @Override
    public void setZIndex(int index) {
        super.setZIndex(index);
        shadowLabel.setZIndex(index + 100);
    }

    @Override
    public void setFontScale(float fontScale) {
        super.setFontScale(fontScale);
        shadowLabel.setFontScale(fontScale);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        shadowLabel.draw(batch, parentAlpha);
        super.draw(batch, parentAlpha);
    }

    @Override
    public void setText(CharSequence newText) {
        super.setText(newText);
        shadowLabel.setText(newText);
    }

    @Override
    public void setAlignment(int labelAlign, int lineAlign) {
        super.setAlignment(labelAlign, lineAlign);
        shadowLabel.setAlignment(labelAlign, lineAlign);
    }

    public void setShadowColor(Color color){
        shadowLabel.setColor(color);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        shadowLabel.setPosition(x + (shiftX * resolutionMult), y - (shiftY * resolutionMult));
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        shadowLabel.setBounds(x + (shiftX * resolutionMult), y - (shiftY * resolutionMult), width, height);
    }

    public Label getShadowLabel() {
        return shadowLabel;
    }

    public void setShadowLabel(Label shadowLabel) {
        this.shadowLabel = shadowLabel;
    }

    @Override
    protected void setParent(Group parent) {
        super.setParent(parent);
        int index = parent.getChildren().indexOf(this, true);
        parent.addActorAt(index, shadowLabel);
    }

    @Override
    public void validate() {
        super.validate();
        shadowLabel.validate();
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        shadowLabel.setX(x + (shiftX * resolutionMult));
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        shadowLabel.setY(y - (shiftY * resolutionMult));
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        shadowLabel.setVisible(visible);
    }

    @Override
    public void setWrap(boolean wrap) {
        super.setWrap(wrap);
        shadowLabel.setWrap(wrap);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        if(shadowLabel != null){
            shadowLabel.setSize(width, height);
        }
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        shadowLabel.setWidth(width);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        shadowLabel.setHeight(height);
    }

    @Override
    public float getWidth() {
        return super.getWidth();
    }

    @Override
    public float getHeight() {
        return super.getHeight();
    }
}