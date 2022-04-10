package ru.levkopo.vezdecodmobile.widgets

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.card.MaterialCardView

const val WIDTH_ZONE = 2.5f
class FlingCardView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
): MaterialCardView(context, attributeSet, defStyleAttr) {
    private var startX: Float = 0f
    var listener: FlingListener? = null

    override fun canScrollHorizontally(direction: Int): Boolean {
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val halfWidth = width/WIDTH_ZONE

        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                translationX += event.x - startX

                return true
            }

            MotionEvent.ACTION_UP -> {
                if(x==0f) performClick()

                val animator = animate()
                if(translationX<-halfWidth) {
                    setStatus(listener?.onLeftEnd() ?: false)
                }else if(translationX>halfWidth) {
                    setStatus(listener?.onRightEnd() ?: false)
                }

                animator.duration = 500L
                animator.translationX(0f)
                animator.setUpdateListener { handleScroll() }
                animator.start()

                return true
            }
        }

        return false
    }

    fun setStatus(show: Boolean) {
        animate()
            .alpha(if(show) 1.0f else 0.0f)
//            .translationX(if(show) 0f else translationX * WIDTH_ZONE)
            .start()
    }

    override fun setTranslationX(translationX: Float) {
        super.setTranslationX(translationX)

        handleScroll()
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun handleScroll() {
        val halfWidth = width / WIDTH_ZONE
        if (translationX < 0) {
            alpha = 1-(-translationX / halfWidth)
            listener?.leftScroll(-translationX, halfWidth)
        } else if (translationX > 0) {
            alpha = 1-(translationX / halfWidth)
            listener?.rightsScroll(translationX, halfWidth)
        } else {
            alpha = 1f
            listener?.leftScroll(0f, halfWidth)
            listener?.rightsScroll(0f, halfWidth)
        }
    }

    abstract class FlingListener {
        open fun leftScroll(position: Float, max: Float) {}
        open fun rightsScroll(position: Float, max: Float) {}

        open fun onLeftEnd(): Boolean = true
        open fun onRightEnd(): Boolean = true
    }
}