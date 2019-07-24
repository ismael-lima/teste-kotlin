package com.ismael.movieapp.injection

import java.lang.annotation.Documented
import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

@Documented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)