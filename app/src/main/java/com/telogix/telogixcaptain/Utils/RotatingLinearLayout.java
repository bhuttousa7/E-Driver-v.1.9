package com.telogix.telogixcaptain.Utils;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

public class RotatingLinearLayout extends LinearLayout {

    private final int mDiagonal;
    private float mBearing;

    public RotatingLinearLayout(final Context pContext, final AttributeSet pAttrs) {
        super(pContext, pAttrs);
        final DisplayMetrics dm = pContext.getResources().getDisplayMetrics();
        mDiagonal = (int) Math.hypot(dm.widthPixels, dm.heightPixels);
    }

    public void setBearing(final float pBearing) {
        mBearing = pBearing;
    }

    @Override
    protected void dispatchDraw(final Canvas pCanvas) {
        pCanvas.rotate(-mBearing, getWidth() >> 1, getHeight() >> 1);
        super.dispatchDraw(pCanvas);
    }

    @Override
    protected void onMeasure(final int pWidthMeasureSpec,
                             final int pHeightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        super.onMeasure(MeasureSpec.makeMeasureSpec(mDiagonal, widthMode), MeasureSpec.makeMeasureSpec(mDiagonal, heightMode));
    }
}
