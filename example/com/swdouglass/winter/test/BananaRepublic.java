/*
 * Copyright 2008 Scott Douglass, all rights reserved.
 *    http://swdouglass.com/
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

package com.swdouglass.winter.test;

import java.io.Serializable;

/**
 * Description here...
 *
 * @author Winter Code Generator
 */
public class BananaRepublic implements Serializable {

  /** Description... */
  private String primaryExport;

  /** Description... */
  private Long id;

  /** Description... */
  private String dictatorName;

  /** Description... */
  private Float grossDomesticProduct;

  /** Description... */
  private Boolean usPuppet;

  /** Description... */
  private Integer dictatorHeight;

  /**
   * Description...
   *
   * @return grossDomesticProduct Description...
   */
  public Float getGrossDomesticProduct() {
    return this.grossDomesticProduct;
  }

  /**
   * Description...
   *
   * @param inGrossDomesticProduct Description...
   */
  public void setGrossDomesticProduct(Float inGrossDomesticProduct) {
    this.grossDomesticProduct = inGrossDomesticProduct;
  }

  /**
   * Description...
   *
   * @return id Description...
   */
  public Long getId() {
    return this.id;
  }

  /**
   * Description...
   *
   * @param inId Description...
   */
  public void setId(Long inId) {
    this.id = inId;
  }

  /**
   * Description...
   *
   * @return usPuppet Description...
   */
  public Boolean getUsPuppet() {
    return this.usPuppet;
  }

  /**
   * Description...
   *
   * @param inUsPuppet Description...
   */
  public void setUsPuppet(Boolean inUsPuppet) {
    this.usPuppet = inUsPuppet;
  }

  /**
   * Description...
   *
   * @return dictatorName Description...
   */
  public String getDictatorName() {
    return this.dictatorName;
  }

  /**
   * Description...
   *
   * @param inDictatorName Description...
   */
  public void setDictatorName(String inDictatorName) {
    this.dictatorName = inDictatorName;
  }

  /**
   * Description...
   *
   * @return dictatorHeight Description...
   */
  public Integer getDictatorHeight() {
    return this.dictatorHeight;
  }

  /**
   * Description...
   *
   * @param inDictatorHeight Description...
   */
  public void setDictatorHeight(Integer inDictatorHeight) {
    this.dictatorHeight = inDictatorHeight;
  }

  /**
   * Description...
   *
   * @return primaryExport Description...
   */
  public String getPrimaryExport() {
    return this.primaryExport;
  }

  /**
   * Description...
   *
   * @param inPrimaryExport Description...
   */
  public void setPrimaryExport(String inPrimaryExport) {
    this.primaryExport = inPrimaryExport;
  }

}
