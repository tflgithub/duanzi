package com.anna.duanzi.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.anna.duanzi.R;
import com.anna.duanzi.utils.ResolutionUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tfl on 2017/8/31.
 * 标签
 */

public class TagView extends RelativeLayout {

    public static final float DEFAULT_LINE_MARGIN = 5;
    public static final float DEFAULT_TAG_MARGIN = 5;
    public static final float DEFAULT_TAG_TEXT_PADDING_LEFT = 8;
    public static final float DEFAULT_TAG_TEXT_PADDING_TOP = 5;
    public static final float DEFAULT_TAG_TEXT_PADDING_RIGHT = 8;
    public static final float DEFAULT_TAG_TEXT_PADDING_BOTTOM = 5;
    public static final float LAYOUT_WIDTH_OFFSET = 2;

    public static final float DEFAULT_TAG_TEXT_SIZE = 14f;
    public static final float DEFAULT_TAG_DELETE_INDICATOR_SIZE = 14f;
    public static final float DEFAULT_TAG_LAYOUT_BORDER_SIZE = 0f;
    public static final float DEFAULT_TAG_RADIUS = 100;
    public static final int DEFAULT_TAG_LAYOUT_COLOR = Color.parseColor("#ffffff");
    public static final int DEFAULT_TAG_LAYOUT_COLOR_PRESS = Color.parseColor("#88363636");
    public static final int DEFAULT_TAG_TEXT_COLOR = Color.parseColor("#FF6600");
    public static final int DEFAULT_TAG_DELETE_INDICATOR_COLOR = Color.parseColor("#88363636");
    public static final int DEFAULT_TAG_LAYOUT_BORDER_COLOR = Color.parseColor("#88363636");
    public static final String DEFAULT_TAG_DELETE_ICON = "×";
    public static final boolean DEFAULT_TAG_IS_DELETABLE = false;

    private int mWidth;
    private int lineMargin;
    private int tagMargin;
    private int textPaddingLeft;
    private int textPaddingRight;
    private int textPaddingTop;
    private int texPaddingBottom;
    private List<Tag> mTags = new ArrayList<>();
    private LayoutInflater mInflater;

    private OnTagClickListener mClickListener;
    private OnTagDeleteListener mDeleteListener;

    public TagView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TagView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.TagView, defStyle, defStyleRes);
        this.lineMargin = (int) typeArray.getDimension(R.styleable.TagView_lineMargin, ResolutionUtil.dpToPx(this.getContext(), DEFAULT_LINE_MARGIN));
        this.tagMargin = (int) typeArray.getDimension(R.styleable.TagView_tagMargin, ResolutionUtil.dpToPx(this.getContext(), DEFAULT_TAG_MARGIN));
        this.textPaddingLeft = (int) typeArray.getDimension(R.styleable.TagView_textPaddingLeft, ResolutionUtil.dpToPx(this.getContext(), DEFAULT_TAG_TEXT_PADDING_LEFT));
        this.textPaddingRight = (int) typeArray.getDimension(R.styleable.TagView_textPaddingRight, ResolutionUtil.dpToPx(this.getContext(),DEFAULT_TAG_TEXT_PADDING_RIGHT));
        this.textPaddingTop = (int) typeArray.getDimension(R.styleable.TagView_textPaddingTop, ResolutionUtil.dpToPx(this.getContext(),DEFAULT_TAG_TEXT_PADDING_TOP));
        this.texPaddingBottom = (int) typeArray.getDimension(R.styleable.TagView_textPaddingBottom, ResolutionUtil.dpToPx(this.getContext(),DEFAULT_TAG_TEXT_PADDING_BOTTOM));
        typeArray.recycle();
        mWidth = ResolutionUtil.getScreenWidth(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth=w;
    }

    private void drawTags() {
        if (getVisibility() != View.VISIBLE) return;
        // clear all tag
        removeAllViews();
        // layout padding left & layout padding right
        float total = getPaddingLeft() + getPaddingRight();
        int listIndex = 1;// List Index
        int index_bottom = 1;// The Tag to add below
        int index_header = 1;// The header tag of this line
        Tag tag_pre = null;
        for (Tag item : mTags) {
            final int position = listIndex - 1;
            final Tag tag = item;
            // inflate tag layout
            View tagLayout = mInflater.inflate(R.layout.tagview_item, null);
            tagLayout.setId(listIndex);
            tagLayout.setBackgroundDrawable(getSelector(tag));
            // tag text
            TextView tagView = (TextView) tagLayout.findViewById(R.id.tv_tag_item_contain);
            tagView.setText(tag.text);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tagView.getLayoutParams();
            params.setMargins(textPaddingLeft, textPaddingTop, textPaddingRight, texPaddingBottom);
            tagView.setLayoutParams(params);
            tagView.setTextColor(tag.tagTextColor);
            tagView.setTextSize(TypedValue.COMPLEX_UNIT_SP, tag.tagTextSize);
            tagLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onTagClick(position, tag);
                    }
                }
            });
            // calculate　of tag layout width
            float tagWidth = tagView.getPaint().measureText(tag.text) + textPaddingLeft + textPaddingRight;
            // tagView padding (left & right)
            // deletable text
            TextView deletableView = (TextView) tagLayout.findViewById(R.id.tv_tag_item_delete);
            if (tag.isDeletable) {
                deletableView.setVisibility(View.VISIBLE);
                deletableView.setText(tag.deleteIcon);
                int offset = ResolutionUtil.dpToPx(getContext(), 2f);
                deletableView.setPadding(offset, textPaddingTop, textPaddingRight + offset, texPaddingBottom);
                /*params = (LinearLayout.LayoutParams) deletableView.getLayoutParams();
				params.setMargins(offset, textPaddingTop, textPaddingRight+offset, texPaddingBottom);
				deletableView.setLayoutParams(params);*/
                deletableView.setTextColor(tag.deleteIndicatorColor);
                deletableView.setTextSize(TypedValue.COMPLEX_UNIT_SP, tag.deleteIndicatorSize);
                deletableView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TagView.this.remove(position);
                        if (mDeleteListener != null) {
                            mDeleteListener.onTagDeleted(position, tag);
                        }
                    }
                });
                tagWidth += deletableView.getPaint().measureText(tag.deleteIcon) + deletableView.getPaddingLeft() + deletableView.getPaddingRight();
            } else {
                deletableView.setVisibility(View.GONE);
            }
            LayoutParams tagParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //add margin of each line
            tagParams.bottomMargin = lineMargin;
            if (mWidth <= total + tagMargin + tagWidth + ResolutionUtil.dpToPx(this.getContext(), LAYOUT_WIDTH_OFFSET)) {
                //need to add in new line
                tagParams.addRule(RelativeLayout.BELOW, index_bottom);
                total = getPaddingLeft() + getPaddingRight();
                index_bottom = listIndex;
                index_header = listIndex;
            } else {
                //no need to new line
                tagParams.addRule(RelativeLayout.ALIGN_TOP, index_header);
                //not header of the line
                if (listIndex != index_header) {
                    tagParams.addRule(RelativeLayout.RIGHT_OF, listIndex - 1);
                    tagParams.leftMargin = tagMargin;
                    total += tagMargin;
                    if (tag_pre.tagTextSize < tag.tagTextSize) {
                        index_bottom = listIndex;
                    }
                }
            }
            total += tagWidth;
            addView(tagLayout, tagParams);
            tag_pre = tag;
            listIndex++;
        }
    }


    private Drawable getSelector(Tag tag) {
        if (tag.background != null) return tag.background;
        StateListDrawable states = new StateListDrawable();
        GradientDrawable gd_normal = new GradientDrawable();
        gd_normal.setColor(tag.layoutColor);
        gd_normal.setCornerRadius(tag.radius);
        if (tag.layoutBorderSize > 0) {
            gd_normal.setStroke(ResolutionUtil.dpToPx(getContext(), tag.layoutBorderSize), tag.layoutBorderColor);
        }
        GradientDrawable gd_press = new GradientDrawable();
        gd_press.setColor(tag.layoutColorPress);
        gd_press.setCornerRadius(tag.radius);
        states.addState(new int[]{android.R.attr.state_pressed}, gd_press);
        //must add state_pressed first，or state_pressed will not take effect
        states.addState(new int[]{}, gd_normal);
        return states;
    }

    public static class Tag {
        public int id;
        public String text;
        public int tagTextColor;
        public float tagTextSize;
        public int layoutColor;
        public int layoutColorPress;
        public boolean isDeletable;
        public int deleteIndicatorColor;
        public float deleteIndicatorSize;
        public float radius;
        public String deleteIcon;
        public float layoutBorderSize;
        public int layoutBorderColor;
        public Drawable background;

        public Tag(String text) {
            init(0, text, DEFAULT_TAG_TEXT_COLOR,DEFAULT_TAG_TEXT_SIZE, DEFAULT_TAG_LAYOUT_COLOR, DEFAULT_TAG_LAYOUT_COLOR_PRESS,
                    DEFAULT_TAG_IS_DELETABLE, DEFAULT_TAG_DELETE_INDICATOR_COLOR, DEFAULT_TAG_DELETE_INDICATOR_SIZE, DEFAULT_TAG_RADIUS, DEFAULT_TAG_DELETE_ICON, DEFAULT_TAG_LAYOUT_BORDER_SIZE, DEFAULT_TAG_LAYOUT_BORDER_COLOR);
        }

        public Tag(String text, int color,boolean isDeletable) {
            init(0, text, color, DEFAULT_TAG_TEXT_SIZE, DEFAULT_TAG_LAYOUT_COLOR, DEFAULT_TAG_LAYOUT_COLOR_PRESS, isDeletable,
                    DEFAULT_TAG_DELETE_INDICATOR_COLOR, DEFAULT_TAG_DELETE_INDICATOR_SIZE, DEFAULT_TAG_RADIUS, DEFAULT_TAG_DELETE_ICON, DEFAULT_TAG_LAYOUT_BORDER_SIZE, DEFAULT_TAG_LAYOUT_BORDER_COLOR);
        }

        public Tag(int id, String text, int color) {
            init(id, text, color, DEFAULT_TAG_TEXT_SIZE, DEFAULT_TAG_LAYOUT_COLOR, DEFAULT_TAG_LAYOUT_COLOR_PRESS, DEFAULT_TAG_IS_DELETABLE,
                    DEFAULT_TAG_DELETE_INDICATOR_COLOR, DEFAULT_TAG_DELETE_INDICATOR_SIZE, DEFAULT_TAG_RADIUS, DEFAULT_TAG_DELETE_ICON, DEFAULT_TAG_LAYOUT_BORDER_SIZE, DEFAULT_TAG_LAYOUT_BORDER_COLOR);
        }

        private void init(int id, String text, int tagTextColor, float tagTextSize, int layout_color, int layout_color_press, boolean isDeletable, int deleteIndicatorColor,
                          float deleteIndicatorSize, float radius, String deleteIcon, float layoutBorderSize, int layoutBorderColor) {
            this.id = id;
            this.text = text;
            this.tagTextColor = tagTextColor;
            this.tagTextSize = tagTextSize;
            this.layoutColor = layout_color;
            this.layoutColorPress = layout_color_press;
            this.isDeletable = isDeletable;
            this.deleteIndicatorColor = deleteIndicatorColor;
            this.deleteIndicatorSize = deleteIndicatorSize;
            this.radius = radius;
            this.deleteIcon = deleteIcon;
            this.layoutBorderSize = layoutBorderSize;
            this.layoutBorderColor = layoutBorderColor;
        }
    }

    public void addTag(Tag tag) {
        mTags.add(tag);
        drawTags();
    }

    public void addTags(String[] tags) {
        if (tags == null || tags.length <= 0) return;
        for (String item : tags) {
            Tag tag = new Tag(item);
            mTags.add(tag);
        }
        drawTags();
    }

    public void addTags(List<Tag> tagList) {
        if (tagList == null || tagList.size() <= 0) return;
        mTags.addAll(tagList);
        drawTags();
    }

    public List<Tag> getTags() {
        return mTags;
    }

    public void remove(int position) {
        mTags.remove(position);
        drawTags();
    }

    public void removeAllTags() {
        mTags.clear();
        drawTags();
    }

    public int getLineMargin() {
        return lineMargin;
    }

    public void setLineMargin(float lineMargin) {
        this.lineMargin = ResolutionUtil.dpToPx(getContext(), lineMargin);
    }

    public int getTagMargin() {
        return tagMargin;
    }

    public void setTagMargin(float tagMargin) {
        this.tagMargin = ResolutionUtil.dpToPx(getContext(), tagMargin);
    }

    public int getTextPaddingLeft() {
        return textPaddingLeft;
    }

    public void setTextPaddingLeft(float textPaddingLeft) {
        this.textPaddingLeft = ResolutionUtil.dpToPx(getContext(), textPaddingLeft);
    }

    public int getTextPaddingRight() {
        return textPaddingRight;
    }

    public void setTextPaddingRight(float textPaddingRight) {
        this.textPaddingRight = ResolutionUtil.dpToPx(getContext(), textPaddingRight);
    }

    public int getTextPaddingTop() {
        return textPaddingTop;
    }

    public void setTextPaddingTop(float textPaddingTop) {
        this.textPaddingTop = ResolutionUtil.dpToPx(getContext(), textPaddingTop);
    }

    public int getTexPaddingBottom() {
        return texPaddingBottom;
    }

    public void setTexPaddingBottom(float texPaddingBottom) {
        this.texPaddingBottom = ResolutionUtil.dpToPx(getContext(), texPaddingBottom);
    }

    public void setOnTagClickListener(OnTagClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setOnTagDeleteListener(OnTagDeleteListener deleteListener) {
        mDeleteListener = deleteListener;
    }

    public interface OnTagClickListener {
        void onTagClick(int position, Tag tag);
    }

    public interface OnTagDeleteListener {
        void onTagDeleted(int position, Tag tag);
    }
}
