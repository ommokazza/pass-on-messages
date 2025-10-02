package net.ommoks.azza.android.app.passonnotifications

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class FilterItemTest {

    private val filters = mutableListOf(
        Filter(
            "01",
            "삼성카드 승인안내",
            mutableListOf(FilterRule("01-01", RuleType.TextContains, "삼성1234승인")),
            "01012345678"
        ),
        Filter(
            "02",
            "제목 - 삼성카드",
            mutableListOf(FilterRule("02-01", RuleType.TitleIs, "삼성카드")),
            "01012345678"
        ),
    )

    @Test
    fun testTextContainsMatched() {
        val isMatched = filters.stream().anyMatch { filter ->
            filter.isMatched(
                "삼성카드 승인안내",
                "[Web발신] 삼성1234승인 김**\n10,000원 일시불\n2/14 12:34 초콜릿공장\n누적123,456원"
            )
        }
        assertTrue(isMatched)
    }

    @Test
    fun testTextContainsNotMatched() {
        val isMatched = filters.stream().anyMatch { filter ->
            filter.isMatched(
                "삼성카드 승인안내",
                "[Web발신] 삼성9999승인 김**\n10,000원 일시불\n2/14 12:34 초콜릿공장\n누적123,456원"
            )
        }
        assertFalse(isMatched)
    }

    @Test
    fun testTitleIsMatched() {
        val isMatched = filters.stream().anyMatch { filter ->
            filter.isMatched(
                "삼성카드",
                "[Web발신]\n(광고)[삼성카드] 무이자할부 LINK 혜택!!"
            )
        }
        assertTrue(isMatched)
    }

    @Test
    fun testTitleIsNotMatched() {
        val isMatched = filters.stream().anyMatch { filter ->
            filter.isMatched(
                "삼성카드 승인안내",
                "[Web발신]\n(광고)[삼성카드] 무이자할부 LINK 혜택!!"
            )
        }
        assertFalse(isMatched)
    }
}
