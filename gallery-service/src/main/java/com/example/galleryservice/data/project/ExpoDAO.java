package com.example.galleryservice.data.project;

import com.example.galleryservice.data.DAO;
import com.example.galleryservice.model.project.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Transactional
public class ExpoDAO extends JdbcDaoSupport implements DAO<Expo> {

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    public ExpoDAO() {
        final DataSource dataSource = getDataSource();
        jdbcTemplate = new JdbcTemplate(Objects.requireNonNull(dataSource));
        jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("expos")
                .usingGeneratedKeyColumns("id");
    }

    @PostConstruct
    private void postConstruct() {

    }

    final static class ExpoRowMapper implements RowMapper<Expo> {
        @Override
        public Expo mapRow(@NotNull final ResultSet rs, final int rowNum) throws SQLException {
            Expo expo = new Expo();
            expo.setId(rs.getLong("id"));
            expo.setName(rs.getString("name"));
            expo.setInfo(rs.getString("info"));
            expo.setArtist(rs.getLong("artist"));
            expo.setStartTime(rs.getTimestamp("startTime").toLocalDateTime());
            expo.setEndTime(rs.getTimestamp("endTime").toLocalDateTime());
            expo.setStatus(ExpoStatus.valueOf(rs.getString("status")));
            return expo;
        }
    }

    public Optional<Expo> findByName(@NotNull final String name) {
        return Optional.of(Objects.requireNonNull(
                jdbcTemplate.queryForObject("select * from expos where name = ?",
                        new Object[]{name},
                        new BeanPropertyRowMapper<>(Expo.class))));
    }

    public List<Expo> findByArtist(final long artist) {
        return jdbcTemplate.query("select * from expos where artist = ?", new Object[]{artist}, new ExpoRowMapper());
    }

    public List<Expo> findByStatus(@NotNull final String status) {
        return jdbcTemplate.query("select * from expos where status = ?", new Object[]{status}, new ExpoRowMapper());
    }

    @Override
    public Optional<Expo> findByID(final long id) {
        return Optional.of(Objects.requireNonNull(
                jdbcTemplate.queryForObject("select * from expos where id = ?",
                        new Object[]{id},
                        new BeanPropertyRowMapper<>(Expo.class))));
    }

    @Override
    public List<Expo> findAll() {
        return jdbcTemplate.query("select * from expos", new ExpoRowMapper());
    }

    @Override
    public void update(@NotNull final Expo expo) {
        jdbcTemplate.update("update expos " + "set name = ?, info = ?, startTime = ?, endTime = ?, status = ? " + " where id = ?",
                expo.getName(), expo.getInfo(), Timestamp.valueOf(expo.getStartTime()), Timestamp.valueOf(expo.getEndTime()), expo.getStatus().toString(), expo.getId());
    }

    @Override
    public long insert(@NotNull final Expo expo) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(expo);
        return jdbcInsert.executeAndReturnKey(parameters).longValue();
    }
}