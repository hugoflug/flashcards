package se.flashcards;

import se.flashcards.InfoSaver.CardLoaderIterator;
import android.content.Context;
import android.os.AsyncTask;

public class LoadCardsTask extends AsyncTask<Void, Card, Void> {

	private CardLoaderIterator it;
	
	public LoadCardsTask(Context c, String listName, BitmapDownsampler sampler) {
		it = InfoSaver.getInfoSaver(c).getCardLoaderIterator(listName, sampler);
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		for (Card c : it) {
			publishProgress(c);
		}
		return null;
	}
}
