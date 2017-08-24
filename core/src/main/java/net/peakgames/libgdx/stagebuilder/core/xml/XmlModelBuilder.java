package net.peakgames.libgdx.stagebuilder.core.xml;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Align;
import net.peakgames.libgdx.stagebuilder.core.builder.ShadowLabel;
import net.peakgames.libgdx.stagebuilder.core.model.*;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class XmlModelBuilder {

    public static final String IMAGE_TAG = "Image";
    public static final String BUTTON_TAG = "Button";
    public static final String TEXT_BUTTON_TAG = "TextButton";
    public static final String LABEL_TAG = "Label";
    public static final String SELECT_BOX_TAG = "SelectBox";
    public static final String GROUP_TAG = "Group";
    public static final String SLIDER_TAG = "Slider";
    public static final String TEXT_FIELD_TAG = "TextField";
    public static final String TEXT_AREA_TAG = "TextArea";
    public static final String CHECKBOX_TAG = "CheckBox";
    public static final String TOGGLE_WIDGET_TAG = "ToggleWidget";
    public static final String VERTICAL_GROUP_TAG = "Vertical";
    public static final String HORIZONTAL_GROUP_TAG = "Horizontal";
    public static final String VIEW_TAG = "View";
    public static final String LOCALIZED_STRING_PREFIX = "@string/";
    
    public static final List<String> PARENT_TAGS_LIST = Arrays.asList(
            GROUP_TAG.toLowerCase(),
            VERTICAL_GROUP_TAG.toLowerCase(), 
            HORIZONTAL_GROUP_TAG.toLowerCase()
    );

    public List<BaseModel> buildModels(FileHandle fileHandle) throws Exception {
        InputStream is = fileHandle.read();
        try {
            XmlPullParser xmlParser = XmlHelper.getXmlParser(is);
            return buildModels(xmlParser);
        } finally {
            is.close();
        }
    }

    public List<BaseModel> buildModels(XmlPullParser xmlParser) throws Exception {
        List<BaseModel> modelList = new LinkedList<BaseModel>();
        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    BaseModel model = processXmlStartTag(xmlParser);
                    if (model != null) {
                        modelList.add(model);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (canHaveChildren(xmlParser.getName())) {
                        eventType = XmlPullParser.END_DOCUMENT;
                    }
                    break;
                default:
                    break;
            }
            if (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = xmlParser.next();
            }
        }
        return modelList;
    }

    private BaseModel processXmlStartTag(XmlPullParser xmlParser) throws Exception {
        String tagName = xmlParser.getName();
        BaseModel model;
        if (IMAGE_TAG.equalsIgnoreCase(tagName)) {
            model = buildImageModel(xmlParser);
        } else if (BUTTON_TAG.equalsIgnoreCase(tagName)) {
            model = buildButtonModel(xmlParser);
        } else if (TEXT_BUTTON_TAG.equalsIgnoreCase(tagName)) {
            model = buildTextButtonModel(xmlParser);
        } else if (TEXT_FIELD_TAG.equalsIgnoreCase(tagName)) {
            model = buildTextFieldModel(xmlParser);
        } else if (TEXT_AREA_TAG.equalsIgnoreCase(tagName)) {
            model = buildTextAreaModel(xmlParser);
        } else if (LABEL_TAG.equalsIgnoreCase(tagName)) {
            model = buildLabelModel(xmlParser);
        } else if (SELECT_BOX_TAG.equalsIgnoreCase(tagName)) {
            model = buildSelectBoxModel(xmlParser);
        } else if ( SLIDER_TAG.equalsIgnoreCase(tagName)) {
            model = buildSliderModel( xmlParser);
        } else if ( GROUP_TAG.equalsIgnoreCase(tagName)) {
            model = buildGroupModel(xmlParser);
        } else if ( CHECKBOX_TAG.equalsIgnoreCase( tagName)){
            model = buildCheckBoxModel( xmlParser);
        } else if ( TOGGLE_WIDGET_TAG.equalsIgnoreCase(tagName)){
            model = buildToggleWidgetModel(xmlParser);
        } else if ( VERTICAL_GROUP_TAG.equalsIgnoreCase(tagName)){
            model = buildVerticalGroupModel(xmlParser);
        } else if ( HORIZONTAL_GROUP_TAG.equalsIgnoreCase(tagName)){
            model = buildHorizontalGroupModel(xmlParser);
        } else if (VIEW_TAG.equalsIgnoreCase(tagName)) {
            model = buildView(xmlParser);
        } else if (isCustomWidget(tagName)) { //for custom widget implementation refer to CustomView
            model = buildCustomWidget(xmlParser, tagName);
        } else {
            model = buildExternalGroupModel(xmlParser, tagName);
        }
        return model;
    }

    private BaseModel buildView(XmlPullParser xmlParser) {
        ViewModel model = new ViewModel();
        setBaseModelParameters(model, xmlParser);
        
        model.setKlass(XmlHelper.readStringAttribute(xmlParser, "class"));
        model.setLayout(XmlHelper.readStringAttribute(xmlParser, "layout", null));

        int numberOfAttributes = xmlParser.getAttributeCount();
        for (int i = 0; i < numberOfAttributes; i++) {
            model.addAttribute(xmlParser.getAttributeName(i), xmlParser.getAttributeValue(i));
        }
        
        return model;
    }

    private BaseModel buildHorizontalGroupModel(XmlPullParser xmlParser) throws Exception {
        return buildOneDimensionalGroupModel(xmlParser, 
                new OneDimensionGroupModel(OneDimensionGroupModel.Orientation.HORIZONTAL));
    }

    private BaseModel buildVerticalGroupModel(XmlPullParser xmlParser) throws Exception {
        return buildOneDimensionalGroupModel(xmlParser, 
                new OneDimensionGroupModel(OneDimensionGroupModel.Orientation.VERTICAL));
    }
    
    private OneDimensionGroupModel buildOneDimensionalGroupModel(XmlPullParser xmlParser, 
                                                                 OneDimensionGroupModel model)
            throws Exception {
        setBaseModelParameters(model, xmlParser);
        model.setFill(XmlHelper.readBooleanAttribute(xmlParser, "fill", false));
        model.setReverse(XmlHelper.readBooleanAttribute(xmlParser, "reverse", false));
        model.setPadLeft(XmlHelper.readFloatAttribute(xmlParser, "padLeft", 0));
        model.setPadTop(XmlHelper.readFloatAttribute(xmlParser, "padTop", 0));
        model.setPadRight(XmlHelper.readFloatAttribute(xmlParser, "padRight", 0));
        model.setPadBottom(XmlHelper.readFloatAttribute(xmlParser, "padBottom", 0));
        model.setPads(XmlHelper.readFloatArrayAttribute(xmlParser, "pads", 4, 0));
        model.setAlign(XmlHelper.readAlignmentAttribute(xmlParser, "align", OneDimensionGroupModel.DEFAULT_ALIGNMENT));
        model.setSpacing(XmlHelper.readFloatAttribute(xmlParser, "spacing", 0));
        xmlParser.next();
        List<BaseModel> subModels = buildModels(xmlParser);
        model.setChildren(subModels);
        return model;
    }

    private BaseModel buildExternalGroupModel(XmlPullParser xmlParser, String tagName) {
        ExternalGroupModel model = new ExternalGroupModel();
        setBaseModelParameters(model, xmlParser);
        model.setFileName(tagName + ".xml");
        return model;
    }

    private BaseModel buildCustomWidget(XmlPullParser xmlParser, String klass) {
        CustomWidgetModel model = new CustomWidgetModel();
        setBaseModelParameters(model, xmlParser);
        model.setKlass(klass);
        int numberOfAttributes = xmlParser.getAttributeCount();
        for (int i = 0; i < numberOfAttributes; i++) {
            model.addAttribute(
                    xmlParser.getAttributeName(i),
                    xmlParser.getAttributeValue(i));
        }
        return model;
    }

    private boolean canHaveChildren(String tag) {
        return PARENT_TAGS_LIST.contains(tag.toLowerCase());
    }

    public boolean isCustomWidget(String tagName) {
        return tagName.contains(".");
    }

    private BaseModel buildGroupModel(XmlPullParser xmlParser) throws Exception {
        GroupModel group = new GroupModel();
        setBaseModelParameters(group, xmlParser);
        xmlParser.next();
        List<BaseModel> subModels = buildModels(xmlParser);
        group.setChildren(subModels);
        return group;
    }

    private BaseModel buildImageModel(XmlPullParser xmlParser) {
        ImageModel image = new ImageModel();
        setBaseModelParameters(image, xmlParser);
        image.setAtlasName(XmlHelper.readStringAttribute(xmlParser, "atlas"));
        image.setFrame(XmlHelper.readStringAttribute(xmlParser, "frame"));
        image.setTextureSrc(XmlHelper.readStringAttribute(xmlParser, "src"));
        image.setType(XmlHelper.readStringAttribute(xmlParser, "type"));
        image.setMinFilter(XmlHelper.readStringAttribute(xmlParser, "minFilter"));
        image.setMagFilter(XmlHelper.readStringAttribute(xmlParser, "magFilter"));
        image.setFlipX(XmlHelper.readBooleanAttribute(xmlParser, "flipX", false));
        image.setFlipY(XmlHelper.readBooleanAttribute(xmlParser, "flipY", false));
        image.setTintColor(XmlHelper.readStringAttribute(xmlParser, "tintColor"));
        
        if (XmlHelper.hasAttribute(xmlParser, "np")) {
            int[] npArray = XmlHelper.readIntArrayAttribute(xmlParser, "np", 4, 0);
            image.setNinepatch(true);
            if (npArray[1] == 0) {
                image.setNinepatchOffset(npArray[0]);
            } else {
                image.setNinepatchOffsetLeft(npArray[0]);
                image.setNinepatchOffsetRight(npArray[1]);
                image.setNinepatchOffsetTop(npArray[2]);
                image.setNinepatchOffsetBottom(npArray[3]);
            }
        } else {
            image.setNinepatch(XmlHelper.readBooleanAttribute(xmlParser, "ninepatch", false));
            image.setNinepatchOffset(XmlHelper.readIntAttribute(xmlParser, "ninepatchOffset", 0));
            image.setNinepatchOffsetLeft(XmlHelper.readIntAttribute(xmlParser, "npLeft", 0));
            image.setNinepatchOffsetRight(XmlHelper.readIntAttribute(xmlParser, "npRight", 0));
            image.setNinepatchOffsetTop(XmlHelper.readIntAttribute(xmlParser, "npTop", 0));
            image.setNinepatchOffsetBottom(XmlHelper.readIntAttribute(xmlParser, "npBottom", 0));
        }
        
        return image;
    }

    private BaseModel buildLabelModel(XmlPullParser xmlParser) {
        LabelModel label = new LabelModel();
        setBaseModelParameters(label, xmlParser);
        label.setText(XmlHelper.readStringAttribute(xmlParser, "text"));
        label.setFontName(XmlHelper.readStringAttribute(xmlParser, "fontName"));
        label.setFontColor(XmlHelper.readStringAttribute(xmlParser, "fontColor"));
        label.setWrap(XmlHelper.readBooleanAttribute(xmlParser, "wrap", false));
        label.setAlignment(XmlHelper.readStringAttribute(xmlParser, "align"));
        label.setShadow(XmlHelper.readBooleanAttribute(xmlParser, "shadow", false));
        label.setShadowX(XmlHelper.readFloatAttribute(xmlParser, "shadowX", ShadowLabel.DEFAULT_SHIFT_X));
        label.setShadowY(XmlHelper.readFloatAttribute(xmlParser, "shadowY", ShadowLabel.DEFAULT_SHIFT_Y));
        label.setShadowColor(XmlHelper.readStringAttribute(xmlParser, "shadowColor", "000000"));
        label.setFontScale(XmlHelper.readFloatAttribute(xmlParser, "fontScale", 1f));
        label.setFontAutoScale(XmlHelper.readBooleanAttribute(xmlParser, "fontAutoScale", false));
        label.setLabelScale(XmlHelper.readFloatAttribute(xmlParser, "labelScale", 0));
        return label;
    }

    private BaseModel buildSelectBoxModel(XmlPullParser xmlParser) {
        SelectBoxModel selectBoxModel = new SelectBoxModel();
        setBaseModelParameters(selectBoxModel, xmlParser);
        selectBoxModel.setName(XmlHelper.readStringAttribute(xmlParser, "name"));
        selectBoxModel.setValue(XmlHelper.readStringAttribute(xmlParser, "value"));
        selectBoxModel.setFontName(XmlHelper.readStringAttribute(xmlParser, "font"));
        selectBoxModel.setFontColor(XmlHelper.readStringAttribute(xmlParser, "fontColor"));
        selectBoxModel.setFontColorSelected(XmlHelper.readStringAttribute(xmlParser, "fontColorSelected"));
        selectBoxModel.setFontColorUnselected(XmlHelper.readStringAttribute(xmlParser, "fontColorUnselected"));
        selectBoxModel.setAtlasName(XmlHelper.readStringAttribute(xmlParser, "atlas"));
        selectBoxModel.setBackground(XmlHelper.readStringAttribute(xmlParser, "background"));
        selectBoxModel.setSelection(XmlHelper.readStringAttribute(xmlParser, "selection"));
        selectBoxModel.setSelectionBackground(XmlHelper.readStringAttribute(xmlParser, "selectionBackground"));
        selectBoxModel.setPaddingLeft(XmlHelper.readIntAttribute(xmlParser, "paddingLeft", 1));
        selectBoxModel.setPaddingRight(XmlHelper.readIntAttribute(xmlParser, "paddingRight", 1));
        selectBoxModel.setPaddingTop(XmlHelper.readIntAttribute(xmlParser, "paddingTop", 1));
        selectBoxModel.setPaddingBottom(XmlHelper.readIntAttribute(xmlParser, "paddingBottom", 1));
        selectBoxModel.setPatchSize(XmlHelper.readIntAttribute(xmlParser, "patchSize", 1));
        selectBoxModel.setMaxTextWidth(XmlHelper.readIntAttribute(xmlParser, "maxTextWidth", 0));
        selectBoxModel.setHorizontalScrollDisabled(XmlHelper.readBooleanAttribute(xmlParser, "horizontalScrollDisabled", false));
        selectBoxModel.setVerticalScrollDisabled(XmlHelper.readBooleanAttribute(xmlParser, "verticalScrollDisabled", false));
        selectBoxModel.setBackgroundNinePatchSizeLeft(XmlHelper.readIntAttribute(xmlParser, "bgPatchSizeLeft", 0));
        selectBoxModel.setBackgroundNinePatchSizeRight(XmlHelper.readIntAttribute(xmlParser, "bgPatchSizeRight", 0));
        selectBoxModel.setBackgroundNinePatchSizeTop(XmlHelper.readIntAttribute(xmlParser, "bgPatchSizeTop", 0));
        selectBoxModel.setBackgroundNinePatchSizeBottom(XmlHelper.readIntAttribute(xmlParser, "bgPatchSizeBottom", 0));
        selectBoxModel.setSelectionBackgroundNinePatchSizeLeft(XmlHelper.readIntAttribute(xmlParser, "selectionBgPatchSizeLeft", 0));
        selectBoxModel.setSelectionBackgroundNinePatchSizeRight(XmlHelper.readIntAttribute(xmlParser, "selectionBgPatchSizeRight", 0));
        selectBoxModel.setSelectionBackgroundNinePatchSizeTop(XmlHelper.readIntAttribute(xmlParser, "selectionBgPatchSizeTop", 0));
        selectBoxModel.setSelectionBackgroundNinePatchSizeBottom(XmlHelper.readIntAttribute(xmlParser, "selectionBgPatchSizeBottom", 0));
        return selectBoxModel;
    }

    private BaseModel buildButtonModel(XmlPullParser xmlParser) {
        ButtonModel button = new ButtonModel();
        setBaseModelParameters(button, xmlParser);
        setButtonModelProperties(button, xmlParser);
        return button;
    }

    private void setButtonModelProperties(ButtonModel button, XmlPullParser xmlParser) {
        button.setAtlasName(XmlHelper.readStringAttribute(xmlParser, "atlas"));
        button.setFrameUp(XmlHelper.readStringAttribute(xmlParser, "frameUp"));
        button.setFrameDown(XmlHelper.readStringAttribute(xmlParser, "frameDown"));
        button.setFrameDisabled(XmlHelper.readStringAttribute(xmlParser, "frameDisabled"));
        button.setFrameChecked( XmlHelper.readStringAttribute( xmlParser, "frameChecked"));
        button.setTextureSrcUp(XmlHelper.readStringAttribute(xmlParser, "textureSrcUp"));
        button.setTextureSrcDown(XmlHelper.readStringAttribute(xmlParser, "textureSrcDown"));
        button.setTextureSrcDisabled(XmlHelper.readStringAttribute(xmlParser, "textureSrcDisabled"));
        button.setTextureSrcChecked( XmlHelper.readStringAttribute( xmlParser, "textureSrcChecked"));
        
        button.setTintColorUp( XmlHelper.readStringAttribute( xmlParser, "tintColorUp"));
        button.setTintColorDown( XmlHelper.readStringAttribute( xmlParser, "tintColorDown"));
        button.setTintColorDisabled( XmlHelper.readStringAttribute( xmlParser, "tintColorDisabled"));
        button.setTintColorChecked( XmlHelper.readStringAttribute( xmlParser, "tintColorChecked"));

        if (XmlHelper.hasAttribute(xmlParser, "np")) {
            int[] npArray = XmlHelper.readIntArrayAttribute(xmlParser, "np", 4, -1);
            button.setNinePatch(true);
            if (npArray[1] == -1) {
                button.setNinePatch(npArray[0]);
            } else {
                button.setNinePatch(npArray[0], npArray[1], npArray[2], npArray[3]);
            }
        } else {
            button.setNinePatch(XmlHelper.readBooleanAttribute(xmlParser, "ninepatch", false));
            button.setNpTop(XmlHelper.readIntAttribute(xmlParser, "npTop", 0));
            button.setNpBottom(XmlHelper.readIntAttribute(xmlParser, "npBottom", 0));
            button.setNpLeft(XmlHelper.readIntAttribute(xmlParser, "npLeft", 0));
            button.setNpRight(XmlHelper.readIntAttribute(xmlParser, "npRight", 0));
        }

        button.setFlipX(XmlHelper.readBooleanAttribute(xmlParser, "flipX", false));
        button.setFlipY(XmlHelper.readBooleanAttribute(xmlParser, "flipY", false));
    }

    private BaseModel buildSliderModel( XmlPullParser xmlPullParser){
        SliderModel sliderModel = new SliderModel();
        setBaseModelParameters( sliderModel, xmlPullParser);
        setSliderModelProperties(sliderModel, xmlPullParser);
        return sliderModel;
    }

    private void setSliderModelProperties( SliderModel sliderModel, XmlPullParser xmlPullParser){
        sliderModel.setAtlasName( XmlHelper.readStringAttribute( xmlPullParser, "atlas"));
        sliderModel.setFrameBackground( XmlHelper.readStringAttribute( xmlPullParser, "frameBackground"));
        sliderModel.setFrameKnob( XmlHelper.readStringAttribute( xmlPullParser, "frameKnob"));
        sliderModel.setTextureSrcBackground( XmlHelper.readStringAttribute( xmlPullParser, "textureSrcBackground"));
        sliderModel.setTextureSrcKnob( XmlHelper.readStringAttribute( xmlPullParser, "textureSrcKnob"));
        sliderModel.setMinValue( XmlHelper.readFloatAttribute( xmlPullParser, "minValue", 0));
        sliderModel.setMaxValue( XmlHelper.readFloatAttribute( xmlPullParser, "maxValue", 1));
        sliderModel.setStepSize( XmlHelper.readFloatAttribute( xmlPullParser, "stepSize", 0.1f));
    }
    
    private BaseModel buildTextFieldModel(XmlPullParser xmlParser) {
    	TextFieldModel textFieldModel = new TextFieldModel();
    	setBaseModelParameters(textFieldModel, xmlParser);
    	textFieldModel.setAtlasName(XmlHelper.readStringAttribute(xmlParser, "atlas"));
    	textFieldModel.setText(XmlHelper.readStringAttribute(xmlParser, "text"));
    	textFieldModel.setBackgroundImageName(XmlHelper.readStringAttribute(xmlParser, "backgroundImage"));
    	textFieldModel.setSelectionImageName(XmlHelper.readStringAttribute(xmlParser, "selectionImage"));
    	textFieldModel.setCursorImageName(XmlHelper.readStringAttribute(xmlParser, "cursorImage"));
        textFieldModel.setBackgroundOffset(XmlHelper.readIntAttribute(xmlParser, "backgroundOffset", 0));
        textFieldModel.setBackgroundPatchSizeLeft(XmlHelper.readIntAttribute(xmlParser, "bgPatchSizeLeft", 0));
        textFieldModel.setBackgroundPatchSizeRight(XmlHelper.readIntAttribute(xmlParser, "bgPatchSizeRight", 0));
        textFieldModel.setBackgroundPatchSizeTop(XmlHelper.readIntAttribute(xmlParser, "bgPatchSizeTop", 0));
        textFieldModel.setBackgroundPatchSizeBottom(XmlHelper.readIntAttribute(xmlParser, "bgPatchSizeBottom", 0));
    	textFieldModel.setSelectionOffset(XmlHelper.readIntAttribute(xmlParser, "selectionOffset", 0));
    	textFieldModel.setCursorOffset(XmlHelper.readIntAttribute(xmlParser, "cursorOffset", 0));
    	textFieldModel.setFontName(XmlHelper.readStringAttribute(xmlParser, "fontName"));
    	textFieldModel.setFontColor(XmlHelper.readStringAttribute(xmlParser, "fontColor"));
    	textFieldModel.setPassword(XmlHelper.readBooleanAttribute(xmlParser, "password", false));
    	textFieldModel.setPasswordChar(XmlHelper.readStringAttribute(xmlParser, "passwordChar", "*"));
        textFieldModel.setHint(XmlHelper.readStringAttribute(xmlParser, "hint"));
        textFieldModel.setPadding(XmlHelper.readFloatAttribute(xmlParser, "padding", 0.0f));
        textFieldModel.setLeftPadding(XmlHelper.readFloatAttribute(xmlParser, "leftPadding", 0.0f));
        textFieldModel.setRightPadding(XmlHelper.readFloatAttribute(xmlParser, "rightPadding", 0.0f));
        textFieldModel.setTopPadding(XmlHelper.readFloatAttribute(xmlParser, "topPadding", 0.0f));
        textFieldModel.setBottomPadding(XmlHelper.readFloatAttribute(xmlParser, "bottomPadding", 0.0f));
        textFieldModel.setAlignment(XmlHelper.readStringAttribute(xmlParser, "align"));
    	return textFieldModel;
    }
    private BaseModel buildTextAreaModel(XmlPullParser xmlParser) {
        TextAreaModel textAreaModel = new TextAreaModel();
        setBaseModelParameters(textAreaModel, xmlParser);
        textAreaModel.setAtlasName(XmlHelper.readStringAttribute(xmlParser, "atlas"));
        textAreaModel.setText(XmlHelper.readStringAttribute(xmlParser, "text"));
        textAreaModel.setBackgroundImageName(XmlHelper.readStringAttribute(xmlParser, "backgroundImage"));
        textAreaModel.setSelectionImageName(XmlHelper.readStringAttribute(xmlParser, "selectionImage"));
        textAreaModel.setCursorImageName(XmlHelper.readStringAttribute(xmlParser, "cursorImage"));
        textAreaModel.setBackgroundOffset(XmlHelper.readIntAttribute(xmlParser, "backgroundOffset", 0));
        textAreaModel.setBackgroundPatchSizeLeft(XmlHelper.readIntAttribute(xmlParser, "bgPatchSizeLeft", 0));
        textAreaModel.setBackgroundPatchSizeRight(XmlHelper.readIntAttribute(xmlParser, "bgPatchSizeRight", 0));
        textAreaModel.setBackgroundPatchSizeTop(XmlHelper.readIntAttribute(xmlParser, "bgPatchSizeTop", 0));
        textAreaModel.setBackgroundPatchSizeBottom(XmlHelper.readIntAttribute(xmlParser, "bgPatchSizeBottom", 0));
        textAreaModel.setSelectionOffset(XmlHelper.readIntAttribute(xmlParser, "selectionOffset", 0));
        textAreaModel.setCursorOffset(XmlHelper.readIntAttribute(xmlParser, "cursorOffset", 0));
        textAreaModel.setFontName(XmlHelper.readStringAttribute(xmlParser, "fontName"));
        textAreaModel.setFontColor(XmlHelper.readStringAttribute(xmlParser, "fontColor"));
        textAreaModel.setPassword(XmlHelper.readBooleanAttribute(xmlParser, "password", false));
        textAreaModel.setPasswordChar(XmlHelper.readStringAttribute(xmlParser, "passwordChar", "*"));
        textAreaModel.setHint(XmlHelper.readStringAttribute(xmlParser, "hint"));
        textAreaModel.setPadding(XmlHelper.readFloatAttribute(xmlParser, "padding", 0.0f));
        return textAreaModel;
    }

    private BaseModel buildTextButtonModel(XmlPullParser xmlParser) {
        TextButtonModel textButton = new TextButtonModel();
        setBaseModelParameters(textButton, xmlParser);
        setButtonModelProperties(textButton, xmlParser);
        setTextButtonModelProperties(textButton, xmlParser);
        return textButton;
    }

    private BaseModel buildCheckBoxModel( XmlPullParser xmlPullParser){
        CheckBoxModel checkBoxModel = new CheckBoxModel();
        setBaseModelParameters( checkBoxModel, xmlPullParser);
        setButtonModelProperties(checkBoxModel, xmlPullParser);
        setTextButtonModelProperties(checkBoxModel, xmlPullParser);
        setCheckBoxModelProperties(checkBoxModel, xmlPullParser);
        return checkBoxModel;
    }

    private BaseModel buildToggleWidgetModel( XmlPullParser xmlPullParser){
        ToggleWidgetModel toggleWidgetModel = new ToggleWidgetModel();
        setBaseModelParameters(toggleWidgetModel, xmlPullParser);

        toggleWidgetModel.setInitialToggle(XmlHelper.readStringAttribute(xmlPullParser, "initialToggle", "left"));
        toggleWidgetModel.setAtlasName(XmlHelper.readStringAttribute(xmlPullParser, "atlas", null));
        toggleWidgetModel.setButtonImageName(XmlHelper.readStringAttribute(xmlPullParser, "buttonImageName", null));
        toggleWidgetModel.setButtonDownImageName(XmlHelper.readStringAttribute(xmlPullParser, "buttonDownImageName", null));
        toggleWidgetModel.setBackgroundImageName(XmlHelper.readStringAttribute(xmlPullParser, "backgroundImageName", "left"));
        toggleWidgetModel.setToggleButtonPadding(XmlHelper.readFloatAttribute(xmlPullParser, "toggleButtonPadding", 1.0f));

        return toggleWidgetModel;
    }

    private void setCheckBoxModelProperties(CheckBoxModel checkBoxModel, XmlPullParser xmlPullParser) {
        checkBoxModel.setFrameCheckboxOff( XmlHelper.readStringAttribute( xmlPullParser, "frameCheckBoxOff"));
        checkBoxModel.setFrameCheckboxOn(XmlHelper.readStringAttribute(xmlPullParser, "frameCheckBoxOn"));
        checkBoxModel.setFrameCheckboxOver( XmlHelper.readStringAttribute( xmlPullParser, "frameCheckBoxOver"));
        checkBoxModel.setTextureSrcCheckboxOff(XmlHelper.readStringAttribute(xmlPullParser, "textureSrcCheckBoxOff"));
        checkBoxModel.setTextureSrcCheckboxOn(XmlHelper.readStringAttribute(xmlPullParser, "textureSrcCheckBoxOn"));
        checkBoxModel.setTextureSrcCheckboxOver(XmlHelper.readStringAttribute(xmlPullParser, "textureSrcCheckBoxOver"));
    }

    private void setTextButtonModelProperties( TextButtonModel textButton, XmlPullParser xmlParser) {
        textButton.setText(XmlHelper.readStringAttribute(xmlParser, "text"));
        textButton.setFontName(XmlHelper.readStringAttribute(xmlParser, "fontName"));
        textButton.setFontColor(XmlHelper.readStringAttribute(xmlParser, "fontColor"));
        textButton.setDisabledFontColor(XmlHelper.readStringAttribute(xmlParser, "disabledFontColor"));
        textButton.setFontScale(XmlHelper.readFloatAttribute(xmlParser, "fontScale", 1.0f));
        textButton.setLabelPadding(XmlHelper.readFloatAttribute(xmlParser, "labelPadding", 0.0f));
        textButton.setLabelPaddingLeft(XmlHelper.readFloatAttribute(xmlParser, "labelPaddingLeft", 0.0f));
        textButton.setLabelPaddingRight(XmlHelper.readFloatAttribute(xmlParser, "labelPaddingRight", 0.0f));
        textButton.setLabelPaddingTop(XmlHelper.readFloatAttribute(xmlParser, "labelPaddingTop", 0.0f));
        textButton.setLabelPaddingBottom(XmlHelper.readFloatAttribute(xmlParser, "labelPaddingBottom", 0.0f));
        textButton.setAlignment(XmlHelper.readStringAttribute(xmlParser, "align"));
        textButton.setWrap(XmlHelper.readBooleanAttribute(xmlParser, "wrap", false));
        textButton.setFontAutoScale(XmlHelper.readBooleanAttribute(xmlParser, "fontAutoScale", false));
    }

    private void setBaseModelParameters(BaseModel model, XmlPullParser xmlParser) {
        model.setName(XmlHelper.readStringAttribute(xmlParser, "name"));
        model.setPathName(XmlHelper.readStringAttribute(xmlParser, "path", null));
        model.setX(XmlHelper.readFloatAttribute(xmlParser, "x", 0.0f));
        model.setY(XmlHelper.readFloatAttribute(xmlParser, "y", 0.0f));
        model.setWidth(XmlHelper.readFloatAttribute(xmlParser, "width", 0.0f));
        model.setHeight(XmlHelper.readFloatAttribute(xmlParser, "height", 0.0f));
        model.setOriginX(XmlHelper.readFloatAttribute(xmlParser, "originX", 0.0f));
        model.setOriginY(XmlHelper.readFloatAttribute(xmlParser, "originY", 0.0f));
        model.setzIndex(XmlHelper.readIntAttribute(xmlParser, "zIndex", 0));
        model.setScale(XmlHelper.readFloatAttribute(xmlParser, "scale", 1));
        model.setScaleX(XmlHelper.readFloatAttribute(xmlParser, "scaleX", 1));
        model.setScaleY(XmlHelper.readFloatAttribute(xmlParser, "scaleY", 1));
        model.setVisible(Boolean.valueOf(XmlHelper.readStringAttribute(xmlParser, "visible", "true")));
        model.setColor(XmlHelper.readStringAttribute(xmlParser, "color", null));
        model.setRotation(XmlHelper.readFloatAttribute(xmlParser, "rotation", 0.0f));
        model.setScreenAlignment(XmlHelper.readStringAttribute(xmlParser, "screenAlign", null));
        model.setScreenPaddingTop(XmlHelper.readFloatAttribute(xmlParser, "screenPaddingTop", 0.0f));
        model.setScreenPaddingBottom(XmlHelper.readFloatAttribute(xmlParser, "screenPaddingBottom", 0.0f));
        model.setScreenPaddingLeft(XmlHelper.readFloatAttribute(xmlParser, "screenPaddingLeft", 0.0f));
        model.setScreenPaddingRight(XmlHelper.readFloatAttribute(xmlParser, "screenPaddingRight", 0.0f));
        model.setTouchable(XmlHelper.readStringAttribute(xmlParser, "touchable", "enabled"));
        model.setDebugEnabled(XmlHelper.readBooleanAttribute(xmlParser, "debug", false));
        
        model.setToLeftOf(XmlHelper.readStringAttribute(xmlParser, "toLeftOf", null));
        model.setToRightOf(XmlHelper.readStringAttribute(xmlParser, "toRightOf", null));
        model.setToAboveOf(XmlHelper.readStringAttribute(xmlParser, "toAboveOf", null));
        model.setToBelowOf(XmlHelper.readStringAttribute(xmlParser, "toBelowOf", null));
        
        model.setAlignInParent(XmlHelper.readAlignmentAttribute(xmlParser, "alignInParent", Align.bottomLeft));
    }

}
