package se.flashcards;

import java.util.List;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

//TODO: Finish class
public class CardsListListAdapter extends BaseAdapter {
	private List<CardList> cardLists;
	
	@Override
	public int getCount() {
		return cardLists.size();
	}

	@Override
	public Object getItem(int index) {
		return cardLists.get(index);
	}

	@Override
	public long getItemId(int index) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
}
