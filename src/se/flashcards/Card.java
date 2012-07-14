package se.flashcards;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

public class Card {
	private CardContent question;
	private CardContent answer;
	
	public Card(CardContent question, CardContent answer) {
		this.question = question;
		this.answer = answer;
	}
	
	public CardContent getQuestion() {
		return question;
	}
	
	public CardContent getAnswer() {
		return answer;
	}
}
