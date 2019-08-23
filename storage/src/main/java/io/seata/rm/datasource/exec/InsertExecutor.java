/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.seata.rm.datasource.exec;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.alibaba.druid.pool.DruidPooledStatement;
import com.mysql.jdbc.JDBC4Connection;
import com.mysql.jdbc.JDBC4PreparedStatement;
import io.seata.common.exception.NotSupportYetException;
import io.seata.common.exception.ShouldNeverHappenException;
import io.seata.rm.datasource.PreparedStatementProxy;
import io.seata.rm.datasource.StatementProxy;
import io.seata.rm.datasource.sql.SQLInsertRecognizer;
import io.seata.rm.datasource.sql.SQLRecognizer;
import io.seata.rm.datasource.sql.struct.ColumnMeta;
import io.seata.rm.datasource.sql.struct.Null;
import io.seata.rm.datasource.sql.struct.TableMeta;
import io.seata.rm.datasource.sql.struct.TableRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Insert executor.
 *
 * @param <T> the type parameter
 * @param <S> the type parameter
 * @author yuanguoyao
 * @date 2019-03-21 21:30:02
 */
public class InsertExecutor<T, S extends Statement> extends AbstractDMLBaseExecutor<T, S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(io.seata.rm.datasource.exec.InsertExecutor.class);
    protected static final String ERR_SQL_STATE = "S1009";

    /**
     * Instantiates a new Insert executor.
     *
     * @param statementProxy    the statement proxy
     * @param statementCallback the statement callback
     * @param sqlRecognizer     the sql recognizer
     */
    public InsertExecutor(StatementProxy statementProxy, StatementCallback statementCallback,
                          SQLRecognizer sqlRecognizer) {
        super(statementProxy, statementCallback, sqlRecognizer);
    }

    @Override
    protected TableRecords beforeImage() throws SQLException {
        return TableRecords.empty(getTableMeta());
    }

    @Override
    protected TableRecords afterImage(TableRecords beforeImage) throws SQLException {
        //Pk column exists or PK is just auto generated
        List<Object> pkValues = containsPK() ? getPkValuesByColumn() : getPkValuesByAuto();

        TableRecords afterImage = buildTableRecords(pkValues);

        if (afterImage == null) {
            throw new SQLException("Failed to build after-image for insert");
        }

        return afterImage;
    }

    protected boolean containsPK() {
        SQLInsertRecognizer recognizer = (SQLInsertRecognizer) sqlRecognizer;
        List<String> insertColumns = recognizer.getInsertColumns();
        TableMeta tmeta = getTableMeta();
        return tmeta.containsPK(insertColumns);
    }

    protected List<Object> getPkValuesByColumn() throws SQLException {
        // insert values including PK
        SQLInsertRecognizer recognizer = (SQLInsertRecognizer) sqlRecognizer;
        List<String> insertColumns = recognizer.getInsertColumns();
        String pk = getTableMeta().getPkName();
        List<Object> pkValues = null;
        if (statementProxy instanceof PreparedStatementProxy) {
            PreparedStatementProxy preparedStatementProxy = (PreparedStatementProxy) statementProxy;
            int insertColumnsSize = insertColumns.size();
            int pkIndex = 0;
            for (int paramIdx = 0; paramIdx < insertColumnsSize; paramIdx++) {
                if (insertColumns.get(paramIdx).equalsIgnoreCase(pk)) {
                    pkIndex = paramIdx;
                    break;
                }
            }
            //all parameters are Prepared Statements
            if (insertColumnsSize == preparedStatementProxy.getParameters().length) {
                pkValues = preparedStatementProxy.getParamsByIndex(pkIndex);
            } else {
                //some parameters are Prepared Statements: values (1, 100, ?)
                // or all parameters are Immediate Statements
                List<List<Object>> insertRows = recognizer.getInsertRows();
                if (insertRows.get(0).get(pkIndex).equals("?")) {
                    pkValues = preparedStatementProxy.getParamsByIndex(pkIndex);
                } else {
                    int finalPkIndex = pkIndex;
                    pkValues = insertRows.stream().map(insertRow -> insertRow.get(finalPkIndex)).collect(Collectors.toList());
                }
            }
        } else {
            for (int paramIdx = 0; paramIdx < insertColumns.size(); paramIdx++) {
                if (insertColumns.get(paramIdx).equalsIgnoreCase(pk)) {
                    List<List<Object>> insertRows = recognizer.getInsertRows();
                    pkValues = new ArrayList<>(insertRows.size());
                    for (List<Object> row : insertRows) {
                        pkValues.add(row.get(paramIdx));
                    }
                    break;
                }
            }
        }
        if (pkValues == null) {
            throw new ShouldNeverHappenException();
        }
        //pk auto generated while column exists and value is null
        if (pkValues.size() == 1 && pkValues.get(0) instanceof Null) {
            pkValues = getPkValuesByAuto();
        }
        return pkValues;
    }


    protected List<Object> getPkValuesByAuto() throws SQLException {
        // PK is just auto generated
        Map<String, ColumnMeta> pkMetaMap = getTableMeta().getPrimaryKeyMap();
        if (pkMetaMap.size() != 1) {
            throw new NotSupportYetException();
        }
        ColumnMeta pkMeta = pkMetaMap.values().iterator().next();
        if (!pkMeta.isAutoincrement()) {
            throw new ShouldNeverHappenException();
        }

 /*       ResultSet genKeys = null;
        try {
            genKeys = statementProxy.getTargetStatement().getGeneratedKeys();
        } catch (SQLException e) {
            // java.sql.SQLException: Generated keys not requested. You need to
            // specify Statement.RETURN_GENERATED_KEYS to
            // Statement.executeUpdate() or Connection.prepareStatement().
            if (ERR_SQL_STATE.equalsIgnoreCase(e.getSQLState())) {
                LOGGER.warn("Fail to get auto-generated keys, use \'SELECT LAST_INSERT_ID()\' instead. Be cautious, statement could be polluted. Recommend you set the statement to return generated keys.");
                genKeys = statementProxy.getTargetStatement().executeQuery("SELECT LAST_INSERT_ID()");
            } else {
                throw e;
            }
        }
        List<Object> pkValues2 = new ArrayList<>();
        while (genKeys.next()) {
            Object v = genKeys.getObject(1);
            pkValues2.add(v);
        }*/
        List<Object> pkValues = new ArrayList<>();
        Statement statement = statementProxy.getTargetStatement();
        if (statement instanceof DruidPooledPreparedStatement) {
            Long lastInsertID = null;
            int updateCount = 0;
            if (((DruidPooledPreparedStatement) statement).getStatement() instanceof JDBC4PreparedStatement) {
                JDBC4PreparedStatement jdbc4PreparedStatement = (JDBC4PreparedStatement) ((DruidPooledPreparedStatement) statement).getStatement();
                lastInsertID = jdbc4PreparedStatement.getLastInsertID();
                updateCount = jdbc4PreparedStatement.getUpdateCount();
                if (updateCount == 1) {
                    pkValues.add(lastInsertID);
                } else if (updateCount > 1) {
                    JDBC4Connection jdbc4Connection = (JDBC4Connection) jdbc4PreparedStatement.getConnection();
                    int autoIncrementIncrement = jdbc4Connection.getAutoIncrementIncrement();
                    for (int i = 0; i < updateCount; i++) {
                        lastInsertID = lastInsertID + autoIncrementIncrement * i;
                        pkValues.add(lastInsertID);
                    }
                }
            }
        } else if (statement instanceof DruidPooledStatement) {

        }
        System.out.println(1/0);
        return pkValues;
    }
}