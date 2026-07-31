package com.example.autosilent.data.repository

import com.example.autosilent.data.local.RuleDao
import com.example.autosilent.data.local.SilentRule
import kotlinx.coroutines.flow.Flow

class RuleRepository(private val ruleDao: RuleDao) {

    val allRules: Flow<List<SilentRule>> = ruleDao.getAllRules()

    suspend fun insertRule(rule: SilentRule): Long {
        return ruleDao.insert(rule)
    }

    suspend fun updateRule(rule: SilentRule) {
        ruleDao.update(rule)
    }

    suspend fun deleteRule(rule: SilentRule) {
        ruleDao.delete(rule)
    }

    suspend fun getRuleById(ruleId: Long): SilentRule? {
        return ruleDao.getRuleById(ruleId)
    }
}