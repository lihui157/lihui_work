package net.cstong.android.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public abstract class MaskedImageView extends ImageView {
	private static final Xfermode MASK_XFERMODE;
	private Bitmap mask;
	private Paint paint;

	static {
		PorterDuff.Mode localMode = PorterDuff.Mode.DST_IN;
		MASK_XFERMODE = new PorterDuffXfermode(localMode);
	}

	public MaskedImageView(final Context paramContext) {
		super(paramContext);
	}

	public MaskedImageView(final Context paramContext, final AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public MaskedImageView(final Context paramContext, final AttributeSet paramAttributeSet, final int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	public abstract Bitmap createMask();

	@Override
	protected void onDraw(final Canvas paramCanvas) {
		Drawable localDrawable = getDrawable();
		if (localDrawable == null) {
			return;
		}
		try {
			if (paint == null) {
				Paint localPaint1 = new Paint();
				paint = localPaint1;
				paint.setFilterBitmap(false);
				Paint localPaint2 = paint;
				Xfermode localXfermode1 = MASK_XFERMODE;
				@SuppressWarnings("unused")
				Xfermode localXfermode2 = localPaint2.setXfermode(localXfermode1);
			}
			float f1 = getWidth();
			float f2 = getHeight();
			int i = paramCanvas.saveLayer(0.0F, 0.0F, f1, f2, null, 31);
			int j = getWidth();
			int k = getHeight();
			localDrawable.setBounds(0, 0, j, k);
			localDrawable.draw(paramCanvas);
			if ((mask == null) || (mask.isRecycled())) {
				Bitmap localBitmap1 = createMask();
				mask = localBitmap1;
			}
			Bitmap localBitmap2 = mask;
			Paint localPaint3 = paint;
			paramCanvas.drawBitmap(localBitmap2, 0.0F, 0.0F, localPaint3);
			paramCanvas.restoreToCount(i);
			return;
		} catch (Exception localException) {
			StringBuilder localStringBuilder = new StringBuilder().append("Attempting to draw with recycled bitmap. View ID = ");
			System.out.println("localStringBuilder==" + localStringBuilder);
		}
	}
}