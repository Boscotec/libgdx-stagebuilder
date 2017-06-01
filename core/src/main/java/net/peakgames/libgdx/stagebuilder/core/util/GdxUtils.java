package net.peakgames.libgdx.stagebuilder.core.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class GdxUtils {

    private GdxUtils() {
        
    }
    
    private static GlyphLayout glyphLayout = new GlyphLayout();

    public static String trim( String text, float width, BitmapFont font){
        if ( text == null){
            return null;
        }
        if ( getTextWidth(text, font) <= width){
            return text;
        }
        text = text.concat(".");

        do{
            int lastIndex = text.length() - 2;
            if ( lastIndex > 0){
                text = text.substring(0, lastIndex - 1);
                text = text.concat(".");
            }
            else{
                break;
            }
        }
        while ( getTextWidth(text, font) > width);
        return text;
    }

    public static void autoTrim(Label label){
        label.setText(trim(label.getText().toString(), label.getWidth(), label.getStyle().font));
    }

    public static void autoScaleLabel(Label label){
        float labelTextWidth = getTextWidth(label);
        float labelWidth = label.getWidth();
        float scaleDownFactor = labelWidth / labelTextWidth;
        if (labelTextWidth > labelWidth) {
            label.setFontScale(label.getStyle().font.getScaleX() * scaleDownFactor);
        }
    }

    public static void autoScaleTextButton(TextButton textButton){
        Label label = textButton.getLabel();
        float textButtonWidth = textButton.getWidth() - textButton.getPadLeft() - textButton.getPadRight();
        float labelWidth = getTextWidth(label);
        if (labelWidth > textButtonWidth) {
            float scaleDownFactor = textButtonWidth / labelWidth;
            label.setFontScale(label.getStyle().font.getScaleX() * scaleDownFactor);
            label.setWidth(label.getWidth() * scaleDownFactor);
        }
    }

    public static float getTextWidth(Label label) {
        return getTextWidth(label.getText().toString(), label.getStyle().font);
    }

    public static float getTextHeight(Label label) {
        return getTextHeight(label.getText().toString(), label.getStyle().font);
    }

    public static float getTextWidth(String text, BitmapFont font) {
        glyphLayout.setText(font, text);
        return glyphLayout.width;
    }

    public static float getTextHeight(String text, BitmapFont font) {
        glyphLayout.setText(font, text);
        return glyphLayout.height;
    }

    public static void setLineHeight(Label label, float height) {
        BitmapFont font = label.getStyle().font;
        BitmapFont.BitmapFontData copiedData = new BitmapFont.BitmapFontData(font.getData().fontFile, font.getData().flipped);
        copiedData.setLineHeight(height);
        Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(copiedData, font.getRegion(), true), new Color(font.getColor()));
        label.setStyle(style);
    }

    public static Label findAndSetLabelText(Group root, String name, String text) {
        return findAndSetLabelText(root, name, text, false);
    }
    
    public static Label findAndSetLabelText(Group root, String name, String text, boolean autoScale) {
        final Label label = root.findActor(name);
        if (label == null) return null;
        label.setText(text);
        if (autoScale) GdxUtils.autoScaleLabel(label);
        return label;
    }
    
    public static TextureRegion flipRegion(TextureRegion region, boolean flipX, boolean flipY) {
        if (flipX || flipY) {
            TextureRegion flippedRegion = new TextureRegion(region);
            flippedRegion.flip(flipX, flipY);
            return flippedRegion;
        }
        
        return region;
    }
    
    public static <T extends Actor> T findActorSafe(Group group, String name) {
        if (name == null) return null;
        return (T) group.findActor(name);
    }
}
