package com.rsetiapp.core.util

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import com.rsetiapp.R

class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var progress: Float = 0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()
    private val percentageText: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.circular_progress_view, this, true)
        percentageText = findViewById(R.id.percentageText)

        // Set up the paints
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 40f
        paint.color = Color.parseColor("#9DC183") // Light green color

        backgroundPaint.style = Paint.Style.STROKE
        backgroundPaint.strokeWidth = 40f
        backgroundPaint.color = Color.parseColor("#E8E8E8") // Light gray color
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = paint.strokeWidth / 2
        rect.set(padding, padding, w - padding, h - padding)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw background circle
        canvas.drawArc(rect, 0f, 360f, false, backgroundPaint)

        // Draw progress arc
        canvas.drawArc(rect, -90f, progress * 360f / 100, false, paint)
    }

    fun setProgress(value: Float) {
        progress = value
        percentageText.text = "${value.toInt()}%"
        invalidate()
    }


    fun setProgressWithAnimation(value: Float) {
        val animator = ValueAnimator.ofFloat(progress, value)
        animator.duration = 5000 // 1 second duration
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            setProgress(animation.animatedValue as Float)
        }
        animator.start()
    }
}