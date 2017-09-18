package com.squareplaces.squareplaces;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by WHITE ROSE on 6/15/2017.
 */

public class TypefaceTextView extends TextView {

    public static final int BOLD = 1;
    public static final int ITALIC = 2;

    public TypefaceTextView(Context context) {
        super(context);
    }

    public TypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.viroText);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.viroText_typeface:
                    setTextViewTypeface(a.getString(attr));
                    break;
                case R.styleable.viroText_style:
                    setTextViewStyle(a.getInt(i, 0));
                    break;
            }
        }
        a.recycle();
    }

    public TypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTextViewTypeface(String fontPath) {
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), fontPath));
    }

    public void setTextViewStyle(int style){
        switch (style){
            case BOLD:
                super.setText(Html.fromHtml(" <b> " + getText().toString() + " </b> "));
                break;
            case ITALIC:
                super.setText(Html.fromHtml(" <i> " + getText().toString() + " </i> "));
                break;
        }
    }
}
