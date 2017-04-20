package at.gepa.net;

public interface IBackgroundTask
{

	boolean isCancelled();

	void doPublishProgress(int i);
	
}
