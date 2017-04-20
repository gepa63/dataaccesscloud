package at.gepa.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


//http://developer.android.com/training/basics/data-storage/files.html

//daten in der cloud:
//amazon upload: http://docs.aws.amazon.com/de_de/AmazonS3/latest/dev/llJavaUploadFile.html
//amazon get: http://docs.aws.amazon.com/de_de/AmazonS3/latest/dev/RetrievingObjectUsingJava.html

//onedrive:
//download + upload
//https://msdn.microsoft.com/en-us/library/office/dn659727.aspx

//dropbox:
//http://stackoverflow.com/questions/13557630/downloading-file-from-dropbox-in-java
//https://www.dropbox.com/developers/documentation/java
//https://www.dropbox.com/developers-v1/core/sdks/java
//exmple: http://stackoverflow.com/questions/6388439/using-dropbox-java-api-for-uploading-files-to-dropbox

//REST:
//http://www.mastertheboss.com/jboss-frameworks/resteasy/using-rest-services-to-manage-download-and-upload-of-files


//apache:
//http://stackoverflow.com/questions/6917105/java-http-client-to-upload-file-over-post


//JetSS3t:
//for amazon s3, amazon coudfront and google storage service:
// http://www.jets3t.org/downloads.html

public class DataAccessFTP {
	
	public static String uploadFileFTP( DataAccessFTPController ftpController, IModel list, IBackgroundTask uploadTask, IWriteHeaderListener headerFactory) 
	{
		URLConnection con = null;
		OutputStream os = null;
        try {
        	con = connect( ftpController.getURL() ); //CSV_URL_FTP()));
    		con.setDoOutput(true);
    		con.setIfModifiedSince(at.gepa.lib.tools.time.TimeTool.getInstance().getTimeInMillis());
    		
    		long lm = getLastModifiedDate(con, 0);
        	if( !list.checkLastModified(lm) )
        	{
        		return "Die Daten wurden seit ihrem letzten Download geändert! Sie müssen vor dem Speichern neu Laden!\nAchtung, Änderungen gehen verloren!";
        	}
        	os = con.getOutputStream();
			FileStreamAccess.writeToOutputStream( os , list, uploadTask, headerFactory );
        	
			lm = saveLastModifiedDate(con);
			list.setLastModified(lm);
			
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        } 
        finally 
        {
    		try{ if( os != null ) os.close(); } catch(Throwable ex){}
        }		
		return null;
	}
	
	
	private static long saveLastModifiedDate(URLConnection con) {
		Calendar today = at.gepa.lib.tools.time.TimeTool.getInstance();
		long lm = today.getTimeInMillis();
		
		URL url = con.getURL();
		String ef = url.toExternalForm() + ".time";
		try {
			URL timeUrl = new URL( ef );
			URLConnection tcon = DataAccessFTP.connect(timeUrl);
			tcon.setDoInput(true);
			ArrayList<String> allLines = FileStreamAccess.readAllLines( tcon.getInputStream() );
			
			tcon = DataAccessFTP.connect(timeUrl);
			tcon.setDoOutput(true);
			
			String strToday = at.gepa.lib.tools.time.TimeUtil.convertToSimpleDateTime(today.getTime());
			String line = String.format("%d-%s", lm, strToday );
			allLines.add(0, line);
			FileStreamAccess.saveAllLines( tcon.getOutputStream(), allLines );
			
		} catch (Exception e) {
			e.printStackTrace();
			lm = -1;
		}
		return lm;
	}


	private static void dumpHeader(URLConnection con) {
		Map<String, List<String>> hfm = con.getHeaderFields();
		for( String key : hfm.keySet() )
		{
			List<String> values = hfm.get(key);
			if( values == null ) continue;
			String x;
			for( int i=0; i < values.size(); i++ )
			{
				x = values.get(i);
			}
		}
	}


	private static long getLastModifiedDate(URLConnection con, long defValue) 
	{
		long lm = 0;
//		long lm = con.getLastModified();
//		if( lm == 0 )
//			lm = con.getIfModifiedSince();
//		if( lm == 0 )
		{
			URL url = con.getURL();
			String ef = url.toExternalForm() + ".time";
			try {
				URL timeUrl = new URL( ef );
				URLConnection tcon = DataAccessFTP.connect(timeUrl);
				tcon.setDoInput(true);
				
				String line = FileStreamAccess.getFirstLine( tcon.getInputStream() );
				if( line == null )
					lm = defValue;
				else
				{
					if( line.contains("-") )
					{
						String sa[] = line.split("-");
						line = sa[0];
					}
					
					try { lm = Long.parseLong(line); } catch(Exception ex){}
				}
			} catch (Exception e) {
				e.printStackTrace();
				lm = 0;
			}
		}
		return lm;
	}


	public static URLConnection connect(URL data) throws IOException
	{
		URLConnection con = null;
		con = (URLConnection)data.openConnection();
		con.setUseCaches(false); // Don't use a Cached Copy
		
		return con;
	}
	public static String downloadFile(DataAccessFTPController ftpController, IBackgroundTask downloadTask, IModel list, IReadHeaderListener readHeaderListener) 
	{
		String ret = null;
		URLConnection con = null;
		InputStream[] input = null;
        try {
        	con = (URLConnection)connect(ftpController.getURL());
        	con.setDoInput(true);
        	
        	input = new InputStream[] {con.getInputStream()};

            int fileLength = DataAccess.calcFileLength(con, input );
            
        	if( ftpController.getContentType() == ContentType.Text )
        	{
        		if( ftpController.hasFieldDelimiter() )
        			ret = FileStreamAccess.readFileFromStream( input[0], fileLength, downloadTask, list, readHeaderListener, ftpController.getFieldDelimiter() );
        		else
        			ret = FileStreamAccess.readFileFromStream( input[0], fileLength, downloadTask, list, readHeaderListener );
        	}
        	else
        		ret = list.setStream(input[0]);
        	if( ret == null )
        		list.setLastModified(getLastModifiedDate(con, at.gepa.lib.tools.time.TimeTool.getInstance().getTimeInMillis()));
        } catch (Exception e) {
            return e.toString();
        } 
        finally 
        {
            try {
                if (input != null && input[0] != null)
                    input[0].close();
            }
            catch(Exception ex)
            {
            	ex.printStackTrace();
            }
            con = null;
        }
		return ret;
	}
	public static String downloadHttpFile(DataAccessFTPController ftpController, IBackgroundTask downloadTask, IModel list, IReadHeaderListener readHeaderListener) 
	{
		HttpURLConnection con = null;
        InputStream input = null;
        try {
        	con = (HttpURLConnection)connect(ftpController.getHttpURL());
        	con.setDoInput(true);
            // download the file
            input = con.getInputStream();

            int fileLength = DataAccess.calcFileLength(con, new InputStream[] {input});
            
            FileStreamAccess.readFileFromStream( input, fileLength, downloadTask, list, readHeaderListener );
            
        } catch (Exception e) {
            return e.toString();
        } 
        finally 
        {
            try {
                if (input != null)
                    input.close();
            }
            catch(Exception ex)
            {
            	ex.printStackTrace();
            }
            if( con != null )
            	con.disconnect();
        }
		return null;
	}
	public static void writeObjectToXML(DataAccessFTPController ftpController, String xmlFile, Object o) throws IOException 
	{
		URL dataPath = ftpController.getURL(xmlFile);
		URLConnection con = (URLConnection)dataPath.openConnection();
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false); // Don't use a Cached Copy

		OutputStream outs = con.getOutputStream();

		FileStreamAccess.writeToXML( outs, o );
		if( outs != null )
		{
			outs.flush();
			outs.close();
		}
	}
	public static Object readObjectFromXML(DataAccessFTPController ftpController, String xmlFile) throws IOException {
		URL dataPath = ftpController.getURL(xmlFile);
		URLConnection con = dataPath.openConnection();
		con.setDoInput(true);
		con.setDoOutput(true);
		InputStream ins = con.getInputStream();
		
		Object o = null;
		try
		{
			o = FileStreamAccess.readFromXML( ins );
		}
		catch(Exception e)
		{
		}
		
		if( ins != null )
			ins.close();
		return o;
	}
	
	public static String downloadFile(DataAccessFTPController ftpController, IBackgroundTask downloadTask, IModel downloadModel) 
	{
		return DataAccessFTP.downloadFile( ftpController, downloadTask, downloadModel, downloadModel.getHeaderListener() );
	}
	public static void uploadFileFTP(DataAccessFTPController ftpController, IModel uploadModel, IBackgroundTask uploadTask) {
		DataAccessFTP.uploadFileFTP(ftpController, uploadModel, uploadTask, uploadModel.getHeaderListener() );
	}


	public static void backupFileIfExists(DataAccessFTPController controller, String ext) {
		
		try {
			
			if( fileExists(controller.getHttpURL()) )
			{
				String sCnt = "";
				int cnt = 0;
				String fName = controller.getFileName()+ext+sCnt;
				while( fileExists(controller.getHttpURL(fName) ) )
				{
					cnt++;
					sCnt = ""+cnt;
					fName = controller.getFileName()+ext+sCnt;
				}
				DataAccessFTP.copyFile(controller.getURL(), controller.getURL(fName) );
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}


	private static void copyFile(URL fromUrl, URL toUrl) {
		
		URLConnection conSource;
		InputStream ins = null;
		OutputStream outs = null;
		try {
			conSource = fromUrl.openConnection();
			
			conSource.setDoInput(true);
			
			URLConnection conDest = toUrl.openConnection();
			conDest.setDoOutput(true);
			
			ins = conSource.getInputStream();
			outs = conDest.getOutputStream();
			
			FileStreamAccess.copy(outs, ins);
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		finally
		{
			try {
				if( ins != null )
					ins.close();
			} catch (IOException e) {}
			try {
				if( outs != null )
					outs.close();
			} catch (IOException e) {}
		}
		
	}


	private static boolean fileExists(URL httpURL) {
		int fileLength = 0;
		HttpURLConnection con = null;
        InputStream input = null;
        try {
        	con = (HttpURLConnection)connect(httpURL);
        	con.setDoInput(true);
            input = con.getInputStream();

            fileLength = DataAccess.calcFileLength(con, new InputStream[] {input});
            
        } catch (Exception e) {
            return false;
        } 
        finally 
        {
            try {
                if (input != null)
                    input.close();
            }
            catch(Exception ex)
            {
            	ex.printStackTrace();
            }
            if( con != null )
            	con.disconnect();
        }
		return fileLength > 0;
	}


	public static int copy(File fromFile, DataAccessFTPController controller) 
	{
		InputStream ins = null;
		OutputStream outs = null;
		int lines = 0;
		try {
			URL toUrl = controller.getURL();
			URLConnection conDest = toUrl.openConnection();
			conDest.setDoOutput(true);
			
			ins = new FileInputStream(fromFile);
			outs = conDest.getOutputStream();
			
			lines = FileStreamAccess.copy(outs, ins);

			long lm = saveLastModifiedDate(conDest);
			controller.setLastModified(lm);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		finally
		{
			try {
				if( ins != null )
					ins.close();
			} catch (IOException e) {}
			try {
				if( outs != null )
					outs.close();
			} catch (IOException e) {}
		}
		return lines;
	}
	
}
