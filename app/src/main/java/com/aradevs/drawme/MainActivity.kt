package com.aradevs.drawme

import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), SurfaceHolder.Callback, View.OnTouchListener {
    //layout for drawing
    lateinit var canvas: SurfaceView
    lateinit var holder: SurfaceHolder
    lateinit var trailPaint : Paint
    lateinit var path: Path
    var  startX : Float by Delegates.notNull()
    var startY : Float by Delegates.notNull()
    var lastX : Float by Delegates.notNull()
    var lastY : Float by Delegates.notNull()
    lateinit var mBitmap: Bitmap

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        
        setContentView(R.layout.activity_main)
        
        canvas = findViewById(R.id.canvas)
        canvas.holder.addCallback(this)
        trailPaint = Paint()
        trailPaint.isAntiAlias = true;
        trailPaint.isDither = true;
        trailPaint.color = Color.BLUE;
        trailPaint.style = Paint.Style.STROKE
        trailPaint.strokeJoin = Paint.Join.ROUND
        trailPaint.strokeCap = Paint.Cap.ROUND
        trailPaint.strokeWidth = 12F
        canvas.setOnTouchListener(this)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startPath(x.toFloat(), y.toFloat())
            }
            MotionEvent.ACTION_MOVE -> {

                path.quadTo(lastX, lastY, (lastX + event.x) / 2, (lastY + event.y) / 2)
                draw(event.rawX, event.rawY)
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_UP -> {
                endPath()
            }
        }
        return true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        this.holder = holder
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        this.holder = holder!!
        this.holder.addCallback(this)
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        Log.e("Destroyed","Canvas was destroyed");
    }

    //drawing stuff
    private  fun draw(x: Float, y: Float) {
        var canvas = Canvas(mBitmap)
        canvas.drawPath(path, trailPaint)
        canvas = holder.lockCanvas()
        canvas.drawBitmap(mBitmap, 0F, 0F, null)
        holder.unlockCanvasAndPost(canvas)
    }

    //setting initial variables
    private fun startPath(x: Float, y: Float) {
        startX = x
        startY = y
        lastX = x
        lastY = y
        path = Path()
        path.moveTo(x, y)
    }

    //work it's done, start a new path
    private fun endPath() {
        path = Path()
    }
}



