package com.aradevs.drawme

import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(), SurfaceHolder.Callback, View.OnTouchListener {
    //layout for drawing
    lateinit var canvas: SurfaceView
    lateinit var holder: SurfaceHolder
    lateinit var trailPaint : Paint
    lateinit var path: Path
    var stroke: Float = 12F;
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
        trailPaint.strokeWidth = stroke
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
        Log.e("Destroyed", "Canvas was destroyed");
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

    fun setPixels(view: View){
        when (view.id) {
            R.id.tenpixels -> {
                trailPaint.strokeWidth = 10F
            }
            R.id.fourteenpixels -> {
                trailPaint.strokeWidth = 14F
            }
            else -> {
                trailPaint.strokeWidth = 18F
            }
        }
    }

    fun eraser(view: View){
        val button = view as Button;
        if(button.text =="Eraser"){
            trailPaint.strokeWidth = 20F
            button.text = "Drawing"
            trailPaint.color = Color.BLACK
        }else{
            button.text = "Eraser"
            trailPaint.color = Color.BLUE
        }
    }

    fun reset(view: View){
        var canvas = Canvas(mBitmap)
        val blackPaint = Paint()
        blackPaint.color = Color.BLACK
        blackPaint.style = Paint.Style.FILL
        canvas.drawPaint(blackPaint)
        canvas = holder.lockCanvas()
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        holder.unlockCanvasAndPost(canvas)
    }
}



