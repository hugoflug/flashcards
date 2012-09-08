package se.flashcards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SlidingDrawer;
import android.view.MotionEvent;


public class WrappingSlidingDrawer extends SlidingDrawer {

    public WrappingSlidingDrawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        int orientation = attrs.getAttributeIntValue("android", "orientation", ORIENTATION_VERTICAL);
        mTopOffset = attrs.getAttributeIntValue("android", "topOffset", 0);
        mVertical = (orientation == SlidingDrawer.ORIENTATION_VERTICAL);
    }

    public WrappingSlidingDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);

        int orientation = attrs.getAttributeIntValue("android", "orientation", ORIENTATION_VERTICAL);
        mTopOffset = attrs.getAttributeIntValue("android", "topOffset", 0);
        mVertical = (orientation == SlidingDrawer.ORIENTATION_VERTICAL);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//    	super.onTouchEvent(event);
//    	return true;
//    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.UNSPECIFIED || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            throw new RuntimeException("SlidingDrawer cannot have UNSPECIFIED dimensions");
        }

        final View handle = getHandle();
        final View content = getContent();
        measureChild(handle, widthMeasureSpec, heightMeasureSpec);

        if (mVertical) {
            int height = heightSpecSize - handle.getMeasuredHeight() - mTopOffset;
            content.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, heightSpecMode));
            heightSpecSize = handle.getMeasuredHeight() + mTopOffset + content.getMeasuredHeight();
            widthSpecSize = content.getMeasuredWidth();
            if (handle.getMeasuredWidth() > widthSpecSize) widthSpecSize = handle.getMeasuredWidth();
        }
        else {
            int width = widthSpecSize - handle.getMeasuredWidth() - mTopOffset;
            getContent().measure(MeasureSpec.makeMeasureSpec(width, widthSpecMode), heightMeasureSpec);
            widthSpecSize = handle.getMeasuredWidth() + mTopOffset + content.getMeasuredWidth();
            heightSpecSize = content.getMeasuredHeight();
            if (handle.getMeasuredHeight() > heightSpecSize) heightSpecSize = handle.getMeasuredHeight();
        }
        
//        if (isVertical) {
//            int height = heightSpecSize - handle.getMeasuredHeight() - topOffset;
//            getContent().measure(MeasureSpec.makeMeasureSpec(widthSpecSize, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
//            heightSpecSize = handle.getMeasuredHeight() + topOffset + content.getMeasuredHeight();
//            widthSpecSize = content.getMeasuredWidth();
//            if (handle.getMeasuredWidth() > widthSpecSize) widthSpecSize = handle.getMeasuredWidth();
//        } else {
//            int width = widthSpecSize - handle.getMeasuredWidth() - topOffset;
//            getContent().measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSpecSize, MeasureSpec.UNSPECIFIED));
//            widthSpecSize = handle.getMeasuredWidth() + topOffset + content.getMeasuredWidth();
//            heightSpecSize = content.getMeasuredHeight();
//            if (handle.getMeasuredHeight() > heightSpecSize) heightSpecSize = handle.getMeasuredHeight();
//        }

        setMeasuredDimension(widthSpecSize, heightSpecSize);
    }

    private boolean mVertical;
    private int mTopOffset;
}
