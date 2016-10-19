package net.peakgames.libgdx.stagebuilder.core.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import net.peakgames.libgdx.stagebuilder.core.ICustomWidget;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;
import net.peakgames.libgdx.stagebuilder.core.util.GdxUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Text and graphic based resource monitor widget.
 * Keeps track of frames per second, java heap size and native heap size.
 *
 * Updates the information every second by default.
 * {@code ResourceMonitorWidget} can be constructed with {@code updateIntervalInSecs} parameter to customize the update interval.
 * Update method can be called to update the information when desired.
 * Graphic can be changed by clicking on labels.
 * Time interval of graphic is changed between 1-16 minutes if you keep clicking labels.
 */
public class ResourceMonitorWidget extends WidgetGroup implements ICustomWidget {

	public enum GraphicType {
		JAVA_HEAP, NATIVE_HEAP, FPS, BATCH_STATS, OPENGL_PROFILER
	}

    private static final int ONE_KILOBYTE = 1024;
    private static final String DRAW_CALLS_LABEL = "Batch: ";
    private static final String OPEN_GL_LABEL = "GL: ";
    private static final String FPS_LABEL = "FramePerSecond: ";
    private static final String JAVA_HEAP_LABEL = "JavaHeap: ";
    private static final String NATIVE_HEAP_LABEL = "NativeHeap: ";
    private static final String KB = " KB";
    private static final float UPDATE_INTERVAL_IN_SECS = 1f;
    private static final int DEFAULT_EMPTY_SPACE_HEIGHT = 10;
    private final String fontName;
    private AssetsInterface assets;
    private ResolutionHelper resolutionHelper;
    private String atlasName;
    private SpriteBatch spriteBatch;
	private boolean openGlProfiler;

    private String backgroundFrame;
    private Label fpsLabel;
    private Label nativeHeapLabel;
    private Label javaHeapLabel;
    private Label intervalLabel;
    private Label batchLabel;
    private Label openGlLabel;
    private Image backgroundImage;
    private ShapeRenderer shapeRenderer;
    private GraphicType graphicType = GraphicType.JAVA_HEAP;
    private Color graphicColor = Color.GREEN;

    private float timeSinceLastUpdate;
    private float updateIntervalInSecs;

    private float touchedX;
    private float touchedY;
    private float maxWidth;

    private List<Long> javaHeapSizesPerSecondList;
    private List<Long> nativeHeapSizesPerSecondList;
    private List<Long> fpsList;
    private final int MAX_VALUES_STORED_IN_LISTS = 960; // 16 minutes
    private final int DEFAULT_VISIBLE_VALUE_COUNT = 60;
    private int visibleValueCount = 60;
    private final int INCREASE_COEFFICIENT = 2;
    private final float NORMALIZATION_COEFFICIENT = 1.1f;
    private float graphicHeight;

    private long maxJavaHeap = 0;
    private long maxNativeHeap = 0;
    private long maxFps = 0;

    private Vector2 leftTopGraphicBound = new Vector2();
    private Vector2 rightTopGraphicBound = new Vector2();
    private Vector2 leftBottomGraphicBound = new Vector2();
    private Vector2 rightBottomGraphicBound = new Vector2();
    private Vector2 graphicPointBeginning = new Vector2();
    private Vector2 graphicPointEnding = new Vector2();

    public static class Builder{

    	private String atlasName = null;
    	private AssetsInterface assets;
    	private String backGroundFrame;
    	private String fontName;
    	private ResolutionHelper resolutionHelper;
		private SpriteBatch spriteBatch;
		private boolean openGlProfiler = false;

		public ResourceMonitorWidget build() {
			ResourceMonitorWidget popupWidget = new ResourceMonitorWidget(this);
			return popupWidget;
		}

		public Builder atlasName(String atlasName) {
			this.atlasName = atlasName;
			return this;
		}

		public Builder assets(AssetsInterface assets) {
			this.assets = assets;
			return this;
		}

		public Builder backGroundFrame(String backGroundFrame) {
			this.backGroundFrame = backGroundFrame;
			return this;
		}

		public Builder resolutionHelper(ResolutionHelper resolutionHelper) {
			this.resolutionHelper = resolutionHelper;
			return this;
		}

		public Builder fontName(String fontName) {
			this.fontName = fontName;
			return this;
		}

		public Builder spriteBatch(SpriteBatch spriteBatch) {
			this.spriteBatch = spriteBatch;
			return this;
		}

		public Builder withGlProfiler() {
			this.openGlProfiler = true;
			return this;
		}
	}

    private ResourceMonitorWidget(Builder builder) {
		this.fontName = builder.fontName;
		this.assets = builder.assets;
		this.backgroundFrame = builder.backGroundFrame;
		this.atlasName = builder.atlasName;
		this.resolutionHelper = builder.resolutionHelper;
		this.updateIntervalInSecs = UPDATE_INTERVAL_IN_SECS;
		this.spriteBatch = builder.spriteBatch;
		this.openGlProfiler = builder.openGlProfiler;
		initialize();
	}

    public ResourceMonitorWidget( AssetsInterface assets, String atlasName, String backgroundFrame, String fontName){
        this.assets = assets;
        this.atlasName = atlasName;
        this.backgroundFrame = backgroundFrame;
        this.fontName = fontName;
        this.updateIntervalInSecs = UPDATE_INTERVAL_IN_SECS;
        initialize();
    }

    public ResourceMonitorWidget( AssetsInterface assets, String atlasName, String backgroundFrame, String fontName, float updateIntervalInSecs){
        this.assets = assets;
        this.atlasName = atlasName;
        this.backgroundFrame = backgroundFrame;
        this.fontName = fontName;
        this.updateIntervalInSecs = updateIntervalInSecs;
        initialize();
    }

    private void initialize(){
        Label.LabelStyle fpsLabelStyle = new Label.LabelStyle( assets.getFont(fontName), Color.RED);
        Label.LabelStyle javaHeapLabelStyle = new Label.LabelStyle( assets.getFont(fontName), Color.GREEN);
        Label.LabelStyle nativeHeapLabelStyle = new Label.LabelStyle( assets.getFont(fontName), Color.BLUE);
        Label.LabelStyle intervalLabelStyle = new Label.LabelStyle(assets.getFont(fontName), Color.WHITE);
        Label.LabelStyle renderLabelStyle = new Label.LabelStyle(assets.getFont(fontName), Color.PINK);
        Label.LabelStyle openGlLabelStyle = new Label.LabelStyle(assets.getFont(fontName), Color.BROWN);
        fpsLabel = new Label( FPS_LABEL, fpsLabelStyle);
        nativeHeapLabel = new Label( NATIVE_HEAP_LABEL, nativeHeapLabelStyle);
        javaHeapLabel = new Label( JAVA_HEAP_LABEL, javaHeapLabelStyle);
        intervalLabel = new Label("1m", intervalLabelStyle);
        batchLabel = new Label(DRAW_CALLS_LABEL, renderLabelStyle);
        openGlLabel = new Label(OPEN_GL_LABEL, openGlLabelStyle);

        addLabelListeners();

        float sizeMultiplier = resolutionHelper.getSizeMultiplier();
		float emptySpaceHeight = DEFAULT_EMPTY_SPACE_HEIGHT * sizeMultiplier;

        nativeHeapLabel.setPosition(0, 15 * sizeMultiplier);
		float nativeHeapHeight = GdxUtils.getTextHeight(nativeHeapLabel);
		float javaHeapHeight = GdxUtils.getTextHeight(javaHeapLabel);
		float fpsHeight = GdxUtils.getTextHeight(fpsLabel);
		javaHeapLabel.setPosition(0, nativeHeapLabel.getY() + nativeHeapHeight + emptySpaceHeight);
		fpsLabel.setPosition(0, javaHeapLabel.getY() + javaHeapHeight + emptySpaceHeight);

        maxWidth = findMaxWidth();
		float height = fpsLabel.getY();
		if (spriteBatch != null) {
			batchLabel.setPosition(0, height + fpsHeight + emptySpaceHeight);
			height = batchLabel.getY();
		}

		if (openGlProfiler) {
			openGlLabel.setPosition(0, height + fpsHeight + emptySpaceHeight);
			height = openGlLabel.getY();
		}

		if (atlasName != null) {
            backgroundImage = new Image(assets.getTextureAtlas(atlasName).findRegion(backgroundFrame));
            backgroundImage.setSize(maxWidth, fpsHeight * NORMALIZATION_COEFFICIENT + height);
			this.setSize(backgroundImage.getWidth(), backgroundImage.getHeight());
            this.addActor(backgroundImage);
        }  else {
        	setSize(maxWidth, fpsHeight * NORMALIZATION_COEFFICIENT + height);
        }

        if (openGlProfiler) {
            GLProfiler.enable();
            this.addActor(openGlLabel);
        }

		if (spriteBatch != null) {
			this.addActor(batchLabel);
		}

        this.addActor(fpsLabel);
        this.addActor(nativeHeapLabel);
        this.addActor(javaHeapLabel);
        this.addActor(intervalLabel);

        this.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				touchedX = x;
				touchedY = y;
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				setPosition(getX() + x - touchedX, getY() + y - touchedY);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

			}
		});

        timeSinceLastUpdate = 0;
        graphicHeight = resolutionHelper.getScreenHeight() / 6;
        shapeRenderer = new ShapeRenderer();
        javaHeapSizesPerSecondList = new ArrayList<Long>(MAX_VALUES_STORED_IN_LISTS);
        nativeHeapSizesPerSecondList = new ArrayList<Long>(MAX_VALUES_STORED_IN_LISTS);
        fpsList = new ArrayList<Long>(MAX_VALUES_STORED_IN_LISTS);
		float intervalLabelHeight = GdxUtils.getTextHeight(intervalLabel.getText().toString(),
				intervalLabel.getStyle().font);
		intervalLabel.setPosition(0, - graphicHeight - intervalLabelHeight * NORMALIZATION_COEFFICIENT);

        this.setPosition(resolutionHelper.getGameAreaPosition().x, intervalLabel.getY() * -1);
    }

    private void addLabelListeners() {
    	fpsLabel.addListener(new ClickListener() {
    		@Override
    		public void clicked(InputEvent event, float x, float y) {
    			super.clicked(event, x, y);
    			setGraphicProperties(GraphicType.FPS, Color.RED);
    		}
    	});

    	javaHeapLabel.addListener(new ClickListener() {
    		@Override
    		public void clicked(InputEvent event, float x, float y) {
    			super.clicked(event, x, y);
    			setGraphicProperties(GraphicType.JAVA_HEAP, Color.GREEN);
    		}
    	});

    	nativeHeapLabel.addListener(new ClickListener() {
    		@Override
    		public void clicked(InputEvent event, float x, float y) {
    			super.clicked(event, x, y);
    			setGraphicProperties(GraphicType.NATIVE_HEAP, Color.BLUE);
    		}
    	});
	}

    private void setGraphicProperties(GraphicType graphicTypeParam, Color graphicColorParam) {
    	if(graphicType == graphicTypeParam) {
			visibleValueCount *= INCREASE_COEFFICIENT;
			if(visibleValueCount > MAX_VALUES_STORED_IN_LISTS) {
				visibleValueCount = DEFAULT_VISIBLE_VALUE_COUNT;
			}
		} else {
			visibleValueCount = DEFAULT_VISIBLE_VALUE_COUNT;
		}
    	intervalLabel.setText((visibleValueCount / DEFAULT_VISIBLE_VALUE_COUNT) + "m");
		graphicType = graphicTypeParam;
		graphicColor = graphicColorParam;
    }

    private float findMaxWidth() {
        float fpsWidth = GdxUtils.getTextWidth(fpsLabel);
        float javaHeapWidth = GdxUtils.getTextWidth(javaHeapLabel);
        float nativeHeapWidth = GdxUtils.getTextWidth(nativeHeapLabel);
        float glWidth = GdxUtils.getTextWidth(openGlLabel);
        float drawWidth = GdxUtils.getTextWidth(batchLabel);
        float maxWidth = fpsWidth;
        if (javaHeapWidth > maxWidth) {
            maxWidth = javaHeapWidth;
        }
        if (nativeHeapWidth > maxWidth) {
            maxWidth = nativeHeapWidth;
        }
        if (drawWidth > maxWidth) {
            maxWidth = GdxUtils.getTextWidth(batchLabel);
        }
        if (glWidth > maxWidth) {
            maxWidth = GdxUtils.getTextWidth(openGlLabel);
        }
        return maxWidth * NORMALIZATION_COEFFICIENT;
    }

    public void update() {
        float widthBefore = findMaxWidth();
	    int fps = Gdx.graphics.getFramesPerSecond();
	    long javaHeapSize = Gdx.app.getJavaHeap() / ONE_KILOBYTE;
	    long nativeHeapSize = Gdx.app.getNativeHeap() / ONE_KILOBYTE;
		String drawCalls = null;
		if (spriteBatch != null) {
			drawCalls = "Draws: " + spriteBatch.renderCalls + " Total: " + spriteBatch.totalRenderCalls
					+ " MaxSprites: " + spriteBatch.maxSpritesInBatch;
			spriteBatch.totalRenderCalls = 0;
		}
        String openGlStats = null;
        if (openGlProfiler) {
        	long switches = GLProfiler.shaderSwitches;
        	long calls = GLProfiler.drawCalls;
        	long txBinds = GLProfiler.textureBindings;
            openGlStats = "Draws: " + calls
                    + " TxBind: " + txBinds + " ShaderSw: " + switches;
			GLProfiler.reset();
		}

	    fpsLabel.setText(FPS_LABEL + fps);
	    javaHeapLabel.setText(JAVA_HEAP_LABEL + javaHeapSize + KB);
	    nativeHeapLabel.setText(NATIVE_HEAP_LABEL + nativeHeapSize + KB);
	    batchLabel.setText(DRAW_CALLS_LABEL + drawCalls);
	    openGlLabel.setText(OPEN_GL_LABEL + openGlStats);
	    maxWidth = findMaxWidth();

	    if (maxWidth != widthBefore) {
	    	setWidth(maxWidth);

			if (backgroundImage != null) {
				float height = backgroundImage.getHeight();
				setHeight(height);
				backgroundImage.setSize(maxWidth, height);
				backgroundImage.invalidate();
			}
	    }

	    updateResourceList(javaHeapSize, nativeHeapSize, fps);
    }

    public void show(){
        this.setVisible(true);
    }

    public void hide(){
        this.setVisible(false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
    	timeSinceLastUpdate += Gdx.graphics.getDeltaTime();
        if ( timeSinceLastUpdate >= updateIntervalInSecs){
            update();
            timeSinceLastUpdate = 0;
        }
        batch.end();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        if (atlasName == null) {
            shapeRenderer.begin(ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.7f);
            shapeRenderer.rect(
                    getX(),
                    nativeHeapLabel.getY() + getY() - 10 * resolutionHelper.getSizeMultiplier(),
                    getWidth(),
                    getHeight());
			shapeRenderer.end();
		}
		shapeRenderer.begin(ShapeType.Line);

		float baseY = getY() - graphicHeight;
		drawGraphicBoundaries(shapeRenderer, baseY);
		shapeRenderer.setColor(graphicColor);

		switch (graphicType) {
			case JAVA_HEAP:
				drawResourceGraphic(baseY, javaHeapSizesPerSecondList, maxJavaHeap);
				break;
			case NATIVE_HEAP:
				drawResourceGraphic(baseY, nativeHeapSizesPerSecondList, maxNativeHeap);
				break;
			case FPS:
				drawResourceGraphic(baseY, fpsList, maxFps);
				break;
			case BATCH_STATS:
				break;
			case OPENGL_PROFILER:
				break;

			default:
			break;
		}
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
        super.draw(batch, parentAlpha );

    }

    private void drawResourceGraphic(float baseY, List<Long> resourcesList, float maxVal) {
    	int startIndex = Math.max(resourcesList.size() - visibleValueCount, 0);
		float xCoord = 0;
		float xInterval = maxWidth / visibleValueCount;
		float yInterval = graphicHeight / maxVal * (0.75f);
		for(int i=startIndex;i<resourcesList.size() - 1;i++) {
			xCoord+=xInterval;
			graphicPointBeginning.set(getX() + xCoord, baseY + resourcesList.get(i) * yInterval + resolutionHelper.getGameAreaPosition().y);
			graphicPointEnding.set(getX() + xCoord + xInterval, baseY + resourcesList.get(i + 1) * yInterval + resolutionHelper.getGameAreaPosition().y);
			shapeRenderer.line(graphicPointBeginning, graphicPointEnding);
		}
	}

	private void drawGraphicBoundaries(ShapeRenderer shapeRenderer, float baseY) {

    	shapeRenderer.setColor(Color.YELLOW);

    	float normalizedY = getY();
    	leftTopGraphicBound.set(getX(), normalizedY + resolutionHelper.getGameAreaPosition().y);
    	leftBottomGraphicBound.set(getX(), baseY + resolutionHelper.getGameAreaPosition().y);
    	rightTopGraphicBound.set(getX() + maxWidth, normalizedY + resolutionHelper.getGameAreaPosition().y);
    	rightBottomGraphicBound.set(getX() + maxWidth, baseY + resolutionHelper.getGameAreaPosition().y);

		shapeRenderer.line(leftTopGraphicBound, leftBottomGraphicBound);
		shapeRenderer.line(leftBottomGraphicBound, rightBottomGraphicBound);
		shapeRenderer.line(rightBottomGraphicBound, rightTopGraphicBound);
		shapeRenderer.line(rightTopGraphicBound, leftTopGraphicBound);

    }

	@Override
	public void build(
			Map<String, String> attributes,
			AssetsInterface assetsInterface,
			net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper resolutionHelper,
			LocalizationService localizationService) {

	}

	private void updateResourceList(long javaHeapSize, long nativeHeapSize, long fps) {
		if(javaHeapSizesPerSecondList.size() == MAX_VALUES_STORED_IN_LISTS) {
			javaHeapSizesPerSecondList.remove(0);
			nativeHeapSizesPerSecondList.remove(0);
			fpsList.remove(0);
		}
		javaHeapSizesPerSecondList.add(javaHeapSize);
		nativeHeapSizesPerSecondList.add(nativeHeapSize);
		fpsList.add(fps);

		if(javaHeapSize > maxJavaHeap) {
			maxJavaHeap = javaHeapSize;
		}
		if(nativeHeapSize > maxNativeHeap) {
			maxNativeHeap = nativeHeapSize;
		}
		if(fps > maxFps) {
			maxFps = fps;
		}
	}

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor hitActor = super.hit(x, y, touchable);
        if (hitActor != null) {
            return hitActor;
        }
        if ( ! this.isTouchable() ) return null;
        return x >= getX() && x < getWidth() && y >= getY() && y < getHeight() ? this : null;
    }
}
