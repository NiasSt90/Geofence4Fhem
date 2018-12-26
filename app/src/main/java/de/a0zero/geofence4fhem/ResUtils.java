package de.a0zero.geofence4fhem;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;


public class ResUtils {

	public static Bitmap getBitmap(Context context, @DrawableRes int drawableResId) {
		Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof VectorDrawableCompat) {
			return getBitmap((VectorDrawableCompat) drawable);
		} else if (drawable instanceof VectorDrawable) {
			return getBitmap((VectorDrawable) drawable);
		} else {
			throw new IllegalArgumentException("Unsupported drawable type");
		}
	}


	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
		Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
				vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		vectorDrawable.draw(canvas);
		return bitmap;
	}


	private static Bitmap getBitmap(VectorDrawableCompat vectorDrawable) {
		Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
				vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		vectorDrawable.draw(canvas);
		return bitmap;
	}
}
