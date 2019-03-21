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
        String sql = "INSERT INTO message (body, received) VALUES (:body, :received)";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("body", body)
                .addValue("received", LocalDateTime.now());

        namedParameterJdbcTemplate.update(sql, parameters);
    }

}