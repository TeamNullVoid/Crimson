package com.nullvoid.crimson.customs

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.TypedValue


class Converter {
    companion object {
        fun drawableToBitmap(metrics: DisplayMetrics, drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                if (drawable.bitmap != null) {
                    return drawable.bitmap
                }
            }
            val bitmap: Bitmap? =
                if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                    Bitmap.createBitmap(
                        1,
                        1,
                        Bitmap.Config.ARGB_8888
                    )
                } else {
                    val size =
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, metrics).toInt()
                    Bitmap.createBitmap(
                        size,
                        size,
                        Bitmap.Config.ARGB_8888
                    )
                }
            val canvas = Canvas(bitmap!!)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }
    }
}