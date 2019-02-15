package me.martichou.be.grateful.recyclerView

import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.ListPreloader.PreloadModelProvider
import com.bumptech.glide.ListPreloader.PreloadSizeProvider
import com.bumptech.glide.RequestManager

class RecyclerViewPreloader<T>
/**
 * Constructor that accepts interfaces for providing the dimensions of images to preload, the list
 * of models to preload for a given position, and the request to use to load images.
 *
 * @param preloadModelProvider     Provides models to load and requests capable of loading them.
 * @param preloadDimensionProvider Provides the dimensions of images to load.
 * @param maxPreload               Maximum number of items to preload.
 */
(@NonNull requestManager: RequestManager,
 @NonNull preloadModelProvider: PreloadModelProvider<T>,
 @NonNull preloadDimensionProvider: PreloadSizeProvider<T>, maxPreload: Int) : RecyclerView.OnScrollListener() {

    private val recyclerScrollListener: RecyclerToListViewScrollListener

    /**
     * Helper constructor that accepts an [android.app.Fragment].
     */
    @Deprecated("Use constructor <code>RecyclerViewPreloader(Fragment, PreloadModelProvider<T>,\n" +
            "    PreloadSizeProvider<T>)</code> instead.")
    constructor(@NonNull fragment: android.app.Fragment,
                @NonNull preloadModelProvider: PreloadModelProvider<T>,
                @NonNull preloadDimensionProvider: PreloadSizeProvider<T>,
                maxPreload: Int) : this(Glide.with(fragment), preloadModelProvider, preloadDimensionProvider, maxPreload)

    init {
        val listPreloader = ListPreloader(requestManager, preloadModelProvider,
                preloadDimensionProvider, maxPreload)
        recyclerScrollListener = RecyclerToListViewScrollListener(listPreloader)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        recyclerScrollListener.onScrolled(recyclerView, dx, dy)
    }
}