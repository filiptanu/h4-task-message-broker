package dev.filiptanu.h4task.messagebroker.broker;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class BrokerRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void insertMessage(String body) {
        String sql = "INSERT INTO message (body, received) " +
                     "VALUES (:body, :received)";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("body", body)
                .addValue("received", LocalDateTime.now());

        namedParameterJdbcTemplate.update(sql, parameters);
    }

    public MessageEntity getFirstUnprocessedMessage() {
        String sql = "UPDATE message " +
                     "SET processed = TRUE " +
                     "WHERE  id = (" +
                     "  SELECT id " +
                     "  FROM   message " +
                     "  WHERE  processed = FALSE " +
                     "  ORDER BY id " +
                     "  LIMIT  1 " +
                     "  FOR UPDATE SKIP LOCKED" +
                     ") " +
                     "RETURNING message.*";

        SqlParameterSource parameters = new MapSqlParameterSource();

        return namedParameterJdbcTemplate.queryForObject(sql, parameters, (rs, rowNum) -> {
            MessageEntity messageEntity = new MessageEntity();

            messageEntity.setId(rs.getInt("id"));
            messageEntity.setBody(rs.getString("body"));
            messageEntity.setReceived(rs.getTimestamp("received").toLocalDateTime());
            messageEntity.setProcessed(rs.getBoolean("processed"));

            return messageEntity;
        });
    }

}