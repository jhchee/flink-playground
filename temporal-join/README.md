# Temporal join with Flink

## Create connection to Kafka
```sql
DROP TABLE IF EXISTS `customers_updating`;

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

## Insert data into Kafka
```sql
INSERT INTO customers_updating VALUES ('George', 'guest', NOW());
INSERT INTO customers_updating VALUES ('George', 'manager', NOW());
INSERT INTO purchases VALUES ('George', 14.00, NOW());
INSERT INTO purchases VALUES ('George', 25.00, NOW());
```


## Create temporal join
```sql
```


### Beware of the watermarks
- Watermarks will prevent the join to happen if the watermark is not reached.
- You will need to publish a message with a timestamp greater than the watermark to trigger the join.