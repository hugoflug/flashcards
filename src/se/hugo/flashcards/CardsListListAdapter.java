package se.hugo.flashcards;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

//TODO: Finish class
public class CardsListListAdapter extends BaseAdapter {
	private List<CardList> cardLists;
	private Context context;
	
	public CardsListListAdapter(Context context, List<CardList> cardLists) {
		this.cardLists = cardLists;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return cardLists.size();
	}

	@Override
	public CardList getItem(int index) {
		return cardLists.get(index);
	}

	@Override
	public long getItemId(int index) {
		return 0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
//		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View view = inflater.inflate(R.layout.card_list_item, null);
		
		CardListListItem cardListView = new CardListListItem(context); //(CardListListItem)view.findViewById(R.id.card_list_list_item);
		cardListView.setTitle(cardLists.get(pos).getName());
		cardListView.setAmount(cardLists.get(pos).getNumberOfCards());
		return cardListView;
	}

	@Override
	public boolean isEmpty() {
		return cardLists.isEmpty();
	}
}
