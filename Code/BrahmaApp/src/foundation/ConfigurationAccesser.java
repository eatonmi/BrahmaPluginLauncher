package foundation;

import java.awt.Color;

public abstract class ConfigurationAccesser {
	public abstract Color getBackgroundColor();
	public abstract void setBackgroundColor(Color color);
	public abstract String getPluginPath();
	public abstract void setPluginPaht(String path);
}
