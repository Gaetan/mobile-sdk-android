package com.appnexus.opensdk;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.appnexus.opensdk.utils.ViewUtil;

public class CircularProgressBar extends ProgressBar{

    private static final double DEFAULT_STROKE_WIDTH = 2.5;
    public static final int TITLE_FONT_SIZE = 14;
    public static final String MONACO = "Monaco";
    public static final String CLOSE_X = "&#xd7;";
    public static final int CROSS_X_FONT_SIZE = 24;

    private String title = "";

	private int strokeWidth = 0;

	private final RectF circleBounds = new RectF();
	private final Paint progressColorPaint = new Paint();
	private final Paint backgroundColorPaint = new Paint();
	private final Paint titlePaint = new Paint();

	public CircularProgressBar(Context context) {
		super(context);
		initializeCountdownView(null, 0);
	}

	public CircularProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeCountdownView(attrs, 0);
	}

	public CircularProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeCountdownView(attrs, defStyle);
	}

	public void initializeCountdownView(AttributeSet attrs, int style){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        strokeWidth = ViewUtil.getSizeInDP(this.getContext(), DEFAULT_STROKE_WIDTH) + 1;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, style, 0);

		String color;
		Resources res = getResources();

		color = a.getString(R.styleable.CircularProgressBar_ccd_progressColor);
		if(color==null) {
            progressColorPaint.setColor(res.getColor(R.color.circular_progress_gray));
        } else {
            progressColorPaint.setColor(Color.parseColor(color));
        }

		color = a.getString(R.styleable.CircularProgressBar_ccd_backgroundColor);
		if(color==null) {
            backgroundColorPaint.setColor(res.getColor(R.color.circular_progress_white));
        } else {
            backgroundColorPaint.setColor(Color.parseColor(color));
        }

		color = a.getString(R.styleable.CircularProgressBar_ccd_titleColor);
		if(color==null) {
            titlePaint.setColor(res.getColor(R.color.circular_progress_white));
        } else {
            titlePaint.setColor(Color.parseColor(color));
        }

		a.recycle();

		progressColorPaint.setAntiAlias(true);
		progressColorPaint.setStyle(Style.STROKE);
		progressColorPaint.setStrokeWidth(strokeWidth);

		backgroundColorPaint.setAntiAlias(true);
		backgroundColorPaint.setStyle(Style.STROKE);
		backgroundColorPaint.setStrokeWidth(strokeWidth);

		titlePaint.setTextSize(TITLE_FONT_SIZE);
		titlePaint.setStyle(Style.FILL);
		titlePaint.setAntiAlias(true);
		titlePaint.setTypeface(Typeface.create(MONACO, Typeface.BOLD));
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		canvas.drawArc(circleBounds, 0, 360, false, backgroundColorPaint);
		float scale = getMax() > 0 ? (float)getProgress()/getMax() * 360: 0;
		canvas.drawArc(circleBounds, 270, -scale, false, progressColorPaint);

        if (!TextUtils.isEmpty(title)){
			int x =  (int)(getMeasuredWidth()/2 - titlePaint.measureText(title) / 2);
			int y = getMeasuredHeight()/2;

			float titleHeight = Math.abs(titlePaint.descent() + titlePaint.ascent());
            y += titleHeight/ 2;
            canvas.drawText(title, x, y, titlePaint);
		}		
		super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min + 2 * strokeWidth, min + 2 * strokeWidth);

        circleBounds.set(strokeWidth, strokeWidth, min + strokeWidth, min + strokeWidth);
    }

    @Override
    public synchronized void setProgress(int progress) {
		super.setProgress(progress);
		invalidate();
	}

	public synchronized void setTitle(String title){
        if(title.equalsIgnoreCase("X")){
            this.title = Html.fromHtml(CLOSE_X).toString();
            titlePaint.setTextSize(ViewUtil.getSizeInDP(this.getContext(), CROSS_X_FONT_SIZE));
        }else{
            this.title = title;
            titlePaint.setTextSize(ViewUtil.getSizeInDP(this.getContext(), TITLE_FONT_SIZE));
        }
		invalidate();
	}


	public String getTitle(){
		return title;
	}

}