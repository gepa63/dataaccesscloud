package at.gepa.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class DataAccessLocal {
	
	public static String loadFromLocal(DataAccessController controller, IBackgroundTask downloadTask, IModel downloadModel) {
		return DataAccessLocal.loadFromLocal(controller, downloadTask, downloadModel, downloadModel.getHeaderListener() );
	}
	public static void saveLocalFile(DataAccessController controller, IModel uploadModel, IBackgroundTask uploadTask) {
		DataAccessLocal.saveLocalFile(controller, uploadModel, uploadTask, uploadModel.getHeaderListener());
	}
	public static String saveLocalFile(DataAccessController controller, IModel uploadModel, IBackgroundTask uploadTask, IWriteHeaderListener headerFactory) {
		FileOutputStream writer = null;
        try {
        	File f = new File( controller.getFileName());// GET_FTP_FILENAME() );
        	if( f.exists() )
        	{
            	if( !uploadModel.checkLastModified( f.lastModified() ) )
            		return "Datei wurde geändert! Sie müssen neu Laden!";
        		
        		if( uploadModel.size() > 0 || f.length() == 0)
        			f.delete();
        	}
        	writer = new FileOutputStream(f);
        	FileStreamAccess.writeToOutputStream( writer, uploadModel, uploadTask, headerFactory );
        	
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
        }		
		return null;
	}
	public static String loadFromLocal(DataAccessController controller, IBackgroundTask downloadTask, IModel list, IReadHeaderListener readHeaderListener) 
	{
		String ret = null;
		FileInputStream fis = null;
        try {
        	File f = new File(controller.getFileName());//FTP_FILENAME);
        	try {
        	if( !f.exists() ) f.createNewFile();
        	} catch(Exception ex){}
        	if( !f.exists() ) return null; 
        	
    		fis = new FileInputStream(f);
        	if( controller.getContentType() == ContentType.Text )
        	{
        		if( controller.hasFieldDelimiter() )
        			ret = FileStreamAccess.readFileFromStream( fis, f.length(), downloadTask, list, readHeaderListener, controller.getFieldDelimiter() );
        		else
        			ret = FileStreamAccess.readFileFromStream( fis, f.length(), downloadTask, list, readHeaderListener );
        	}
        	else
        		ret = list.setStream(fis);
        	
            list.setLastModified(f.lastModified());
        } catch (Exception e) {
        	if( e == null || e instanceof NullPointerException )
        	{
        		System.out.println("null");
        	}
            return e.toString();
        } 
        finally 
        {
            try {
                if (fis != null)
                	fis.close();
            }
            catch(Exception ex)
            {
            	ex.printStackTrace();
            }
        }
		return ret;
	}
	public static boolean localFileExists(DataAccessController controller) {
		File f = new File( controller.getFileName() );
		if( f.exists() )
		{
			if( f.length() > 0 )
				return true;
		}
		return false;
	}
	public static boolean copy(File source, File dest) 
	{
		boolean ret = false;
		try {
			FileInputStream fis = new FileInputStream(source);
			FileOutputStream fos = new FileOutputStream(dest);
			int buffSize = 512;
			byte buffer[] = new byte[buffSize];
			int bytesRead = 0;
			while( (bytesRead = fis.read(buffer)) > 0 )
			{
				fos.write(buffer, 0, bytesRead);
			}
			ret = true;
			fis.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	public static String buildUniqueFilename(String fileName, String ext) {
		File f = null;
		String sCnt = "";
		int cnt = 0;
		do
		{
			f = new File( fileName + ext + sCnt);
			if( !f.exists() )
				break;
			cnt++;
			sCnt = "" + cnt;
		}while( true );
		return f.getAbsolutePath();
	}

}
