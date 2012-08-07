package se.flashcards;

import java.util.ArrayList;
import java.util.List;

import se.flashcards.InfoSaver.CardLoaderIterator;
import android.content.Context;
import android.os.AsyncTask;

public class LoadCardsTask extends AsyncTask<Void, Card, Void> {
	private CardLoaderIterator it;
	private List<Card> addLast;
	
	public LoadCardsTask(Context c, String listName, BitmapDownsampler sampler) {
		it = InfoSaver.getInfoSaver(c).getCardLoaderIterator(c, listName, sampler);
		addLast = new ArrayList<Card>();
	}
	
	public LoadCardsTask(Context c, String listName, BitmapDownsampler sampler, List<Card> addLast) {
		it = InfoSaver.getInfoSaver(c).getCardLoaderIterator(c, listName, sampler);
		this.addLast = addLast;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		for (Card c : it) {
			publishProgress(c);
		}
		for (Card c : addLast) {
			publishProgress(c);
		}
		return null;
	}
	
	public void addLastLater(Card c) {
		addLast.add(c);
	}
}
