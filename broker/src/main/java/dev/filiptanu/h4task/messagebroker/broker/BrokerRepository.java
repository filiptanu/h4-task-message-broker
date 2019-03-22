package dev.filiptanu.h4task.messagebroker.broker;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class BrokerRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("${clear.pending.messages.time.interval.milliseconds}")
    private int clearPendingMessagesTimeIntervalMilliseconds;

    public void insertMessage(String body) {
        String sql = "INSERT INTO message (body, received_from_producer, processing_status) " +
                     "VALUES (:body, :received_from_producer, :processing_status)";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("body", body)
                .addValue("received_from_producer", LocalDateTime.now())
                .addValue("processing_status", ProcessingStatus.NOT_PROCESSED.toString());

        namedParameterJdbcTemplate.update(sql, parameters);
    }

    public MessageEntity getFirstUnprocessedMessage(String consumerId) {
        String sql = "UPDATE message " +
                     "SET processing_status = :processing_status_to_set, consumer_id = :consumer_id, sent_to_consumer = :sent_to_consumer " +
                     "WHERE  id = (" +
                     "  SELECT id " +
                     "  FROM   message " +
                     "  WHERE  processing_status = :processing_status_to_get " +
                     "  ORDER BY id " +
                     "  LIMIT  1 " +
                     "  FOR UPDATE SKIP LOCKED" +
                     ") " +
                     "RETURNING message.*";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("processing_status_to_set", ProcessingStatus.PENDING.toString())
                .addValue("consumer_id", consumerId)
                .addValue("sent_to_consumer", LocalDateTime.now())
                .addValue("processing_status_to_get", ProcessingStatus.NOT_PROCESSED.toString());

        return namedParameterJdbcTemplate.queryForObject(sql, parameters, (rs, rowNum) -> {
            MessageEntity messageEntity = new MessageEntity();

            messageEntity.setId(rs.getInt("id"));
            messageEntity.setBody(rs.getString("body"));
            messageEntity.setReceivedFromProducer(rs.getTimestamp("received_from_producer").toLocalDateTime());
            messageEntity.setProcessingStatus(ProcessingStatus.valueOf(rs.getString("processing_status")));
            messageEntity.setConsumerId(rs.getString("consumer_id"));
            messageEntity.setSentToConsumer(rs.getTimestamp("sent_to_consumer").toLocalDateTime());

            return messageEntity;
        });
    }

    public void confirmMessage(int messageId, String consumerId) {
        String sql = "UPDATE message " +
                     "SET processing_status = :processing_status_to_set, confirmed_from_consumer = :confirmed_from_consumer " +
                     "WHERE id = :message_id AND consumer_id = :consumer_id AND processing_status = :current_processing_status";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("processing_status_to_set", ProcessingStatus.PROCESSED.toString())
                .addValue("confirmed_from_consumer", LocalDateTime.now())
                .addValue("message_id", messageId)
                .addValue("consumer_id", consumerId)
                .addValue("current_processing_status", ProcessingStatus.PENDING.toString());

        namedParameterJdbcTemplate.update(sql, parameters);
    }

    public void clearPendingMessages() {
        String sql = "UPDATE message " +
                     "SET processing_status = :processing_status_not_processed, consumer_id = :consumer_id, sent_to_consumer = :sent_to_consumer, confirmed_from_consumer = :confirmed_from_consumer " +
                     "WHERE processing_status = :processing_status_pending AND sent_to_consumer < :pending_period";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("processing_status_not_processed", ProcessingStatus.NOT_PROCESSED.toString())
                .addValue("consumer_id", null)
                .addValue("sent_to_consumer", null)
                .addValue("confirmed_from_consumer", null)
                .addValue("processing_status_pending", ProcessingStatus.PENDING.toString())
                .addValue("pending_period", LocalDateTime.now().minusMinutes(TimeUnit.MILLISECONDS.toMinutes(clearPendingMessagesTimeIntervalMilliseconds)));

        namedParameterJdbcTemplate.update(sql, parameters);
    }

}