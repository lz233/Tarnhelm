@file:Suppress("unused")

package cn.ac.lz233.tarnhelm.xposed.util

import android.content.res.XResources
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.xposed.Config
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge.*
import de.robv.android.xposed.XposedHelpers.*
import de.robv.android.xposed.callbacks.XC_LayoutInflated
import java.lang.reflect.Field
import java.lang.reflect.Member

typealias MethodHookParam = MethodHookParam

fun Class<*>.hookMethod(method: String?, vararg args: Any?) = try {
    LogUtil._d(tag = App.TAG, obj = "Hook ${name}.$method Successful")
    findAndHookMethod(this, method, *args)
} catch (e: NoSuchMethodError) {
    LogUtil.e(e)
    null
} catch (e: ClassNotFoundError) {
    LogUtil.e(e)
    null
} catch (e: ClassNotFoundException) {
    LogUtil.e(e)
    null
}

fun Member.hookMethod(callback: XC_MethodHook) = try {
    hookMethod(this, callback)
} catch (e: Throwable) {
    LogUtil.e(e)
    null
}

inline fun Member.replaceMethod(crossinline hooker: (MethodHookParam) -> Any?) = hookMethod(object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam) = try {
        hooker(param)
    } catch (e: Throwable) {
        LogUtil.e(e)
        null
    }
})

inline fun Member.hookAfterMethod(crossinline hooker: (MethodHookParam) -> Unit) = hookMethod(object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        try {
            hooker(param)
        } catch (e: Throwable) {
            LogUtil.e(e)
        }
    }
})

inline fun Member.hookBeforeMethod(crossinline hooker: (MethodHookParam) -> Unit) = hookMethod(object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        try {
            hooker(param)
        } catch (e: Throwable) {
            LogUtil.e(e)
        }
    }
})

inline fun Class<*>.hookBeforeMethod(method: String?, vararg args: Any?, crossinline hooker: (MethodHookParam) -> Unit) = hookMethod(method, *args, object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        try {
            hooker(param)
        } catch (e: Throwable) {
            LogUtil.e(e)
        }
    }
})

inline fun Class<*>.hookAfterMethod(method: String?, vararg args: Any?, crossinline hooker: (MethodHookParam) -> Unit) = hookMethod(method, *args, object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        try {
            hooker(param)
        } catch (e: Throwable) {
            LogUtil.e(e)
        }
    }
})

fun Class<*>.setReturnConstant(methodName: String?, vararg args: Any?, result: Any?): XC_MethodHook.Unhook? = try {
    hookMethod(methodName, *args, XC_MethodReplacement.returnConstant(result))
} catch (e: Throwable) {
    LogUtil.e(e)
    null
}

inline fun Class<*>.replaceMethod(method: String?, vararg args: Any?, crossinline hooker: (MethodHookParam) -> Any?) = hookMethod(method, *args, object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam) = try {
        hooker(param)
    } catch (e: Throwable) {
        LogUtil.e(e)
        null
    }
})

fun Class<*>.hookAllMethods(methodName: String?, hooker: XC_MethodHook): Set<XC_MethodHook.Unhook> = try {
    hookAllMethods(this, methodName, hooker)
} catch (e: NoSuchMethodError) {
    LogUtil.e(e)
    emptySet()
} catch (e: ClassNotFoundError) {
    LogUtil.e(e)
    emptySet()
} catch (e: ClassNotFoundException) {
    LogUtil.e(e)
    emptySet()
}

inline fun Class<*>.hookBeforeAllMethods(methodName: String?, crossinline hooker: (MethodHookParam) -> Unit): Set<XC_MethodHook.Unhook> = hookAllMethods(methodName, object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        try {
            hooker(param)
        } catch (e: Throwable) {
            LogUtil.e(e)
        }
    }
})

inline fun Class<*>.hookAfterAllMethods(methodName: String?, crossinline hooker: (MethodHookParam) -> Unit): Set<XC_MethodHook.Unhook> = hookAllMethods(methodName, object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        try {
            hooker(param)
        } catch (e: Throwable) {
            LogUtil.e(e)
        }
    }
})

inline fun Class<*>.replaceAfterAllMethods(methodName: String?, crossinline hooker: (MethodHookParam) -> Any?): Set<XC_MethodHook.Unhook> = hookAllMethods(methodName, object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam) = try {
        hooker(param)
    } catch (e: Throwable) {
        LogUtil.e(e)
        null
    }
})

fun Class<*>.hookConstructor(vararg args: Any?) = try {
    findAndHookConstructor(this, *args)
} catch (e: NoSuchMethodError) {
    LogUtil.e(e)
    null
} catch (e: ClassNotFoundError) {
    LogUtil.e(e)
    null
} catch (e: ClassNotFoundException) {
    LogUtil.e(e)
    null
}

inline fun Class<*>.hookBeforeConstructor(vararg args: Any?, crossinline hooker: (MethodHookParam) -> Unit) = hookConstructor(*args, object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        try {
            hooker(param)
        } catch (e: Throwable) {
            LogUtil.e(e)
        }
    }
})

inline fun Class<*>.hookAfterConstructor(vararg args: Any?, crossinline hooker: (MethodHookParam) -> Unit) = hookConstructor(*args, object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        try {
            hooker(param)
        } catch (e: Throwable) {
            LogUtil.e(e)
        }
    }
})

inline fun Class<*>.replaceConstructor(vararg args: Any?, crossinline hooker: (MethodHookParam) -> Unit) = hookConstructor(*args, object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam) {
        try {
            hooker(param)
        } catch (e: Throwable) {
            LogUtil.e(e)
        }
    }
})

fun Class<*>.hookAllConstructors(hooker: XC_MethodHook): Set<XC_MethodHook.Unhook> = try {
    hookAllConstructors(this, hooker)
} catch (e: NoSuchMethodError) {
    LogUtil.e(e)
    emptySet()
} catch (e: ClassNotFoundError) {
    LogUtil.e(e)
    emptySet()
} catch (e: ClassNotFoundException) {
    LogUtil.e(e)
    emptySet()
}

inline fun Class<*>.hookAfterAllConstructors(crossinline hooker: (MethodHookParam) -> Unit) = hookAllConstructors(object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        try {
            hooker(param)
        } catch (e: Throwable) {
            LogUtil.e(e)
        }
    }
})

inline fun Class<*>.hookBeforeAllConstructors(crossinline hooker: (MethodHookParam) -> Unit) = hookAllConstructors(object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        try {
            hooker(param)
        } catch (e: Throwable) {
            LogUtil.e(e)
        }
    }
})

inline fun Class<*>.replaceAfterAllConstructors(crossinline hooker: (MethodHookParam) -> Unit) = hookAllConstructors(object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam) {
        try {
            hooker(param)
        } catch (e: Throwable) {
            LogUtil.e(e)
        }
    }
})

fun String.hookMethod(method: String?, vararg args: Any?, classLoader: ClassLoader = Config.classLoader) = try {
    findClass(classLoader).hookMethod(method, *args)
} catch (e: ClassNotFoundError) {
    LogUtil.e(e)
    null
} catch (e: ClassNotFoundException) {
    LogUtil.e(e)
    null
}

inline fun String.hookBeforeMethod(method: String?, vararg args: Any?, classLoader: ClassLoader = Config.classLoader, crossinline hooker: (MethodHookParam) -> Unit) = try {
    findClass(classLoader).hookBeforeMethod(method, *args, hooker = hooker)
} catch (e: ClassNotFoundError) {
    LogUtil.e(e)
    null
} catch (e: ClassNotFoundException) {
    LogUtil.e(e)
    null
}

inline fun String.hookAfterMethod(method: String?, vararg args: Any?, classLoader: ClassLoader = Config.classLoader, crossinline hooker: (MethodHookParam) -> Unit) = try {
    findClass(classLoader).hookAfterMethod(method, *args, hooker = hooker)
} catch (e: ClassNotFoundError) {
    LogUtil.e(e)
    null
} catch (e: ClassNotFoundException) {
    LogUtil.e(e)
    null
}

fun String.setReturnConstant(method: String?, vararg args: Any?, result: Any?, classLoader: ClassLoader = Config.classLoader) = try {
    findClass(classLoader).setReturnConstant(method, *args, result = result)
} catch (e: ClassNotFoundError) {
    LogUtil.e(e)
    null
} catch (e: ClassNotFoundException) {
    LogUtil.e(e)
    null
}

inline fun String.replaceMethod(method: String?, vararg args: Any?, classLoader: ClassLoader = Config.classLoader, crossinline hooker: (MethodHookParam) -> Any?) = try {
    findClass(this, classLoader).replaceMethod(method, *args, hooker = hooker)
} catch (e: ClassNotFoundError) {
    LogUtil.e(e)
    null
} catch (e: ClassNotFoundException) {
    LogUtil.e(e)
    null
}

inline fun String.replaceAfterAllMethods(methodName: String?, classLoader: ClassLoader = Config.classLoader, crossinline hooker: (MethodHookParam) -> Any?) = try {
    findClass(classLoader).replaceAfterAllMethods(methodName, hooker)
} catch (e: ClassNotFoundError) {
    LogUtil.e(e)
    null
} catch (e: ClassNotFoundException) {
    LogUtil.e(e)
    null
}

inline fun String.hookBeforeConstructor(vararg args: Any?, classLoader: ClassLoader = Config.classLoader, crossinline hooker: (MethodHookParam) -> Unit) =
    findClass(classLoader).hookConstructor(*args, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            try {
                hooker(param)
            } catch (e: Throwable) {
                LogUtil.e(e)
            }
        }
    })

inline fun String.hookAfterConstructor(vararg args: Any?, classLoader: ClassLoader = Config.classLoader, crossinline hooker: (MethodHookParam) -> Unit) =
    findClass(classLoader).hookConstructor(*args, object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            try {
                hooker(param)
            } catch (e: Throwable) {
                LogUtil.e(e)
            }
        }
    })

inline fun String.replaceConstructor(vararg args: Any?, classLoader: ClassLoader = Config.classLoader, crossinline hooker: (MethodHookParam) -> Unit) =
    findClass(classLoader).hookConstructor(*args, object : XC_MethodReplacement() {
        override fun replaceHookedMethod(param: MethodHookParam) {
            try {
                hooker(param)
            } catch (e: Throwable) {
                LogUtil.e(e)
            }
        }
    })

fun String.callStaticMethod(methodName: String?, vararg args: Any?, classLoader: ClassLoader = Config.classLoader) = findClass(classLoader).callStaticMethod(methodName, *args)

@Suppress("UNCHECKED_CAST")
fun <T> String.callStaticMethodAs(methodName: String?, vararg args: Any?, classLoader: ClassLoader = Config.classLoader) = findClass(classLoader).callStaticMethod(methodName, *args) as T

fun MethodHookParam.invokeOriginalMethod(): Any? = invokeOriginalMethod(method, thisObject, args)

fun Any.getObjectField(field: String?): Any? = getObjectField(this, field)

@Suppress("UNCHECKED_CAST")
fun <T> Any.getObjectFieldAs(field: String?) = getObjectField(this, field) as T

fun Any.getIntField(field: String?) = getIntField(this, field)

fun Any.getLongField(field: String?) = getLongField(this, field)

fun Any.getBooleanField(field: String?) = getBooleanField(this, field)

fun Any.callMethod(methodName: String?, vararg args: Any?): Any? =
    callMethod(this, methodName, *args)

fun Class<*>.callStaticMethod(methodName: String?, vararg args: Any?): Any? =
    callStaticMethod(this, methodName, *args)

@Suppress("UNCHECKED_CAST")
fun <T> Class<*>.callStaticMethodAs(methodName: String?, vararg args: Any?) = callStaticMethod(this, methodName, *args) as T

@Suppress("UNCHECKED_CAST")
fun <T> Class<*>.getStaticObjectFieldAs(field: String?) = getStaticObjectField(this, field) as T

fun Class<*>.getStaticObjectField(field: String?): Any? = getStaticObjectField(this, field)

fun Class<*>.setStaticObjectField(field: String?, obj: Any?) = apply {
    setStaticObjectField(this, field, obj)
}

@Suppress("UNCHECKED_CAST")
fun <T> Any.callMethodAs(methodName: String?, vararg args: Any?) = callMethod(this, methodName, *args) as T

fun Any.callMethod(methodName: String?, parameterTypes: Array<Class<*>>, vararg args: Any?): Any = callMethod(this, methodName, parameterTypes, *args)

fun Class<*>.callStaticMethod(methodName: String?, parameterTypes: Array<Class<*>>, vararg args: Any?): Any = callStaticMethod(this, methodName, parameterTypes, *args)

fun String.findClass(classLoader: ClassLoader? = Config.classLoader): Class<*> = findClass(this, classLoader)

fun String.findClassOrNull(classLoader: ClassLoader? = Config.classLoader): Class<*>? = findClassIfExists(this, classLoader)

fun Class<*>.new(vararg args: Any?): Any = newInstance(this, *args)

fun Class<*>.new(parameterTypes: Array<Class<*>>, vararg args: Any?): Any = newInstance(this, parameterTypes, *args)

fun Class<*>.findFieldOrNull(field: String?): Field? = findFieldIfExists(this, field)

fun <T> T.setIntField(field: String?, value: Int) = apply {
    setIntField(this, field, value)
}

fun <T> T.setLongField(field: String?, value: Long) = apply {
    setLongField(this, field, value)
}

fun <T> T.setObjectField(field: String?, value: Any?) = apply {
    setObjectField(this, field, value)
}

fun <T> T.setBooleanField(field: String?, value: Boolean) = apply {
    setBooleanField(this, field, value)
}

inline fun XResources.hookLayout(id: Int, crossinline hooker: (XC_LayoutInflated.LayoutInflatedParam) -> Unit) {
    try {
        hookLayout(id, object : XC_LayoutInflated() {
            override fun handleLayoutInflated(liparam: LayoutInflatedParam) {
                try {
                    hooker(liparam)
                } catch (e: Throwable) {
                    LogUtil.e(e)
                }
            }
        })
    } catch (e: Throwable) {
        LogUtil.e(e)
    }
}

inline fun XResources.hookLayout(pkg: String, type: String, name: String, crossinline hooker: (XC_LayoutInflated.LayoutInflatedParam) -> Unit) {
    try {
        val id = getIdentifier(name, type, pkg)
        hookLayout(id, hooker)
    } catch (e: Throwable) {
        LogUtil.e(e)
    }
}

fun getHookField(clazz: Class<*>, name: String): Any {
    val field: Field = clazz.getDeclaredField(name)
    field.isAccessible = true
    return field.get(clazz)
}