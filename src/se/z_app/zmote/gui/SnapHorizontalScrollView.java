package se.z_app.zmote.gui;

import java.util.ArrayList;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * This class extends the functionality of the HorizontalScrollView
 * and create a new type of view which is able to snap the screen on
 * the items you pass it in an ArrayList
 * @author Francisco Valladares
 */
public class SnapHorizontalScrollView extends HorizontalScrollView {
    private static final int SWIPE_MIN_DISTANCE = 5;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;
 
    private ArrayList mItems = null;
    private GestureDetector mGestureDetector;
    private int mActiveFeature = 0;
 
    /**
     * Same as default constructor for HorizontalScrollView with same parameters
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SnapHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    /**
     * Same as default constructor for HorizontalScrollView with same parameters
     * @param context
     * @param attrs
     */
    public SnapHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    /**
     * Same as default constructor for HorizontalScrollView with same parameters
     * @param context
     */
    public SnapHorizontalScrollView(Context context) {
        super(context);
    }
 
    /**
     * Inserts the items in the scroll view
     * @param items list of items to insert
     */
	public void setFeatureItems(ArrayList items){
    	
        LinearLayout internalWrapper = new LinearLayout(getContext());	// Main container for the items
        internalWrapper.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        internalWrapper.setOrientation(LinearLayout.HORIZONTAL);
        addView(internalWrapper);
        
        this.mItems = items;
        for(int i = 0; i< items.size();i++){
            // Adding the items to the container
            internalWrapper.addView((View) mItems.get(i));
        }
        
        // Now we set the listener to make it snap
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //If the user swipes
                if (mGestureDetector.onTouchEvent(event)) {
                    return true;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL ){
                    int scrollX = getScrollX();
                    int featureWidth = v.getMeasuredWidth();
                    mActiveFeature = ((scrollX + (featureWidth/2))/featureWidth);
                    int scrollTo = mActiveFeature*featureWidth;
                    smoothScrollTo(scrollTo, 0);
                    return true;
                }
                else{
                    return false;
                }
            }
        });
        mGestureDetector = new GestureDetector(new MyGestureDetector());
    }
	
	/**
	 * Custom version of SimpleOnGestureListener
	 * @author Fransisco Valladares
	 */
    class MyGestureDetector extends SimpleOnGestureListener {
    	
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                //right to left
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    int featureWidth = getMeasuredWidth();
                    mActiveFeature = (mActiveFeature < (mItems.size() - 1))? mActiveFeature + 1:mItems.size() -1;
                    smoothScrollTo(mActiveFeature*featureWidth, 0);
                    return true;
                }
                //left to right
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    int featureWidth = getMeasuredWidth();
                    mActiveFeature = (mActiveFeature > 0)? mActiveFeature - 1:0;
                    smoothScrollTo(mActiveFeature*featureWidth, 0);
                    return true;
                }
            } catch (Exception e) {
                Log.e("Fling", "There was an error processing the Fling event:" + e.getMessage());
            }
            return false;
        }
    }
    
}
