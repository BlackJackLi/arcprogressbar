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
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;


public class ArcProgressBar extends ProgressBar {

	private final String TAG = getClass().getSimpleName();

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
	private Paint splitPaint;
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
		splitPaint.setColor(splitProgressColor);
		int i = 0;

		if (getProgress() > 0) {
			for (; i <= target; i++) {
				canvas.drawLine(radius - splitWidth, 0, radius, 0, splitPaint);
				canvas.rotate(blockDegree);
			}
		}
		splitPaint.setColor(splitColor);
		if (isCircle) {
			for (; i < blockCount; i++) {
				canvas.drawLine(radius - splitWidth, 0, radius, 0, splitPaint);
				canvas.rotate(blockDegree);
			}
		} else {
			for (; i <= blockCount; i++) {
				canvas.drawLine(radius - splitWidth, 0, radius, 0, splitPaint);
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

		initCenter();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable parcelable = super.onSaveInstanceState();
		SavedState ss = new SavedState(parcelable);
		ss.border = border;
		ss.degree = degree;
		ss.fingerSrc = fingerSrc;
		ss.fingerWidth = fingerWidth;
		ss.fingerHeight = fingerHeight;
		ss.fingerMargin = fingerMargin;
		ss.fingerRotate = fingerRotate;
		ss.splitWidth = splitWidth;
		ss.splitHeight = splitHeight;
		ss.splitColor = splitColor;
		ss.splitProgressColor = splitProgressColor;
		ss.edgeWidth = edgeWidth;
		ss.edgeColor = edgeColor;
		ss.edgeMargin = edgeMargin;
		ss.rotate = rotate;
		ss.blockCount = blockCount;
		ss.radius = radius;
		return ss;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());
		border = ss.border;
		degree = ss.degree;
		fingerSrc = ss.fingerSrc;
		fingerWidth = ss.fingerWidth;
		fingerHeight = ss.fingerHeight;
		fingerMargin = ss.fingerMargin;
		fingerRotate = ss.fingerRotate;
		splitWidth = ss.splitWidth;
		splitHeight = ss.splitHeight;
		splitColor = ss.splitColor;
		splitProgressColor = ss.splitProgressColor;
		edgeWidth = ss.edgeWidth;
		edgeColor = ss.edgeColor;
		edgeMargin = ss.edgeMargin;
		rotate = ss.rotate;
		blockCount = ss.blockCount;
		radius = ss.radius;
		initBlock();
		initSplit();
		initFinger();
		initEdge();
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
		initBlock();
		initSplit();
		initFinger();
		initEdge();
	}

	private void initCenter() {
		dx = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
		dy = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop();
	}

	private void initBlock() {
		if (degree >= 360) {
			isCircle = true;
		}
		blockDegree = (float) (degree * 1.0 / (blockCount));
	}

	private void initSplit() {
		splitPaint = new Paint();
		splitPaint.setAntiAlias(true);
		splitPaint.setStrokeWidth(splitHeight);
	}

	private void initFinger() {
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
	}

	private void initEdge() {
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

	public int getDegree() {
		return degree;
	}

	public int getFingerSrc() {
		return fingerSrc;
	}

	public float getFingerWidth() {
		return fingerWidth;
	}

	public float getFingerHeight() {
		return fingerHeight;
	}


	public float getFingerMargin() {
		return fingerMargin;
	}

	public float getFingerRotate() {
		return fingerRotate;
	}

	public float getSplitWidth() {
		return splitWidth;
	}

	public float getSplitHeight() {
		return splitHeight;
	}

	public int getSplitColor() {
		return splitColor;
	}

	public void setSplitColor(int splitColor) {
		this.splitColor = splitColor;
		invalidate();
	}

	public int getSplitProgressColor() {
		return splitProgressColor;
	}

	public void setSplitProgressColor(int splitProgressColor) {
		this.splitProgressColor = splitProgressColor;
		invalidate();
	}

	public float getEdgeWidth() {
		return edgeWidth;
	}

	public int getEdgeColor() {
		return edgeColor;
	}

	public void setEdgeColor(int edgeColor) {
		this.edgeColor = edgeColor;
		if (edgePaint == null) {
			initEdge();
		}else {
			edgePaint.setColor(edgeColor);
		}
		invalidate();
	}

	public float getEdgeMargin() {
		return edgeMargin;
	}

	public int getRotate() {
		return rotate;
	}

	public void setRotate(int rotate) {
		this.rotate = rotate;
		invalidate();
	}

	public int getBlockCount() {
		return blockCount;
	}

	public float getRadius() {
		return radius;
	}

	public boolean isCircle() {
		return isCircle;
	}

	private float dpToPx(float dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
	}

	private static class SavedState extends BaseSavedState {

		private float border;
		private int degree;
		private int fingerSrc;
		private float fingerWidth;
		private float fingerHeight;
		private float fingerMargin;
		private float fingerRotate;
		private float splitWidth;
		private float splitHeight;
		private int splitColor;
		private int splitProgressColor;
		private float edgeWidth;
		private int edgeColor;
		private float edgeMargin;
		private int rotate;
		private int blockCount;
		private float radius;


		/**
		 * Constructor called from {@link #CREATOR}
		 */
		public SavedState(Parcel source) {
			super(source);
			readFromParcel(source);
		}

		/**
		 * Constructor called from {@link ArcProgressBar#onSaveInstanceState()}
		 */
		public SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeFloat(border);
			out.writeInt(degree);
			out.writeInt(fingerSrc);
			out.writeFloat(fingerWidth);
			out.writeFloat(fingerHeight);
			out.writeFloat(fingerMargin);
			out.writeFloat(fingerRotate);
			out.writeFloat(splitWidth);
			out.writeFloat(splitHeight);
			out.writeInt(splitColor);
			out.writeInt(splitProgressColor);
			out.writeFloat(edgeWidth);
			out.writeInt(edgeColor);
			out.writeFloat(edgeMargin);
			out.writeInt(rotate);
			out.writeInt(blockCount);
			out.writeFloat(radius);
		}

		private void readFromParcel(Parcel in) {
			border = in.readFloat();
			degree = in.readInt();
			fingerSrc = in.readInt();
			fingerWidth = in.readFloat();
			fingerHeight = in.readFloat();
			fingerMargin = in.readFloat();
			fingerRotate = in.readFloat();
			splitWidth = in.readFloat();
			splitHeight = in.readFloat();
			splitColor = in.readInt();
			splitProgressColor = in.readInt();
			edgeWidth = in.readFloat();
			edgeColor = in.readInt();
			edgeMargin = in.readFloat();
			rotate = in.readInt();
			blockCount = in.readInt();
			radius = in.readFloat();
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};

		@Override
		public String toString() {
			return "SavedState{" + "border=" + border + ", degree=" + degree + ", fingerSrc=" + fingerSrc + ", fingerWidth=" + fingerWidth + ", fingerHeight="
					+ fingerHeight + ", fingerMargin=" + fingerMargin + ", fingerRotate=" + fingerRotate + ", splitWidth=" + splitWidth + ", splitHeight=" +
					splitHeight + ", splitColor=" + splitColor + ", splitProgressColor=" + splitProgressColor + ", edgeWidth=" + edgeWidth + ", edgeColor=" +
					edgeColor + ", edgeMargin=" + edgeMargin + ", rotate=" + rotate + ", blockCount=" + blockCount + ", radius=" + radius + '}';
		}
	}
}
