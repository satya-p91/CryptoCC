package com.satya.cryptocc.controller

import com.satya.cryptocc.dto.DateIntervalModel
import com.satya.cryptocc.dto.TransactionModel
import com.satya.cryptocc.service.BitcoinService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/btc")
class BitcoinController {

    @Autowired
    private lateinit var btcService: BitcoinService

    @GetMapping("/")
    fun home() : String?{
        return "Welcome to CryptoCC Project!"
    }

    @PostMapping("/addTxn")
    fun addTxn(@RequestBody data: TransactionModel): Map<String, Any?>{
        val resp = mutableMapOf<String,Any?>()
        if (data.amount <= 0.0){
            resp["success"] = false
            resp["message"] = "This transaction have zero or less amount."
            return  resp
        }
        val (success, newDate) = btcService.validateDateTime(data.datetime)
        if(!success){
            resp["success"] = false
            resp["message"] = "This transaction have non parsable datetime, kindly provide date in format: 2022-01-13T01:40:00+01:30"
            return  resp
        }
        newDate?.let {
            data.datetime = newDate
        }
        return try {
            val success = btcService.addTransaction(data)
            resp["success"] = success
            resp["message"] = if (success) "Transaction inserted successfully!" else "Failed to insert this transaction"
            resp
        }catch (e: Exception){
            e.printStackTrace()
            resp["success"] = false
            resp["message"] = e.localizedMessage
            resp
        }
    }

    @PostMapping("/getTotalAssetsBetweenInterval")
    fun getTotalAssetsBetweenInterval(@RequestBody req: DateIntervalModel): Map<String, Any?>{
        val resp = mutableMapOf<String,Any?>()

        val (success, newDate) = btcService.validateDateTime(req.startDateTime)
        val (success1, newDate1) = btcService.validateDateTime(req.endDateTime)
        if(!success || !success1){
            resp["success"] = false
            resp["message"] = "Datetime not parsable, kindly provide date in format: 2022-01-13T01:40:00+01:30"
            return  resp
        }
        newDate?.let {
            req.startDateTime = newDate
        }
        newDate1?.let {
            req.endDateTime = newDate1
        }

        return try {
            val data = btcService.getTotalAmtBetween(req.startDateTime, req.endDateTime)
            resp["success"] = true
            resp["data"] = data
            resp
        }catch (e : Exception){
            e.printStackTrace()
            resp["success"] = false
            resp["message"] = e.localizedMessage
            resp
        }
    }

}