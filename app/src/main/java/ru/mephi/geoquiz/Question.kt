package ru.mephi.geoquiz

import androidx.annotation.StringRes

data class Question (@StringRes var textResId: Int, val answer: Boolean, var answered: Boolean = false, var isCheated: Boolean = false)