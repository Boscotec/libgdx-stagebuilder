package net.peakgames.libgdx.stagebuilder.core.demo.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import net.peakgames.libgdx.stagebuilder.core.CustomView;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

import java.util.Map;

public class DemoCustomView extends CustomView {
	
	@Override
	public void build(Map<String, String> attr, AssetsInterface assets, 
	                  ResolutionHelper resHelper, LocalizationService locale) {
		Label label = findActor("label");
		Image image = findActor("thisImage");
		
		label.setText("This is image's width is: " + image.getWidth());
	}
	
}
