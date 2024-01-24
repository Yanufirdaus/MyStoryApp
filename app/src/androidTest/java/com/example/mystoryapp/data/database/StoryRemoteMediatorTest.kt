package com.example.mystoryapp.data.database

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mystoryapp.data.response.*
import com.example.mystoryapp.data.retrofit.ApiService
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call

@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {

    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runBlocking {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
        )
        val pagingState = PagingState<Int, ListStoryItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}
class FakeApiService : ApiService {

    override fun register(name: String, email: String, password: String): Call<RegisterResponse> {
        TODO("Not yet implemented")
    }

    override fun login(email: String, password: String): Call<LoginResponse> {
        TODO("Not yet implemented")
    }

    override fun getStories(): Call<ListStoryResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getStories(page: Int, size: Int): ListStoryResponse {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                "photoUrl $i",
                "createdAt + $i",
                "name $i",
                "description $i",
                i.toDouble(),
                i.toString(),
                i.toDouble(),
            )
            items.add(quote)
        }

        val startIndex = (page - 1) * size
        val endIndex = minOf(startIndex + size, items.size)

        return ListStoryResponse(items.subList(startIndex, endIndex))
    }

    override fun getDetailStory(id: String): Call<DetailStoryResponse> {
        TODO("Not yet implemented")
    }

    override fun uploadImage(
        file: MultipartBody.Part,
        description: RequestBody
    ): Call<UploadResponse> {
        TODO("Not yet implemented")
    }

    override fun uploadImageWithLocation(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float?,
        lon: Float?
    ): Call<UploadResponse> {
        TODO("Not yet implemented")
    }

    override fun getStoriesWithLocation(location: Int): Call<StoryResponse> {
        TODO("Not yet implemented")
    }
}