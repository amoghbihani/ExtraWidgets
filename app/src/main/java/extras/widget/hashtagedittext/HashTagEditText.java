package extras.widget.hashtagedittext;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
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
    private static final String BLOCKED_CHAR_SET = "~^|$%&*!-@()[]{}:?'\",;+/=\\<>`£¥€¢•©"
            + "\u0394\u20AC\u2122\251\256\u220F\u2293\u22F9\u03A0\u04B0\u2022"
            + "\u221A\367\327\266\u2206\243\242\245\260\u2105\u263A\u2665";

    private TagList mTagList = new TagList();
    private TagClient mTagClient = new TagClient();
    private int mTagLayoutId = -1;
    private FrameLayout.LayoutParams mLayoutParams;

    private InputFilter mInputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            if (end > start) {
                for (int index = start; index < end; index++) {
                    if (BLOCKED_CHAR_SET.contains(String.valueOf(source.charAt(index)))) {
                        return "";
                    }
                }
            }
            return null;
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(TAG, "onTextChanged " + start + " " + before + " " + count);
            if (count == 1 && getText().charAt(start) == ' ') {
                createTag(getWordStart(start), start);
            } else if (before > count) {
                checkAndRemoveTag(start + count);
            }
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

    public void setTagText(CharSequence charSequence) {
        setText(charSequence);
        createTags();
    }

    public void setTagLayout(int layoutId) {
        mTagLayoutId = layoutId;
    }

    public FrameLayout.LayoutParams getDefaultParams() {
        return mLayoutParams;
    }

    private void initialize() {
        addTextChangedListener(mTextWatcher);
        setFilters(new InputFilter[] {mInputFilter});
        mLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
    }

    private int getWordStart(int pos) {
        int start = mTagList.getPreviousTagEnd(pos);
        for (; start < getText().length(); ++start) {
            if (getText().charAt(start) != ' ') {
                break;
            }
        }
        return start;
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
            Log.d(TAG, "" + ex.getMessage());
        } catch (NullPointerException ex) {
            Log.d(TAG, "" + ex.getMessage());
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

    private void createTags() {
        mTagList.clear();
        CharSequence text = getText();
        for (int i = 0; i < length(); ++i) {
            while (i < length() && text.charAt(i) == ' ') {
                ++i;
            }
            int start = i;
            while (i < length() && text.charAt(i) != ' ') {
                ++i;
            }
            int end = i;
            createTag(start, end);
        }
    }
}
