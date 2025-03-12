- creer (ou mise à jours) le cluter
```bash
# construitre ou metre à jour
docker compose up -d 
# detruire le cluster
docker compose down
# consulter les conteneurs existants
docker ps
# commandes
docker exec -it broker3 bash
kafka-topics --create --topic premier \
    --bootstrap-server broker1:29092,broker2:29092,broker3:29092
kafka-topics --create --topic second \
    --bootstrap-server broker2:29092 \
    --partitions 5 \
    --replication-factor 3
kafka-topics --list \
    --bootstrap-server broker1:29092,broker2:29092,broker3:29092
kafka-topics --describe \
    --topic second \
    --bootstrap-server broker1:29092,broker2:29092,broker3:29092
kafka-console-producer \
    --topic second \
    --bootstrap-server broker1:29092
kafka-console-consumer \
    --topic second \
    --bootstrap-server broker1:29092
    --from-beginning
```

example de avro :

```avro
Binary Avro Data
----------------------------
00000000: 0E 6A 6F 68 6E 20 44 6F 65 20 31 38 00 0A 6A 6F  .john Doe 18..jo
00000010: 68 6E 40 65 78 61 6D 70 6C 65 2E 63 6F 6D        hn@example.com
```

```bash
kafka-producer-perf-test \
    --topic second \
    --num-records 30000 \
    --record-size 1000 \
    --throughput -1 \
    --producer-props \
    bootstrap.servers=broker1:29092,broker2:29092,broker3:29092
    --print-metrics
kafka-consumer-perf-test \
    --topic second \
    --messages 30000 \
    --bootstrap-server broker1:29092,broker2:29092,broker3:29092
    --print-metrics
kafka-console-producer \
    --topic letrois \
    --bootstrap-server broker2:29092 \
    --property parse.key=true \
    --property key.separator="=>"
 kafka-console-consumer \
    --topic letrois \
    --bootstrap-server broker1:29092 \
    --frombeginning \
    --property print.key=true \
    --property key.separator="-msg="
    --partition 0
kafka-console-consumer \
    --topic letrois \
    --bootstrap-server broker1:29092 \
    --property print.key=true \
    --property key.separator="-msg=" \
    --partition 0 \
    --offset 2

docker exec -it schema-registry bash
kafka-avro-console-producer \
    --topic app-topic4 \
    --bootstrap-server \
    broker1:29092,broker2:29092,broker3:29092 \
    --property schema.registry.url=http://localhost:8081 \
    --property value.schema="$(< /etc/tutorial/user-schema.json)" \
    --property parse.key=true \
    --property key.separator="=>" \
    --property key.serializer=org.apache.kafka.common.serialization.StringSerializer

kafka-avro-console-consumer \
    --topic app-topic4 \
    --bootstrap-server broker1:29092,broker2:29092,broker3:29092 \
    --property schema.registry.url=http://localhost:8081 \
    --property print.key=true \
    --property key.separator=" ;msg="
    --from-beginning 
```

jconsole
remote acess : localhost:9101


```sql
CREATE STREAM pageviews_stream
WITH (KAFKA_TOPIC='pageviews',VALUE_FORMAT='AVRO');

CREATE TABLE users_table (id VARCHAR PRIMARY KEY)
WITH (KAFKA_TOPIC='users',VALUE_FORMAT='AVRO');

SELECT * FROM  PAGEVIEWS_STREAM EMIT CHANGES;
SELECT * FROM  USERS_TABLE EMIT CHANGES LIMIT 10;

CREATE STREAM user_pageviews
AS
SELECT users_table.id AS userid, pageid, regionid, gender
FROM  PAGEVIEWS_STREAM
LEFT JOIN  USERS_TABLE
ON pageviews_stream.userid = users_table.id
EMIT CHANGES;
```