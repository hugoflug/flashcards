package se.flashcards;

import java.util.List;

import se.flashcards.InfoSaver.CardLoaderIterator;
import android.content.Context;
import android.os.AsyncTask;

public class LoadCardsTask extends AsyncTask<Void, Card, Void> {
	private CardLoaderIterator it;
	
	public LoadCardsTask(Context c, String listName, BitmapDownsampler sampler) {
		it = InfoSaver.getInfoSaver(c).getCardLoaderIterator(c, listName, sampler);
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		for (Card c : it) {
			publishProgress(c);
		}
		return null;
	}
}
