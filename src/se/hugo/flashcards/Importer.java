package se.hugo.flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import au.com.bytecode.opencsv.CSVReader;

public class Importer {
	
	public static List<Card> importCards(Context context, String filename, BitmapDownsampler sampler) throws IOException {
		if (Util.isFile(filename, "zip")) {
			return importCardsFromZip(context, new ZipFile(filename), sampler);
		} else {
			return importCardsFromFolder(filename, sampler);
		}
	}
	
	public static List<Card> importCardsFromZip(Context context, ZipFile file, BitmapDownsampler sampler) throws IOException {
		Enumeration<? extends ZipEntry> e = file.entries();
		
		while (e.hasMoreElements()) {
			ZipEntry z = e.nextElement();
			if (Util.isFile(z.getName(), "csv")) {
				CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(z)));
				List<String[]> lines = reader.readAll();
				reader.close();
				
				ArrayList<Card> cardList = new ArrayList<Card>(lines.size());
				for (String[] s : lines) {
					if (s.length < 2) {
						throw new IOException();
					}
					
					CardContent c1, c2;
					if (Util.isImageFile(s[0])) {
						ZipEntry entry = file.getEntry(s[0]);
						Bitmap bitmap = sampler.decode(file, entry);
						Util.saveBitmapToFile(context, bitmap, entry.getName());
						File savedPath = context.getFileStreamPath(entry.getName());
						c1 = new CardContent(sampler.decode(file, entry), Uri.fromFile(savedPath));
					} else {
						c1 = new CardContent(s[0]);
					}
					
					if (Util.isImageFile(s[1])) {
						ZipEntry entry = file.getEntry(s[1]);	
						Bitmap bitmap = sampler.decode(file, entry);
						Util.saveBitmapToFile(context, bitmap, entry.getName());
						File savedPath = context.getFileStreamPath(entry.getName());
						c2 = new CardContent(sampler.decode(file, entry), Uri.fromFile(savedPath));
					} else {
						c2 = new CardContent(s[1]);
					}
					
					cardList.add(new Card(c1, c2));
				}
				return cardList;
			}
		}
		
		return null;
	}
	
	public static List<Card> importCardsFromFolder(String filename, BitmapDownsampler sampler) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(filename));
		List<String[]> lines = reader.readAll();
		reader.close();
		
		ArrayList<Card> cardList = new ArrayList<Card>(lines.size());
		for (String[] s : lines) {
			if (s.length < 2) {
				throw new IOException();
			}
			
			String filepath = removeLastSegment(filename);
			
			CardContent c1, c2;
			if (Util.isImageFile(s[0])) {
				Uri imageUri = Uri.parse("file://" + filepath + s[0]);
				
				c1 = new CardContent(sampler.decode(imageUri), imageUri);
			} else {
				c1 = new CardContent(s[0]);
			}
			
			if (Util.isImageFile(s[1])) {
				Uri imageUri = Uri.parse("file://" + filepath + s[1]);
				c2 = new CardContent(sampler.decode(imageUri), imageUri);
			} else {
				c2 = new CardContent(s[1]);
			}
			
			cardList.add(new Card(c1, c2));
		}
		
		return cardList;
	}
	
	private static String removeLastSegment(String filepath) {
		Pattern p = Pattern.compile("(^.*/).*");
		Matcher m = p.matcher(filepath); 
		return m.replaceAll("$1");
	}
	
	public static void exportAsCSV(Context context, long listId) throws IOException {
		InfoSaver saver = InfoSaver.getInfoSaver(context);
		List<Card> cards = saver.getCards(context, listId, null); //TODO: not null later?
		String name = saver.nameFromId(listId);
		exportCards(name, cards);
	}
	
	public static void exportCards(String name, List<Card> cards) {
		StringBuilder csvBuilder = new StringBuilder();
		
		ArrayList<Uri> uris = new ArrayList<Uri>();
		for (Card card : cards) {
			String question = card.getQuestion().getUriOrString();
			String answer = card.getAnswer().getUriOrString();
			csvBuilder.append(question + "," + answer + "\n");
		}
		
		Log.v("flashcards", "csv_out: " + csvBuilder.toString()); //TEMP
		
		//TODO: save csv file
		//if images, save images + csv file in same archive
	}
}
