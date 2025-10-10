package net.ommoks.azza.android.app.pass_on_messages.data.impl

import android.content.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import net.ommoks.azza.android.app.pass_on_messages.common.Constants
import net.ommoks.azza.android.app.pass_on_messages.data.MainRepository
import net.ommoks.azza.android.app.pass_on_messages.data.datasource.FileDataSource
import net.ommoks.azza.android.app.pass_on_messages.data.model.FilterLog
import net.ommoks.azza.android.app.pass_on_messages.data.model.FilterModel
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class MainRepositoryImplTest {

    private lateinit var mockMainRepository : MainRepository
    private val context = mock(Context::class.java)
    private val mockFileDataSource = MockFileDataSource()

    @Before
    fun setUp() {
        mockMainRepository = MainRepositoryImpl(context, mockFileDataSource)
    }

    @After
    fun tearDown() {
        mockFileDataSource.fileMap.clear()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testFilterLog() = runTest {
        val fModel = FilterModel("id_001", "test_rule", mutableListOf(), "1234")
        mockMainRepository.updateLastTimestamp(fModel, 100L)
        assertTrue(100L == mockMainRepository.getLastTimestamp(fModel))

        mockMainRepository.updateLastTimestamp(fModel, 200L)
        assertTrue(200L == mockMainRepository.getLastTimestamp(fModel))

        mockMainRepository.updateLastTimestamp(fModel, 300L)
        mockMainRepository.updateLastTimestamp(fModel, 400L)
        mockMainRepository.updateLastTimestamp(fModel, 500L)
        mockMainRepository.updateLastTimestamp(fModel, 600L)
        mockMainRepository.updateLastTimestamp(fModel, 700L)
        mockMainRepository.updateLastTimestamp(fModel, 800L)
        mockMainRepository.updateLastTimestamp(fModel, 900L)
        mockMainRepository.updateLastTimestamp(fModel, 1000L)
        mockMainRepository.updateLastTimestamp(fModel, 1100L)

        assertTrue(1100L == mockMainRepository.getLastTimestamp(fModel))

        val jsonStr = mockFileDataSource.readFromInternalTextFile(fModel.logFilename())
        val filterLog = Json.decodeFromString<FilterLog>(jsonStr)
        assertTrue(Constants.MAX_TIMESTAMP_COUNT == filterLog.timestamps.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testFilterLogDeleted() = runTest {
        val fModel1 = FilterModel("id_001", "test_rule", mutableListOf(), "1234")
        val fModel2 = FilterModel("id_002", "test_rule", mutableListOf(), "1234")
        val fModel3 = FilterModel("id_003", "test_rule", mutableListOf(), "1234")
        mockMainRepository.saveFilters(mutableListOf(fModel1, fModel2, fModel3))

        mockMainRepository.updateLastTimestamp(fModel1, 100)
        mockMainRepository.updateLastTimestamp(fModel2, 200)
        mockMainRepository.updateLastTimestamp(fModel3, 300)

        // Remove a filter
        mockMainRepository.saveFilters(mutableListOf(fModel1, fModel3))

        assertNull(mockMainRepository.getLastTimestamp(fModel2))
    }
}

class MockFileDataSource : FileDataSource {
    val fileMap = HashMap<String, String>()

    override suspend fun writeToInternalTextFile(filename: String, content: String, append: Boolean) {
        fileMap.put(filename, content)
    }

    override suspend fun readFromInternalTextFile(filename: String): String {
        return fileMap.get(filename) ?: ""
    }

    override fun deleteFile(filename: String) {
        fileMap.remove(filename)
    }
}
