package com.dicoding.picodiploma.storyappdicoding.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.picodiploma.storyappdicoding.data.api.StoryApiService
import com.dicoding.picodiploma.storyappdicoding.data.response.ListStoryItem


class StoryPagingResources(private val storyApiService: StoryApiService) : PagingSource<Int, ListStoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = storyApiService.getStory(page, params.loadSize)

            val listStoryItems = responseData.listStory

            LoadResult.Page(
                data = listStoryItems,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (listStoryItems.isNullOrEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object{
        const val INITIAL_PAGE_INDEX = 1
    }
}