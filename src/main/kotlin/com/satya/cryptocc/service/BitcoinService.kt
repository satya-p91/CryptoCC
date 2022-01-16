package com.satya.cryptocc.service

import com.satya.cryptocc.dto.TransactionModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Connection
import java.time.OffsetDateTime
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
    private fun getConnection(): Connection? = ds?.let { it.connection }
}