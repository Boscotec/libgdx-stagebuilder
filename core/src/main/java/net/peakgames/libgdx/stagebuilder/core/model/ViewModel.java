package net.peakgames.libgdx.stagebuilder.core.model;

import java.util.HashMap;
import java.util.Map;

public class ViewModel extends BaseModel {
	
	private Map<String, String> attrs = new HashMap<String, String>();
	private String klass;
	private String layout;

	public String getKlass() {
		return klass;
	}

	public void setKlass(String klass) {
		this.klass = klass;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public Map<String, String> getAttrs() {
		return attrs;
	}

	public void addAttribute(String key, String value) {
		attrs.put(key, value);
	}
}
