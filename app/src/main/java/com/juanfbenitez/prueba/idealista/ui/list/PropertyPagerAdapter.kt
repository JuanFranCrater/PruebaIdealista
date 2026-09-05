package com.juanfbenitez.prueba.idealista.ui.list

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Backs the ViewPager2 in [PropertyListFragment] with two fixed pages: Buy (sale) and Rent.
 */
class PropertyPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val operations = listOf(PropertyOperation.SALE, PropertyOperation.RENT)

    override fun getItemCount(): Int = operations.size

    override fun createFragment(position: Int): Fragment =
        PropertyOperationPageFragment.newInstance(operations[position])

    fun operationAt(position: Int): String = operations[position]

    fun positionOf(operation: String): Int = operations.indexOf(operation).coerceAtLeast(0)
}
