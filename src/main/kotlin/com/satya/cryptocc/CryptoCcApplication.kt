package com.satya.cryptocc

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import javax.sql.DataSource

@SpringBootApplication
class CryptoCcApplication{

	@Value("\${connectionUrl}")
	private lateinit var connectionUrl : String

	@Bean(destroyMethod = "close")
	fun dataSource(): DataSource {
		val hikariConfig = HikariConfig().apply {
			jdbcUrl = connectionUrl
			addDataSourceProperty("cachePrepStmts" , "true")
			addDataSourceProperty("prepStmtCacheSize" , "250")
			addDataSourceProperty("prepStmtCacheSqlLimit" , "2048")
		}
		return HikariDataSource(hikariConfig)
	}
}

fun main(args: Array<String>) {
	runApplication<CryptoCcApplication>(*args)
}
