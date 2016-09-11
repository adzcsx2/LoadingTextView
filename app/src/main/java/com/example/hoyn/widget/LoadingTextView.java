package com.example.hoyn.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.hoyn.loadingtextview.R;

/**
 * Created by Hoyn on 2016/9/11.
 */
public class LoadingTextView extends TextView {

    private int mTextOriginColor = 0xff333333;
    private int mTextChangeColor = 0xffffffff;
    private float mProgress = 0;
    private float mProgress2 = 0;
    private float mTextWidth = 0;
    private Rect mTextBound = new Rect();
    private Paint mPaint;
    private float mTextStartX;
    private float offset = 0.2f;
    private boolean isStart = false;
    public LoadingTextView(Context context) {
        super(context);
    }

    public LoadingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(getTextSize());
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.LoadingTextView);
        mTextChangeColor = ta.getColor(
                R.styleable.LoadingTextView_text_change_color, mTextChangeColor);
        offset = ta.getFloat(R.styleable.LoadingTextView_text_offset, offset);
        mTextOriginColor = getCurrentTextColor();

    }

    public LoadingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        invalidate();
    }
    public float getProgress2() {
        return mProgress;
    }

    public void setProgress2(float progress) {
        this.mProgress2 = progress;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //绘制文字并获取文字宽度
        mTextWidth = getPaint().measureText(getText().toString());
        mPaint.getTextBounds(getText().toString(), 0, getText().toString().length(), mTextBound);
//        mTextHeight = mTextBound.height();
        //得到控件的宽高
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
        //计算出文字第一个字相对控件的宽高(文字开始位置)
        mTextStartX = getMeasuredWidth() / 2 - mTextWidth / 2;
    }

    /**
     * 获取控件宽度
     * @param measureSpec
     * @return
     */
    private int measureWidth(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int val = MeasureSpec.getSize(measureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                // result = mTextBound.width();
                result = (int)mTextWidth;
                result += getPaddingLeft() + getPaddingRight();
                break;
        }
        result = mode == MeasureSpec.AT_MOST ? Math.min(result, val) : result;
        return result;
    }

    /**
     * 获取控件高度
     * @param measureSpec
     * @return
     */
    private int measureHeight(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int val = MeasureSpec.getSize(measureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = mTextBound.height();
                result += getPaddingTop() + getPaddingBottom();
                break;
        }
        result = mode == MeasureSpec.AT_MOST ? Math.min(result, val) : result;
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        //绘制原始的字
        drawOriginLeft(canvas);
        //绘制变色的字
        drawChangeLeft(canvas);
    }

    /**
     * 动画开始时，前端整体为变化色
     * 动画过程中，前端为基础色，中间为变化色
     * 动画结束时，后端整体为变化色
     * @param canvas
     */
    private void drawChangeLeft(Canvas canvas) {
        //绘制变色部分
        drawText_h(canvas, mTextChangeColor, (int) (mTextStartX + mProgress2 * mTextWidth),
                (int) (mTextStartX + mProgress * mTextWidth));
        //绘制前端原色
        drawText_h(canvas, mTextOriginColor, (int)mTextStartX,
                (int) (mTextStartX + mProgress2 * mTextWidth));
    }
    private void drawOriginLeft(Canvas canvas) {
        drawText_h(canvas, mTextOriginColor, (int)mTextStartX,
                (int) (mTextStartX + 1 * mTextWidth));
    }
    private void drawText_h(Canvas canvas, int color, int startX, int endX) {
        mPaint.setColor(color);
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(startX, 0, endX, getMeasuredHeight());// left, top,
        // right, bottom
        canvas.drawText(getText().toString(), mTextStartX,
                getMeasuredHeight() / 2
                        - ((mPaint.descent() + mPaint.ascent()) / 2), mPaint);
        canvas.restore();
    }
    public void start(int duration){
        if(!isStart){
            animStart(duration);
        }
        isStart = true;
    }

    ObjectAnimator anim,anim2;
    private void animStart(final int duration) {
        anim = ObjectAnimator.ofFloat(this, "progress", 0, 1);
        anim.setDuration(duration);
        anim.start();
        anim2 = ObjectAnimator.ofFloat(this, "progress2", 0, 1);
        anim2.setDuration(duration);
        anim2.setStartDelay((int)(duration*offset));
        anim2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mProgress = 0;
                mProgress2 = 0;
                if(isStart){
                    anim.start();
                    anim2.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim2.start();
    }

    public void stop(){
        isStart = false;
        if(anim.isStarted()){
            anim.cancel();
        }
        if(anim2.isStarted()){
            anim2.cancel();
        }
    }
}
