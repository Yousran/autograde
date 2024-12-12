package com.example.autograde

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout

class EditTextCustom @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textInputLayout = findTextInputLayoutParent()
                if (s.isNullOrEmpty() || s.length < 6) {
                    setError("Password must not be less than 6 characters", null)
                } else {
                    textInputLayout?.error = null
                }
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun findTextInputLayoutParent(): TextInputLayout? {
        var parentView = parent
        while (parentView != null && parentView !is TextInputLayout) {
            parentView = parentView.parent
        }
        return parentView as? TextInputLayout
    }
}
