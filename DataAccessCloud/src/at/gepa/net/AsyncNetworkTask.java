package at.gepa.net;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;


public class AsyncNetworkTask extends SwingWorker<Integer, Integer>
{
	private Exception lastException; 
	
	public AsyncNetworkTask()
	{
	}
	@Override
	protected Integer doInBackground() throws Exception {
		
		lastException = null; 
		return 1;
	}
	
	
	@Override
	protected void done()
	{
		if( hasError() )
			JOptionPane.showMessageDialog(null, lastException.getMessage());
	}
	public boolean hasError() {
		return lastException != null;
	}
}