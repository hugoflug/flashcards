package se.hugo.flashcards;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface InputStreamProvider {
	public InputStream getInputStream() throws FileNotFoundException;
}
