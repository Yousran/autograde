package com.example.autograde

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.autograde.R

class ButtonCustom : AppCompatButton {

    constructor(context: Context) : super(context) // untuk di Activity/Fragment
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)  // untuk di XML

    private var enabledBackground: Drawable
    private var disabledBackground: Drawable
    init {
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if(isEnabled) enabledBackground else disabledBackground
    }
}