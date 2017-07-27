package com.wcl.scanview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * 扫描控件
 * @author 王春龙
 *2017年5月23日
 */
public class ScanView extends View implements ValueAnimator.AnimatorUpdateListener{

	private static final int PLAY_STATE_PLAYING = 1;
	private static final int PLAY_STATE_PAUSE = 2;

	Drawable drawableLine;
	Drawable drawableFrame;

	int linePaddingLeft;
	int linePaddingRight;
	int linePaddingTop;
	int linePaddingBottom;

	int framePaddingLeft;
	int framePaddingRight;
	int framePaddingTop;
	int framePaddingBottom;

	float lineHeight = 0.3f;

	private ValueAnimator animatorLine;
	private Interpolator interpolator;

	private int duration = 2500;

	int playState = PLAY_STATE_PLAYING;

	public ScanView(Context context) {
		super(context);
		init(context);
	}

	public ScanView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScanView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttrs(context, attrs);
	}

	private void initAttrs(Context context, AttributeSet attrs){
		init(context);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScanView);
		drawableLine = typedArray.getDrawable(R.styleable.ScanView_scanLineDrawable);
		drawableFrame = typedArray.getDrawable(R.styleable.ScanView_scanFrameDrawable);
		lineHeight = typedArray.getFloat(R.styleable.ScanView_lineHeight, lineHeight);

		framePaddingLeft = framePaddingRight = framePaddingTop = framePaddingBottom =
				typedArray.getDimensionPixelSize(R.styleable.ScanView_framePadding, 0);
		framePaddingLeft = typedArray.getDimensionPixelSize(R.styleable.ScanView_framePaddingLeft, framePaddingLeft);
		framePaddingRight = typedArray.getDimensionPixelSize(R.styleable.ScanView_framePaddingRight, framePaddingRight);
		framePaddingTop = typedArray.getDimensionPixelSize(R.styleable.ScanView_framePaddingTop, framePaddingTop);
		framePaddingBottom = typedArray.getDimensionPixelSize(R.styleable.ScanView_framePaddingBottom, framePaddingBottom);

		linePaddingLeft = linePaddingRight = linePaddingTop = linePaddingBottom =
				typedArray.getDimensionPixelSize(R.styleable.ScanView_linePadding, 0);
		linePaddingLeft = typedArray.getDimensionPixelSize(R.styleable.ScanView_linePaddingLeft, linePaddingLeft);
		linePaddingRight = typedArray.getDimensionPixelSize(R.styleable.ScanView_linePaddingRight, linePaddingRight);
		linePaddingBottom = typedArray.getDimensionPixelSize(R.styleable.ScanView_linePaddingBottom, linePaddingBottom);
		linePaddingTop = typedArray.getDimensionPixelSize(R.styleable.ScanView_linePaddingTop, linePaddingTop);

		duration = typedArray.getInt(R.styleable.ScanView_duration, duration);

		typedArray.recycle();
	}

	private void init(Context context){
		interpolator = new LinearInterpolator();
	}

	/**
	 * 设定扫描条图片资源
	 * @param drawable
	 */
	public void setDrawableLine(Drawable drawable){
		this.drawableLine = drawable;
		setLinePadding(linePaddingLeft, linePaddingTop, linePaddingRight, linePaddingBottom);
	}

	/**
	 * 设定扫描条图片资源
	 * @param resId
	 */
	public void setDrawableLine(int resId){
		setDrawableLine(getResources().getDrawable(resId));
	}

	/**
	 * 设定扫描框图片资源
	 * @param drawable
	 */
	public void setDrawableFrame(Drawable drawable){
		this.drawableFrame = drawable;
		setFramePadding(framePaddingLeft, framePaddingTop, framePaddingRight, framePaddingBottom);
	}

	/**
	 * 设定扫描框图片资源
	 * @param resId
	 */
	public void setDrawableFrame(int resId){
		setDrawableFrame(getResources().getDrawable(resId));
	}

	/**
	 * 扫描条完成一次扫描所用的时间
	 * @param duration
	 */
	public void setDuration(int duration) {
		this.duration = duration;
		if(animatorLine != null) {
			animatorLine.setDuration(duration);
		}
	}

	/**
	 * 设定扫描条插值计算器
	 * @param interpolator
	 */
	public void setInterpolator(Interpolator interpolator) {
		this.interpolator = interpolator;
		if(animatorLine != null) {
			animatorLine.setInterpolator(this.interpolator);
		}
	}

	/**
	 * 设定扫描条插值计算器
	 * @param interpolatorId
	 */
	public void setInterpolator(int interpolatorId) {
		setInterpolator( AnimationUtils.loadInterpolator(getContext(), interpolatorId));
	}

	/**
	 * lineHeight 0-1.0：设定扫描线占据的ScanView控件的高度比例
	 * lineHeight >1.0：设定扫描线指定相对应的像素高度
	 * @param lineHeight
	 */
	public void setLineHeight(float lineHeight) {
		this.lineHeight = lineHeight;
		setLinePadding(linePaddingLeft, linePaddingRight, linePaddingTop, linePaddingBottom);
	}

	public void setLinePadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
		this.linePaddingLeft = paddingLeft;
		this.linePaddingRight = paddingRight;
		this.linePaddingBottom = paddingBottom;
		this.linePaddingTop = paddingTop;

		if(drawableLine != null) {
			int lineHeight;
			if(this.lineHeight > 1){
				lineHeight = (int) this.lineHeight;
			}
			else {
				lineHeight = (int) (getMeasuredHeight() * this.lineHeight);
			}
			drawableLine.setBounds(new Rect(linePaddingLeft, linePaddingTop,
					getMeasuredWidth() - linePaddingRight, lineHeight + linePaddingTop));
			if(animatorLine != null){
				animatorLine.setIntValues(
						0, getMeasuredHeight() - linePaddingTop - linePaddingBottom + drawableLine.getBounds().height());
			}
		}
	}

	public void setFramePadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
		this.framePaddingLeft = paddingLeft;
		this.framePaddingRight = paddingRight;
		this.framePaddingBottom = paddingBottom;
		this.framePaddingTop = paddingTop;
		if(drawableFrame != null) {
			drawableFrame.setBounds(new Rect(framePaddingLeft, framePaddingTop,
					getMeasuredWidth() - framePaddingRight, getMeasuredHeight() - framePaddingBottom));
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setLinePadding(linePaddingLeft, linePaddingRight, linePaddingTop, linePaddingBottom);
		setFramePadding(framePaddingLeft, framePaddingTop, framePaddingRight, framePaddingBottom);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if(changed) {
			if (animatorLine != null) {
				animatorLine.cancel();
				animatorLine = null;
			}

			animatorLine = ValueAnimator.ofInt(
					0, getMeasuredHeight() - linePaddingTop - linePaddingBottom + drawableLine.getBounds().height())
					.setDuration(duration);
			animatorLine.setRepeatMode(ValueAnimator.RESTART);
			animatorLine.setRepeatCount(ValueAnimator.INFINITE);
			animatorLine.setInterpolator(interpolator);
			animatorLine.addUpdateListener(this);

			play();
		}
	}

	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		invalidate();
	}

	/**
	 * 启动扫描
	 */
	public void play() {
		startAnim();
		playState = PLAY_STATE_PLAYING;
	}

	private void startAnim() {
		if(animatorLine != null && !animatorLine.isRunning()){
			animatorLine.start();
		}
	}

	/**
	 * 停止扫描
	 */
	public void stop(){
		stopAnim();
		playState = PLAY_STATE_PAUSE;
	}

	private void stopAnim() {
		if(animatorLine != null){
			animatorLine.end();
		}
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		if(visibility == VISIBLE){
			if(playState == PLAY_STATE_PLAYING) {
				startAnim();
			}
		}
		else{
			stopAnim();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(drawableLine == null || drawableFrame == null) return;

		canvas.save();
		int transDis = 0;
		if(animatorLine != null){
			transDis = (int) animatorLine.getAnimatedValue();
		}
		Rect drawableLineBounds = drawableLine.getBounds();
		canvas.clipRect(drawableLineBounds.left, drawableLineBounds.top, drawableLineBounds.right, getHeight() - drawableLineBounds.top);
		canvas.translate(0, transDis - drawableLineBounds.height() + drawableLineBounds.top);
		drawableLine.draw(canvas);
		canvas.restore();

		drawableFrame.draw(canvas);

		super.onDraw(canvas);
	}

}
