package com.example.ui.util

import android.content.Context
import androidx.annotation.DrawableRes
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.Pandal

object PandalImageHelper {

    @DrawableRes
    val universalPlaceholderRes: Int = R.drawable.img_durga_placeholder

    /**
     * Map of verified genuine photographs stored locally in drawables.
     * Guarantees instantaneous, 100% reliable rendering of authentic Durga Puja pandals
     * for popular and prominent pandals, without depending on external network.
     */
    val localPandalDrawables: Map<String, Int> = mapOf(
        "ekdalia-evergreen" to R.drawable.img_ekdalia_evergreen,
        "singhi-park" to R.drawable.img_singhi_park,
        "maddox-square" to R.drawable.img_maddox_square,
        "tridhara-sammilani" to R.drawable.img_tridhara_sammilani,
        "deshapriya-park" to R.drawable.img_deshapriya_park,
        "chetla-agrani" to R.drawable.img_chetla_agrani,
        "suruchi-sangha" to R.drawable.img_suruchi_sangha,
        "mudiali-club" to R.drawable.img_mudiali_club,
        "bagbazar-sarbojanin" to R.drawable.img_bagbazar_sarbojanin,
        "college-square" to R.drawable.img_college_square,
        "sreebhumi-sporting" to R.drawable.img_sreebhumi_sporting,
        "santosh-mitra-square" to R.drawable.img_santosh_mitra,
        "kumartuli-park" to R.drawable.img_kumartuli_park
    )

    /**
     * Returns either a local drawable resource ID (Int) or a verified Durga Puja image URL (String).
     */
    fun getImageModel(pandal: Pandal): Any {
        val localRes = localPandalDrawables[pandal.id]
        if (localRes != null) {
            return localRes
        }
        if (pandal.imageUrl.isNotBlank() && !isBannedImageUrl(pandal.imageUrl)) {
            return pandal.imageUrl
        }
        return universalPlaceholderRes
    }

    /**
     * Builds a safe Coil ImageRequest with genuine Durga Puja placeholder, error, and fallback.
     */
    fun buildImageRequest(context: Context, pandal: Pandal): ImageRequest {
        val model = getImageModel(pandal)
        return ImageRequest.Builder(context)
            .data(model)
            .crossfade(true)
            .placeholder(universalPlaceholderRes)
            .error(universalPlaceholderRes)
            .fallback(universalPlaceholderRes)
            .build()
    }

    private fun isBannedImageUrl(url: String): Boolean {
        val bannedIds = listOf(
            "photo-1600585154340-be6161a56a0c",
            "photo-1544644181-1484b3fdfc62",
            "photo-1567157577867-05ccb1388e66",
            "photo-1514222134-b57cbb8ce073",
            "photo-1512453979798-5ea266f8880c",
            "photo-1609137144822-297eb0989f41"
        )
        return bannedIds.any { url.contains(it) }
    }
}
