package com.example.galleryservice.data.gallery;

import com.example.galleryservice.model.gallery.ClientOwnerPayment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Repository
@Data
public class ClientOwnerPaymentDAO extends PaymentDAO {

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Autowired
    public ClientOwnerPaymentDAO(@NotNull final DataSource dataSource) {
        super(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource).withSchemaName("testbase")
                .withTableName("client_owner_payment");
    }

    final static class ClientOwnerPaymentRowMapper implements RowMapper<ClientOwnerPayment> {
        @Override
        public ClientOwnerPayment mapRow(@NotNull final ResultSet rs, final int rowNum) throws SQLException {
            ClientOwnerPayment clientOwnerPayment = new ClientOwnerPayment();
            clientOwnerPayment.setId(rs.getBigDecimal("id").longValue());
            clientOwnerPayment.setReservation(rs.getLong("reservation"));
            clientOwnerPayment.setClient(rs.getLong("client"));
            clientOwnerPayment.setOwner(rs.getLong("owner"));
            return clientOwnerPayment;
        }
    }

    public ClientOwnerPayment findByReservation(final long reservation) {
        try {
            return jdbcTemplate.queryForObject("select * from testbase.client_owner_payment where reservation = ?",
                    new Object[]{reservation},
                    new ClientOwnerPaymentRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<ClientOwnerPayment> findByClient(final long client) {
        return jdbcTemplate.query("select * from testbase.client_owner_payment where client = ?", new Object[]{client}, new ClientOwnerPaymentRowMapper());
    }

    public List<ClientOwnerPayment> findByOwner(final long owner) {
        return jdbcTemplate.query("select * from testbase.client_owner_payment where owner = ?", new Object[]{owner}, new ClientOwnerPaymentRowMapper());
    }

    public ClientOwnerPayment findByID(final long id) {
        try {
            return jdbcTemplate.queryForObject("select * from testbase.client_owner_payment where id = ?",
                    new Object[]{id},
                    new ClientOwnerPaymentRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<ClientOwnerPayment> findAllClientOwnerPayments() {
        return jdbcTemplate.query("select * from testbase.client_owner_payment", new ClientOwnerPaymentRowMapper());
    }

    public int insert(@NotNull final ClientOwnerPayment clientOwnerPayment) {
        final Map<String, Object> parameters = new HashMap<>(4);
        parameters.put("id", clientOwnerPayment.getId());
        parameters.put("reservation", clientOwnerPayment.getReservation());
        parameters.put("client", clientOwnerPayment.getClient());
        parameters.put("owner", clientOwnerPayment.getOwner());
        return jdbcInsert.execute(parameters);
    }
}
