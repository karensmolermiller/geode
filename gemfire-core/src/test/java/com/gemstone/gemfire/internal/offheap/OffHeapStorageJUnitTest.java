/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gemstone.gemfire.internal.offheap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.gemstone.gemfire.test.junit.categories.UnitTest;

@Category(UnitTest.class)
public class OffHeapStorageJUnitTest {

  private final static long MEGABYTE = 1024 * 1024;
  private final static long GIGABYTE = 1024 * 1024 * 1024;

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testParseOffHeapMemorySizeNegative() {
    assertEquals(0, OffHeapStorage.parseOffHeapMemorySize("-1"));
  }
  @Test
  public void testParseOffHeapMemorySizeNull() {
    assertEquals(0, OffHeapStorage.parseOffHeapMemorySize(null));
  }
  @Test
  public void testParseOffHeapMemorySizeEmpty() {
    assertEquals(0, OffHeapStorage.parseOffHeapMemorySize(""));
  }
  @Test
  public void testParseOffHeapMemorySizeBytes() {
    assertEquals(MEGABYTE, OffHeapStorage.parseOffHeapMemorySize("1"));
    assertEquals(Integer.MAX_VALUE * MEGABYTE, OffHeapStorage.parseOffHeapMemorySize("" + Integer.MAX_VALUE));
  }
  @Test
  public void testParseOffHeapMemorySizeKiloBytes() {
    try {
      OffHeapStorage.parseOffHeapMemorySize("1k");
      fail("Did not receive expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // Expected
    }
  }
  @Test
  public void testParseOffHeapMemorySizeMegaBytes() {
    assertEquals(MEGABYTE, OffHeapStorage.parseOffHeapMemorySize("1m"));
    assertEquals(Integer.MAX_VALUE * MEGABYTE, OffHeapStorage.parseOffHeapMemorySize("" + Integer.MAX_VALUE + "m"));
  }
  @Test
  public void testParseOffHeapMemorySizeGigaBytes() {
    assertEquals(GIGABYTE, OffHeapStorage.parseOffHeapMemorySize("1g"));
    assertEquals(Integer.MAX_VALUE * GIGABYTE, OffHeapStorage.parseOffHeapMemorySize("" + Integer.MAX_VALUE + "g"));
  }
  @Test
  public void testCalcMaxSlabSize() {
    assertEquals(100, OffHeapStorage.calcMaxSlabSize(100L));
    assertEquals(Integer.MAX_VALUE, OffHeapStorage.calcMaxSlabSize(Long.MAX_VALUE));
    try {
      System.setProperty("gemfire.OFF_HEAP_SLAB_SIZE", "99");
      assertEquals(99*1024*1024, OffHeapStorage.calcMaxSlabSize(100L*1024*1024));
      System.setProperty("gemfire.OFF_HEAP_SLAB_SIZE", "88m");
      assertEquals(88*1024*1024, OffHeapStorage.calcMaxSlabSize(100L*1024*1024));
      System.setProperty("gemfire.OFF_HEAP_SLAB_SIZE", "77M");
      assertEquals(77*1024*1024, OffHeapStorage.calcMaxSlabSize(100L*1024*1024));
      System.setProperty("gemfire.OFF_HEAP_SLAB_SIZE", "1g");
      assertEquals(1*1024*1024*1024, OffHeapStorage.calcMaxSlabSize(2L*1024*1024*1024));
      System.setProperty("gemfire.OFF_HEAP_SLAB_SIZE", "2G");
      assertEquals(2L*1024*1024*1024, OffHeapStorage.calcMaxSlabSize(2L*1024*1024*1024+1));
      System.setProperty("gemfire.OFF_HEAP_SLAB_SIZE", "foobarG");
      try {
        OffHeapStorage.calcMaxSlabSize(100);
        fail("expected IllegalArgumentException");
      } catch (IllegalArgumentException expected) {
      }
      System.setProperty("gemfire.OFF_HEAP_SLAB_SIZE", "");
      assertEquals(100, OffHeapStorage.calcMaxSlabSize(100L));
      assertEquals(Integer.MAX_VALUE, OffHeapStorage.calcMaxSlabSize(Long.MAX_VALUE));
    } finally {
      System.clearProperty("gemfire.OFF_HEAP_SLAB_SIZE");
    }
  }
}
