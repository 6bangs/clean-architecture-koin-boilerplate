package org.buffer.android.boilerplate.ui.browse

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Flowable
import org.buffer.android.boilerplate.data.browse.Bufferoo
import org.buffer.android.boilerplate.data.repository.BufferooRepository
import org.buffer.android.boilerplate.ui.R
import org.buffer.android.boilerplate.ui.di.applicationModule
import org.buffer.android.boilerplate.ui.di.browseModule
import org.buffer.android.boilerplate.ui.test.util.BufferooFactory
import org.buffer.android.boilerplate.ui.test.util.RecyclerViewMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import org.koin.test.mock.declareMock

@RunWith(AndroidJUnit4::class)
class BrowseActivityTest: KoinTest {

    @Rule @JvmField
    val activity = ActivityTestRule<BrowseActivity>(BrowseActivity::class.java, false, false)

    var bufferoos: List<Bufferoo> = listOf()

    @Before
    fun setUp() {
        loadKoinModules(applicationModule, browseModule)
        declareMock<BufferooRepository> {
            whenever(getBufferoos()).thenReturn(Flowable.just(bufferoos))
        }
    }

    @Test
    fun activityLaunches() {
        bufferoos = BufferooFactory.makeBufferooList(2)
        activity.launchActivity(null)
    }

    @Test
    fun bufferoosDisplay() {
        bufferoos = BufferooFactory.makeBufferooList(1)
        activity.launchActivity(null)

        checkBufferooDetailsDisplay(bufferoos[0], 0)
    }

    @Test
    fun bufferoosAreScrollable() {
        bufferoos = BufferooFactory.makeBufferooList(20)
        activity.launchActivity(null)

        bufferoos.forEachIndexed { index, bufferoo ->
            onView(withId(R.id.recycler_browse)).perform(RecyclerViewActions.
                    scrollToPosition<RecyclerView.ViewHolder>(index))
            checkBufferooDetailsDisplay(bufferoo, index) }
    }

    private fun checkBufferooDetailsDisplay(bufferoo: Bufferoo, position: Int) {
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_browse).atPosition(position))
                .check(matches(hasDescendant(withText(bufferoo.name))))
        onView(RecyclerViewMatcher.withRecyclerView(R.id.recycler_browse).atPosition(position))
                .check(matches(hasDescendant(withText(bufferoo.title))))
    }

}