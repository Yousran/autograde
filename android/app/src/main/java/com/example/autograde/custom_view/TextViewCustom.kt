package com.example.autograde

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.example.autograde.R


class TextViewCustom @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs), View.OnClickListener {

    private var isCheckedBackground: Drawable? = null
    private var isNotChecked: Drawable? = null

    private var isCheckedBackgroundColor: Int = 0
    private var isNotCheckedColor: Int = 0

    var isChecked : Boolean = false
    
    

    init {
        isCheckedBackground = ContextCompat.getDrawable(context, R.drawable.bg_nav_number_active)
        isNotChecked = ContextCompat.getDrawable(context, R.drawable.bg_nav_number_inactive)

        isCheckedBackgroundColor = ContextCompat.getColor(context, android.R.color.white)
        isNotCheckedColor = ContextCompat.getColor(context, android.R.color.black)
        
        updateTextViewAppearance()



        isClickable = true
        isFocusable = true
        setOnClickListener(this)
    }
    

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        updateTextViewAppearance()
    }

    private fun updateTextViewAppearance() {
        // Perbarui tampilan berdasarkan kondisi
        when {
            isChecked -> {
                background = isCheckedBackground
                setTextColor(isCheckedBackgroundColor)
            }
            else -> {
                background = isNotChecked
                setTextColor(isNotCheckedColor)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (!isInEditMode) {
            updateTextViewAppearance()
        }
        super.onDraw(canvas)
    }
    
    override fun onClick(v: View?) {

    }
}
