package at.gepa.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


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

public class DataAccessHTTP {
	
	public static String uploadHttpFile( DataAccessHTTPController ftpController, IModel list, IBackgroundTask uploadTask, IWriteHeaderListener headerFactory) 
	{
		HttpURLConnection con = null;
		OutputStreamWriter writer = null;
        try {
        	con = connect( ftpController.getHttpURL() ); //CSV_URL_FTP()));
        	if( !list.checkLastModified(con.getLastModified()) )
        		return "Datei wurde geändert! Sie müssen neu Laden!";

        	FileStreamAccess.writeToOutputStream( con.getOutputStream(), list, uploadTask, headerFactory );

        	list.setLastModified(con.getLastModified());
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        } finally {
            try {
                // Close the writer regardless of what happens...
            	if( writer != null)
            		writer.close();
            } catch (Exception e) {
            }
            if( con != null )
            	con.disconnect();
        }		
		return null;
	}
	
	
	public static HttpURLConnection connect(URL data) throws IOException
	{
		HttpURLConnection con = null;
		con = (HttpURLConnection)data.openConnection();
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false); // Don't use a Cached Copy
		
		return con;
	}
	public static String downloadHttpFile(DataAccessHTTPController httpController, IBackgroundTask downloadTask, IModel list, IReadHeaderListener readHeaderListener) 
	{
		String ret = null;
		HttpURLConnection con = null;
		InputStream[] input = null;
        try {
        	URL _url = httpController.getHttpURL();
        	con = (HttpURLConnection)connect(_url);
        	con.setDoInput(true);
        	con.setDoOutput(true);
            // download the file
        	input = new InputStream[] {con.getInputStream()};

            int fileLength = DataAccess.calcFileLength(con, input );
            
        	if( httpController.getContentType() == ContentType.Text )
        	{
        		if( httpController.hasFieldDelimiter() )
        			ret = FileStreamAccess.readFileFromStream( input[0], fileLength, downloadTask, list, readHeaderListener, httpController.getFieldDelimiter() );
        		else
        			ret = FileStreamAccess.readFileFromStream( input[0], fileLength, downloadTask, list, readHeaderListener );
        	}
        	else
        		ret = list.setStream(input[0]);
        	
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
            if( con != null )
            	con.disconnect();
        }
		return ret;
	}
	
	public static String downloadFile(DataAccessHTTPController controller, IBackgroundTask downloadTask, IModel downloadModel) 
	{
		return DataAccessHTTP.downloadHttpFile( controller, downloadTask, downloadModel, downloadModel.getHeaderListener() );
	}
	public static void uploadFile(DataAccessHTTPController controller, IModel uploadModel, IBackgroundTask uploadTask) {
		DataAccessHTTP.uploadHttpFile(controller, uploadModel, uploadTask, uploadModel.getHeaderListener() );
	}
}
