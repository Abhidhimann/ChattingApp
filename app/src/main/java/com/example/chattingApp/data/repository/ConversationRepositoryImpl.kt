package com.example.chattingApp.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.chattingApp.data.remote.SingleChatService
import com.example.chattingApp.domain.model.Conversation
import com.example.chattingApp.domain.model.UserSummary
import com.example.chattingApp.utils.tempTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepositoryImpl @Inject constructor(
    private val singleChatService: SingleChatService,
    private val appPrefs: SharedPreferences
) {

    // todo later change it to db
    private var getUser: UserSummary? = run {
        val userId = appPrefs.getString("user_id", "")
        val profileImageUrl = appPrefs.getString("profile_url", "")
        val name = appPrefs.getString("user_name", "")
        if (userId == null || profileImageUrl == null || name == null) return@run null
        return@run UserSummary(name = name, profileImageUrl = profileImageUrl, userId = userId)
    }

    suspend fun observerConversations() = withContext(Dispatchers.IO){
        val selfUser = getUser
        if (selfUser == null) {
            Log.i(tempTag(), "Error in getting user from prefs")
            return@withContext emptyFlow<Conversation>()
        }
        singleChatService.observeSingleChats(selfUser.userId).map {
            Log.i(tempTag(), "got conversation: $it")
            it.toConversation(selfUser)
        }
    }
}