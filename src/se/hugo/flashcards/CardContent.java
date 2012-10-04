package se.hugo.flashcards;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class CardContent implements Parcelable {
	private Bitmap bitmap;
	private Uri bitmapUri;
	private String string;
	private boolean isBitmap;
	
	public CardContent(String string) {
		this.string = string;
		isBitmap = false;
	}
	
	public CardContent(Bitmap bitmap, Uri bitmapUri) {
		this.bitmap = bitmap;
		this.bitmapUri = bitmapUri;
		isBitmap = true;
	}
	
	public Uri getUri() {
		return bitmapUri;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public String getString() {
		return string;
	}
	
	public boolean isBitmap() {
		return isBitmap;
	}
	
	public void reloadBitmap(BitmapDownsampler sampler) throws IOException {
		if (isBitmap) {
			bitmap = sampler.decode(bitmapUri);
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	
	
	public static final Parcelable.Creator<CardContent> CREATOR = new Parcelable.Creator<CardContent>() {
		public CardContent createFromParcel(Parcel in) {
		    return new CardContent(in);
		}
		
		public CardContent[] newArray(int size) {
		    return new CardContent[size];
		}
	};
	
	public CardContent(Parcel in) {
		isBitmap = in.readInt() == 1;
		
		if (isBitmap) {
			bitmapUri = in.readParcelable(null);
		} else {
			string = in.readString();
		}
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(isBitmap ? 1 : 0 );
		
		if (isBitmap) {
			out.writeParcelable(bitmapUri, 0);
		} else {
			out.writeString(string);
		}
	}
}
