package extras.widget.hashtagedittext;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;

public class HashTagEditText extends EditText{
    private static final String TAG = "HashTagEditText";

    private TagList mTagList = new TagList();
    private TagClient mTagClient = new TagClient();
    private int mTagLayoutId = -1;
    private FrameLayout.LayoutParams mLayoutParams;

    private TextWatcher mTextWatcher = new TextWatcher() {
        private int mPreviousStart = 0;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(TAG, "onTextChanged " + start + " " + before + " " + count);
            if (count == 1 && getText().charAt(start) == ' ') {
                createTag(mPreviousStart, start);
            } else if (before > count) {
                checkAndRemoveTag(start + count);
            }
            mPreviousStart = start;
            mTagList.updateEntries(start, count - before);
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    public class TagClient {
        public void deleteTag(Tag tag) {
            removeTag(tag);
            getText().replace(tag.start(), tag.end(), "");
        }
    }

    public HashTagEditText(Context context) {
        super(context);
        initialize();
    }

    public HashTagEditText(Context context, AttributeSet attr) {
        super(context, attr);
        initialize();
    }

    public HashTagEditText(Context context, AttributeSet attr, int defStyleAttr) {
        super(context, attr, defStyleAttr);
        initialize();
    }

    @TargetApi(21)
    public HashTagEditText(Context context, AttributeSet attr, int defStyleAttr, int defStyleRes) {
        super(context, attr, defStyleAttr, defStyleRes);
        initialize();
    }

    public void setTagLayout(int layoutId) {
        mTagLayoutId = layoutId;
    }

    public FrameLayout.LayoutParams getDefaultParams() {
        return mLayoutParams;
    }

    private void initialize() {
        addTextChangedListener(mTextWatcher);
        mLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
    }

    private void createTag(int start, int end) {
        try {
            SpannableStringBuilder string = (SpannableStringBuilder) getText();
            Tag currentTag = new Tag(getContext(), mTagClient, start, end,
                    string.subSequence(start, end).toString(), mTagLayoutId);
            string.setSpan(currentTag.imageSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            setMovementMethod(LinkMovementMethod.getInstance());
            string.setSpan(currentTag.clickableSpan(), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTagList.insert(currentTag);
        } catch (IndexOutOfBoundsException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (NullPointerException ex) {
            Log.d(TAG, ex.getMessage());
        }
    }

    private void removeTag(Tag tag) {
        if (tag == null) {
            return;
        }
        SpannableStringBuilder ssb = (SpannableStringBuilder) getText();
        ssb.removeSpan(tag.imageSpan());
        ssb.removeSpan(tag.clickableSpan());
        mTagList.remove(tag);
    }

    private void checkAndRemoveTag(int end) {
        removeTag(mTagList.getContainingTag(end));
    }
}
