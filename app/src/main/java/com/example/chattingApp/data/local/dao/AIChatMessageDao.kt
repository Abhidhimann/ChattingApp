package com.example.chattingApp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chattingApp.data.local.entity.AIChatMessageEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface AIChatMessageDao {
    @Query("SELECT * FROM ai_chat_messages ORDER BY timestamp DESC")
    fun observeAIChatMessages(): Flow<List<AIChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAIChatMessage(aiChatMessage: AIChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAIChatMessages(aiChatMessages: List<AIChatMessageEntity>)

    @Query("DELETE FROM ai_chat_messages")
    fun clearAIChatConversation()

    @Delete
    suspend fun deleteAIChatMessages(aiChatMessages: List<AIChatMessageEntity>)
}