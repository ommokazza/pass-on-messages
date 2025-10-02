package net.ommoks.azza.android.app.passonnotifications

import net.ommoks.azza.android.app.passonnotifications.data.model.Filter
import net.ommoks.azza.android.app.passonnotifications.data.model.FilterRule
import net.ommoks.azza.android.app.passonnotifications.data.model.RuleType
import net.ommoks.azza.android.app.passonnotifications.data.model.isMatched
import org.junit.Test
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
        Filter(
            "03",
            "MG새마을금고 - 출금",
            mutableListOf(
                FilterRule("03-01", RuleType.TitleIs, "MG새마을금고"),
                FilterRule("03-02", RuleType.TextContains, "출금"),
            ),
            "01012345678"
        ),
    )

    private val notFilter = mutableListOf(
        Filter(
            "04",
            "광고아님",
            mutableListOf(FilterRule("04-01", RuleType.TextNotContains, "(광고)")),
            "01012345678"
        ),
    )


    @Test
    fun testTextContainsMatched() {
        val matchedFilter = filters.filter { f ->
            f.isMatched(
                "삼성카드 승인안내",
                "[Web발신] 삼성1234승인 김**\n10,000원 일시불\n2/14 12:34 초콜릿공장\n누적123,456원"
            )
        }
        assertTrue(matchedFilter.size == 1 && matchedFilter[0].id == "01")
    }

    @Test
    fun testTextContainsNotMatched() {
        val matchedFilter = filters.filter { f ->
            f.isMatched(
                "삼성카드 승인안내",
                "[Web발신] 삼성9999승인 김**\n10,000원 일시불\n2/14 12:34 초콜릿공장\n누적123,456원"
            )
        }
        assertTrue(matchedFilter.isEmpty())
    }

    @Test
    fun testTitleIsMatched() {
        val matchedFilter = filters.filter { f ->
            f.isMatched(
                "삼성카드",
                "[Web발신]\n(광고)[삼성카드] 무이자할부 LINK 혜택!!"
            )
        }
        assertTrue(matchedFilter.size == 1 && matchedFilter[0].id == "02")
    }

    @Test
    fun testTitleIsNotMatched() {
        val matchedFilter = filters.filter { f ->
            f.isMatched(
                "삼성카드 승인안내",
                "[Web발신]\n(광고)[삼성카드] 무이자할부 LINK 혜택!!"
            )
        }
        assertTrue(matchedFilter.isEmpty())
    }

    @Test
    fun testTitleAndTextMatched() {
        val matchedFilter = filters.filter { f ->
            f.isMatched(
                "MG새마을금고",
                "[Web발신]\n<새마을금고>1234**5\n아파트관리비 출금123,456"
            )
        }
        assertTrue(matchedFilter.size == 1 && matchedFilter[0].id == "03")
    }

    @Test
    fun testTitleAndTextNotMatched() {
        val matchedFilter = filters.filter { f ->
            f.isMatched(
                "MG새마을금고",
                "[Web발신]\n<새마을금고>1234**5\n당근거래 입금70,000"
            )
        }
        assertTrue(matchedFilter.isEmpty())
    }

    @Test
    fun testTextNotContainsMatched() {
        val matchedFilter = notFilter.filter { f ->
            f.isMatched(
                "박정민수학과학",
                "[Web발신]\n이번 주 토요일에는 보강수업이 있습니다. 감사합니다."
            )
        }
        assertTrue(matchedFilter.size == 1 && matchedFilter[0].id == "04")
    }

    @Test
    fun testTextNotContainsNotMatched() {
        val matchedFilter = notFilter.filter { f ->
            f.isMatched(
                "삼성카드",
                "[Web발신]\n(광고)[삼성카드] 무이자할부 LINK 혜택!!"
            )
        }
        assertTrue(matchedFilter.isEmpty())
    }
}
