package com.egnyte.androidsdk.picker;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class NavigateBackView extends Button {

    private String name;
    private String path;

    public NavigateBackView(Context context) {
        super(context);
    }

    public NavigateBackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigateBackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public NavigateBackView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void update(String name, String path) {
        this.name = name;
        this.path = path;
        update();
    }

    private void update() {
        setText(name == null ? name : "< " + name);
        setVisibility(name == null ? View.GONE : View.VISIBLE);
    }

    String getPath() {
        return path;
    }
}
