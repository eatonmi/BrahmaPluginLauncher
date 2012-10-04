package foundation;

public class DependencyRetreiver {
	private static DependencyManager DependencyInstance = null;
	
	public DependencyRetreiver()
	{
		if(DependencyRetreiver.DependencyInstance == null)
		{
			DependencyRetreiver.DependencyInstance = new DependencyManager();
		}
	}
	
	public static DependencyManager getManager()
	{
		return DependencyRetreiver.DependencyInstance;
	}
}
