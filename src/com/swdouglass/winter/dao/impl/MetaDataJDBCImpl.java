/*
 * Copyright 2008 Scott Douglass, all rights reserved.
 *   http://swdouglass.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * on the World Wide Web for all details:
 *
 *   http://www.fsf.org/licensing/licenses/gpl.txt
 *
 */

package com.swdouglass.winter.dao.impl;

import com.swdouglass.winter.dao.MetaDataDAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author scott
 */
public class MetaDataJDBCImpl implements MetaDataDAO {

  private String selectQuery = "select * from ";
  private DataSource dataSource;

  public Map<String, String> getTableMetaData(String inTableName) {
    HashMap<String, String> columns = new HashMap<String, String>();
    try {
      PreparedStatement stmt = getDataSource().getConnection().prepareStatement(getSelectQuery().concat(inTableName));
      ResultSet rs = stmt.executeQuery();
      ResultSetMetaData rsmd = rs.getMetaData();
      for (int i = 1; i <= rsmd.getColumnCount(); i++ ) {
        columns.put(rsmd.getColumnName(i), rsmd.getColumnClassName(i));
      }
    } catch (SQLException ex) {
      Logger.getLogger(MetaDataJDBCImpl.class.getName()).log(Level.SEVERE, null, ex);
    }

    return columns;
  }

  /**
   * @return the selectQuery
   */
  public String getSelectQuery() {
    return selectQuery;
  }

  /**
   * @param selectQuery the selectQuery to set
   */
  public void setSelectQuery(String selectQuery) {
    this.selectQuery = selectQuery;
  }

  /**
   * @return the dataSource
   */
  public DataSource getDataSource() {
    return dataSource;
  }

  /**
   * @param dataSource the dataSource to set
   */
  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

}
