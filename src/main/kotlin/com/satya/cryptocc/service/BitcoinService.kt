package com.satya.cryptocc.service

import com.satya.cryptocc.dto.TransactionModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Connection
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.sql.DataSource

@Service
class BitcoinService {
    @Autowired
    private lateinit var ds: DataSource

    @Throws(Exception::class)
    fun addTransaction(data: TransactionModel): Boolean{
        val conn = getConnection()
        conn?.let {
            val stmt = it.prepareStatement("insert into bitcoin_txn values (?, ?, (select total_assets from bitcoin_txn order by datetime desc limit 1)+?)")
            stmt.setObject(1, OffsetDateTime.parse(data.datetime))
            stmt.setDouble(2,data.amount)
            stmt.setDouble(3,data.amount)
            var result = stmt.executeUpdate() > 0
            conn.close()
            return result
        }
        return false;
    }

    fun validateDateTime(data: String): Pair<Boolean, String?>{
        if (data.isNullOrBlank()) {
            return Pair(false, null)
        }
        try{
            OffsetDateTime.parse(data)
            return Pair(true, null)
        }catch (e: Exception){
            println(data)
            if(!data.contains("T") && data.contains(" ")) {
                var newData = data.replace(" ", "T")
                return try {
                    OffsetDateTime.parse(newData)
                    Pair(true, newData)
                } catch (e: Exception) {
                    println(data)
                    e.printStackTrace()
                    Pair(false, null)
                }
            }
            return Pair(false, null)
        }
    }

    @Throws(Exception::class)
    fun getTotalAmtBetween(datetime: String, datetime2: String): List<TransactionModel>?{
        var data = mutableListOf<TransactionModel>()
        //since postgres return data in UTC time zone, save time of input date, and convert the db result into that timezone
        var dte = OffsetDateTime.parse(datetime)
        var offset = dte.offset
        val conn = getConnection()
        conn?.let {
            val stmt = it.prepareStatement("select distinct on (datetime) max(total_assets), date_trunc('hour', datetime) as datetime from bitcoin_txn where datetime between ? and ? group by datetime,total_assets order by datetime,total_assets desc")
            stmt.setObject(1, OffsetDateTime.parse(datetime))
            stmt.setObject(2, OffsetDateTime.parse(datetime2))
            println()
            val rs = stmt.executeQuery()
            while (rs.next()){
                val d = rs.getObject(2, OffsetDateTime::class.java)
                data.add(TransactionModel(addTimezoneToDbDate(d,offset),rs.getDouble(1)))
            }
            rs.close()
            conn.close()
        }
        return data
    }

    @Throws(Exception::class)
    private fun getTimeZone(datetime: String) = datetime.substring(datetime.lastIndexOf("+"))

    @Throws(Exception::class)
    private fun addTimezoneToDbDate(d: OffsetDateTime, offset: ZoneOffset): String{
        var datetimeStr = d.atZoneSameInstant(ZoneId.ofOffset("UTC",offset))
        return datetimeStr.toString().dropLast(11)
    }

    @Throws(Exception::class)
    private fun getConnection(): Connection? = ds?.let { it.connection }
}