package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.assets.StageBuilderListener;
import net.peakgames.libgdx.stagebuilder.core.model.*;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;
import net.peakgames.libgdx.stagebuilder.core.xml.XmlModelBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StageBuilder {
    public static final String ROOT_GROUP_NAME = "AbsoluteLayoutRootGroup";
    public static final String TAG = StageBuilder.class.getSimpleName();
    public static final String LANDSCAPE_LAYOUT_FOLDER = "layout-land";
    public static final String PORTRAIT_LAYOUT_FOLDER = "layout-port";
    public static final String DEFAULT_LAYOUT_FOLDER = "layout";

    private Map<Class<? extends BaseModel>, ActorBuilder> builders = new HashMap<Class<? extends BaseModel>, ActorBuilder>();
    private AssetsInterface assets;
    private ResolutionHelper resolutionHelper;
    private LocalizationService localizationService;
    private ExecutorService groupBuildingPool;
    private StageBuilderListener stageBuilderListener;

    public StageBuilder(AssetsInterface assets, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        this.assets = assets;
        this.resolutionHelper = resolutionHelper;
        this.localizationService = localizationService;

        registerWidgetBuilders(assets);
        groupBuildingPool = Executors.newFixedThreadPool(1);
    }

    public void switchOrientation() {

    }

    /**
     * There must be a widget builder for every type of widget model. Models represents widget data and builders use this data to create scene2d actors.
     *
     * @param assets assets interface.
     */
    private void registerWidgetBuilders(AssetsInterface assets) {
        builders.put(ImageModel.class, new ImageBuilder(this.assets, this.resolutionHelper, this.localizationService));
        builders.put(GroupModel.class, new GroupBuilder(builders, assets, this.resolutionHelper, this.localizationService));
        builders.put(ButtonModel.class, new ButtonBuilder(this.assets, this.resolutionHelper, this.localizationService));
        builders.put(TextButtonModel.class, new TextButtonBuilder(this.assets, this.resolutionHelper, this.localizationService));
        builders.put(LabelModel.class, new LabelBuilder(this.assets, this.resolutionHelper, this.localizationService));
        builders.put(SelectBoxModel.class, new SelectBoxBuilder(this.assets, this.resolutionHelper, this.localizationService));
        builders.put(CustomWidgetModel.class, new CustomWidgetBuilder(this.assets, this.resolutionHelper, this.localizationService));
        builders.put(ExternalGroupModel.class, new ExternalGroupModelBuilder(this.assets, this.resolutionHelper, this.localizationService, this));
        builders.put(SliderModel.class, new SliderBuilder( this.assets, this.resolutionHelper, this.localizationService));
        builders.put(TextFieldModel.class, new TextFieldBuilder(assets, resolutionHelper, localizationService));
        builders.put(TextAreaModel.class, new TextAreaBuilder(assets, resolutionHelper, localizationService));
        builders.put(CheckBoxModel.class, new CheckBoxBuilder( assets, resolutionHelper, localizationService));
        builders.put(ToggleWidgetModel.class, new ToggleWidgetBuilder( assets, resolutionHelper, localizationService));
        builders.put(OneDimensionGroupModel.class, new OneDimensionalGroupBuilder(builders, assets, resolutionHelper, localizationService));
    }

    public Group buildGroup(String fileName) throws Exception {
        Group group = new Group();
        GroupModel groupModel = buildGroupOnly(fileName, group);
        fillGroupActors(group, groupModel);
        return group;
    }

    public Group buildGroup(GroupModel groupModel) {
        return (Group) builders.get(GroupModel.class).build(groupModel);
    }

    public void fillGroupActors(Group group, GroupModel groupModel) {
        for (BaseModel model : groupModel.getChildren()) {
            ActorBuilder builder = builders.get(model.getClass());
            group.addActor(builder.build(model));
        }
    }

    public GroupModel buildGroupOnly(String fileName, Group groupToBeUpdated) throws Exception {
        XmlModelBuilder xmlModelBuilder = new XmlModelBuilder();
        List<BaseModel> modelList = xmlModelBuilder.buildModels(getLayoutFile(fileName));
        GroupModel groupModel = (GroupModel) modelList.get(0);
        GroupBuilder groupBuilder = (GroupBuilder) builders.get(GroupModel.class);
        groupBuilder.setBasicProperties(groupModel, groupToBeUpdated);
        updateGroupSizeAndPosition(groupToBeUpdated, groupModel);
        return groupModel;
    }

    public void buildGroupAsync(String fileName){
        groupBuildingPool.execute(new GroupBuildingTask(fileName));
    }

    public void buildGroupAsync(String fileName, String groupName){
        groupBuildingPool.execute(new GroupBuildingTask(fileName, groupName));
    }

    public StageBuilderListener getStageBuilderListener() {
        return stageBuilderListener;
    }

    public void setStageBuilderListener(StageBuilderListener stageBuilderListener) {
        this.stageBuilderListener = stageBuilderListener;
    }

    private class GroupBuildingTask implements Runnable {
        private String fileName;
        private String groupName;

        private GroupBuildingTask(String fileName) {
            this.fileName = fileName;
        }

        private GroupBuildingTask(String fileName, String groupName) {
            this.fileName = fileName;
            this.groupName = groupName;
        }

        @Override
        public void run() {
            try {
                final Group group = new Group();
                final GroupModel groupModel = buildGroupOnly(fileName, group);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        fillGroupActors(group, groupModel);
                        if(groupName != null) {
                            group.setName(groupName);
                        }
                        fireOnGroupBuilded(fileName, group);
                    }
                });
            } catch (Exception e) {
                fireOnGroupBuildFailed(fileName, e);
            }
        }
    }

    private void fireOnGroupBuildFailed(String fileName, Exception e) {
        if(stageBuilderListener != null){
            stageBuilderListener.onGroupBuildFailed(fileName, e);
        }
    }

    private void fireOnGroupBuilded(String fileName, Group group) {
        if(stageBuilderListener != null){
            stageBuilderListener.onGroupBuilded(fileName, group);
        }
    }

    private void updateGroupSizeAndPosition(Group group, GroupModel referenceModel) {
        float multiplier = resolutionHelper.getPositionMultiplier();
        group.setX(referenceModel.getX() * multiplier);
        group.setY(referenceModel.getY() * multiplier);
        group.setWidth(referenceModel.getWidth() * multiplier);
        group.setHeight(referenceModel.getHeight() * multiplier);
    }

    public Stage build(String fileName, Viewport viewport) {
        try {
            XmlModelBuilder xmlModelBuilder = new XmlModelBuilder();
            List<BaseModel> modelList = xmlModelBuilder.buildModels(getLayoutFile(fileName));
            GroupModel groupModel = (GroupModel) modelList.get(0);
            Stage stage = new Stage(viewport);
            Group rootGroup = createRootGroup();
            addActorsToStage(rootGroup, groupModel.getChildren());
            stage.addActor(rootGroup);
            return stage;
        } catch (Exception e) {
            throw new RuntimeException("Failed to build stage.", e);
        }
    }

    public Group createRootGroup() {
        Group rootGroup = new Group();
        rootGroup.setName(ROOT_GROUP_NAME);
        rootGroup.setX(resolutionHelper.getGameAreaPosition().x);
        rootGroup.setY(resolutionHelper.getGameAreaPosition().y);
        return rootGroup;
    }

    private void addActorsToStage(Group rootGroup, List<BaseModel> models) {
        for (BaseModel model : models) {
            try {
                ActorBuilder builder = builders.get(model.getClass());
                rootGroup.addActor(builder.build(model));
            } catch (Exception e) {
                throw new RuntimeException("Failed to build stage on actor: " + model.getName(), e);
            }
        }
    }

    public FileHandle getLayoutFile(String fileName) {
        boolean isLandscape = resolutionHelper.getScreenWidth() > resolutionHelper.getScreenHeight();
        if (isLandscape) {
            String path = LANDSCAPE_LAYOUT_FOLDER + "/" + fileName;
            FileHandle fileHandle = Gdx.files.internal(path);
            if (fileExists(fileHandle)) {
                return fileHandle;
            }
        } else {
            String path = PORTRAIT_LAYOUT_FOLDER + "/" + fileName;
            FileHandle fileHandle = Gdx.files.internal(path);
            if (fileExists(fileHandle)) {
                return fileHandle;
            }
        }

        String path = DEFAULT_LAYOUT_FOLDER + "/" + fileName;
        return Gdx.files.internal(path);
    }

    /**
     * File.exists is too slow on Android.
     * @param file
     * @return true if file exists.
     */
    private boolean fileExists(FileHandle file) {
        boolean exists = false;
        try {
            file.read().close();
            exists = true;
        } catch (Exception e) {
            //ignore
        }
        return exists;
    }

    public AssetsInterface getAssets() {
        return assets;
    }

    public ResolutionHelper getResolutionHelper() {
        return resolutionHelper;
    }

    public LocalizationService getLocalizationService() {
        return localizationService;
    }

    public static void disableMultiTouch(Stage stage) {
        InputProcessor cancelMultiTouchInputProcessor = new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (pointer > 0) {
                    return true;
                }
                return super.touchDown(screenX, screenY, pointer, button);
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (pointer > 0) {
                    return true;
                }
                return super.touchDragged(screenX, screenY, pointer);
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (pointer > 0) {
                    return true;
                }
                return super.touchUp(screenX, screenY, pointer, button);
            }
        };
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(cancelMultiTouchInputProcessor);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }
}
