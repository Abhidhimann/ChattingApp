package com.example.chattingApp.repository

import android.content.SharedPreferences
import com.example.chattingApp.data.local.dao.AIChatMessageDao
import com.example.chattingApp.data.local.entity.AIChatMessageEntity
import com.example.chattingApp.data.remote.dto.MessageResponse
import com.example.chattingApp.data.remote.dto.SingleChatResponse
import com.example.chattingApp.data.remote.dto.UserSummaryResponse
import com.example.chattingApp.data.remote.services.aichat.AiChatService
import com.example.chattingApp.data.remote.services.chatsocket.ChatSocketService
import com.example.chattingApp.data.remote.services.singlechat.SingleChatService
import com.example.chattingApp.data.repository.ChatRepositoryImpl
import com.example.chattingApp.domain.model.AIChatMessage
import com.example.chattingApp.domain.model.Message
import com.example.chattingApp.domain.model.MessageStatus
import com.example.chattingApp.domain.model.MessageType
import com.example.chattingApp.domain.model.UserSummary
import com.example.chattingApp.utils.ResultResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: ChatRepositoryImpl
    private val singleChatService = mockk<SingleChatService>()
    private val chatSocketService = mockk<ChatSocketService>()
    private val aiChatService = mockk<AiChatService>()
    private val aiChatMessageDao = mockk<AIChatMessageDao>()
    private val appPrefs = mockk<SharedPreferences>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = ChatRepositoryImpl(
            singleChatService,
            chatSocketService,
            aiChatService,
            aiChatMessageDao,
            appPrefs
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun mockUserPrefs(user: UserSummary) {
        every { appPrefs.getString("user_id", "") } returns user.userId
        every { appPrefs.getString("profile_url", "") } returns user.profileImageUrl
        every { appPrefs.getString("user_name", "") } returns user.name
    }

    @Test
    fun `getConversationDetails success returns conversation`() = runTest {
        val user = UserSummary("John", "url", "user1")
        mockUserPrefs(user)

        val user1 = UserSummaryResponse("John", "http://url", "user1")
        val user2 = UserSummaryResponse("Ram", "http://url2", "user2")

        val singleChat = SingleChatResponse(chatId = "chat1", updatedAt = null, participantIds = emptyList(), originator = user1, recipient = user2 )
        coEvery { singleChatService.getSingleChat("chat1") } returns ResultResponse.Success(singleChat)

        val result = repository.getConversationDetails("chat1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(result is ResultResponse.Success)
        assertEquals("chat1", (result as ResultResponse.Success).data.conversationId)
    }

    @Test
    fun `observeMessages returns mapped messages`() = runTest {
        val user = UserSummary("John", "url", "user1")
        mockUserPrefs(user)

        val messageDto =  MessageResponse(
            messageId = "",
            textContent = "How are you",
            senderId = "",
            timeStamp = 0,
            conversationId = "",
        )
        coEvery { chatSocketService.observeMessages("conv1") } returns flowOf(messageDto)

        val messages = repository.observeMessages("conv1").toList()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, messages.size)
        assertEquals("How are you", messages[0].textContent)
    }

    @Test
    fun `sendMessage invokes chatSocketService`() = runTest {
        val message =  Message(
            messageId = "1",
            textContent = "How are you",
            senderId = "",
            status = MessageStatus.INITIAL,
            timeStamp = 0,
            conversationId = "",
            type = MessageType.OUTGOING
        )
        coEvery { chatSocketService.sendMessage(any()) } returns ResultResponse.Success("DocId5")

        repository.sendMessage(message)
        coVerify { chatSocketService.sendMessage(message.toMessageDto()) }
    }

    @Test
    fun `observeAIChatMessages maps entities to model`() = runTest {
        val entity = AIChatMessageEntity(id = 1, role = "user", content = "Hi", type = 0, timestamp = 123L)
        every { aiChatMessageDao.observeAIChatMessages() } returns flowOf(listOf(entity))

        val result = repository.observeAIChatMessages().first()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, result.size)
        assertEquals("Hi", result[0].content)
    }

    @Test
    fun `deleteAIChatMessage success returns Success`() = runTest {
        val aiMessage = AIChatMessage(1, "", "how are you")
        coEvery { aiChatMessageDao.deleteAIChatMessages(any()) } returns Unit

        val result = repository.deleteAIChatMessage(listOf(aiMessage))
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(result is ResultResponse.Success)
    }

    @Test
    fun `clearAIChatConversation success returns Success`() = runTest {
        every { aiChatMessageDao.clearAIChatConversation() } returns Unit

        val result = repository.clearAIChatConversation()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(result is ResultResponse.Success)
    }

    @Test
    fun `getChatBotResponse returns Failed when ai_model is null`() = runTest {
        val aiMessage = AIChatMessage(1, "", "how are you")
        every { appPrefs.getString("ai_model", null) } returns null
        coEvery { aiChatMessageDao.insertAIChatMessage(any()) } returns Unit

        val result = repository.getChatBotResponse(listOf(aiMessage))
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(result is ResultResponse.Failed)
    }
}
