package com.example.accountbook.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.accountbook.data.db.AccountBookDbHelper
import com.example.accountbook.data.mapper.DomainToPresenterMapper
import com.example.accountbook.data.mapper.PresenterToDomainMapper
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.di.IoDispatcher
import com.example.accountbook.domain.model.CalendarItem
import com.example.accountbook.domain.model.HistoriesListItem
import com.example.accountbook.domain.model.HistoriesTotalData
import com.example.accountbook.domain.repository.AccountRepository
import com.example.accountbook.utils.dateToYearMonthDay
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val dbHelper: AccountBookDbHelper,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val presenterToDomainMapper: PresenterToDomainMapper,
    private val domainToPresenterMapper: DomainToPresenterMapper
) : AccountRepository {

    companion object {
        const val TAG = "AccountRepositoryImpl"
    }

    override suspend fun getHistoriesTotalData(
        isExpense: Int,
        start: Long,
        end: Long
    ): HistoriesTotalData {
        return withContext(dispatcher) {
            dbHelper.getHistoriesTotalData(isExpense, start, end)
        }
    }

    override suspend fun getAllCategories(isExpense: Int): List<Categories> {
        return withContext(dispatcher) {
            dbHelper.getAllCategories(isExpense)
        }
    }

    override suspend fun getAllPayments(): List<Payments> {
        return withContext(dispatcher) {
            dbHelper.getAllPayments()
        }
    }

    override suspend fun deleteAll(tableName: String) {
        withContext(dispatcher) {
            dbHelper.deleteAll(tableName)
        }
    }

    override suspend fun getSumPrice(isExpense: Int, start: Long, end: Long): Long {
        return withContext(dispatcher) {
            dbHelper.getSumPrice(isExpense, start, end)
        }
    }

    override suspend fun insertHistory(historiesListItem: HistoriesListItem) {
        withContext(dispatcher) {
            dbHelper.insertHistory(presenterToDomainMapper.getHistories(historiesListItem))
        }
    }

    override suspend fun updateHistory(historiesListItem: HistoriesListItem) {
        withContext(dispatcher) {
            dbHelper.updateHistory(presenterToDomainMapper.getHistories(historiesListItem))
        }
    }

    override suspend fun deleteHistory(id: Int) {
        withContext(dispatcher) {
            dbHelper.deleteHistory(id)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getCalendarItems(
        isExpense: Int,
        start: Long,
        end: Long,
        year: Int,
        month: Int,
        day: Int
    ): List<CalendarItem>{
        Log.e(TAG, "getCalendarItems: $year $month $day", )
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
        return withContext(dispatcher) {
            val dayHashMap = dbHelper.getHistoriesTotalDataGroupByDay(isExpense, start, end)
            dayHashMap.forEach {
                Log.e(TAG, "getCalendarItems: $it", )
            }
            tempCalendarItem.map { calendarItem ->
                with(calendarItem){
                    val dayString = "${this.year} ${this.month} ${this.day}"
                    if (dayHashMap.containsKey(dayString)){
                        Log.e(TAG, "getCalendarItems: yes", )
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


}