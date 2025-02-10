package cn.ac.lz233.tarnhelm.util.ktx

import android.content.Intent

fun Intent.useFlymeChooser(use: Boolean = true) {
    this.putExtra("KEY_DISABLE_FLYME_CHOOSER", !use)
}