package at.gepa.net;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;

import org.jets3t.service.Constants;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

public class DataAccessCloudAmazonGoogle
{
	public static final String BUCKET_NAME = "bloodpreasure";
	//see http://www.jets3t.org/toolkit/code-samples.html#downloading
	
	public static String uploadFile(DataAccessCloudController controller, IModel list, IBackgroundTask uploadTask, IWriteHeaderListener headerFactory) {
		
		
		AWSCredentials awsCredentials = new AWSCredentials(controller.getAccessKey(), controller.getSecretKey());
		
		S3Service s3Service = new RestS3Service(awsCredentials);
		
		S3Bucket euBucket;
		try {
			String bucketName = controller.getCloudBucket();
			if( bucketName == null || bucketName.isEmpty() )
				bucketName = BUCKET_NAME;
			try {
				euBucket = s3Service.getBucket(bucketName);
			if( euBucket == null )
				throw new Exception("not found");
			}
			catch(Exception ex) 
			{
				euBucket = s3Service.createBucket( bucketName, S3Bucket.LOCATION_EUROPE);
			}
			
			StringWriter buffer = new StringWriter();
			StreamResult sr = new StreamResult(buffer);

			FileStreamAccess.writeToOutputStream(sr.getOutputStream(), list, uploadTask, headerFactory);

			String strBuffer = buffer.getBuffer().toString();
			ByteArrayInputStream objectToUpload = new ByteArrayInputStream( strBuffer.getBytes() );
			
			S3Object fileObject = new S3Object( controller.getFileName() );
			fileObject.setDataInputStream(objectToUpload);
			fileObject.setContentLength( strBuffer.getBytes(Constants.DEFAULT_ENCODING).length );
			fileObject.setContentType("text/plain");

			s3Service.putObject(euBucket, fileObject);			
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		
		/*
		 * String greeting = "Hello World!";
S3Object helloWorldObject = new S3Object("HelloWorld2.txt");
ByteArrayInputStream greetingIS = new ByteArrayInputStream(greeting.getBytes());
helloWorldObject.setDataInputStream(greetingIS);
helloWorldObject.setContentLength(
    greeting.getBytes(Constants.DEFAULT_ENCODING).length);
helloWorldObject.setContentType("text/plain");*/
		

		return null;
	}

	public static String downloadFile(DataAccessCloudController controller, IBackgroundTask downloadTask, IModel list, IReadHeaderListener readHeaderListener) {
		
		String k = controller.getAccessKey();
		String sk = controller.getSecretKey();
		AWSCredentials awsCredentials = new AWSCredentials(k, sk);
		
		S3Service s3Service = new RestS3Service(awsCredentials);
		
		try {
			String bucketName = controller.getCloudBucket();
			if( bucketName == null || bucketName.isEmpty() )
				bucketName = BUCKET_NAME;
			
			S3Object objectComplete = s3Service.getObject(bucketName, controller.getFileName());
			
			int fileLength = 1024;
			FileStreamAccess.readFileFromStream( objectComplete.getDataInputStream(), fileLength, downloadTask, list, readHeaderListener);
			
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return null;
	}
	
	

}
