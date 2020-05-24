package ch.instantpastime.nback.ui

import androidx.annotation.StringRes

data class TranslatableValue<T>(val value: T, @StringRes val langId: Int)
