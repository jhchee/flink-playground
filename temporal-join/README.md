# Temporal Joins

## Event Time Temporal Join
### What is it?
- A temporal join is a join between one tables that have a time dimension.
### Create connection to Kafka
```sql
DROP TABLE IF EXISTS `customers_updating`;
-- Create a versioned table with a primary key
CREATE OR REPLACE TABLE `customers_updating` (
    `id` STRING,
    `account_type` STRING,
    `updated_at` TIMESTAMP_LTZ(3),
    WATERMARK FOR `updated_at` AS `updated_at`,
    PRIMARY KEY (`id`) NOT ENFORCED
) WITH (
    'connector' = 'upsert-kafka',
    'topic' = 'customers_updating',
    'properties.bootstrap.servers' = 'broker:29092',
    'properties.group.id' = 'demo-group',
    'properties.sasl.mechanism' = 'PLAIN',
    'key.format' = 'json',
    'value.format' = 'json'
);

DROP TABLE IF EXISTS `purchases`;
-- This is an append-only table
CREATE OR REPLACE TABLE `purchases` (
    `customer_id` STRING,
    `cost` DECIMAL(10, 2),
    `purchased_at` TIMESTAMP_LTZ(3),
    WATERMARK FOR `purchased_at` AS `purchased_at`
) WITH (
    'connector' = 'kafka',
    'topic' = 'purchases',
    'properties.bootstrap.servers' = 'broker:29092',
    'properties.group.id' = 'demo-group',
    'properties.sasl.mechanism' = 'PLAIN',
    'scan.startup.mode' = 'earliest-offset',
    'value.format' = 'json'
);
```

### Insert data into Kafka
```sql
INSERT INTO customers_updating VALUES ('George', 'guest', NOW());
INSERT INTO customers_updating VALUES ('George', 'manager', NOW());
INSERT INTO purchases VALUES ('George', 14.00, NOW());
INSERT INTO purchases VALUES ('George', 25.00, NOW());
```

### Create temporal join
```sql
SELECT customer_id, account_type, cost, updated_at, purchased_at
FROM purchases 
LEFT JOIN customers_updating FOR SYSTEM_TIME AS OF purchases.purchased_at
ON purchases.customer_id = customers_updating.id;
```

### Considerations
- Be aware that watermarks will prevent the join to happen if the watermark is not reached. You will need to publish a message with a timestamp greater than the watermark to trigger the join.
- Versioned table may contain multiple versions of the same key. Old rows will be removed once the probe site watermark is greater than the row's timestamp.