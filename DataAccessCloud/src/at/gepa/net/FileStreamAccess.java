package at.gepa.net;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.itextpdf.text.pdf.PdfReader;

public class FileStreamAccess {
	public static final String crlf = "\n";
	public static String DELIM_FIELD = ",";

	public static String writeToOutputStream(OutputStream outputStream, IModel list, IBackgroundTask uploadTask, IWriteHeaderListener headerFactory ) throws Exception {
		OutputStreamWriter writer = new OutputStreamWriter( outputStream, "UTF-8" );

		headerFactory.writeHeader(writer, list);
        
        if( list != null )
        {
        	int total = 0;
        	int fileLength = list.size();
        	for( int i=0; i < fileLength; i++ )
        	{
        		IWriteable bv = list.get(i);
				if (uploadTask != null && uploadTask.isCancelled()) 
                {
					writer.close();
                    return null;
                }				
        		
        		bv.write(writer, DELIM_FIELD);
        		writer.write(crlf);
        		
				total++;
				if( uploadTask != null )
					uploadTask.doPublishProgress((int) (total * 100 / fileLength));
        	}
        }
        writer.flush();
        return null;
	}
	public static String readFileFromStream(InputStream input, long fileLength, IBackgroundTask downloadTask, IModel list, IReadHeaderListener readHeaderListener) throws IOException 
	{
		return FileStreamAccess.readFileFromStream(input, fileLength, downloadTask, list, readHeaderListener, DELIM_FIELD);
	}	
	public static String readFileFromStream(InputStream input, long fileLength, IBackgroundTask downloadTask, IModel list, IReadHeaderListener readHeaderListener, String fieldDelim) throws IOException 
	{
        long total = 0;
		BufferedReader reader = new BufferedReader( new InputStreamReader(input, "UTF-8") );

		try { waitTillReady( reader, 50 ); }catch(Exception ex){ reader.close(); return ex.getMessage(); };
		
		list.clearModel();
		String line = null;
		
		readHeaderListener.readHeader(reader);
		
		IElement prev = null;
		
		while( (line = reader.readLine()) != null )
		{
			if (downloadTask != null && downloadTask.isCancelled()) 
            {
				reader.close();
				list.done();
                return null;
            }
			if( !list.isLineToProceed( line, fieldDelim ) ) continue;
			
			IElement bp = list.createInstance( line.split(mapDelim(fieldDelim)) );
			if( bp == null ) continue;
			
			if( prev != null )
			{
				list.checkPrevious( prev, bp );
			}
			prev = bp;
			list.add(bp);
			
			total += line.length()+crlf.length();
			if (fileLength > 0 && downloadTask != null ) // only if total length is known
				downloadTask.doPublishProgress((int) (total * 100 / fileLength));
		}
		list.done();
		return null;
	}

	private static String mapDelim(String fieldDelim) {
		String sa [] = new String[]{"(", "[", "]", ")", "{", "}"};
		for( String s : sa )
		{
			if( s.equals(fieldDelim) )
				return "\\" + fieldDelim;
		}
		return fieldDelim;
	}
	private static void waitTillReady(BufferedReader reader, int waitCounter) throws Exception
	{
		do
		{
			if( reader.ready() ) return;
			Thread.sleep(10);
			waitCounter--;
		}while( waitCounter > 0 );
		throw new Exception("File/Device not ready!");
	}

	public static void writeToXML(OutputStream outs, Object o) throws IOException  {
		XMLEncoder encoder = new XMLEncoder( outs );
		
		encoder.writeObject(o);
		encoder.flush();
		
		encoder.close();
	}

	public static Object readFromXML(InputStream ins) throws Exception {
		XMLDecoder decoder = null;

		decoder = new XMLDecoder( ins );
		
		Object o = decoder.readObject();
		decoder.close();
		return o;
	}

	public static PdfReader openPDF(File lastFile) throws Throwable {
		FileInputStream input = new FileInputStream(lastFile);
		PdfReader reader = new PdfReader( input );
		return reader;
	}

	public static String getLastLine(InputStream inputStream) throws Exception {
		BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream, "UTF-8") );

		try { waitTillReady( reader, 50 ); }catch(Exception ex){ reader.close(); return ex.getMessage(); };
		
		String line = null;
		String lastLine = null;
		while( (line = reader.readLine()) != null )
		{
			lastLine = line;
		}
		reader.close();
		return lastLine;
	}
	public static String getFirstLine(InputStream inputStream) throws Exception {
		BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream, "UTF-8") );

		try { waitTillReady( reader, 50 ); }catch(Exception ex){ reader.close(); return ex.getMessage(); };
		
		String line = reader.readLine();
		reader.close();
		return line;
	}

	public static void addLine(OutputStream outputStream, String line ) throws Exception {
		OutputStreamWriter writer = new OutputStreamWriter( outputStream, "UTF-8" );

        if( line != null )
        {
        	writer.append(line+crlf);
            writer.flush();
            writer.close();
            writer = null;
        }
	}

	public static ArrayList<String> readAllLines(InputStream inputStream) throws Exception 
	{
		ArrayList<String> ret = new ArrayList<String> ();
		BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream, "UTF-8") );
		String line = null;
		while( (line = reader.readLine()) != null )
		{
			ret.add(line);
		}
		reader.close();
		return ret;
	}

	public static void saveAllLines(OutputStream outputStream, ArrayList<String> allLines) throws Exception  {
		BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(outputStream) );
		for( String line : allLines)
		{
			writer.write(line+crlf);
		}
        writer.flush();
        writer.close();
        writer = null;
	}
	public static int copy(File fromFile, File toFile, boolean overwriteIfExists) throws IOException {
		
		if( toFile.exists() && overwriteIfExists )
			toFile.delete();
		BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(toFile), "UTF-8") );
		BufferedReader reader = new BufferedReader( new InputStreamReader(  new FileInputStream(fromFile), "UTF-8") );
		return copy( writer, reader);
	}
	public static int copy( File fromFile, File toFile ) throws IOException 
	{
		FileOutputStream os = new FileOutputStream(toFile);
		FileInputStream is =  new FileInputStream(fromFile);
		return copy( os, is);
	}	
	public static int copy(OutputStream os, InputStream is) throws IOException {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter( new OutputStreamWriter( os, "UTF-8") );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return 0;
		}
		BufferedReader reader;
		try {
			reader = new BufferedReader( new InputStreamReader(  is, "UTF-8") );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return 0;
		}
		return copy( writer, reader);
	}
	public static int copy( BufferedWriter writer, BufferedReader reader ) throws IOException 
	{
		int lines = 0;
		String line = null;
		while( (line = reader.readLine()) != null )
		{
			writer.append(line+crlf);
			lines++;
		}
		reader.close();
		writer.close();
		return lines;
	}
	public static boolean containsData(File f) {
		BufferedReader reader = null;
		String line = null;
		boolean ret = false;
		try {
			reader = new BufferedReader( new InputStreamReader(new FileInputStream(f), "UTF-8") );
			while( (line = reader.readLine()) != null )
			{
				if( !line.isEmpty() )
				{
					ret = true;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			if( reader != null )
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return ret;
	}
	
}
