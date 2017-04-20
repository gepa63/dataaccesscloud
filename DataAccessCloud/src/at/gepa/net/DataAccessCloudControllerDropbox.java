package at.gepa.net;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

public class DataAccessCloudControllerDropbox extends DataAccessCloudController {
	
	public static interface IDropboxSessionHandler
	{
		public void handle( DropboxAPI<AndroidAuthSession> sourceClient );
	}
	
	private IDropboxSessionHandler handler;
	
	public void setSessionHandler( IDropboxSessionHandler h )
	{
		handler = h;
	}
	
	public IDropboxSessionHandler getHandler()
	{
		return handler;
	}
	
	public DataAccessCloudControllerDropbox(String fname, String accessKey, String secretKey, String cloudBucket ) 
	{
		this(fname, accessKey, secretKey);
		
	}
	public DataAccessCloudControllerDropbox(String fname, String accessKey, String secretKey ) 
	{
		super(fname, accessKey, secretKey, "");
		
		//accessKey = 6r1wiyd6b12joqe
		//secretkey = kelhrhbxs88xt56
		//user access token: 91-CXAPzdXYAAAAAAAARMg3si3PHVbnzc_HRn0PAEZ_8upmzCtHuK2XQJwncpsTf
	}

}
