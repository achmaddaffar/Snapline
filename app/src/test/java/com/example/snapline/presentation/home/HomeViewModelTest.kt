package com.example.snapline.presentation.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.snapline.data.DataDummy
import com.example.snapline.data.StoryPagingSource
import com.example.snapline.data.local.entity.StoryEntity
import com.example.snapline.domain.repository.StoryRepository
import com.example.snapline.util.MainDispatcherRule
import com.example.snapline.util.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateStoryEntityList()
        val data: PagingData<StoryEntity> = StoryPagingSource.snapshot(dummyStories)
        val expectedStories = MutableLiveData<PagingData<StoryEntity>>()
        expectedStories.value = data
        Mockito.`when`(storyRepository.getStory()).thenReturn(expectedStories)

        val viewModel = HomeViewModel(storyRepository)
        val actualStories: PagingData<StoryEntity> = viewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories.map { it.toStoryItem() })

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0].toStoryItem(), differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryEntity> = PagingData.from(emptyList())
        val expectedStories = MutableLiveData<PagingData<StoryEntity>>()
        expectedStories.value = data
        Mockito.`when`(storyRepository.getStory()).thenReturn(expectedStories)

        val viewModel = HomeViewModel(storyRepository)
        val actualStories: PagingData<StoryEntity> = viewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories.map { it.toStoryItem() })

        Assert.assertEquals(0, differ.snapshot().size)
    }
}