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
			String a = card.getAnswerUri().toString();
			String q = card.getQuestionUri().toString();
			edit.putString("cardlist_" + listName + "_q_" + nr, q);
			edit.putString("cardlist_" + listName + "_a_" + nr, a);
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
			if (!gotNext) {
				nextQ = prefs.getString("cardlist_" + listName + "_q_" + pos, "");
			}
			return !nextQ.equals("");
		}

		@Override
		public Card next() {
			Uri q = null;
			if (gotNext) {
				q = Uri.parse(nextQ);
			} else {
				q = Uri.parse(prefs.getString("cardlist_" + listName + "_q_" + pos, ""));
			}
			Uri a = Uri.parse(prefs.getString("cardlist_" + listName + "_a_" + pos, ""));
			Card card = null;
			try {
				card = new Card(q, sampler.decode(q), a, sampler.decode(a));
			} catch (IOException e) {
				Log.v("flashcards", "Couldn't load bitmap");
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
