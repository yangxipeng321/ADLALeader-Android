package hk.com.mobileye.jason.adlaleader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by Jason on 2016/4/16.
 * The strip below Tabs to indicate current tab
 */
public class SlidingTabStrip extends LinearLayout {
    private static final int DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS = 6;
    private static final byte DEFAULT_BOTTOM_BORDER_COLOR_ALPHA = 0x26;
    private static final int SELECTED_INDICATOR_THICKNESS_DIPS = 4;
    private static final int DEFAULT_SELECTED_INDICATOR_COLOR = 0xFF33B5E5;
    private static final int DEFAULT_TITLE_COLOR_ALPHA = 0xA0;

    private static final int DEFAULT_DIVIDER_THICKNESS_DIPS = 1;
    private static final byte DEFAULT_DIVIDER_COLOR_ALPHA = 0x20;
    private static final float DEFAULT_DIVIDER_HEIGHT = 0.5f;

    private final int mBottomBorderThickness;
    private final Paint mBottomBoderPaint;
    private final int mDefaultBottomBorderColor;

    private final int mSelectedIndicatorThickness;
    private final Paint mSelectedIndicatorPaint;

    private final Paint mDividerPaint;
    private final float mDividerHeight;

    private int mSelectedPosition;
    private float mSelectionOffset;

    private SlidingTabLayout.TabColorizer mCustomTabColorizer;
    private final SimpleTabColorizer mDefaultTabColorizer;

    public SlidingTabStrip(Context context) {
        this(context, null);
    }

    public SlidingTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        final float density = getResources().getDisplayMetrics().density;

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorForeground, outValue, true);
        final int themeForgroundColor = outValue.data;

        mDefaultBottomBorderColor = setColorAlpha(themeForgroundColor,
                DEFAULT_BOTTOM_BORDER_COLOR_ALPHA);

        mDefaultTabColorizer = new SimpleTabColorizer();
        mDefaultTabColorizer.setIndicatorColors(DEFAULT_SELECTED_INDICATOR_COLOR);
        mDefaultTabColorizer.setDividerColors(setColorAlpha(themeForgroundColor,
                DEFAULT_DIVIDER_COLOR_ALPHA));

        mBottomBorderThickness = (int)(DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS * density);
        mBottomBoderPaint = new Paint();
        mBottomBoderPaint.setColor(mDefaultBottomBorderColor);

        mSelectedIndicatorThickness = (int) (SELECTED_INDICATOR_THICKNESS_DIPS * density);
        mSelectedIndicatorPaint = new Paint();

        mDividerHeight = DEFAULT_DIVIDER_HEIGHT;
        mDividerPaint = new Paint();
        mDividerPaint.setStrokeWidth((int)(DEFAULT_DIVIDER_THICKNESS_DIPS * density));
    }

    void setCustomTabColorizer(SlidingTabLayout.TabColorizer customTabColorizer) {
        mCustomTabColorizer = customTabColorizer;
        invalidate();
    }

    void setSelectedIndicatorColors(int... colors) {
        // Make sure that the custom colorizer is removed.
        mCustomTabColorizer = null;
        mDefaultTabColorizer.setIndicatorColors(colors);
        invalidate();
    }

    void setDividerColors(int... colors) {
        // Make sure that the custom colorizer is removed.
        mCustomTabColorizer = null;
        mDefaultTabColorizer.setDividerColors(colors);
        invalidate();
    }

    void onViewPagerPageChanged(int position, float positionOffset) {
        mSelectedPosition = position;
        mSelectionOffset = positionOffset;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int height = getHeight();
        final int childCount = getChildCount();
        final int dividerHeightPx = (int) (Math.min(Math.max(0f, mDividerHeight), 1f) * height);
        final SlidingTabLayout.TabColorizer tabColorizer = mCustomTabColorizer != null
                ? mCustomTabColorizer
                : mDefaultTabColorizer;

        // Thick colored underline below the current selection.
        if (childCount > 0) {
            View selectTitle = getChildAt(mSelectedPosition);
            int left = selectTitle.getLeft();
            int right = selectTitle.getRight();
            int color = tabColorizer.getIndicatorColor(mSelectedPosition);

            if (mSelectionOffset > 0f && mSelectedPosition < (childCount - 1)) {
                int nextColor = tabColorizer.getIndicatorColor(mSelectedPosition + 1);
                if (color != nextColor) {
                    color = blendColors(nextColor, color, mSelectionOffset);
                }

                //Draw the selection partway between the tabs
                View nextTitle = getChildAt(mSelectedPosition + 1);
                left = (int) (mSelectionOffset * nextTitle.getLeft() + (1.0f - mSelectionOffset) * left);
                right = (int) (mSelectionOffset * nextTitle.getRight() + (1.0f - mSelectionOffset) * right);
            }

            mSelectedIndicatorPaint.setColor(color);

            canvas.drawRect(left, height - mSelectedIndicatorThickness, right, height,
                    mSelectedIndicatorPaint);
        }

        // Thin underline along the entry bottom edge.
//        canvas.drawRect(0, height-mBottomBorderThickness, getWidth(), height, mBottomBoderPaint);

        // Vertical separators between the titles
//        int seperatorTop = (height - dividerHeightPx) /2;
//        for (int i = 0; i < childCount -1; i++) {
//            View child = getChildAt(i);
//            mDividerPaint.setColor(tabColorizer.getDividerColor(i));
//            canvas.drawLine(child.getRight(), seperatorTop, child.getRight(),
//                    seperatorTop + dividerHeightPx, mDividerPaint);
//        }

        //Change Tab text color
        for (int i = 0; i < childCount; i++) {
            int curColor = tabColorizer.getIndicatorColor(i);
            int textColor = Color.argb(DEFAULT_TITLE_COLOR_ALPHA, Color.red(curColor),
                    Color.green(curColor), Color.blue(curColor));
            View tabView = getChildAt(i);
            if (TextView.class.isInstance(tabView)) {
                TextView titleView = (TextView)tabView;

                if (i == mSelectedPosition && mSelectionOffset <= 0.5) {
                    titleView.setTextColor(tabColorizer.getIndicatorColor(i));
                } else if (i == mSelectedPosition + 1 && mSelectionOffset > 0.7 ) {
                    titleView.setTextColor(tabColorizer.getIndicatorColor(i));
                } else {
                    titleView.setTextColor(textColor);
                }
            }
        }
    }

    /**
     * Set the alpha value of the {@code color} to be the given {@code alpha} value
     */
    private static int setColorAlpha(int color, byte alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * Blend {@code color1} and {@code color2} using the given ratio.
     *
     * @param ratio of which to blend. 1.0 will return {@code color1}, 0.5 will give an even blend,
     *              0.0 will return {@code color2}.
     */
    private static int blendColors(int color1, int color2, float ratio) {
        final float inverseRatio = 1f - ratio;
        float r = Color.red(color1) * ratio + Color.red(color2) * inverseRatio;
        float g = Color.green(color1) * ratio + Color.green(color2) * inverseRatio;
        float b = Color.blue(color1) * ratio + Color.blue(color2) * inverseRatio;
        return Color.rgb((int)r, (int)g, (int)b);
    }

    private static class SimpleTabColorizer implements SlidingTabLayout.TabColorizer {
        private int[] mIndicatorColors;
        private int[] mDividerColors;

        @Override
        public int getIndicatorColor(int position) {
            return mIndicatorColors[position % mIndicatorColors.length];
        }

        @Override
        public int getDividerColor(int position) {
            return mDividerColors[position % mDividerColors.length];
        }

        void setIndicatorColors(int... colors) {
            mIndicatorColors = colors;
        }

        void setDividerColors(int... colors) {
            mDividerColors = colors;
        }
    }
}

