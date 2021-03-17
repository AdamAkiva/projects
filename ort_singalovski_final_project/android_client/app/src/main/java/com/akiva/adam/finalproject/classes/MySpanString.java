package com.akiva.adam.finalproject.classes;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

/**
 * A class used to create a custom SpanString
 */
public class MySpanString extends SpannableString {

    public MySpanString(CharSequence source) {
        super(source);
    }

    public MySpanString setUnderLine() {
        this.setSpan(new UnderlineSpan(), 0, this.length(), 0);
        return this;
    }

    public MySpanString setBold() {
        this.setSpan(new StyleSpan(Typeface.BOLD), 0, this.length(), 0);
        return this;
    }

    public MySpanString setItalic() {
        this.setSpan(new StyleSpan(Typeface.ITALIC), 0, this.length(), 0);
        return this;
    }
}
