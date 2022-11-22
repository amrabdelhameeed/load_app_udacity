package com.example.load_app_udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    fun ValueAnimator.disableViewDuringAnimation() {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                isEnabled = false
            }
            override fun onAnimationEnd(animation: Animator) {
                isEnabled = true
            }
        })
    }
      var valAnimator = ValueAnimator()
      var buttonText: String = "Null"
      var bgButton = 0
      var loadingPrecent: Float = 0f
    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.LoadingButton
        ) {
            getString(R.styleable.LoadingButton_text)?.let { buttonText = it }
            bgButton = getColor(R.styleable.LoadingButton_bgButton, 0)
        }
    }
      var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                buttonText = "Loading..."
                invalidate()
                bgButton =  ContextCompat.getColor(
                    context,
                    R.color.colorPrimaryDark
                )
                invalidate()
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = ContextCompat.getColor(context, R.color.colorPrimary)
                }.color = ContextCompat.getColor(context, R.color.colorPrimary)
                valAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
                    addUpdateListener {
                        loadingPrecent = animatedValue as Float
                        invalidate()
                    }
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.REVERSE
                    duration = 3000
                    disableViewDuringAnimation()
                    start()
                }
                isEnabled = false
            }
            ButtonState.Completed -> {
                loadingPrecent = 0F
                buttonText ="Download Now"
                invalidate()
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = ContextCompat.getColor(context, R.color.colorPrimary)
                }.color = ContextCompat.getColor(context, R.color.colorPrimary)
                isEnabled = true
                bgButton = ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
                invalidate()
                valAnimator.cancel()
            }
            ButtonState.Clicked -> {
                buttonText ="Clicked"
                    invalidate()
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = ContextCompat.getColor(context, R.color.colorPrimary)
                }.color = ContextCompat.getColor(context, R.color.colorAccent)
                bgButton = ContextCompat.getColor(context, R.color.colorAccent)
                invalidate()
            }
        }
          invalidate()
    }
    val rectText = Rect()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val height = measuredHeight.toFloat()
        val width = measuredWidth.toFloat()
        canvas.drawColor(bgButton)
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 70.0F
            color = Color.WHITE
        }.getTextBounds(buttonText, 0, buttonText.length, rectText)
        val centerY = height / 2 - rectText.centerY()
        val centerX = width / 2
        canvas.drawRoundRect(
            0F,
            0F,
            width,
            height,
            0F,
            0F,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = ContextCompat.getColor(context, R.color.colorPrimary)
            }
        )
        if (buttonState == ButtonState.Loading) {
            canvas.drawRoundRect(
                0F,
                0F,
                loadingPrecent * width,
                height,
                0F,
                0F,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
                }
            )
            val padding = 50F
            val arcSize = 160F
            val spaceFromLeft = width - rectText.width().toFloat() - padding / 2
            canvas.drawArc(
                spaceFromLeft,
                padding,
                arcSize+ spaceFromLeft ,
                height - padding,
                0F,
                loadingPrecent * 360F,
                true,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = ContextCompat.getColor(context, R.color.colorAccent)
                }
            )
        }
        canvas.drawText(buttonText, centerX, centerY, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 70.0F
            color = Color.WHITE
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int =paddingRight + paddingLeft +  suggestedMinimumWidth
        val width: Int = resolveSizeAndState(
            minWidth,
            widthMeasureSpec,
            1
        )
        val height: Int = resolveSizeAndState(
            MeasureSpec.getSize(width),
            heightMeasureSpec,
            0
        )
        setMeasuredDimension(width, height)
    }
    fun setState(state: ButtonState) {
        this.buttonState = state
    }
}