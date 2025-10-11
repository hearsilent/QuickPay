package com.hearsilent.quickpay.libs

import android.text.TextPaint
import android.text.style.URLSpan
import android.view.View

open class SpanWithUnderline protected constructor(private val color: Int) : URLSpan("") {

    override fun onClick(view: View) {
    }

    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.isUnderlineText = true
        textPaint.color = color
    }
}