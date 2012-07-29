package se.flashcards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

public class InfoSaver
{
	private SharedPreferences prefs;
	
	public static InfoSaver getInfoSaver(Context c) {
		return new InfoSaver(c, "flashcards");
	}
	
	public InfoSaver(Context c, String name) {
		prefs = c.getSharedPreferences(name, Context.MODE_PRIVATE);
	}
	
	public SharedPreferences getPrefs() {
		return prefs;
	}
	
	public void saveCardLists(List<String> cardLists) {
		SharedPreferences.Editor edit = prefs.edit();
		int nr = 0;
		for (String s : cardLists) {
			edit.putString("cardlist_" + nr, s);
			nr++;
		}
		edit.putInt("cardlist_length", nr);
		edit.commit();
	}
	
	public List<String> getCardLists() {
		List<String> cardLists = new ArrayList<String>();
		int cardListsAmnt = prefs.getInt("cardlist_length", 0);
		for (int i = 0; i < cardListsAmnt; i++) {
			cardLists.add(prefs.getString("cardlist_" + i, "katja le hipster"));
		}
		
		//to be implemented when actual deletion is implemented
//		int i = 0;
//		while (true) {
//			String str = prefs.getString("cardlist_" + i, "");
//			if (str.equals("")) {
//				break;
//			}
//			cardLists.add(str);
//			i++;
//		}
		
		return cardLists;
	}

	public void saveCards(String listName, List<Card> cards) {
		int nr = 0;
		for (Card card : cards) {			
			saveCardContent("cardlist_" + listName + "_q_" + nr, card.getQuestion());
			saveCardContent("cardlist_" + listName + "_a_" + nr, card.getAnswer());	
			nr++;
		}
	}
	
	public boolean cardContentExists(String name) {
		return !prefs.getString(name, "").equals("");
	}
	
	public void saveCardContent(String name, CardContent content) {
		SharedPreferences.Editor editor = prefs.edit();
		
		String toSave = "";
		boolean isBmp = false;
		if (content.isBitmap()) {
			toSave = content.getUri().toString();
			isBmp = true;
		} else {
			toSave = content.getString();
			isBmp = false;
		}
		editor.putBoolean(name + "_bmp", isBmp);
		editor.putString(name, toSave);
		editor.commit();
	}
	
	public CardContent loadCardContent(String name, BitmapDownsampler downSampler) {
		boolean isBitmap = prefs.getBoolean(name + "_bmp", true);
		if (isBitmap) {
			Uri q = Uri.parse(prefs.getString(name, ""));
			try {
				return new CardContent(downSampler.decode(q), q);
			} catch (IOException e) {
				Log.v("flashcards", "Couldn't load question bitmap");
				return null;
			}
		} else {
			return new CardContent(prefs.getString(name, ""));
		}
	}
	
	public static class CardLoaderIterator implements Iterator<Card>, Iterable<Card> {
		private int pos;
		private BitmapDownsampler sampler;
		private String listName;
		private InfoSaver infoSaver;
		
		private boolean gotNext;
		private String nextQ;
		
		public CardLoaderIterator(String listName, BitmapDownsampler sampler, InfoSaver infoSaver) {
			this.sampler = sampler;
			this.listName = listName;
			this.infoSaver = infoSaver;
		}
		
		@Override
		public boolean hasNext() {
			//TODO: optimize, no unnecessary calls to this
			return !infoSaver.getPrefs().getString("cardlist_" + listName + "_q_" + pos, "").equals("");
		}

		@Override
		public Card next() {
			CardContent question = infoSaver.loadCardContent("cardlist_" + listName + "_q_" + pos, sampler);
			CardContent answer = infoSaver.loadCardContent("cardlist_" + listName + "_a_" + pos, sampler);
			
			pos++;
			gotNext = false;
			return new Card(question, answer);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<Card> iterator() {
			return this;
		}	
	}
	
	public CardLoaderIterator getCardLoaderIterator(Context c, String listName, BitmapDownsampler sampler) {
		return new CardLoaderIterator(listName, sampler, getInfoSaver(c));
	}
	
	public List<Card> getCards(Context c, String listName, BitmapDownsampler sampler) throws IOException {
		List<Card> cardList = new ArrayList<Card>();
		CardLoaderIterator it = getCardLoaderIterator(c, listName, sampler);
		
		for (Card card : it) {
			cardList.add(card);
		}
		
		return cardList;
	}
}
