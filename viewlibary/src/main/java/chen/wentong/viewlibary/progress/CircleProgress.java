package chen.wentong.viewlibary.progress;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import chen.wentong.myandroidutils.SpUtil;
import chen.wentong.viewlibary.R;


/**
 * Created by ${wentong.chen} on 18/1/23.
 * 圆形进度条
 */

public class CircleProgress extends View {
    private final int DEFAULT_RADIUS = SpUtil.dp2px(getContext(), 30);
    private final int DEFAULT_PROGRESS_WIDTH = SpUtil.dp2px(getContext(), 5);
    private float mStartAngle = 0;             //起始绘制角度
    private float mSweepAngle = 0;               //结束绘制角度
    private float mFinishWidth = DEFAULT_PROGRESS_WIDTH;             //外层进度条宽度
    private float mUnfinishWidth = DEFAULT_PROGRESS_WIDTH;           //内层进度条宽度
    private int mFinishColor = Color.RED;               //外层进度条颜色
    private int mUnfinishColor = Color.GRAY;             //外层进度条颜色
    private int mRadius;                    //进度条半径
    private float mMaxProgress = 100;
    private float mCurrentProgress;
    private Paint mUnfinishPaint;
    private Paint mFinishPaint;
    private RectF mRectf;
    private boolean mShowAnim;
    private int mAnimDuration;
    private float mMinDrawAngle;
    private float mProgress = 60;
    private ProgressAnimatorListener mProgressAnimatorListener;
    private ValueAnimator mAnimator;


    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
        if (mShowAnim && mAnimDuration > 0) {
            startAnim(mAnimDuration, mProgress);
        } else {
            setProgress(mProgress);
        }
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        mStartAngle = ta.getFloat(R.styleable.CircleProgress_startAngle, mStartAngle);
        mSweepAngle = ta.getFloat(R.styleable.CircleProgress_sweepAngle, mSweepAngle);
        mMaxProgress = ta.getFloat(R.styleable.CircleProgress_maxProgress, mMaxProgress);
        mProgress = ta.getFloat(R.styleable.CircleProgress_progress, mProgress);
        mFinishColor = ta.getColor(R.styleable.CircleProgress_finishColor, mFinishColor);
        mUnfinishColor = ta.getColor(R.styleable.CircleProgress_unfinishColor, mUnfinishColor);
        mRadius = (int) ta.getDimension(R.styleable.CircleProgress_radius, mRadius);
        mFinishWidth = ta.getDimension(R.styleable.CircleProgress_finishWidth, mFinishWidth);
        mUnfinishWidth = ta.getDimension(R.styleable.CircleProgress_unfinishWidth, mUnfinishWidth);
        mShowAnim = ta.getBoolean(R.styleable.CircleProgress_showAnim, mShowAnim);
        mAnimDuration = ta.getInt(R.styleable.CircleProgress_animDuration, mAnimDuration);

        setMaxProgress(mMaxProgress);
        ta.recycle();
    }

    private void init() {
        mRectf = new RectF();
        mUnfinishPaint = new Paint();
        mUnfinishPaint.setAntiAlias(true);
        mUnfinishPaint.setStyle(Paint.Style.STROKE);
        mUnfinishPaint.setColor(mUnfinishColor);
        mUnfinishPaint.setStrokeWidth(mUnfinishWidth);

        mFinishPaint = new Paint();
        mFinishPaint.setAntiAlias(true);
        mFinishPaint.setStyle(Paint.Style.STROKE);
        mFinishPaint.setColor(mFinishColor);
        mFinishPaint.setStrokeWidth(mFinishWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getSize(widthMeasureSpec);
        int height = getSize(heightMeasureSpec);
        mRadius = Math.min(width, height);
        setMeasuredDimension(mRadius + getPaddingLeft() + getPaddingRight(),
                mRadius + getPaddingTop() + getPaddingBottom());
    }

    private int getSize(int measureSpec) {
        int result = 0;
        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = Math.min(size, DEFAULT_RADIUS);
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float distance = Math.max(mFinishWidth, mUnfinishWidth) / 2;
        mRectf.left = getPaddingLeft() + distance ;
        mRectf.top = getPaddingTop() + distance;
        mRectf.right = getPaddingLeft() + mRadius - distance;
        mRectf.bottom = getPaddingTop() + mRadius - distance;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mShowAnim && mAnimDuration > 0) {
            mSweepAngle = 360 * mCurrentProgress / mMaxProgress;
        } else {
            mSweepAngle = 360 * mProgress / mMaxProgress;
        }
        //绘制进度条前景
        canvas.drawArc(mRectf, mStartAngle, mSweepAngle, false, mFinishPaint);
        //绘制进度条后景
        canvas.drawArc(mRectf, mSweepAngle + mStartAngle, 360 - mSweepAngle, false, mUnfinishPaint);
    }

    public void startAnim(int duration, final float progress) {

        if (mAnimDuration > 0 || mShowAnim) {
            mCurrentProgress = 0;
            if (mAnimator == null) {
                mAnimator = ObjectAnimator.ofFloat(0, progress);
                mAnimator.setInterpolator(new LinearInterpolator());
                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float animatedValue = (float) animation.getAnimatedValue();
                        if (animatedValue == 0 || animatedValue >= mCurrentProgress + mMinDrawAngle
                                || animatedValue == progress) {
                            mCurrentProgress = animatedValue;
//                            LogUtils.d("mCurrentProgress = " + mCurrentProgress);
                            if (mProgressAnimatorListener != null) {
                                mProgressAnimatorListener.onProgressChange(mCurrentProgress);
                            }
                            postInvalidate();
                        }
                    }
                });
                mAnimator.setDuration(duration);
            } else {
                mAnimator.cancel();
            }
            mAnimator.start();
        }
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        postInvalidate();
    }

    public void setMaxProgress(float maxProgress) {
        this.mMaxProgress = maxProgress;
        mMinDrawAngle = mMaxProgress / 360;
    }

    public void cancelAnim() {
        if (mAnimator != null) {
            mAnimator.cancel();
            mProgressAnimatorListener = null;
            mAnimator = null;
        }
    }

    public void setProgressAnimatorListener(ProgressAnimatorListener listener) {
        this.mProgressAnimatorListener = listener;
    }

    public interface ProgressAnimatorListener {
        void onProgressChange(float currentProgress);
    }
}
