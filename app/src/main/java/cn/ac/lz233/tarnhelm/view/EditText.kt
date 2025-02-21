package cn.ac.lz233.tarnhelm.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.textfield.TextInputEditText

class EditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : TextInputEditText(context, attrs) {
    private var canTouch = true

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (canTouch) {
            super.onTouchEvent(event)
        } else {
            true
        }
    }
}