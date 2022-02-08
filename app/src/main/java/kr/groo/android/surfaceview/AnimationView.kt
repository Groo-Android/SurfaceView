package kr.groo.android.surfaceview

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.coroutines.*

class AnimationView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : SurfaceView(context, attrs, defStyle), SurfaceHolder.Callback {

    companion object {
        private val animationScope = CoroutineScope(Dispatchers.IO)
    }

    private var currentTime = 0L
    private val surfaceHolder = holder
    private val paint = Paint().apply {
        color = Color.RED
    }

    private var animationJob: Job? = null
    private var items: Array<Point>? = null

    init {
        surfaceHolder.addCallback(this)
        items = arrayOf(
            Point(100, 100),
            Point(200, 100),
            Point(300, 100),
            Point(400, 100),
            Point(500, 100),
            Point(600, 100),
            Point(700, 100),
            Point(800, 100),
            Point(900, 100),
            Point(1000, 100),
        )
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceCreated(holder: SurfaceHolder) {
        animationJob = animationScope.launch {
            while (animationJob?.isActive == true) {
                val time = System.currentTimeMillis()
                println("${time - currentTime}")
                currentTime = time

                val canvas = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    surfaceHolder.lockHardwareCanvas()
                } else {
                    surfaceHolder.lockCanvas()
                }

                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

                    items?.forEach {
                        canvas.drawCircle(it.x.toFloat(), it.y.toFloat(), 40F, paint)
                        it.y += 1
                    }

                    surfaceHolder.unlockCanvasAndPost(canvas)
                    delay(1000 / 60)
                }
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        animationJob?.cancel()
    }
}