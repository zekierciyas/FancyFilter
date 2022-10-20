package com.zekierciyas.fancyfilterlib

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class FancyFilter {

    class Builder {

        private var lutProcessor: LutProcessor? = null
        private var bitmap: Bitmap? = null
        private var filterRes: Int? = null

        fun withContext(context: Context) = apply { lutProcessor = LutProcessor(context) }

        fun bitmap(bitmap: Bitmap?) = apply { this.bitmap = bitmap }

        fun filter(filter: Int) = apply {
            filterRes = filter
        }

        fun applyFilter (onEmit: (lut: Bitmap) -> Unit){
            process(onEmit)
        }

        private fun process(onEmit: (lut: Bitmap) -> Unit){
            CoroutineScope(Job() + Dispatchers.Default).launch {
                processAndEmit()
                    .flowOn(Dispatchers.IO)
                    .collect { readyLut ->
                        onEmit(readyLut)
                    }
            }
        }

        private fun processAndEmit(): Flow<Bitmap> = flow {
            val bitmap = lutProcessor!!.filter(bitmap!!, filterRes)
            emit(bitmap)
        }
    }


}