package se.hugo.flashcards;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import au.com.bytecode.opencsv.CSVReader;

public class Importer {
	
	public static List<Card> importCardsx(String filename) throws IOException {
		ArrayList<Card> cardList = new ArrayList<Card>();
		cardList.add(new Card(new CardContent("foo"), new CardContent("bar")));
		cardList.add(new Card(new CardContent("hip"), new CardContent("ster")));
		return cardList;
	}
	
	public static List<Card> importCards(String filename) throws IOException {	
		CSVReader reader = new CSVReader(new FileReader(filename));
		List<String[]> lines = reader.readAll();
		
		ArrayList<Card> cardList = new ArrayList<Card>(lines.size());
		for (String[] s : lines) {
			if (s.length < 2) {
				throw new IOException();
			}
			cardList.add(new Card(new CardContent(s[0]), new CardContent(s[1])));
		}
		return cardList;
	}
	
	public static void exportAsCSV(Context context, long listId) throws IOException {
		InfoSaver saver = InfoSaver.getInfoSaver(context);
		List<Card> cards = saver.getCards(context, listId, null); //TODO: not null later?
		String name = saver.nameFromId(listId);
		exportCards(name, cards);
	}
	
	public static void exportCards(String name, List<Card> cards) {
		Intent shareIntent = new Intent();
		
		StringBuilder csvBuilder = new StringBuilder();
		
		ArrayList<Uri> uris = new ArrayList<Uri>();
		for (Card card : cards) {
			String question = card.getQuestion().getUriOrString();
			String answer = card.getAnswer().getUriOrString();
			csvBuilder.append(question + "," + answer + "\n");
		}
		
		Log.v("flashcards", "csv_out: " + csvBuilder.toString()); //TEMP
		
		//TODO: send cards as image content
		
		shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
		shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		shareIntent.setType("*/*");
	}
}
