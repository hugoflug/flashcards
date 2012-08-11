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
	
	public static String cardListIdentifier(int cardListNr) {
		return "cardlist_" + cardListNr;
	}
	
	public static String cardQuestionIdentifier(long listId, int nr) {
		return "cardlist_" + listId + "_q_" + nr;
	}
	
	public static String cardAnswerIdentifier(long listId, int nr) {
		return "cardlist_" + listId + "_a_" + nr;
	}
	
	public static String cardListIdentifier(long id) {
		return "cardlist_name_" + id;
	}
	
	public static String cardNameIdentifier(long id) {
		return "card_list_name_" + id;
	}
	
	public SharedPreferences getPrefs() {
		return prefs;
	}
	
	public void removeCardList(long id) {
		SharedPreferences.Editor editor = prefs.edit();
		int nr = 0;
		while (!prefs.getString(cardQuestionIdentifier(id, nr), "").equals("")) {
			removeCardContent(cardQuestionIdentifier(id, nr));
			removeCardContent(cardAnswerIdentifier(id, nr));
			nr++;
		}
		editor.commit();
	}
	
	public void saveCardLists(List<CardList> cardLists) {
		SharedPreferences.Editor edit = prefs.edit();
		int nr = 0;
		for (CardList cl : cardLists) {
			edit.putLong(cardListIdentifier(nr), cl.getID());
			edit.putString(cardNameIdentifier(cl.getID()), cl.getName());
			nr++;
		}
		edit.putInt("cardlist_length", nr);
		edit.commit();
	}
	
	public List<CardList> getCardLists() {
		List<CardList> cardLists = new ArrayList<CardList>();
		int cardListsAmnt = prefs.getInt("cardlist_length", 0);
		for (int i = 0; i < cardListsAmnt; i++) {
			long id = prefs.getLong(cardListIdentifier(i), 0);
			String name = nameFromId(id);
			Log.v("flashcards", "id: " + id);
			cardLists.add(new CardList(id, name));
		}
		
		return cardLists;
	}
	
	public String nameFromId(long id) {
		return prefs.getString(cardNameIdentifier(id), null);
	}
	
	public void renameCardList(long id, String newName) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(cardNameIdentifier(id), newName);
		editor.commit();
	}
	
	public void removeAllCardsAbove(long id, int pos) {
		int remPos = pos;
		while (cardContentExists(cardAnswerIdentifier(id, remPos))) {
			removeCardContent(cardAnswerIdentifier(id, remPos));
			removeCardContent(cardQuestionIdentifier(id, remPos));
			remPos++;
		}
	}
	
	public void saveCards(long id, List<Card> cards) {
		int nr = 0;
		for (Card card : cards) {			
			saveCardContent(cardQuestionIdentifier(id, nr), card.getQuestion());
			saveCardContent(cardAnswerIdentifier(id, nr), card.getAnswer());	
			nr++;
		}
		removeAllCardsAbove(id, nr);
	}
	
	public boolean cardContentExists(String name) {
		return !(prefs.getString(name, null) == null);
	}
	
	private void removeCardContent(String identifier) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(identifier);
		editor.remove(identifier + "_bmp");
		editor.commit();
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
	
	public CardContent loadCardContent(String identifier, BitmapDownsampler downSampler) {
		boolean isBitmap = prefs.getBoolean(identifier + "_bmp", true);
		if (isBitmap) {
			Uri q = Uri.parse(prefs.getString(identifier, ""));
			try {
				return new CardContent(downSampler.decode(q), q);
			} catch (IOException e) {
				Log.v("flashcards", "Couldn't load question bitmap");
				return null;
			}
		} else {
			return new CardContent(prefs.getString(identifier, ""));
		}
	}
	
	public static class CardLoaderIterator implements Iterator<Card>, Iterable<Card> {
		private int pos;
		private BitmapDownsampler sampler;
		private long listId;
		private InfoSaver infoSaver;
		
		private boolean gotNext;
		private String nextQ;
		
		public CardLoaderIterator(long listId, BitmapDownsampler sampler, InfoSaver infoSaver) {
			this.sampler = sampler;
			this.listId = listId;
			this.infoSaver = infoSaver;
		}
		
		@Override
		public boolean hasNext() {
			//TODO: optimize, no unnecessary calls to this
			return !infoSaver.getPrefs().getString(cardQuestionIdentifier(listId, pos), "").equals("");
		}

		@Override
		public Card next() {
			CardContent question = infoSaver.loadCardContent(cardQuestionIdentifier(listId, pos), sampler);
			CardContent answer = infoSaver.loadCardContent(cardAnswerIdentifier(listId, pos), sampler);
			
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
	
	public CardLoaderIterator getCardLoaderIterator(Context c, long listId, BitmapDownsampler sampler) {
		return new CardLoaderIterator(listId, sampler, getInfoSaver(c));
	}
	
	public List<Card> getCards(Context c, long listId, BitmapDownsampler sampler) throws IOException {
		List<Card> cardList = new ArrayList<Card>();
		CardLoaderIterator it = getCardLoaderIterator(c, listId, sampler);
		
		for (Card card : it) {
			cardList.add(card);
		}
		
		return cardList;
	}
}
