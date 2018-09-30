package com.wcl.scanview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
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

	int lineWidth = -1;
	float lineHeight = 0.3f;
	int linePaddingLeft;
	int linePaddingRight;
	int linePaddingTop;
	int linePaddingBottom;

	int frameWidth = -1;
	int frameHeight = -1;
	int framePaddingLeft;
	int framePaddingRight;
	int framePaddingTop;
	int framePaddingBottom;


	private ValueAnimator animatorLine;
	private Interpolator interpolator;

	private int duration = 2500;

	int playState = PLAY_STATE_PLAYING;
	private Paint paintDimBorder;

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

		framePaddingLeft = framePaddingRight = framePaddingTop = framePaddingBottom =
				typedArray.getDimensionPixelSize(R.styleable.ScanView_framePadding, 0);
		framePaddingLeft = typedArray.getDimensionPixelSize(R.styleable.ScanView_framePaddingLeft, framePaddingLeft);
		framePaddingRight = typedArray.getDimensionPixelSize(R.styleable.ScanView_framePaddingRight, framePaddingRight);
		framePaddingTop = typedArray.getDimensionPixelSize(R.styleable.ScanView_framePaddingTop, framePaddingTop);
		framePaddingBottom = typedArray.getDimensionPixelSize(R.styleable.ScanView_framePaddingBottom, framePaddingBottom);

		frameWidth = typedArray.getDimensionPixelSize(R.styleable.ScanView_frameWidth, frameWidth);
		frameHeight = typedArray.getDimensionPixelSize(R.styleable.ScanView_frameHeight, frameHeight);

		linePaddingLeft = linePaddingRight = linePaddingTop = linePaddingBottom =
				typedArray.getDimensionPixelSize(R.styleable.ScanView_linePadding, 0);
		linePaddingLeft = typedArray.getDimensionPixelSize(R.styleable.ScanView_linePaddingLeft, linePaddingLeft);
		linePaddingRight = typedArray.getDimensionPixelSize(R.styleable.ScanView_linePaddingRight, linePaddingRight);
		linePaddingBottom = typedArray.getDimensionPixelSize(R.styleable.ScanView_linePaddingBottom, linePaddingBottom);
		linePaddingTop = typedArray.getDimensionPixelSize(R.styleable.ScanView_linePaddingTop, linePaddingTop);

		lineWidth = typedArray.getDimensionPixelSize(R.styleable.ScanView_lineWidth, lineWidth);

		TypedValue typedValueLineHeight = typedArray.peekValue(R.styleable.ScanView_lineHeight);
		if(typedValueLineHeight.type == TypedValue.TYPE_FLOAT){
			lineHeight = typedValueLineHeight.getFloat();
		}
		else if(typedValueLineHeight.type == TypedValue.TYPE_DIMENSION){
			lineHeight = typedArray.getDimensionPixelSize(R.styleable.ScanView_lineHeight, 0);
		}

		duration = typedArray.getInt(R.styleable.ScanView_duration, duration);

		int dimBorderColor = typedArray.getColor(R.styleable.ScanView_dim_border, Color.TRANSPARENT);
		typedArray.recycle();

		paintDimBorder = new Paint();
		paintDimBorder.setColor(dimBorderColor);
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
	 * lineHeight 大于1.0：设定扫描线指定相对应的像素高度
	 * @param lineHeight
	 */
	public void setLineHeight(float lineHeight) {
		this.lineHeight = lineHeight;
		setLinePadding(linePaddingLeft, linePaddingTop, linePaddingRight, linePaddingBottom);
	}

	public void setLinePadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
		this.linePaddingLeft = paddingLeft;
		this.linePaddingRight = paddingRight;
		this.linePaddingBottom = paddingBottom;
		this.linePaddingTop = paddingTop;

		if(drawableLine != null) {
			int lineRealHeight;
			if(this.lineHeight > 1){
				lineRealHeight = (int) this.lineHeight;
			}
			else {
				if(frameHeight != -1){
					lineRealHeight = (int)(frameHeight * this.lineHeight);
				}
				else {
					lineRealHeight = (int) (getMeasuredHeight() * this.lineHeight);
				}
			}
			if(lineWidth != -1){
				linePaddingLeft = (getMeasuredWidth() - lineWidth) / 2;
				linePaddingRight = linePaddingLeft;
			}
			drawableLine.setBounds(linePaddingLeft, linePaddingTop,
					getMeasuredWidth() - linePaddingRight, lineRealHeight + linePaddingTop);

			if(animatorLine != null) {
				resetLineAnimatorValue();
			}
		}
	}

	public void setFramePadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
		this.framePaddingLeft = paddingLeft;
		this.framePaddingRight = paddingRight;
		this.framePaddingBottom = paddingBottom;
		this.framePaddingTop = paddingTop;
		if(drawableFrame != null) {
			if(frameWidth != -1){
				framePaddingLeft = (getMeasuredWidth() - frameWidth) / 2;
				framePaddingRight = framePaddingLeft;
			}
			if(frameHeight != -1){
				framePaddingTop = (getMeasuredHeight() - frameHeight) / 2;
				framePaddingBottom = framePaddingTop;
			}
			drawableFrame.setBounds(framePaddingLeft, framePaddingTop,
					getMeasuredWidth() - framePaddingRight, getMeasuredHeight() - framePaddingBottom);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setFramePadding(framePaddingLeft, framePaddingTop, framePaddingRight, framePaddingBottom);
		setLinePadding(linePaddingLeft, linePaddingTop, linePaddingRight, linePaddingBottom);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if(changed) {
			resetLineAnimatorValue();
			play();
		}
	}

	private void resetLineAnimatorValue() {
		int startPos = drawableLine.getBounds().top - drawableLine.getBounds().height();
		int endPos = getMeasuredHeight() - linePaddingBottom + drawableLine.getBounds().height();
		if(frameHeight != -1){
			startPos = drawableFrame.getBounds().top - drawableLine.getBounds().height();
			endPos = drawableFrame.getBounds().bottom;
		}

		if (animatorLine == null) {
			animatorLine = new ValueAnimator();
			animatorLine.setRepeatMode(ValueAnimator.RESTART);
			animatorLine.setRepeatCount(ValueAnimator.INFINITE);
			animatorLine.setInterpolator(interpolator);
			animatorLine.addUpdateListener(this);
			animatorLine.setDuration(duration);
		}

		animatorLine.setIntValues(startPos, endPos);
	}

	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		transLineDis = (int)animation.getAnimatedValue();
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

	private int transLineDis = 0;
	@Override
	protected void onDraw(Canvas canvas) {
		if(drawableLine == null || drawableFrame == null) return;

		canvas.save();

		Rect drawableLineBounds = drawableLine.getBounds();
		int clipLineTop = linePaddingTop;
		int clipLineBottom = getHeight() - linePaddingBottom;
		if(frameHeight != -1){
			clipLineTop = drawableFrame.getBounds().top;
			clipLineBottom = drawableFrame.getBounds().bottom;
		}
		canvas.clipRect(drawableLineBounds.left, clipLineTop, drawableLineBounds.right, clipLineBottom);
		canvas.translate(0, transLineDis);
		drawableLine.draw(canvas);

		canvas.restore();

		drawableFrame.draw(canvas);

		drawDimBorder(canvas);

		super.onDraw(canvas);
	}

	/**
	 * 绘制边框区域颜色
	 * @param canvas
	 */
	private void drawDimBorder(Canvas canvas) {
		Rect drawableFrameBounds = drawableFrame.getBounds();
		canvas.drawRect(0, 0, drawableFrameBounds.left, getHeight(), paintDimBorder);
		canvas.drawRect(drawableFrameBounds.left, 0, drawableFrameBounds.right, drawableFrameBounds.top, paintDimBorder);
		canvas.drawRect(drawableFrameBounds.right, 0, getWidth(), getHeight(), paintDimBorder);
		canvas.drawRect(drawableFrameBounds.left, drawableFrameBounds.bottom, drawableFrameBounds.right, getHeight(), paintDimBorder);
	}

}
