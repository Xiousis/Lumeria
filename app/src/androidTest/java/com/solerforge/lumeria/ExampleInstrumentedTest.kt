package com.solerforge.lumeria

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.data.PlayerDataRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Instrumented test for PlayerDataRepository.
 */
@RunWith(AndroidJUnit4::class)
class PlayerDataRepositoryTest {
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.solerforge.lumeria", appContext.packageName)
    }

    @Test
    fun testSaveAndLoad() = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val repository = PlayerDataRepository(appContext)
        
        val testData = PlayerData(level = 42, gold = 999)
        repository.savePlayerData(testData, 1)
        
        val loadedData = repository.getPlayerDataFlow(1).first()
        assertNotNull(loadedData)
        assertEquals(42, loadedData?.level)
        assertEquals(999, loadedData?.gold)
    }
}
