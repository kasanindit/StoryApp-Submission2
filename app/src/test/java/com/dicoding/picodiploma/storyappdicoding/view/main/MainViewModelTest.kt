package com.dicoding.picodiploma.storyappdicoding.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.picodiploma.storyappdicoding.DataDummy
import com.dicoding.picodiploma.storyappdicoding.MainDispatcherRule
import com.dicoding.picodiploma.storyappdicoding.adapter.StoryAdapter
import com.dicoding.picodiploma.storyappdicoding.data.repository.StoryRepository
import com.dicoding.picodiploma.storyappdicoding.data.repository.UserRepository
import com.dicoding.picodiploma.storyappdicoding.data.response.ListStoryItem
import com.dicoding.picodiploma.storyappdicoding.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock private lateinit var storyRepository: StoryRepository

    private lateinit var mainViewModel: MainViewModel


    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data = StoryPagingResource.snapshot(dummyStory)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStories)

        val mainViewModel = MainViewModel(userRepository ,storyRepository)
        val actualStory: PagingData<ListStoryItem> = mainViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStory)

        val mainViewModel = MainViewModel(userRepository ,storyRepository)
        val actualStory: PagingData<ListStoryItem> = mainViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertEquals(0, differ.snapshot().size)

    }

}

class StoryPagingResource : PagingSource<Int, LiveData<List<ListStoryItem>>>(){
    companion object{
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem>{
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object  : ListUpdateCallback{
    override fun onInserted(position: Int, count: Int) {}

    override fun onRemoved(position: Int, count: Int) {}

    override fun onMoved(fromPosition: Int, toPosition: Int) {}

    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

