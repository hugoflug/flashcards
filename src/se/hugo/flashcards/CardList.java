package se.hugo.flashcards;

import java.util.Random;

public class CardList {
	private long id;
	private String name;
	private int numberOfCards;
	
	public CardList(long id, String name, int numberOfCards) {
		this.name = name;
		this.id = id;
		this.numberOfCards = numberOfCards;
	}
	//
	public CardList(long id, String name) {
		this(id, name, 0); 
	}
	
	public CardList(String name) {
		this.name = name;
		
		Random random = new Random();
		id = random.nextLong();
		
		numberOfCards = 0; 
	}
	
	public void rename(String newName) {
		name = newName;
	}
	
	public long getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public int getNumberOfCards() {
		return numberOfCards;
	}
	
	public void setNumberOfCards(int noc) {
		numberOfCards = noc;
	}
}
