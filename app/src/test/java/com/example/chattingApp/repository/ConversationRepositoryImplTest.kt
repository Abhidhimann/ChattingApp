package com.example.chattingApp.repository

import android.content.SharedPreferences
import com.example.chattingApp.data.remote.dto.SingleChatResponse
import com.example.chattingApp.data.remote.dto.UserSummaryResponse
import com.example.chattingApp.data.remote.services.singlechat.SingleChatService
import com.example.chattingApp.data.repository.ConversationRepositoryImpl
import com.example.chattingApp.domain.repository.ConversationRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConversationRepositoryImplTest {

    private lateinit var repository: ConversationRepository
    private val singleChatService = mockk<SingleChatService>()
    private val appPrefs = mockk<SharedPreferences>()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = ConversationRepositoryImpl(singleChatService, appPrefs)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `observerConversations returns empty flow when user is missing`() = runTest {
        // SharedPreferences returns null for all keys
        every { appPrefs.getString("user_id", "") } returns null
        every { appPrefs.getString("profile_url", "") } returns null
        every { appPrefs.getString("user_name", "") } returns null

        val flow = repository.observerConversations()
        val collected = flow.toList()

        assertTrue(collected.isEmpty())
    }

    @Test
    fun `observerConversations maps API response to Conversation`() = runBlocking {
        val user1 = UserSummaryResponse("John", "http://url", "user1")
        val user2 = UserSummaryResponse("Ram", "http://url2", "user2")

        val user = user1.toUserSummary()
        // Mock SharedPreferences to return a valid user
        every { appPrefs.getString("user_id", "") } returns user.userId
        every { appPrefs.getString("profile_url", "") } returns user.profileImageUrl
        every { appPrefs.getString("user_name", "") } returns user.name

        // Mocking API response
        val apiChat = SingleChatResponse(chatId = "chat1", updatedAt = null, participantIds = emptyList(), originator = user1, recipient = user2 )
        coEvery { singleChatService.observeSingleChats(user.userId) } returns flowOf(apiChat)

        val result = repository.observerConversations().first()

        assertNotNull(result)
        assertEquals("chat1", result?.conversationId)
    }

    // has we don't have usecase now, omitting domain/usercase ut
}