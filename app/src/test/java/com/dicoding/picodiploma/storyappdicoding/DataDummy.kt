package com.dicoding.picodiploma.storyappdicoding

import com.dicoding.picodiploma.storyappdicoding.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0 .. 10) {
            val story = ListStoryItem(
                id = i.toString(),
                name = "author $i",
                description = "Story Description $i",
                photoUrl = "http://example.com/photo$i.jpg",
                lat = null,
                lon = null
            )
            items.add(story)
        }
        return items
    }
}