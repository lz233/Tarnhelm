package cn.ac.lz233.tarnhelm.util.ktx

import android.util.TypedValue
import cn.ac.lz233.tarnhelm.App

fun Float.dp2px() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, App.context.resources.displayMetrics)