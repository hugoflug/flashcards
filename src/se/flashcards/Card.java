package se.flashcards;

import android.graphics.Bitmap;
import android.view.View;

public class Card {
	private Bitmap question;
	private Bitmap answer;
	
	public Card(Bitmap question, Bitmap answer) {
		this.question = question;
		this.answer = answer;
	}
	
	public Bitmap getQuestion() {
		return question;
	}
	
	public Bitmap getAnswer() {
		return answer;
	}
}
