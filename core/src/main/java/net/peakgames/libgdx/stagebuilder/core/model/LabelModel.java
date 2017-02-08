package net.peakgames.libgdx.stagebuilder.core.model;

public class LabelModel extends BaseModel {
    private String text;
    private String fontName;
    private String fontColor;
    private float fontScale;
    private boolean wrap;
    private boolean fontAutoScale;
    /**
     * combination of "left, right, top, bottom, center
     */
    private String alignment;
    private boolean shadow;
    /**
     * Default shadow color is BLACK
     */
    private String shadowColor;
    private float labelScale;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public boolean isWrap() {
        return wrap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public boolean isShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public String getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(String shadowColor) {
        this.shadowColor = shadowColor;
    }

    public float getFontScale() {
        return fontScale;
    }

    public void setFontScale(float fontScale) {
        this.fontScale = fontScale;
    }

    public void setFontAutoScale(boolean fontAutoScale) {this.fontAutoScale = fontAutoScale;}

    public boolean isFontAutoScale() {return fontAutoScale;}

    public float getLabelScale() {
        return labelScale;
    }

    public void setLabelScale(float labelScale) {
        this.labelScale = labelScale;
    }
}
