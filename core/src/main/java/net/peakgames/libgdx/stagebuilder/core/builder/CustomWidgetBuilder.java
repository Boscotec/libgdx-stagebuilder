package net.peakgames.libgdx.stagebuilder.core.builder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import net.peakgames.libgdx.stagebuilder.core.ICustomWidget;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.model.BaseModel;
import net.peakgames.libgdx.stagebuilder.core.model.CustomWidgetModel;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

public class CustomWidgetBuilder extends ActorBuilder {

    public CustomWidgetBuilder(AssetsInterface assets, ResolutionHelper resolutionHelper, 
                               LocalizationService localizationService) {
        super(assets, resolutionHelper, localizationService);
    }

    @Override
    public Actor build(BaseModel model, Group parent) {
        try {
            CustomWidgetModel customWidgetModel = (CustomWidgetModel) model;
            localizeAttributes(customWidgetModel);
            Class<?> klass = Class.forName(customWidgetModel.getKlass());
            ICustomWidget customWidget = (ICustomWidget) klass.newInstance();
            setBasicProperties(model, (Actor) customWidget);
            customWidget.build(
                    customWidgetModel.getAttributeMap(),
                    this.assets,
                    this.resolutionHelper,
                    this.localizationService);

            return (Actor) customWidget;
        } catch (Exception e) {
            Gdx.app.log("GdxWidgets", "Failed to create custom widget.", e);
            return null;
        }

    }
    
    private void localizeAttributes(CustomWidgetModel customWidgetModel) {
    	for (Entry<String,String> mapEntry : customWidgetModel.getAttributeMap().entrySet()) {
    		customWidgetModel.addAttribute(mapEntry.getKey(), getLocalizedString(mapEntry.getValue()));
    	}
    }

}
