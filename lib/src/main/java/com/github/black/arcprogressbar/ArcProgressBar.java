package com.github.black.arcprogressbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;


public class ArcProgressBar extends ProgressBar {

	private float border = dpToPx(4);

	private int degree = 180;
	private int fingerSrc = -1;
	private float fingerWidth = 0;
	private float fingerHeight = 0;
	private float fingerMargin = -1;
	private float fingerRotate = 0;

	private float splitWidth = dpToPx(40);
	private float splitHeight = dpToPx(6);
	private int splitColor = getResources().getColor(R.color.white);
	private int splitProgressColor = getResources().getColor(R.color.light_green);

	private float edgeWidth = 0;
	private int edgeColor = getResources().getColor(R.color.white);
	private float edgeMargin = 0;

	private int rotate = 0;
	private int blockCount = 36;

	private float radius = 400;

	private float blockDegree;
	private Paint blockPaint;
	private Bitmap fingerBitmap;

	private RectF edgeRetF;
	private Paint edgePaint;
	private float edgeBuffer;

	private int dx;
	private int dy;

	private boolean isCircle = false;

	public ArcProgressBar(Context context) {
		super(context);
		setIndeterminate(false);
	}

	public ArcProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		setIndeterminate(false);
		initAttribute(context, attrs);
	}

	public ArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setIndeterminate(false);
		initAttribute(context, attrs);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public ArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		setIndeterminate(false);
		initAttribute(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.translate(dx, dy);
		canvas.rotate(180);//为了从左到右旋转,默认旋转180度
		canvas.rotate(rotate);
		canvas.save();

		if (edgeWidth > 0) {
			canvas.drawArc(edgeRetF, 0 - edgeBuffer, degree + edgeBuffer * 2, false, edgePaint);
		}

		int target = (int) (getProgress() * 1.0 / getMax() * blockCount);
		blockPaint.setColor(splitProgressColor);
		int i = 0;

		if (getProgress() > 0) {
			for (; i <= target; i++) {
				canvas.drawLine(radius - splitWidth, 0, radius, 0, blockPaint);
				canvas.rotate(blockDegree);
			}
		}
		blockPaint.setColor(splitColor);
		if (isCircle) {
			for (; i < blockCount; i++) {
				canvas.drawLine(radius - splitWidth, 0, radius, 0, blockPaint);
				canvas.rotate(blockDegree);
			}
		} else {
			for (; i <= blockCount; i++) {
				canvas.drawLine(radius - splitWidth, 0, radius, 0, blockPaint);
				canvas.rotate(blockDegree);
			}
		}

		canvas.restore();

		if (fingerSrc != -1) {
			float progressRotate = degree * getProgress() / getMax();
			canvas.rotate(progressRotate);
			canvas.drawBitmap(fingerBitmap, radius + fingerMargin + edgeMargin + edgeWidth, -(fingerBitmap.getHeight()) / 2, null);
		}

		int saveCount = canvas.getSaveCount();

		canvas.restoreToCount(saveCount);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int mode = MeasureSpec.getMode(widthMeasureSpec);
		if (mode == MeasureSpec.AT_MOST) {
			float width = (radius + border + edgeWidth + edgeMargin + fingerMargin + Math.max(fingerHeight, fingerWidth)) * 2;
			widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (width + getPaddingLeft() + getPaddingRight()), MeasureSpec.EXACTLY);
		}
		mode = MeasureSpec.getMode(heightMeasureSpec);
		if (mode == MeasureSpec.AT_MOST) {
			float height = (radius + border + edgeWidth + edgeMargin + fingerMargin + Math.max(fingerHeight, fingerWidth)) * 2;

			heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (height + getPaddingTop() + getPaddingBottom()), MeasureSpec.EXACTLY);
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		initProgressBar();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		Parcelable arg = super.onSaveInstanceState();

		bundle.putParcelable("arg", arg);

		bundle.putInt("degree", degree);
		bundle.putInt("fingerSrc", fingerSrc);
		bundle.putFloat("splitWidth", splitWidth);
		bundle.putFloat("splitHeight", splitHeight);
		bundle.putFloat("fingerWidth", fingerWidth);
		bundle.putFloat("fingerHeight", fingerHeight);
		bundle.putFloat("fingerMargin", fingerMargin);
		bundle.putFloat("fingerRotate", fingerRotate);
		bundle.putInt("splitColor", splitColor);
		bundle.putInt("splitProgressColor", splitProgressColor);
		bundle.putFloat("edgeWidth", edgeWidth);
		bundle.putInt("edgeColor", edgeColor);
		bundle.putFloat("edgeMargin", edgeMargin);
		bundle.putInt("rotate", rotate);
		bundle.putInt("blockCount", blockCount);
		bundle.putFloat("radius", radius);
		bundle.putBoolean("isCircle", isCircle);

		return bundle;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		Bundle bundle = (Bundle) state;
		Parcelable superState = ((Bundle) state).getParcelable("arg");
		super.onRestoreInstanceState(superState);

		degree = bundle.getInt("degree", 180);
		fingerSrc = bundle.getInt("fingerSrc", -1);
		fingerWidth = bundle.getFloat("fingerWidth", 0);
		fingerHeight = bundle.getFloat("fingerHeight", 0);
		fingerMargin = bundle.getFloat("fingerMargin", 0);
		fingerRotate = bundle.getFloat("fingerRotate", 0);
		splitWidth = bundle.getFloat("splitWidth", 40);
		splitHeight = bundle.getFloat("splitHeight", 6);
		splitColor = bundle.getInt("splitColor", getResources().getColor(R.color.white));
		splitProgressColor = bundle.getInt("splitProgressColor", getResources().getColor(R.color.light_green));
		edgeWidth = bundle.getFloat("edgeWidth", 0);
		edgeColor = bundle.getInt("edgeColor", getResources().getColor(R.color.light_green));
		edgeMargin = bundle.getFloat("edgeMargin", 0);
		rotate = bundle.getInt("rotate", 0);
		blockCount = bundle.getInt("blockCount", 36);
		radius = bundle.getFloat("radius", dpToPx(200));
		isCircle = bundle.getBoolean("isCircle");
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (fingerBitmap != null) {
			fingerBitmap.recycle();
		}
	}

	private void initAttribute(Context context, AttributeSet attrs) {
		if (null != attrs && attrs.getAttributeCount() > 0) {
			TypedArray mAttrs = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar);
			if (mAttrs != null) {
				for (int i = 0; i < mAttrs.getIndexCount(); i++) {
					int attr = mAttrs.getIndex(i);
					if (attr == R.styleable.ArcProgressBar_arc_progress_bar_degree) {
						degree = mAttrs.getInt(R.styleable.ArcProgressBar_arc_progress_bar_degree, 180);
						if (degree >= 360) {
							isCircle = true;
						}
					} else if (attr == R.styleable.ArcProgressBar_arc_progress_bar_finger) {
						fingerSrc = mAttrs.getResourceId(R.styleable.ArcProgressBar_arc_progress_bar_finger, -1);
					} else if (attr == R.styleable.ArcProgressBar_arc_progress_bar_finger_width) {
						fingerWidth = mAttrs.getDimension(R.styleable.ArcProgressBar_arc_progress_bar_finger_width, 0);
					} else if (attr == R.styleable.ArcProgressBar_arc_progress_bar_finger_height) {
						fingerHeight = mAttrs.getDimension(R.styleable.ArcProgressBar_arc_progress_bar_finger_height, 0);
					} else if (attr == R.styleable.ArcProgressBar_arc_progress_bar_finger_margin) {
						fingerMargin = mAttrs.getDimension(R.styleable.ArcProgressBar_arc_progress_bar_finger_margin, -1);
					} else if (attr == R.styleable.ArcProgressBar_arc_progress_bar_finger_rotate) {
						fingerRotate = mAttrs.getFloat(R.styleable.ArcProgressBar_arc_progress_bar_finger_rotate, 0);
					} else if (attr == R.styleable.ArcProgressBar_arc_progress_bar_split_width) {
						splitWidth = mAttrs.getDimension(R.styleable.ArcProgressBar_arc_progress_bar_split_width, dpToPx(40));
					} else if (attr == R.styleable.ArcProgressBar_arc_progress_bar_split_height) {
						splitHeight = mAttrs.getDimension(R.styleable.ArcProgressBar_arc_progress_bar_split_height, dpToPx(4));
					} else if (attr == R.styleable.ArcProgressBar_arc_progress_bar_split_color) {
						splitColor = mAttrs.getColor(R.styleable.ArcProgressBar_arc_progress_bar_split_color, getResources().getColor(R.color.white));
					} else if (attr == R.styleable.ArcProgressBar_arc_progress_bar_split_progress_color) {
						splitProgressColor = mAttrs.getColor(R.styleable.ArcProgressBar_arc_progress_bar_split_progress_color, getResources().getColor(R.color
								.light_green));
					} else if (attr == R.styleable.ArcProgressBar_arc_progress_bar_edge_width) {
						edgeWidth = mAttrs.getDimension(R.styleable.ArcProgressBar_arc_progress_bar_edge_width, 0);
					} else if (attr == R.styleable.ArcProgressBar_arc_progress_bar_edge_color) {
						edgeColor = mAttrs.getColor(R.styleable.ArcProgressBar_arc_progress_bar_edge_color, getResources().getColor(R.color.white));
					} else if (attr == R.styleable.ArcProgressBar_arc_progress_bar_edge_margin) {
						edgeMargin = mAttrs.getDimension(R.styleable.ArcProgressBar_arc_progress_bar_edge_margin, dpToPx(20));
					} else if (attr == R.styleable.ArcProgressBar_arc_progress_bar_rotate) {
						rotate = mAttrs.getInt(R.styleable.ArcProgressBar_arc_progress_bar_rotate, 0);
					} else if (attr == R.styleable.ArcProgressBar_arc_progress_bar_block_count) {
						blockCount = mAttrs.getInt(R.styleable.ArcProgressBar_arc_progress_bar_block_count, 36);
					} else if (attr == R.styleable.ArcProgressBar_arc_progress_bar_radius) {
						radius = mAttrs.getDimension(R.styleable.ArcProgressBar_arc_progress_bar_radius, dpToPx(200));
					}
				}
				mAttrs.recycle();
			}
		}
	}

	private void initProgressBar() {
		dx = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
		dy = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop();


		blockDegree = (float) (degree * 1.0 / (blockCount));
		blockPaint = new Paint();
		blockPaint.setAntiAlias(true);
		blockPaint.setStrokeWidth(splitHeight);

		if (fingerBitmap != null) {
			fingerBitmap.recycle();
		}

		if (fingerSrc != -1) {

			Bitmap bmp = BitmapFactory.decodeResource(getResources(), fingerSrc);

			Matrix matrix = new Matrix();

			float widthRatio = 1;
			float heightRatio = 1;
			if (fingerWidth > 0) {
				widthRatio = fingerWidth / bmp.getWidth();
			} else {
				fingerWidth = bmp.getWidth();
			}

			if (fingerHeight > 0) {
				heightRatio = fingerHeight / bmp.getHeight();
			} else {
				fingerHeight = bmp.getHeight();
			}

			if (fingerMargin < 0) {
				fingerMargin = dpToPx(4);
			}

			matrix.setScale(widthRatio, heightRatio);
			matrix.preRotate(fingerRotate);

			fingerBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
			if (fingerBitmap != bmp) {
				bmp.recycle();
			}
		}

		if (edgeWidth > 0) {
			edgePaint = new Paint();
			edgePaint.setStrokeWidth(edgeWidth);
			edgePaint.setColor(edgeColor);
			edgePaint.setAntiAlias(true);
			edgePaint.setStyle(Paint.Style.STROKE);

			edgeRetF = new RectF(-radius - edgeMargin, -radius - edgeMargin, radius + edgeMargin, radius + edgeMargin);

			edgeBuffer = (float) Math.toDegrees(Math.asin(splitHeight / 2 / radius));
			edgeBuffer = (float) Math.toDegrees(Math.atan(splitHeight / 2 / radius));
		}

	}

	private float dpToPx(float dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
	}
}
