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

package com.swdouglass.winter.dao;

import java.util.Map;

/**
 *
 * @author scott
 */
public interface MetaDataDAO {
  /**
   * Returns a Map where each element has as the key the name of the database
   * column and the value is the name of the Java class that column maps to.
   *
   * @param inTableName The table to be analysed.
   * @return Map<column name String, column class String>
   */
  public Map<String, String> getTableMetaData(String inTableName);
}
