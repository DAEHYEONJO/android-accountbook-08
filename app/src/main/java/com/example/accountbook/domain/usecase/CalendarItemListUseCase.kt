package com.example.accountbook.domain.usecase

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.accountbook.data.repository.AccountRepositoryImpl
import com.example.accountbook.domain.model.CalendarItem
import com.example.accountbook.domain.repository.AccountRepository
import com.example.accountbook.utils.dateToYearMonthDay
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class CalendarItemListUseCase @Inject constructor(private val repository: AccountRepository) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(isExpense: Int,
                                start: Long,
                                end: Long,
                                year: Int,
                                month: Int,
                                day: Int): List<CalendarItem>{
        val tempCalendarItem = ArrayList<CalendarItem>()
        val monthCalendar = Calendar.getInstance()
        monthCalendar.set(year, month-1, day)
        monthCalendar[Calendar.DAY_OF_MONTH] = 1
        val firstDayOfMonth = monthCalendar[Calendar.DAY_OF_WEEK] - 1
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        var secondWeekCount = 0

        // 실제 오늘의 날짜를 확인하기 위함
        val(_, todayMonth, todayDay) = dateToYearMonthDay(Calendar.getInstance().time)

        // 현재 확인하려는 달의 date
        val nowLocalDate = LocalDate.of(year, month, todayDay)
        val nowMonth = nowLocalDate.month.value

        while (tempCalendarItem.size < 42) {
            val weekOfMonth = monthCalendar[Calendar.WEEK_OF_MONTH]

            if (weekOfMonth == 2) secondWeekCount++
            if (secondWeekCount == 8) break

            val curDay = monthCalendar[Calendar.DAY_OF_MONTH]
            val curMonth = monthCalendar[Calendar.MONTH]+1
            val dayOfWeek = monthCalendar[Calendar.DAY_OF_WEEK]

            tempCalendarItem.add(
                CalendarItem(
                    date = monthCalendar.time,
                    year = year,
                    month = curMonth,
                    day = curDay,
                    isSaturday = dayOfWeek==7,
                    isThisMonth = nowMonth == curMonth,
                    isToday = todayMonth==curMonth && todayDay==curDay && (nowMonth==curMonth)
                )
            )
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        val dayHashMap = repository.getCalendarHashMap(isExpense, start, end)
        return tempCalendarItem.map { calendarItem ->
            with(calendarItem){
                val dayString = "${this.year} ${this.month} ${this.day}"
                if (dayHashMap.containsKey(dayString)){
                    Log.e(AccountRepositoryImpl.TAG, "getCalendarItems: yes", )
                    val curCalendarItem = dayHashMap[dayString]
                    incomePrice = curCalendarItem!!.incomePrice
                    expensePrice = curCalendarItem.expensePrice
                    totalPrice = curCalendarItem.totalPrice
                }
            }
            calendarItem
        }
    }
}