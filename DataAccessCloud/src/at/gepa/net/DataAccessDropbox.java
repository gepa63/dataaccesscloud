package at.gepa.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

public class DataAccessDropbox {

	public static String uploadFile( DataAccessCloudControllerDropbox dropboxController, IModel list, IBackgroundTask uploadTask, IWriteHeaderListener headerFactory) 
	{
		try {
			DropboxAPI<AndroidAuthSession> sourceClient = createAccess(dropboxController);
			
			DataAccessDropbox.uploadFile( sourceClient, dropboxController, list, uploadTask, headerFactory);
			
			if (sourceClient.getSession().authenticationSuccessful()) {
				sourceClient.getSession().finishAuthentication();
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return null;
	}

	private static void uploadFile(DropboxAPI<AndroidAuthSession> sourceClient,
			DataAccessCloudControllerDropbox dropboxController, IModel list,
			IBackgroundTask uploadTask, IWriteHeaderListener headerFactory) throws Exception 
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		FileStreamAccess.writeToOutputStream(outputStream, list, uploadTask, headerFactory);
		

		String buffer = outputStream.toString();
		ByteArrayInputStream input = new ByteArrayInputStream( buffer.getBytes("UTF-8") );
	    
		sourceClient.putFileOverwrite( dropboxController.getFileName(), (InputStream)input, (long)buffer.length(), null );
	}

	public static String downloadFile( DataAccessCloudControllerDropbox dropboxController, IBackgroundTask downloadTask, IModel list, IReadHeaderListener readHeaderListener) {
		String ret = null;
		try 
		{
			DropboxAPI<AndroidAuthSession> sourceClient = createAccess(dropboxController);
			
			 
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			DropboxFileInfo fi = null;
			try
			{
				String path = "home/Docs";
				//dropboxController.getFileName()
				Entry files = sourceClient.metadata(path, 100, null, true, null);
				if( files != null )
				{
					boolean docreate = true;
					for( Entry entry : files.contents )
					{
						String fn = entry.fileName(); 
						if( fn.equals(dropboxController.getFileName()) )
						{
							path = "/home/Docs/"+fn;
							docreate = false;
						}
					}
					if( docreate )
						uploadFile( sourceClient, dropboxController, list, downloadTask, (IWriteHeaderListener)readHeaderListener);
				}
				fi = sourceClient.getFile( dropboxController.getFileName(), null, outputStream, null);
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
				return ex.getMessage();
			}
//			if( fi != null && fi.getFileSize() == 0 )
//			{
//				uploadFile( sourceClient, dropboxController, list, downloadTask, (IWriteHeaderListener)readHeaderListener);
//			}
				
			String buffer = outputStream.toString();
			ByteArrayInputStream input = new ByteArrayInputStream( buffer.getBytes("UTF-8") );
				
        	if( dropboxController.getContentType() == ContentType.Text )
        	{
        		ret = FileStreamAccess.readFileFromStream(input, buffer.length(), downloadTask, list, readHeaderListener);
        	}
        	else
        		ret = list.setStream(input);
			
			if (sourceClient.getSession().authenticationSuccessful()) {
				sourceClient.getSession().finishAuthentication();

//		            // Store it locally in our app for later use
//		            TokenPair tokens = session.getAccessTokenPair();
//		            storeKeys(tokens.key, tokens.secret);
//		            setLoggedIn(true);
//		        } catch (IllegalStateException e) {
//		//Keep this toast.. It will show you the completed authentication..
//		            Toast.makeText(getBaseContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//		            Log.i("Dropbox", "Error authenticating", e);
		        }
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			ret = e.getMessage();
		}
		
		return ret;
	}

	private static DropboxAPI<AndroidAuthSession> createAccess(DataAccessCloudControllerDropbox dropboxController)  throws Exception
	{
		AndroidAuthSession session =null;
		AppKeyPair pair = new AppKeyPair( dropboxController.getAccessKey(), dropboxController.getSecretKey() );
		
		session = new AndroidAuthSession(pair);
        
    	DropboxAPI<AndroidAuthSession> sourceClient = new DropboxAPI<AndroidAuthSession>(session);
    	dropboxController.getHandler().handle(sourceClient);
    	
    	int cnt = 1000;
    	while (!session.authenticationSuccessful())
    	{
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
    		cnt--;
    		if( cnt < 0 )
    			break;
    	}
		return sourceClient;
	}

}
