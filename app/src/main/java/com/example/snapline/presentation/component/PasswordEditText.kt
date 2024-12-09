package com.example.snapline.presentation.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.snapline.R
import com.example.snapline.util.Helper.isValidPassword

class PasswordEditText : AppCompatEditText {
    private lateinit var clearButtonImage: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setHintTextColor(ContextCompat.getColor(context, R.color.gray900))
        setTextColor(ContextCompat.getColor(context, R.color.gray900))
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        maxLines = 1
    }

    private fun init() {

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val errorText =
                    if (s.isEmpty()) context.getString(R.string.error_field_cannot_be_blank)
                    else if (!s.toString().isValidPassword()) context.getString(R.string.error_short_password_char)
                    else null
                setError(errorText, null)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null,
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }
}