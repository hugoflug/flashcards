package se.flashcards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.flashcards.InfoSaver.CardLoaderIterator;
import android.content.Context;
import android.os.AsyncTask;

public class LoadCardsTask extends AsyncTask<Void, Card, Void> {
	private CardLoaderIterator it;
	private List<Card> addLast;
	private Map<Integer, Card> replaceLaterMap;
	
	public LoadCardsTask(Context c, long listId, BitmapDownsampler sampler) {
		this(c, listId, sampler, new ArrayList<Card>());
	}
	
	public LoadCardsTask(Context c, long listId, BitmapDownsampler sampler, List<Card> addLast) {
		it = InfoSaver.getInfoSaver(c).getCardLoaderIterator(c, listId, sampler);
		this.addLast = addLast;
		replaceLaterMap = new HashMap<Integer, Card>();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		int i = 0;
		for (Card c : it) {
			Card replacementCard = replaceLaterMap.get(i);
			if (replacementCard != null) {
				publishProgress(replacementCard);
			} else {
				publishProgress(c);
			}
			i++;
		}
		for (Card c : addLast) {
			publishProgress(c);
		}
		
		return null;
	}
	
	public void replaceLater(int pos, Card newCard) {
		replaceLaterMap.put(pos, newCard);
	}
	
	public void addLastLater(Card c) {
		addLast.add(c);
	}
}
