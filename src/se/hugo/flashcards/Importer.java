package se.hugo.flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
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
	
	public static File exportAsCSV(Context context, long listId, BitmapDownsampler sampler) throws IOException {
		InfoSaver saver = InfoSaver.getInfoSaver(context);
		List<Card> cards = saver.getCards(context, listId, sampler);
		String name = saver.nameFromId(listId);
		return exportCards(context, name, cards);
	}
	
	public static boolean hasBitmaps(List<Card> cards) {
		for (Card card : cards) {
			if (card.getQuestion().isBitmap() || card.getAnswer().isBitmap()) {
				return true;
			}
		}
		return false;
	}
	
	private static String escape(String s) {
		if (s.contains(",")) {
			return "\"" + s + "\"";
		} else {
			return s;
		}
	}
	
	private static String nameFile(String filename, String extension) {
		int nr = 2;
		String oFilename = filename;
		while (new File(oFilename + "." + extension).exists()) {
			oFilename = filename + " (" + nr + ")";
			nr++;
		}
		return oFilename + "." + extension;
	}
	
	public static File exportCards(Context context, String name, List<Card> cards) throws IOException {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String savePath = prefs.getString(SettingsActivity.SAVE_PATH, context.getString(R.string.default_save_folder));
		
		if (hasBitmaps(cards)) {						
			String filename = nameFile(Environment.getExternalStorageDirectory() + savePath + name, "zip");
			
			StringBuilder csvBuilder = new StringBuilder();
			
			File file = new File(filename);
			file.getParentFile().mkdirs();
			
			FileOutputStream fos = new FileOutputStream(file);
			ZipOutputStream zos = new ZipOutputStream(fos);

			
			Set<String> imageFiles = new HashSet<String>();
			
			for (Card card : cards) {
				String qStr, aStr;
				
				CardContent question = card.getQuestion();
				if (question.isBitmap()) {
					Uri uri = question.getUri();
					qStr = uri.getLastPathSegment();
					
					if (!imageFiles.contains(qStr)) {
						zos.putNextEntry(new ZipEntry(qStr));
						Bitmap bmp = question.getBitmap();
						bmp.compress(Bitmap.CompressFormat.PNG, 90, zos);
						zos.closeEntry();
						imageFiles.add(qStr);
					}
					
				} else {
					qStr = escape(question.getString());
				}
				
				CardContent answer = card.getAnswer();
				if (answer.isBitmap()) {
					Uri uri = answer.getUri();
					aStr = uri.getLastPathSegment();
					
					if (!imageFiles.contains(aStr)) {
						zos.putNextEntry(new ZipEntry(aStr));
						Bitmap bmp = answer.getBitmap();
						bmp.compress(Bitmap.CompressFormat.PNG, 90, zos);
						zos.closeEntry();
						imageFiles.add(aStr);
					}
				} else {
					aStr = escape(answer.getString());
				}
				
				csvBuilder.append(qStr + "," + aStr + "\n");
			}
			
			byte[] bytes = csvBuilder.toString().getBytes("UTF-8");
			
			zos.putNextEntry(new ZipEntry(name + ".csv"));
			zos.write(bytes, 0, bytes.length);
			zos.closeEntry();
			
			zos.close();
			fos.close(); //necessary?
			
			return new File(filename);
		} else {		
			String filename = nameFile(Environment.getExternalStorageDirectory() + savePath + name, "csv");
			
			File file = new File(filename);
			file.getParentFile().mkdirs();
			
			FileOutputStream fos = new FileOutputStream(filename);
			
			OutputStreamWriter writer  = new OutputStreamWriter(fos);
			for (Card card : cards) {
				String question = escape(card.getQuestion().getString());
				String answer = escape(card.getAnswer().getString());
				writer.write(question + "," + answer + "\n");
			}
			writer.close();
			return new File(filename);
		}
	}
}
