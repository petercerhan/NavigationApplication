package com.example.navigationapplication.library

import android.util.Log
import androidx.fragment.app.Fragment
import com.example.navigationapplication.R

private const val TAG = "FragmentBackStack"

/**
 * Logs [FragmentManager] back-stack entries and attached fragments for learning / debugging.
 * Uses [Fragment.getParentFragmentManager] (same manager used for [FragmentTransaction.replace]).
 */
fun Fragment.logBackStackState(reason: String) {
    val fm = parentFragmentManager
    val lines = buildList {
        add("reason=$reason")
//        add("FragmentManager=${fm.javaClass.name}")
        add("backStackEntryCount=${fm.backStackEntryCount}")
        for (i in 0 until fm.backStackEntryCount) {
            val e = fm.getBackStackEntryAt(i)
            add("  backStack[$i] name=${e.name} id=${e.id}")
        }
        add("fragments.size=${fm.fragments.size}")
        fm.fragments.forEachIndexed { index, f ->
            add(
                "  fragments[$index] ${f.javaClass.simpleName} " +
                    "tag=${f.tag} id=${f.id} isAdded=${f.isAdded} isVisible=${f.isVisible}"
            )
        }
        val host = fm.findFragmentById(R.id.main)
        add("findFragmentById(R.id.main)=${host?.javaClass?.simpleName}")
    }
    Log.d(TAG, lines.joinToString("\n"))
}
