package at.gepa.net;

import java.io.BufferedReader;
import java.io.IOException;

public interface IReadHeaderListener {

	void readHeader(BufferedReader reader) throws IOException;

}
