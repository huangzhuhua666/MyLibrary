package com.example.hzh.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.hzh.ui.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Create by hzh on 2019/06/13.
 * <p>
 * 可设置前缀（字体大小、颜色）、后缀（字体大小、颜色、后缀的开始位置）的TextView
 * </p>
 * <p>
 * 使用setContent(String)代替setText(CharSequence)
 * </p>
 * <p>
 * 使用setContentSize(int)代替setTextSize(float)
 * </p>
 * <p>
 * 使用setContentColor(int)代替setTextColor(int)
 * </p>
 * <p>
 * xml中使用midContent代替text
 * </p>
 * <p>
 * xml中使用contentSize代替textSize
 * </p>
 * <p>
 * xml中使用contentColor代替textColor
 * </p>
 * <p>
 * xml中使用contentStyle代替textStyle
 * </p>
 * 同时设置suffix和suffixStartStr时，suffixStartStr无效
 */
public class PSFixTextView extends AppCompatTextView {

    /**
     * 正常字体（默认）
     */
    public static final int STYLE_NORMAL = 0;

    /**
     * 加粗字体
     */
    public static final int STYLE_BOLD = 1;

    /**
     * 斜体
     */
    public static final int STYLE_ITALIC = 2;

    /**
     * 加粗斜体
     */
    public static final int STYLE_BOLD_ITALIC = 3;

    @IntDef({STYLE_NORMAL, STYLE_BOLD, STYLE_ITALIC, STYLE_BOLD_ITALIC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TextStyle {
    }

    private SpannableStringBuilder mBuilder;

    private String mContent;
    private int mContentSize;
    @ColorInt
    private int mContentColor;
    private int mContentStyle;

    private String mPrefix;
    private int mPrefixSize;
    @ColorInt
    private int mPrefixColor;
    private int mPrefixStyle;
    private boolean isAddSpaceAfterPrefix;

    private String mSuffix;
    private String mSuffixStartStr;
    private int mSuffixSize;
    @ColorInt
    private int mSuffixColor;
    private int mSuffixStyle;
    private boolean isAddSpaceBeforeSuffix;

    public PSFixTextView(Context context) {
        this(context, null);
    }

    public PSFixTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PSFixTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PSFixTextView);

        // 内容
        mContent = ta.getString(R.styleable.PSFixTextView_midContent);
        // 内容字体大小
        mContentSize = (int) (ta.getDimension(R.styleable.PSFixTextView_contentSize, 16));
        // 内容字体颜色
        mContentColor = ta.getColor(R.styleable.PSFixTextView_contentColor,
                Color.parseColor("#666666"));
        // 内容字体style
        switch (ta.getInt(R.styleable.PSFixTextView_contentStyle, 0)) {
            case 0:
                mContentStyle = STYLE_NORMAL;
                break;
            case 1:
                mContentStyle = STYLE_BOLD;
                break;
            case 2:
                mContentStyle = STYLE_ITALIC;
                break;
            case 3:
                mContentStyle = STYLE_BOLD_ITALIC;
                break;
            default:
                mContentStyle = STYLE_NORMAL;
                break;
        }

        // 前缀
        mPrefix = ta.getString(R.styleable.PSFixTextView_prefixText);
        // 前缀字体大小
        mPrefixSize = (int) (ta.getDimension(R.styleable.PSFixTextView_prefixSize, mContentSize));
        // 前缀字体颜色
        mPrefixColor = ta.getColor(R.styleable.PSFixTextView_prefixColor, mContentColor);
        // 前缀字体style
        switch (ta.getInt(R.styleable.PSFixTextView_prefixStyle, mContentStyle)) {
            case 0:
                mPrefixStyle = STYLE_NORMAL;
                break;
            case 1:
                mPrefixStyle = STYLE_BOLD;
                break;
            case 2:
                mPrefixStyle = STYLE_ITALIC;
                break;
            case 3:
                mPrefixStyle = STYLE_BOLD_ITALIC;
                break;
            default:
                mPrefixStyle = STYLE_NORMAL;
                break;
        }
        // 前缀后是否追加一个空格
        isAddSpaceAfterPrefix = ta.getBoolean(R.styleable.PSFixTextView_addSpaceAfterPrefix,
                false);

        // 后缀
        mSuffix = ta.getString(R.styleable.PSFixTextView_suffixText);
        // 后缀开始的字符（从内容中选择）
        mSuffixStartStr = ta.getString(R.styleable.PSFixTextView_suffixStartStr);
        // 后缀字体大小
        mSuffixSize = (int) (ta.getDimension(R.styleable.PSFixTextView_suffixSize, mContentSize));
        // 后缀字体颜色
        mSuffixColor = ta.getColor(R.styleable.PSFixTextView_suffixColor, mContentColor);
        // 后缀字体style
        switch (ta.getInt(R.styleable.PSFixTextView_suffixStyle, mContentStyle)) {
            case 0:
                mSuffixStyle = STYLE_NORMAL;
                break;
            case 1:
                mSuffixStyle = STYLE_BOLD;
                break;
            case 2:
                mSuffixStyle = STYLE_ITALIC;
                break;
            case 3:
                mSuffixStyle = STYLE_BOLD_ITALIC;
                break;
            default:
                mSuffixStyle = STYLE_NORMAL;
                break;
        }
        // 后缀前是否追加一个空格
        isAddSpaceBeforeSuffix = ta.getBoolean(R.styleable.PSFixTextView_addSpaceBeforeSuffix,
                false);

        ta.recycle();

        updateUI();
    }

    /**
     * 更新UI
     */
    private void updateUI() {
        mBuilder = new SpannableStringBuilder();

        // 添加前缀
        if (!TextUtils.isEmpty(mPrefix)) {
            mBuilder.append(diySpannable(mPrefix, mPrefixSize, mPrefixColor, mPrefixStyle));
        }

        if (isAddSpaceAfterPrefix) {
            mBuilder.append(" ");// 前缀后追加一个空格
        }

        // 添加内容
        if (!TextUtils.isEmpty(mContent)) {
            String text;
            if (!TextUtils.isEmpty(mSuffixStartStr)) {
                // 设置了suffixStartStr，suffixStartStr前的字符才是内容
                int suffixStartIndex = mContent.lastIndexOf(mSuffixStartStr);
                text = mContent.substring(0, suffixStartIndex);
            } else {
                text = mContent;
            }
            mBuilder.append(diySpannable(text, mContentSize, mContentColor, mContentStyle));
        }

        // 后缀前追加一个空格
        if (isAddSpaceBeforeSuffix) {
            mBuilder.append(" ");
        }

        // 添加后缀
        if (!TextUtils.isEmpty(mSuffix)) {// 使用suffix
            mBuilder.append(diySpannable(mSuffix, mSuffixSize, mSuffixColor, mSuffixStyle));
        } else if (!TextUtils.isEmpty(mContent) && !TextUtils.isEmpty(mSuffixStartStr)) {
            // 使用suffixStartStr，内容后面的字符才是后缀
            int suffixStartIndex = mContent.lastIndexOf(mSuffixStartStr);
            String suf = mContent.substring(suffixStartIndex);
            mBuilder.append(diySpannable(suf, mSuffixSize, mSuffixColor, mSuffixStyle));
        }

        setText(mBuilder);
        mBuilder.clear();
    }

    /**
     * 设置指定字符风格、大小、颜色
     *
     * @param text  字符
     * @param size  字体
     * @param color 颜色
     * @param style 风格类型
     * @return SpannableString
     */
    private SpannableString diySpannable(String text, int size, int color, int style) {
        SpannableString span = new SpannableString(text);
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(size);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        StyleSpan styleSpan;
        switch (style) {
            case STYLE_NORMAL:
                styleSpan = new StyleSpan(Typeface.NORMAL);
                break;
            case STYLE_BOLD:
                styleSpan = new StyleSpan(Typeface.BOLD);
                break;
            case STYLE_ITALIC:
                styleSpan = new StyleSpan(Typeface.ITALIC);
                break;
            case STYLE_BOLD_ITALIC:
                styleSpan = new StyleSpan(Typeface.BOLD_ITALIC);
                break;
            default:
                styleSpan = new StyleSpan(Typeface.NORMAL);
                break;
        }
        span.setSpan(sizeSpan, 0, text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        span.setSpan(colorSpan, 0, text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        span.setSpan(styleSpan, 0, text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return span;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        mContent = content;
        updateUI();
    }

    /**
     * 设置内容字体大小
     *
     * @param size 字体大小
     */
    public void setContentSize(int size) {
        mContentSize = size;
        updateUI();
    }

    /**
     * 设置内容字体颜色
     *
     * @param color 字体颜色
     */
    public void setContentColor(int color) {
        mContentColor = color;
        updateUI();
    }

    /**
     * 设置内容字体颜色
     *
     * @param color 字体颜色
     */
    public void setContentColor(String color) {
        mContentColor = Color.parseColor(color);
        updateUI();
    }

    /**
     * 设置内容字体风格，默认{@link #STYLE_NORMAL}
     *
     * @param style 正常：{@link #STYLE_NORMAL}、加粗：{@link #STYLE_BOLD}、
     *              斜体：{@link #STYLE_ITALIC}、加粗斜体：{@link #STYLE_BOLD_ITALIC}
     */
    public void setContentStyle(@TextStyle int style) {
        mContentStyle = style;
        updateUI();
    }

    /**
     * 设置前缀
     *
     * @param prefix 前缀
     */
    public void setPrefix(String prefix) {
        mPrefix = prefix;
        updateUI();
    }

    /**
     * 设置前缀字体大小
     *
     * @param prefixSize 前缀字体大小
     */
    public void setPrefixSize(int prefixSize) {
        mPrefixSize = prefixSize;
        updateUI();
    }

    /**
     * 设置前缀字体颜色
     *
     * @param prefixColor 前缀字体颜色
     */
    public void setPrefixColor(int prefixColor) {
        mPrefixColor = prefixColor;
        updateUI();
    }

    /**
     * 设置前缀字体颜色
     *
     * @param prefixColor 前缀字体颜色
     */
    public void setPrefixColor(String prefixColor) {
        mPrefixColor = Color.parseColor(prefixColor);
        updateUI();
    }

    /**
     * 设置前缀字体风格
     *
     * @param prefixStyle 正常：{@link #STYLE_NORMAL}、加粗：{@link #STYLE_BOLD}、
     *                    斜体：{@link #STYLE_ITALIC}、加粗斜体：{@link #STYLE_BOLD_ITALIC}
     */
    public void setPrefixStyle(@TextStyle int prefixStyle) {
        mPrefixStyle = prefixStyle;
        updateUI();
    }

    /**
     * 设置前缀后追加一个空格
     *
     * @param isAdd true、false
     */
    public void setAddSpaceAfterPrefix(boolean isAdd) {
        isAddSpaceAfterPrefix = isAdd;
        updateUI();
    }

    /**
     * 设置后缀，设置这个属性后{@link #mSuffixStartStr}无效
     *
     * @param suffix 后缀
     */
    public void setSuffix(String suffix) {
        mSuffix = suffix;
        mSuffixStartStr = null;
        updateUI();
    }

    /**
     * 设置后缀开始的字符（从内容中截取），设置这个属性后{@link #mSuffix}无效
     *
     * @param suffixStartStr 后缀开始的字符
     */
    public void setSuffixStartStr(String suffixStartStr) {
        mSuffixStartStr = suffixStartStr;
        mSuffix = null;
        updateUI();
    }

    /**
     * 设置后缀字体大小
     *
     * @param suffixSize 后缀字体大小
     */
    public void setSuffixSize(int suffixSize) {
        mSuffixSize = suffixSize;
        updateUI();
    }

    /**
     * 设置后缀字体颜色
     *
     * @param suffixColor 后缀字体颜色
     */
    public void setSuffixColor(int suffixColor) {
        mSuffixColor = suffixColor;
        updateUI();
    }

    /**
     * 设置后缀字体颜色
     *
     * @param suffixColor 后缀字体颜色
     */
    public void setSuffixColor(String suffixColor) {
        mSuffixColor = Color.parseColor(suffixColor);
        updateUI();
    }

    /**
     * 设置后缀字体风格
     *
     * @param suffixStyle 正常：{@link #STYLE_NORMAL}、加粗：{@link #STYLE_BOLD}、
     *                    斜体：{@link #STYLE_ITALIC}、加粗斜体：{@link #STYLE_BOLD_ITALIC}
     */
    public void setSuffixStyle(@TextStyle int suffixStyle) {
        mSuffixStyle = suffixStyle;
        updateUI();
    }

    /**
     * 设置后缀前追加一个空格
     *
     * @param isAdd true、false
     */
    public void setAddSpaceBeforeSuffix(boolean isAdd) {
        isAddSpaceBeforeSuffix = isAdd;
        updateUI();
    }
}
