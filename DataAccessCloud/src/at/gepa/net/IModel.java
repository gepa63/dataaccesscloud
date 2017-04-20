package at.gepa.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public interface IModel {

	void clearModel();

	void checkPrevious(IElement prev, IElement bp);

	void add(IElement bp);

	IElement createInstance(String[] split);

	int size();

	void writeHeader(OutputStreamWriter writer, String header) throws IOException;

	IWriteable get(int i);

	IReadWriteHeaderListener getHeaderListener();

	void done();

	boolean contains(IElement bp);

	void add(int i, IElement bp);

	boolean checkLastModified(long lastModified);

	void setLastModified(long lastModified);

	String setStream(InputStream input);

	boolean isLineToProceed(String line, String fieldDelim);
}
