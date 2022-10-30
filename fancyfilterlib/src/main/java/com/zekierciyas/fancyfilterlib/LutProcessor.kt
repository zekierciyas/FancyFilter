package com.zekierciyas.fancyfilterlib

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.renderscript.*

class LutProcessor(private val context: Context) {

  private var renderScript: RenderScript = RenderScript.create(context)
  private var rscriptLut: ScriptIntrinsic3DLUT = ScriptIntrinsic3DLUT.create(renderScript, Element.U8_4(renderScript))
  private var outputBitmap: Bitmap? = null

  fun filter(sourceBitmap: Bitmap, lutImageRes: Int?): Bitmap{
    outputBitmap = Bitmap.createBitmap(sourceBitmap.width, sourceBitmap.height, sourceBitmap.config)
    val allocIn = Allocation.createFromBitmap(renderScript, sourceBitmap)
    val allocOut = Allocation.createFromBitmap(renderScript, outputBitmap)

    val lutBitmap = BitmapFactory.decodeResource(context.resources, lutImageRes!!)
    val width: Int = lutBitmap?.width ?: 0
    val height: Int = lutBitmap?.height ?: 0
    val sideLength = width / height

    val pixels = IntArray(width * height)
    val lut = IntArray(width * height)

    lutBitmap?.getPixels(pixels, 0, width, 0, 0, width, height)
    lutBitmap?.recycle()//Done with Lut bitmap

    var i = 0

      for (red in 0 until sideLength) {
        for (green in 0 until sideLength) {
          val p = red + green * width
          for (blue in 0 until sideLength) {
            lut[i++] = pixels[p + blue * height]
          }
        }
      }

    val type = Type.Builder(renderScript, Element.U8_4(renderScript))
      .setX(sideLength)
      .setY(sideLength)
      .setZ(sideLength)
      .create()

    val allocCube = Allocation.createTyped(renderScript, type)
    allocCube?.copyFromUnchecked(lut)

    rscriptLut.setLUT(allocCube)
    rscriptLut.forEach(allocIn, allocOut)

    allocOut?.copyTo(outputBitmap)

    return outputBitmap!!
  }

  fun filters(listOfSourceBitmap: List<Bitmap?>, listOfLutImageRes: List<Int?>): List<Bitmap> {
    if (listOfLutImageRes.isEmpty()) return emptyList()
    if (listOfSourceBitmap.size ==  1) {
      val listOfBitmap = mutableListOf<Bitmap>()
      listOfLutImageRes.forEach {
        val bitmap = filter(listOfSourceBitmap.first()!!, it)
        listOfBitmap.add(bitmap)
      }
      return listOfBitmap
    } else if (listOfLutImageRes.size == listOfSourceBitmap.size) {
      val listOfBitmap = mutableListOf<Bitmap>()
      listOfLutImageRes.forEach { lutImageRes ->
        listOfSourceBitmap.forEach { sourceBitmap ->
          val bitmap = filter(sourceBitmap = sourceBitmap!!,lutImageRes = lutImageRes )
          listOfBitmap.add(bitmap)
        }
      }
      return listOfBitmap
    }
    return emptyList()
  }
}
