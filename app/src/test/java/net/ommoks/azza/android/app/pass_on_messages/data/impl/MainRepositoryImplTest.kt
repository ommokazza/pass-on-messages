package net.ommoks.azza.android.app.pass_on_messages.data.impl

import android.content.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import net.ommoks.azza.android.app.pass_on_messages.common.Constants
import net.ommoks.azza.android.app.pass_on_messages.data.MainRepository
import net.ommoks.azza.android.app.pass_on_messages.data.datasource.FileDataSource
import net.ommoks.azza.android.app.pass_on_messages.data.model.Filter
import net.ommoks.azza.android.app.pass_on_messages.data.model.FilterLog
import org.junit.After
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
        val filter = Filter("id_001", "test_rule", mutableListOf(), "1234")
        mockMainRepository.updateLastTimestamp(filter, 100)
        assertTrue(100L == mockMainRepository.getLastTimestamp(filter))

        mockMainRepository.updateLastTimestamp(filter, 200)
        assertTrue(200L == mockMainRepository.getLastTimestamp(filter))

        mockMainRepository.updateLastTimestamp(filter, 300)
        mockMainRepository.updateLastTimestamp(filter, 400)
        mockMainRepository.updateLastTimestamp(filter, 500)
        mockMainRepository.updateLastTimestamp(filter, 600)
        mockMainRepository.updateLastTimestamp(filter, 700)
        mockMainRepository.updateLastTimestamp(filter, 800)
        mockMainRepository.updateLastTimestamp(filter, 900)
        mockMainRepository.updateLastTimestamp(filter, 1000)
        mockMainRepository.updateLastTimestamp(filter, 1100)

        assertTrue(1100L == mockMainRepository.getLastTimestamp(filter))

        val filterLog = Json.decodeFromString<FilterLog>(mockFileDataSource.fileMap.get(filter.id) ?: "")
        assertTrue(Constants.MAX_TIMESTAMP_COUNT == filterLog.timestamps.size)
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
}
