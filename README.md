# CryptoCC

#### This project contains two apis /btc/getTotalAssetsBetweenInterval and /btc/addTxn. For data storage, it uses `PostgreSQL` and `TimeScaleDB`.

### /btc/getTotalAssetsBetweenInterval [POST]
Content-Type : application/json

sample request body: 

```json
{
    "startDateTime": "2022-01-13T10:40:01+05:30",
    "endDateTime": "2022-01-13T20:40:01+05:30"
}
```

sample response data: 

```json
{
    "success": true,
    "data": [
        {
            "datetime": "2022-01-13T10:00+05:30",
            "amount": 3.0
        },
        {
            "datetime": "2022-01-13T11:00+05:30",
            "amount": 8.0
        },
        {
            "datetime": "2022-01-13T12:00+05:30",
            "amount": 9.0
        },
        {
            "datetime": "2022-01-13T16:00+05:30",
            "amount": 10.0
        }
    ]
}
```

**description**: this api takes `startDateTime` and `endDateTime` and return total bitcoins balance at end of each hour between these date.

### /btc/addTxn [POST]
Content-Type : application/json

sample request body:
```json
{
    "datetime": "2022-01-13T19:15:05+05:30",
    "amount": 3
}
```

sample successful response json:
```json
{
    "success": true,
    "message": "Transaction inserted successfully!"
}
```

**description**: this api takes `datetime` and `amount` and saves it in db.


### DB setup:

This project uses timescaledb HyperTable to store data in time series manner, therefore, we need to
setup postgreSQL and timescaledb extension.

Follow this [digital ocean link](https://www.digitalocean.com/community/tutorials/how-to-install-postgresql-on-ubuntu-20-04-quickstart) to setup PostgreSQL and [TimescaleDB doc](https://docs.timescale.com/install/latest/self-hosted/installation-debian/#setting-up-the-timescaledb-extension)
to setup TimescaleDB.

Then use following command to setup tables and data:

To import db schema: `psql -d cryptoassets < db_schema.bak`

after importing db, goto psql db : `psql -d cryptoassets`

then enable hypertable: `SELECT create_hypertable('bitcoin_txn', 'datetime');`

then import table data from bitcoin_txn_data.csv with following command:
`timescaledb-parallel-copy --db-name cryptoassets --table bitcoin_txn \
--file bitcoin_txn_data.csv --workers 4 --copy-options "CSV"`


### Generate Build:
To generate build of this project run command: `./mvnw clean package`

