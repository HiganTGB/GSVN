package com.gsvn.productservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsvn.productservice.model.entity.Dimensions;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;

@MappedTypes(Dimensions.class)
public class DimensionsTypeHandler extends BaseTypeHandler<Dimensions> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Dimensions parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setObject(i, objectMapper.writeValueAsString(parameter), Types.OTHER);
        } catch (Exception e) {
            throw new SQLException("Error converting Dimensions to JSON", e);
        }
    }

    @Override
    public Dimensions getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public Dimensions getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public Dimensions getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private Dimensions parse(String json) throws SQLException {
        if (json == null || json.isEmpty()) return null;
        try {
            return objectMapper.readValue(json, Dimensions.class);
        } catch (Exception e) {
            throw new SQLException("Error parsing JSON to Dimensions", e);
        }
    }
}