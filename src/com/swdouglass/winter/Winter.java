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
package com.swdouglass.winter;

import com.swdouglass.winter.dao.MetaDataDAO;
import com.swdouglass.winter.dao.PersistenceTestDAO;
import gnu.getopt.Getopt;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author scott
 */
public class Winter {

  private static Logger logger = Logger.getLogger("Winter");
  private String propertiesFile = "winter.properties";
  private MetaDataDAO metaDataDAO;
  private PersistenceTestDAO persistenceTestDAO;
  private Map<String, String> columns;
  private Map<String, String> attributes;
  //private String idColumnName = "id";
  private String conventionsDirectory = "conventions";
  private Properties javaConvention = new Properties();
  private Properties ddlConvention = new Properties();

  private static final String FS = System.getProperty("file.separator");
  private static final String XML_COMMENT = "xml";
  private static final String JAVA_COMMENT = "java";
  private static final String CAMEL_CASE = "camel";
  private static final String UPPER_CASE = "upper";
  private static final String LOWER_CASE = "lower";
  private static final String JAVA_CLASS = "class";
  private static final String JAVA_ATTRIBUTE = "attribute";
  private static final String INDENT_TAB = "tab";
  private static final String INDENT_SPACE = "space";
  private static final String WINTER = "Winter Code Generator";
  //private static final String INDENT = "  "; // 2 spaces by default

  private String javaIndent = "  ";// 2 spaces by default
  private String ddlIndent = "  ";

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {

    String packageName = "com.swdouglass";
    String idColumnName = "id";
    String action = "class";
    String tableName = "";

    Getopt g = new Getopt("Winter", args, "cp:i:t:");
    //
    int c;
    while ((c = g.getopt()) != -1) {
      switch (c) {
        case 'p':
          packageName = g.getOptarg();
          break;
        case 'c':
          action = "class"; //default anyway
          break;
        case 'i':
          idColumnName = g.getOptarg();
          break;
        case 't':
          tableName = g.getOptarg();
          break;
        case '?':
        default:
          System.out.print("Usage: java -jar Winter.jar -t table_name -p package_name [ -i index ]");
          System.exit(-1);
      }
    }

    Winter w = new Winter();
    if (action.equals("class")) {
      w.generateClassFromTable(tableName, idColumnName, packageName);// table name, package name
    }
  }

  public void generateClassFromTable(String inTableName, String inIdColumnName, String inPackageName) {
    String outputDir = System.getProperty("winter.output_dir", System.getProperty("user.dir"));

    // convert java package name to directory path
    StringBuilder sb = new StringBuilder(outputDir);
    sb.append(FS);
    String[] parts = inPackageName.split("\\.");
    for (int i = 0; i < parts.length; i++) {
      sb.append(parts[i]);
      sb.append(FS);
    }
    File packageDir = new File(sb.toString());
    packageDir.mkdirs();
    String className = dbNameToJavaName(inTableName, JAVA_CLASS);
    sb.append(className);
    StringBuilder hbm = new StringBuilder(sb.toString());
    sb.append(".java");

    File classFile = new File(sb.toString());
    hbm.append(".hbm.xml");
    File hbmFile = new File(hbm.toString());
    try {
      FileWriter fw = new FileWriter(classFile);
      fw.write(readFileAsString(System.getProperty("winter.license", "license.txt"), JAVA_COMMENT));
      fw.write(javaFromTable(inPackageName, className, inTableName, inIdColumnName));
      fw.flush();
      fw.close();
      fw = new FileWriter(hbmFile);
      //fw.write(readFileAsString(System.getProperty("winter.license", "license.txt"), XML_COMMENT));
      fw.write(hbmFromTable(inPackageName, className, inTableName, inIdColumnName));
      fw.flush();
      fw.close();
    } catch (IOException ex) {
      logger.severe(ex.toString());
    }
  }

  public String javaFromTable(String inPackageName, String inClassName,
          String inTableName, String inIdColumnName) {
    StringBuilder sb = new StringBuilder();

    sb.append("package ");
    sb.append(inPackageName);
    sb.append(";\n\n");
    sb.append("import java.io.Serializable;\n\n");

    sb.append("/**\n");
    sb.append(" * Description here...\n");
    sb.append(" *\n");
    sb.append(" * @author ");
    sb.append(System.getProperty("winter.author", WINTER));
    sb.append("\n */\n");
    sb.append("public class ");
    sb.append(inClassName);
    sb.append(" implements Serializable {\n\n");

    columns = this.getMetaDataDAO().getTableMetaData(inTableName);
    attributes = new HashMap<String, String>();
    // attributes
    for (String column : columns.keySet()) {
      sb.append(javaIndent);
      sb.append("/** Description... */\n");
      sb.append(javaIndent);
      sb.append("private ");
      // don't use java.lang. part
      String javaType = columns.get(column).substring(columns.get(column).lastIndexOf('.') + 1);
      sb.append(javaType);
      sb.append(" ");
      String attributeName = dbNameToJavaName(column, JAVA_ATTRIBUTE);
      sb.append(attributeName);
      sb.append(";\n\n");
      attributes.put(attributeName, javaType);
    }

    // getters and setters
    for (String attribute : attributes.keySet()) {
      StringBuilder upAttr = new StringBuilder();
      upAttr.append(attribute.substring(0, 1).toUpperCase(Locale.ENGLISH));
      upAttr.append(attribute.substring(1));
      // get
      sb.append(javaIndent);
      sb.append("/**\n");
      sb.append(javaIndent);
      sb.append(" * Description...\n");
      sb.append(javaIndent);
      sb.append(" *\n");
      sb.append(javaIndent);
      sb.append(" * @return ");
      sb.append(attribute);
      sb.append(" Description...\n");
      sb.append(javaIndent);
      sb.append(" */\n");
      sb.append(javaIndent);
      sb.append("public ");
      sb.append(attributes.get(attribute));
      sb.append(" get");
      sb.append(upAttr.toString());
      sb.append("() {\n");
      sb.append(javaIndent);
      sb.append(javaIndent);
      sb.append("return this.");
      sb.append(attribute);
      sb.append(";\n");
      sb.append(javaIndent);
      sb.append("}\n\n");

      //set
      sb.append(javaIndent);
      sb.append("/**\n");
      sb.append(javaIndent);
      sb.append(" * Description...\n");
      sb.append(javaIndent);
      sb.append(" *\n");
      sb.append(javaIndent);
      sb.append(" * @param in");
      sb.append(upAttr.toString());
      sb.append(" Description...\n");
      sb.append(javaIndent);
      sb.append(" */\n");
      sb.append(javaIndent);
      sb.append("public void set");
      sb.append(upAttr.toString());
      sb.append("(");
      sb.append(attributes.get(attribute));
      sb.append(" in");
      sb.append(upAttr.toString());
      sb.append(") {\n");
      sb.append(javaIndent);
      sb.append(javaIndent);
      sb.append("this.");
      sb.append(attribute);
      sb.append(" = in");
      sb.append(upAttr.toString());
      sb.append(";\n");
      sb.append(javaIndent);
      sb.append("}\n\n");

    }
    sb.append("}\n");
    return sb.toString();
  }

  public String hbmFromTable(String inPackageName, String inClassName,
          String inTableName, String inIdColumnName) {
    StringBuilder sb = new StringBuilder();
    sb.append("<?xml version=\"1.0\"?>\n");
    sb.append("<!DOCTYPE hibernate-mapping PUBLIC\n");
    sb.append("  \"-//Hibernate/Hibernate Mapping DTD//EN\"\n");
    sb.append("  \"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">\n\n");
    sb.append("<hibernate-mapping package=\"");
    sb.append(inPackageName);
    sb.append("\">\n");
    sb.append("  <class name=\"");
    sb.append(inClassName);
    sb.append("\" table=\"");
    sb.append(inTableName);
    sb.append("\">\n");
    // generally I use this hibernate sequence generator instead of a native one
    // for portability
    sb.append("    <id name=\"");
    sb.append(dbNameToJavaName(inIdColumnName, JAVA_ATTRIBUTE));
    sb.append("\" column=\"");
    sb.append(inIdColumnName);
    sb.append("\">\n");
    sb.append("      <generator class=\"org.hibernate.id.enhanced.TableGenerator\">\n");
    sb.append("        <param name=\"optimizer\">hilo</param>\n");
    sb.append("        <param name=\"segment_value\">");
    sb.append(inTableName);
    sb.append("</param>\n");
    sb.append("        <param name=\"table_name\">");
    sb.append(System.getProperty("winter.sequences_table", "sequences"));
    sb.append("</param>\n");
    sb.append("      </generator>\n");
    sb.append("    </id>\n\n");
    // properties/attributes
    if (columns == null) {
      columns = this.getMetaDataDAO().getTableMetaData(inTableName);
    }
    for (String column : columns.keySet()) {
      if (!"id".equalsIgnoreCase(column)) {
        sb.append("    <property name=\"");
        sb.append(dbNameToJavaName(column, JAVA_ATTRIBUTE));
        sb.append("\" column=\"");
        sb.append(column);
        sb.append("\"/>\n");
      }
    }

    sb.append("  </class>\n");
    sb.append("</hibernate-mapping>\n");
    return sb.toString();
  }


  public String dbNameToJavaName(String inDbName, String inType) {
    StringBuilder sb = new StringBuilder();
    inDbName.toLowerCase(Locale.ENGLISH);
    if (ddlConvention.getProperty("wordSeparator") != null) {

      String[] parts = inDbName.split(ddlConvention.getProperty("wordSeparator"));
      // skip first part, as we are lowerUpper for attributes
      if (CAMEL_CASE.equalsIgnoreCase(javaConvention.getProperty("case", CAMEL_CASE))) {
        if (LOWER_CASE.equalsIgnoreCase(javaConvention.getProperty(inType, LOWER_CASE))) {
          sb.append(parts[0]);
          for (int i = 1; i < parts.length; i++) {
            sb.append(parts[i].substring(0, 1).toUpperCase(Locale.ENGLISH));
            sb.append(parts[i].substring(1));
          }
        } else if (UPPER_CASE.equalsIgnoreCase(javaConvention.getProperty(inType))) {
          for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i].substring(0, 1).toUpperCase(Locale.ENGLISH));
            sb.append(parts[i].substring(1));
          }
        }
      } else if (LOWER_CASE.equalsIgnoreCase(javaConvention.getProperty("case"))) {
        sb.append(inDbName);
      } else if (UPPER_CASE.equalsIgnoreCase(javaConvention.getProperty("case"))) {
        sb.append(inDbName.toUpperCase(Locale.ENGLISH));
      }
    } else if (LOWER_CASE.equalsIgnoreCase(javaConvention.getProperty("case"))) {
      sb.append(inDbName);
    } else if (UPPER_CASE.equalsIgnoreCase(javaConvention.getProperty("case"))) {
      sb.append(inDbName.toUpperCase(Locale.ENGLISH));
    }
    return sb.toString();
  }

  /**
   * Read in and format into a String the license file.
   *
   * @param filePath File containing license text
   * @param inType Can be "xml" or "java", determines comment style
   * @return
   * @throws java.io.IOException
   */
  private static String readFileAsString(String filePath, String inType)
          throws java.io.IOException {
    StringBuilder fileData = new StringBuilder();
    BufferedReader reader = new BufferedReader(
            new FileReader(filePath));
    String linePrefix = "";
    if (XML_COMMENT.equals(inType)) {
      fileData.append("<!--\n");
      linePrefix = "  ";
    } else if (JAVA_COMMENT.equals(inType)) {
      fileData.append("/*\n");
      linePrefix = " * ";
    }
    String line;
    while ((line = reader.readLine()) != null) {
      fileData.append(linePrefix);
      fileData.append(line);
      fileData.append("\n");
    }
    if (XML_COMMENT.equals(inType)) {
      fileData.append("\n-->\n");
    } else if (JAVA_COMMENT.equals(inType)) {
      fileData.append(" *\n");
      fileData.append(" */\n");
    }
    fileData.append("\n");
    reader.close();
    return fileData.toString();
  }

  public Winter() {
    this.init();
  }

  public Winter(String inPropertiesFile) {
    this.setPropertiesFile(inPropertiesFile);
    this.init();
  }

  public void init() {
    // generic:
    try {
      System.getProperties().load(
              new FileInputStream(new File(this.getPropertiesFile())));
    } catch (IOException ex) {
      logger.severe("No properties file found! Gotta have it!\n" + ex.toString());
      System.exit(1);
    }
    //http://static.springframework.org/spring/docs/2.5.x/reference/beans.html#context-introduction-ctx-vs-beanfactory
    ApplicationContext context =
            new ClassPathXmlApplicationContext("applicationContext.xml");
    BeanFactory beanFactory = (BeanFactory) context;

    // specific:
    setMetaDataDAO(
            (MetaDataDAO) beanFactory.getBean("metaDataDAO"));
    // not currently used...
    setPersistenceTestDAO((PersistenceTestDAO) beanFactory.getBean("persistenceTestDAO"));

    try {

      // load up the naming conventions
      ddlConvention.load(new FileInputStream(
              new File(this.conventionsDirectory + FS +
              System.getProperty("winter.db.naming") + ".properties")));
      javaConvention.load(new FileInputStream(
              new File(this.conventionsDirectory + FS +
              System.getProperty("winter.java.naming") + ".properties")));
    } catch (IOException ex) {
      logger.severe("Could not load naming properties, using defaults.\n" + ex.toString());
    }

    // set up the indentation value
    ddlIndent = createIndent(
            ddlConvention.getProperty("indentCharacter", "space"),
            ddlConvention.getProperty("indent", "1"));
    javaIndent = createIndent(
            javaConvention.getProperty("indentCharacter", "space"),
            javaConvention.getProperty("indent", "1"));

  }

  private String createIndent(String indentType, String indentLength) {
    StringBuilder sb = new StringBuilder();
    String indent = " ";
    if (INDENT_TAB.equalsIgnoreCase(indentType)) {
      indent = "\t";
    } 
    Integer num = new Integer(indentLength);
    for (int i = 0; i < num; i++) {
      sb.append(indent);
    }
    return sb.toString();
  }

  /**
   * @return the metadataDAO
   */
  public MetaDataDAO getMetaDataDAO() {
    return metaDataDAO;
  }

  /**
   * @param metadataDAO the metadataDAO to set
   */
  public void setMetaDataDAO(MetaDataDAO metadataDAO) {
    this.metaDataDAO = metadataDAO;
  }

  /**
   * @return the persistenceTestDAO
   */
  public PersistenceTestDAO getPersistenceTestDAO() {
    return persistenceTestDAO;
  }

  /**
   * @param persistenceTestDAO the persistenceTestDAO to set
   */
  public void setPersistenceTestDAO(PersistenceTestDAO persistenceTestDAO) {
    this.persistenceTestDAO = persistenceTestDAO;
  }

  /**
   * @return the propertiesFile
   */
  public String getPropertiesFile() {
    return propertiesFile;
  }

  /**
   * @param propertiesFile the propertiesFile to set
   */
  public void setPropertiesFile(String propertiesFile) {
    this.propertiesFile = propertiesFile;
  }
}
