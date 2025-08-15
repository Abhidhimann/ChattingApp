package com.example.chattingApp.viewmodel

import com.example.chattingApp.domain.model.Conversation
import com.example.chattingApp.domain.repository.ConversationRepository
import com.example.chattingApp.presentation.ui.screens.chatlistscreen.ChatListScreenEvent
import com.example.chattingApp.presentation.viewmodels.ChatListViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatListViewModelTest {

    // testDispatcher → A special coroutine dispatcher for testing, gives you control over when coroutines run (via advanceUntilIdle()).
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ConversationRepository
    private lateinit var viewModel: ChatListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Replaces the real Dispatchers.Main with our test dispatcher so viewModelScope.launch {} runs in a controlled way.
        repository = mockk<ConversationRepository>()
        viewModel = ChatListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        // Restores the real Dispatchers.Main so other tests aren’t affected.
    }

    @Test
    fun `ObserveConversations updates state with new conversation`() = runTest {
        // initialize fake data and mock
        val conversation1 = Conversation(
            conversationId = "1",
            title = "Chat 1",
            unReadMessageCount = 1,
            lastMessage = "",
            participantsIds = emptyList()
        )
        val flow = flow {
            emit(conversation1)
        }

        // Used in MockK to stub suspend functions
        // when the ViewModel calls repository.observerConversations(), it will get this fake flow instead of going to the real DB/network
        coEvery { repository.observerConversations() } returns flow

        // Act
        viewModel.onEvent(ChatListScreenEvent.ObserveConversations)
        testDispatcher.scheduler.advanceUntilIdle()
        // testDispatcher.scheduler.advanceUntilIdle() is the part that tells your test dispatcher to run all queued coroutine tasks until there’s nothing left to run.

        // Assert
        assertEquals(1, viewModel.state.conversations.size)
//        assertEquals("1", viewModel.state.conversations[0].conversationId)
//        assertEquals("Chat 1", viewModel.state.conversations[0].title)
    }

    @Test
    fun `ObserveConversations updates existing conversation`() = runTest {
        val conversation1 = Conversation(
            conversationId = "1",
            title = "Chat 1",
            unReadMessageCount = 1,
            lastMessage = "",
            participantsIds = emptyList()
        )
        val updatedConversation = Conversation(
            conversationId = "1",
            title = "Chat 2",
            unReadMessageCount = 1,
            lastMessage = "",
            participantsIds = emptyList()
        )

        // First flow emits conversation1, second flow emits updatedConversation
        val flow = flow {
            emit(conversation1)
            emit(updatedConversation)
        }
        coEvery { repository.observerConversations() } returns flow

        // Act
        viewModel.onEvent(ChatListScreenEvent.ObserveConversations)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(1, viewModel.state.conversations.size)
        assertEquals("Chat 2", viewModel.state.conversations[0].title)
    }
}
