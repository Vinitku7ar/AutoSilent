package com.example.autosilent.data.local


import androidx.room.*
import com.example.autosilent.data.local.SilentRule
import kotlinx.coroutines.flow.Flow

@Dao
interface RuleDao {

    @Insert
    suspend fun insert(rule: SilentRule): Long

    @Update
    suspend fun update(rule: SilentRule)

    @Delete
    suspend fun delete(rule: SilentRule)

    @Query("SELECT * FROM silent_rules ORDER BY id DESC")
    fun getAllRules(): Flow<List<SilentRule>>

    @Query("SELECT * FROM silent_rules WHERE id = :ruleId")
    suspend fun getRuleById(ruleId: Long): SilentRule?
}