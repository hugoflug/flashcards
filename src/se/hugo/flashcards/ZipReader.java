package se.hugo.flashcards;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.graphics.Bitmap;
import au.com.bytecode.opencsv.CSVReader;

public class ZipReader {
	private ZipInputStream zin;
	private List<Bitmap> bmpList;
	private List<String[]> csvLines;
	
	public ZipReader(InputStream in) {
		zin = new ZipInputStream(in);
	}
	
	public void parseZip(BitmapDownsampler sampler) throws IOException {
		ArrayList<Bitmap> bmpList = new ArrayList<Bitmap>();
		ZipEntry ze = null;
        while ((ze = zin.getNextEntry()) != null) {
        	String name = ze.getName();
        	if (Util.isImageFile(name)) {
        		InputStreamProvider provider = new InputStreamProvider() {
					@Override
					public InputStream getInputStream() throws FileNotFoundException {
						return zin;
					}
        		};
        		sampler.decode(provider);
        	} else {
        		CSVReader reader = new CSVReader(new InputStreamReader(zin));
        		csvLines = reader.readAll();
        		reader.close();
        	}
        }
	}
	
	public List<Bitmap> getBitmaps() {
		return bmpList;
	}
	
	public List<String[]> getCsvLines() {
		return csvLines;
	}
}
