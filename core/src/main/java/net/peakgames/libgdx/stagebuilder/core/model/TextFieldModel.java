package net.peakgames.libgdx.stagebuilder.core.model;

public class TextFieldModel extends ButtonModel {

	private String text;
	private String fontName;
	private String fontColor;
	private String cursorImageName;
	private String selectionImageName;
	private String backgroundImageName;
    private int backgroundOffset;
    private int backgroundPatchSizeLeft;
    private int backgroundPatchSizeRight;
    private int backgroundPatchSizeTop;
    private int backgroundPatchSizeBottom;

	private int cursorOffset;
	private int selectionOffset;
	private boolean password;
	private String passwordChar = "*";
    private String hint;
	private String hintColor;
    private float padding;
	private float leftPadding;
	private float rightPadding;
	private float topPadding;
	private float bottomPadding;
	
	private String alignment;

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

	public String getCursorImageName() {
		return cursorImageName;
	}

	public void setCursorImageName(String cursorImageName) {
		this.cursorImageName = cursorImageName;
	}

	public String getSelectionImageName() {
		return selectionImageName;
	}

	public void setSelectionImageName(String selectionImageName) {
		this.selectionImageName = selectionImageName;
	}

	public String getBackgroundImageName() {
		return backgroundImageName;
	}

	public void setBackgroundImageName(String backgroundImageName) {
		this.backgroundImageName = backgroundImageName;
	}

    public boolean isBackgroundUsingPatchSize() {
        return backgroundPatchSizeLeft > 0
                && backgroundPatchSizeRight > 0
                && backgroundPatchSizeTop > 0
                && backgroundPatchSizeBottom > 0;
    }

    public int getBackgroundOffset() {
		return backgroundOffset;
	}

	public void setBackgroundOffset(int backGroundOffset) {
		this.backgroundOffset = backGroundOffset;
	}

    public int getBackgroundPatchSizeBottom() {
        return backgroundPatchSizeBottom;
    }

    public void setBackgroundPatchSizeBottom(int backgroundPatchSizeBottom) {
        this.backgroundPatchSizeBottom = backgroundPatchSizeBottom;
    }

    public int getBackgroundPatchSizeLeft() {
        return backgroundPatchSizeLeft;
    }

    public void setBackgroundPatchSizeLeft(int backgroundPatchSizeLeft) {
        this.backgroundPatchSizeLeft = backgroundPatchSizeLeft;
    }

    public int getBackgroundPatchSizeRight() {
        return backgroundPatchSizeRight;
    }

    public void setBackgroundPatchSizeRight(int backgroundPatchSizeRight) {
        this.backgroundPatchSizeRight = backgroundPatchSizeRight;
    }

    public int getBackgroundPatchSizeTop() {
        return backgroundPatchSizeTop;
    }

    public void setBackgroundPatchSizeTop(int backgroundPatchSizeTop) {
        this.backgroundPatchSizeTop = backgroundPatchSizeTop;
    }

	public int getCursorOffset() {
		return cursorOffset;
	}

	public void setCursorOffset(int cursorOffset) {
		this.cursorOffset = cursorOffset;
	}

	public int getSelectionOffset() {
		return selectionOffset;
	}

	public void setSelectionOffset(int selectionOffset) {
		this.selectionOffset = selectionOffset;
	}

	public boolean isPassword() {
		return password;
	}

	public void setPassword(boolean password) {
		this.password = password;
	}

	public String getPasswordChar() {
		return passwordChar;
	}

	public void setPasswordChar(String passwordChar) {
		this.passwordChar = passwordChar;
	}

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

	public String getHintColor() {
		return hintColor;
	}

	public void setHintColor(String hintColor) {
		this.hintColor = hintColor;
	}

	public float getPadding() {
        return padding;
    }

    public void setPadding(float padding) {
        this.padding = padding;
    }

	public float getLeftPadding() {
		return leftPadding;
	}

	public void setLeftPadding(float leftPadding) {
		this.leftPadding = leftPadding;
	}

	public float getRightPadding() {
		return rightPadding;
	}

	public void setRightPadding(float rightPadding) {
		this.rightPadding = rightPadding;
	}

	public float getTopPadding() {
		return topPadding;
	}

	public void setTopPadding(float topPadding) {
		this.topPadding = topPadding;
	}

	public float getBottomPadding() {
		return bottomPadding;
	}

	public void setBottomPadding(float bottomPadding) {
		this.bottomPadding = bottomPadding;
	}

	public String getAlignment() {
		return alignment;
	}

	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}
}
