package se.hugo.flashcards;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
			cardList.add(new Card(new CardContent(s[0]), new CardContent(s[1])));
		}
		return cardList;
	}
}
