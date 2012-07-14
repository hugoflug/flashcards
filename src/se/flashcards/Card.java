package se.flashcards;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

public class Card {
	private Bitmap question;
	private Bitmap answer;
	private Uri qUri;
	private Uri aUri;
	
	public Card(Uri questionUri, Bitmap question, Uri answerUri, Bitmap answer) {
		this.question = question;
		this.answer = answer;
		this.qUri = questionUri;
		this.aUri = answerUri;
	}
	
	public Bitmap getQuestion() {
		return question;
	}
	
	public Bitmap getAnswer() {
		return answer;
	}
	
	public Uri getQuestionUri() {
		return qUri;
	}
	
	public Uri getAnswerUri() {
		return aUri;
	}
}

//package se.flashcards;
//
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.view.View;
//
//public class Card {
//	private CardContent question;
//	private CardContent answer;
//	
//	public Card(CardContent question, CardContent answer) {
//		this.question = question;
//		this.answer = answer;
//	}
//	
//	public CardContent getQuestion() {
//		return question;
//	}
//	
//	public CardContent getAnswer() {
//		return answer;
//	}
//	
//	public void save() {
//		question.save();
//		answer.save();
//	}
//}
