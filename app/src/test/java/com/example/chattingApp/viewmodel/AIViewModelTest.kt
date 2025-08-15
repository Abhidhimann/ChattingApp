package com.example.chattingApp.viewmodel

import com.example.chattingApp.domain.model.AIChatMessage
import com.example.chattingApp.domain.model.AIChatMessageType
import com.example.chattingApp.domain.repository.ChatRepository
import com.example.chattingApp.presentation.ui.screens.aichatbot.AIChatBotScreenEvent
import com.example.chattingApp.presentation.viewmodels.AIViewModel
import com.example.chattingApp.utils.AIChatBotException
import com.example.chattingApp.utils.ResultResponse
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AIViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ChatRepository
    private lateinit var viewModel: AIViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = AIViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `SendQuery success updates state`() = runTest {
        coEvery { repository.getChatBotResponse(any()) } returns ResultResponse.Success(Unit)

        viewModel.onEvent(AIChatBotScreenEvent.SendQuery("Hello"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.state.isLoading)
        assertFalse(viewModel.state.isSomeError)
        assertFalse(viewModel.state.isLimitExceed)
    }

    @Test
    fun `SendQuery fails with GeneralException sets isSomeError`() = runTest {
        coEvery { repository.getChatBotResponse(any()) } returns ResultResponse.Failed(
            AIChatBotException.GeneralException("Error")
        )

        viewModel.onEvent(AIChatBotScreenEvent.SendQuery("Hi"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.state.isLoading)
        assertTrue(viewModel.state.isSomeError)
    }



    @Test
    fun `observeAIChatMessages updates state`() = runTest {
        val messages = listOf(
            AIChatMessage(1, "user", "msg1", System.currentTimeMillis(), AIChatMessageType.OUTGOING)
        )
        coEvery { repository.observeAIChatMessages() } returns flowOf(messages)

        viewModel.onEvent(AIChatBotScreenEvent.ObserverAIChatMessages)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(messages, viewModel.state.aiChatMessages)
    }
}