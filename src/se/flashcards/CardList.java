package se.flashcards;

import java.util.Random;

public class CardList {
	private long id;
	private String name;
	
	public CardList(long id, String name) {
		this.name = name;
		this.id = id;
	}
	
	public CardList(String name) {
		this.name = name;
		
		Random random = new Random();
		id = random.nextLong();
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
}
