package at.gepa.net;

import java.io.IOException;
import java.io.OutputStreamWriter;

public interface IWriteHeaderListener {

	public void writeHeader(OutputStreamWriter writer, IModel list) throws IOException;

}
