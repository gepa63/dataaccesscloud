package at.gepa.ws.publicholiday;

import at.gepa.net.AsyncNetworkTask;
import at.gepa.net.DataAccess;
import at.gepa.net.DataAccess.eAccessType;
import at.gepa.net.DataAccessHTTPController;
import at.gepa.net.IBackgroundTask;

/*
 * 
 * http://www.dagmar-mueller.de/wdz/html/feiertagsberechnung.html
 * 
 * 
 * */


public class WSPublicHoliday
extends AsyncNetworkTask
implements IBackgroundTask
{
	private int yearFrom;
	private int yearTo;
	private PublicHolidays holidays;
	
	public static interface IPublicHolidaysListener
	{
		public void setHolidays( PublicHolidays ph, boolean error );
	}
	private IPublicHolidaysListener listener;
	
	public IPublicHolidaysListener getListener() {
		return listener;
	}

	public void setListener(IPublicHolidaysListener listener) {
		this.listener = listener;
	}

	public WSPublicHoliday(int yearfrom, int yearto, PublicHolidays ph)
	{
		if( ph == null )
			holidays = new PublicHolidays();
		else
			holidays = ph;
		yearFrom = yearfrom;
		yearTo = yearto;
		setListener(null);
	}

	@Override
	protected Integer doInBackground() throws Exception {
		
		super.doInBackground();
		try {
			
			for( int y = yearFrom; y <= yearTo; y++ )
			{
				addYear(y);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 1;
	}
	@Override
	protected void done()
	{
		if( !super.hasError() )
		{
			holidays.done();
		}
		if( listener != null )
			listener.setHolidays(holidays, hasError() );
	}
	
	private void addYear(int y) {
		DataAccessHTTPController controller = new DataAccessHTTPController("http://fsmat.at/~mwetzer/tele/feiertage.php?J="+y+"&submit=Berechne"); 
		controller.setFieldDelimiter("(");
		DataAccess dataAccess = new DataAccess(controller, eAccessType.HttpAccess);
		dataAccess.loadFile(this, holidays, holidays.getHeaderListener() );
	}

	@Override
	public void doPublishProgress(int i) {
	}
	
}
