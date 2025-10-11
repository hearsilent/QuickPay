package com.hearsilent.quickpay.libs

import android.text.Spannable
import android.text.method.ArrowKeyMovementMethod
import android.text.method.MovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.widget.TextView

class SelectableLinkMovementMethod : ArrowKeyMovementMethod() {

    override fun onTouchEvent(widget: TextView, span: Spannable, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            // Locate the touch position
            var x = event.x.toInt()
            var y = event.y.toInt()
            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop
            x += widget.scrollX
            y += widget.scrollY

            // Locate spans which were clicked
            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val offset = layout.getOffsetForHorizontal(line, x.toFloat())

            // Get clickable spans on clicked position
            val clickableSpans =
                span.getSpans<ClickableSpan?>(offset, offset, ClickableSpan::class.java)
            if (clickableSpans.size != 0) {
                clickableSpans[0]!!.onClick(widget)
                return true
            }
        }
        return super.onTouchEvent(widget, span, event)
    }

    companion object {
        private val linkMovementMethod = SelectableLinkMovementMethod()

        val instance: MovementMethod
            get() = linkMovementMethod
    }
}
