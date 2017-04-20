package at.gepa.net;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;

public interface IWriteable
extends Serializable
{

	void write(OutputStreamWriter writer, String delimField) throws IOException ;

}
