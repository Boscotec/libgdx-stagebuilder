package net.peakgames.libgdx.stagebuilder.core;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

import java.util.Map;

//This class will deprecate ICustomWidget implementation
public abstract class CustomView extends WidgetGroup {
	public abstract void build(Map<String, String> attrs, AssetsInterface assets, 
	                           ResolutionHelper resHelper, LocalizationService locale);
}
