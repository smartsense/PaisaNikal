package aepsapp.easypay.com.aepsandroid.network

import android.content.Context

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

/**
 * Created by ADMIN on 7/18/2016.
 */
class VolleyRequestQueue private constructor(private val mCtx: Context) {
    private var mRequestQueue: RequestQueue? = null
    val imageLoader: ImageLoader

    private val requestQueue: RequestQueue
        get() {
            mRequestQueue=mRequestQueue?:Volley.newRequestQueue(mCtx.applicationContext)
            return mRequestQueue!!
        }

    init {
        mRequestQueue = requestQueue

        imageLoader = ImageLoader(mRequestQueue, LruBitmapCache(
                LruBitmapCache.getCacheSize(mCtx)))
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    companion object {
        private var mInstance: VolleyRequestQueue? = null

        @Synchronized
        fun getInstance(context: Context): VolleyRequestQueue {

            mInstance = mInstance ?: VolleyRequestQueue(context)
            /*if (mInstance == null) {
                mInstance = VolleyRequestQueue(context)
            }*/
            return mInstance!!
        }
    }

}
