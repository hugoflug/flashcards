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
		SharedPreferences.Editor edit = prefs.edit();
		int nr = 0;
		for (Card card : cards) {			
			//can probably be refactored nicer (DRY)
			if (card.getAnswer().isBitmap()) {
				String a = card.getAnswer().getUri().toString();
				edit.putBoolean("cardlist_" + listName + "_a_" + nr + "_bmp", true);
				edit.putString("cardlist_" + listName + "_a_" + nr, a);
			} else {
				String a = card.getAnswer().getString();
				edit.putBoolean("cardlist_" + listName + "_a_" + nr + "_bmp", false);
				edit.putString("cardlist_" + listName + "_a_" + nr, a);
			}
			if (card.getQuestion().isBitmap()) {
				String q = card.getQuestion().getUri().toString();
				edit.putBoolean("cardlist_" + listName + "_q_" + nr + "_bmp", true);
				edit.putString("cardlist_" + listName + "_q_" + nr, q);
			} else {
				String q = card.getQuestion().getString();
				edit.putBoolean("cardlist_" + listName + "_q_" + nr + "_bmp", false);
				edit.putString("cardlist_" + listName + "_q_" + nr, q);
			}	
			nr++;
		}
		edit.commit();
	}
	
	public static class CardLoaderIterator implements Iterator<Card>, Iterable<Card> {
		private int pos;
		private BitmapDownsampler sampler;
		private String listName;
		private SharedPreferences prefs;
		
		private boolean gotNext;
		private String nextQ;
		
		public CardLoaderIterator(String listName, BitmapDownsampler sampler, SharedPreferences prefs) {
			this.sampler = sampler;
			this.listName = listName;
			this.prefs = prefs;
		}
		
		@Override
		public boolean hasNext() {
			//TODO: optimize, no unnecessary calls to this
			return !prefs.getString("cardlist_" + listName + "_q_" + pos, "").equals("");
		}

		@Override
		public Card next() {
			Card card = null;
			boolean isBitmap = prefs.getBoolean("cardlist_" + listName + "_q_" + pos + "_bmp", true);
			if (isBitmap) {
				Uri q = Uri.parse(prefs.getString("cardlist_" + listName + "_q_" + pos, ""));
				Uri a = Uri.parse(prefs.getString("cardlist_" + listName + "_a_" + pos, ""));
				try {
					//TEMP, should be able to load Strings effortlessly as well
					CardContent question = new CardContent(sampler.decode(q), q);
					CardContent answer = new CardContent(sampler.decode(a), a);
					card = new Card(question, answer);
				} catch (IOException e) {
					Log.v("flashcards", "Couldn't load bitmap");
				}
			} else {
				String q = prefs.getString("cardlist_" + listName + "_q_" + pos, "");
				String a = prefs.getString("cardlist_" + listName + "_a_" + pos, "");
				card = new Card(new CardContent(q), new CardContent(a));
			}
			pos++;
			gotNext = false;
			return card;
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
	
	public CardLoaderIterator getCardLoaderIterator(String listName, BitmapDownsampler sampler) {
		return new CardLoaderIterator(listName, sampler, prefs);
	}
	
	public List<Card> getCards(String listName, BitmapDownsampler sampler) throws IOException {
		List<Card> cardList = new ArrayList<Card>();
		CardLoaderIterator it = getCardLoaderIterator(listName, sampler);
		
		for (Card card : it) {
			cardList.add(card);
		}
		
		return cardList;
	}
}
