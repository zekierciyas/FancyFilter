package com.zekierciyas.fancyfilterlib

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.WorkerThread
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception
import kotlin.jvm.Throws

class FancyFilter {

    /**
     *  #Example Usages:
     *
     * Usage 1: Single Image && Single Filter
     *
     *
     * ```
     *    FancyFilter.Builder()
     *    .withContext(this)
     *    .filter(FancyFilters.NO_9)
     *    .bitmap(bitmap)
     *    .applyFilter{
     *    runOnUiThread {
     *    println("Time period while applying filter ${System.currentTimeMillis() - start}")
     *    preview1.setImageBitmap(it)
     *    }
     *   }
     *
     *  ```
     *
     *  Usage 2: Single Image && Multiple Filters
     *
     *
     *  ```
        FancyFilter.Builder()
        .withContext(this)
        .filters(listOf(FancyFilters.NO_49, FancyFilters.NO_28))
        .bitmap(bitmap)
        .applyFilters{
        runOnUiThread {
        println("Time period while applying filter ${System.currentTimeMillis() - start}")
        preview1.setImageBitmap(it.first())
        preview2.setImageBitmap(it[1])
        }
        }
     *
     *  ```
     *
     *  Usage 3: Multiple Images && Multiple Filters
     *
     *
     *  ```
        FancyFilter.Builder()
        .withContext(this)
        .filters(listOf(FancyFilters.NO_49, FancyFilters.NO_28))
        .bitmaps(listOf(bitmap1,bitmap2))
        .applyFilters{
        runOnUiThread {
        println("Time period while applying filter ${System.currentTimeMillis() - start}")
        preview1.setImageBitmap(it.first())
        preview2.setImageBitmap(it[1])
        }
        }
    ```

     */

    class Builder {

        private var lutProcessor: LutProcessor? = null

        /** Values to for single requests*/
        private var bitmap: Bitmap? = null
        private var filterRes: Int? = null

        /** Values to for multiple requests as provided list*/
        private var listBitmap: List<Bitmap?> = mutableListOf()
        private var listFilterRes: List<Int?> = mutableListOf()

        /** Passing context as actvitiy pointer*/
        fun withContext(context: Context) = apply { lutProcessor = LutProcessor(context) }

        /** @param bitmap : Bitmap that will be filtered end of the process*/
        fun bitmap(bitmap: Bitmap?) = apply { this.bitmap = bitmap }

        /** @param filter : Required filter id from [FancyFilters] */
        fun filter(filter: Int) = apply {
            filterRes = filter
        }

        /** Applying only one filter and passing to [filterRes] to get LUT and process.
         * @param onEmit : [Unit] interface as [Bitmap] type
         * */
        fun applyFilter (onEmit: (lut: Bitmap) -> Unit){
            process(onEmit)
        }

        /** Passing list of source bitmap as [listBitmap] and processing all together
         *  @param bitmaps : List of bitmap that required for process
         */
        fun bitmaps(bitmaps: List<Bitmap?>) = apply { this.listBitmap = bitmaps }


        /** Passing list of filters to [listFilterRes] as [Int] for applying multiple filters
         * to same images or multiple images
         */
        fun filters(filters: List<Int>) = apply {
            listFilterRes = filters
        }

        /** Applying multiple filters and passing to [listFilterRes] to get LUT and process.
         * @param onEmit : [Unit] interface as [List] of [Bitmap] type
         * */
        @Throws
        fun applyFilters(onEmit: (lut: List<Bitmap?>) -> Unit) {
            multipleProcess(onEmit)
        }

        private fun process(onEmit: (lut: Bitmap) -> Unit){
            CoroutineScope(Job() + Dispatchers.Default).launch {
                processAndEmit(filterRes!!)
                    .flowOn(Dispatchers.Default)
                    .collect { readyLut ->
                        println("JOB IS DONE")
                        this.coroutineContext.job.cancel()
                        onEmit(readyLut)
                    }
            }
        }

        private fun multipleProcess(onEmit: (lut: List<Bitmap?>) -> Unit){
            CoroutineScope(Job() + Dispatchers.Default).launch {
                    multipleProcessAndEmit()
                        .flowOn(Dispatchers.Default)
                        .collect { readyLut ->
                            onEmit.invoke(readyLut)
                        }
            }
        }

        private fun processAndEmit(filterRes: Int): Flow<Bitmap> = flow {
            val bitmap = lutProcessor!!.filter(bitmap!!, filterRes)
            emit(bitmap)
        }

        @Throws
        private fun multipleProcessAndEmit(): Flow<List<Bitmap>> = flow {
            if (listBitmap.isEmpty())  emit(lutProcessor!!.filters(listOf(bitmap), listFilterRes))
            else{
                if (listFilterRes.isNullOrEmpty()) throw Exception("Filter list is not provided")
                emit(lutProcessor!!.filters(listBitmap, listFilterRes))
            }
        }
    }
}