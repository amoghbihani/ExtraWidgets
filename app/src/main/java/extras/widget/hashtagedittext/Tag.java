package extras.widget.hashtagedittext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

class Tag {
    private int mStart;
    private int mEnd;
    private Context mContext;
    private HashTagEditText.TagClient mClient;
    private ImageSpan mImageSpan;
    private ClickableSpan mClickSpan;
    private int mLayoutId;

    Tag(Context context, HashTagEditText.TagClient client,
            int start, int end, String string, int layoutId) {
        mContext = context;
        mClient = client;
        mStart = start;
        mEnd = end;
        mLayoutId = layoutId;
        createStyleSpan(string);
    }

    public int start() {
        return mStart;
    }

    public int end() {
        return mEnd;
    }

    public int length() {
        return mEnd - mStart + 1;
    }

    public ImageSpan imageSpan() {
        return mImageSpan;
    }

    public ClickableSpan clickableSpan() {
        return mClickSpan;
    }

    public void updateMarkers(int length) {
        mStart += length;
        mEnd += length;
    }

    private void createStyleSpan(String string) {
        TextView textView;
        if (mLayoutId != -1) {
            textView = (TextView) View.inflate(mContext, mLayoutId, null);
        } else {
            textView = getTextView();
        }
        textView.setText(string);
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(spec, spec);
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        textView.setDrawingCacheEnabled(true);
        Bitmap viewBmp = textView.getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
        textView.destroyDrawingCache();
        mImageSpan = new ImageSpan(mContext, viewBmp, DynamicDrawableSpan.ALIGN_BOTTOM);

        mClickSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (mClient != null) {
                    mClient.deleteTag(Tag.this);
                }
            }
        };
    }

    private TextView getTextView() {
        TextView textView = new TextView(mContext);
        textView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        textView.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, android.R.drawable.presence_offline, 0);
        textView.setTextColor(Color.BLACK);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setBackgroundColor(Color.parseColor("#FAFAFA"));
        textView.setPadding(4, 4, 4, 4);

        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(2, Color.parseColor("#B1BCBE"));
        gd.setCornerRadius(4);
        textView.setBackground(gd);
        return textView;
    }
}
