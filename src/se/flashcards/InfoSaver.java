package se.flashcards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

public class InfoSaver 
{
	private SharedPreferences prefs;
	
	public static InfoSaver getInfoSaver(Context c) {
		return new InfoSaver(c, "flashcards");
	}
	
	public InfoSaver(Context c, String name) {
		prefs = c.getSharedPreferences(name, c.MODE_PRIVATE);
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
		edit.putInt("cardlist_" + listName + "_length", nr);
		edit.commit();
	}
	
	public List<Card> getCards(String listName, BitmapDownsampler sampler) throws IOException {
		List<Card> cardList = new ArrayList<Card>();
		int cardAmnt = prefs.getInt("cardlist_" + listName + "_length", 0);
		for (int i = 0; i < cardAmnt; i++) {
			Uri q = Uri.parse(prefs.getString("cardlist_" + listName + "_q_" + i, ""));
			Uri a = Uri.parse(prefs.getString("cardlist_" + listName + "_a_" + i, ""));
			Card card = new Card(q, sampler.decode(q), a, sampler.decode(a));
			cardList.add(card);
		}
		return cardList;
	}
}
