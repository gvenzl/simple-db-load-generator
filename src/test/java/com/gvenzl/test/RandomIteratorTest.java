/*
 * Since: September, 2012
 * Author: gvenzl
 * Name: RandomIteratorTest.java
 * Description: Tests the RandomIterator class.
 *
 * Copyright 2012 Gerald Venzl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gvenzl.test;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gvenzl.util.RandomIterator;

public class RandomIteratorTest {

    private ArrayList<Integer> myTestList;
    private RandomIterator<Integer> myRandomIterator;

    @Before
    public void setUp() {
        myTestList = new ArrayList<>();

        myTestList.add(1);
        myTestList.add(2);
        myTestList.add(3);
        myTestList.add(4);
        myTestList.add(5);
        myTestList.add(6);
        myTestList.add(7);
        myTestList.add(8);
        myTestList.add(9);
        myTestList.add(10);

        myRandomIterator = new RandomIterator<>(myTestList);
    }

    // Test whether next() iterates over the 10 items of the list (without checking the randomness)
    @Test
    public void test_next()
    {
        for (int i=0;i<myTestList.size();i++)
        {
            myRandomIterator.next();
        }
    }

    // Test whether next() throws NoSuchElement exception when iteration is complete
    @Test
    public void test_nextNegativeException()
    {
        for (int i=0;i<myTestList.size()+1;i++)
        {
            try
            {
                myRandomIterator.next();
            }
            catch(NoSuchElementException e)
            {
                // Exception got not thrown at the position right after the last entry
                if(i!=myTestList.size())
                    throw e;
            }
        }
    }

    // Test whether hasNext works correct
    @Test
    public void test_hasNext()
    {
        int i;

        for (i=0;myRandomIterator.hasNext();i++)
        {
            myRandomIterator.next();
        }

        if(i!=myTestList.size())
        {
            throw new RuntimeException("hasNext() did return false on the wrong position!");
        }
    }

    // Test randomness of data returned
    @Test
    public void test_randomness()
    {

        ArrayList<Integer> arrayList = new ArrayList<>();

        for(int i=0;i<10000;i++)
        {
            arrayList.add(i);
        }

        RandomIterator<Integer> randomIterator2 = new RandomIterator<>(arrayList);

        for (int i=0;i<myTestList.size();i++)
        {
            Assert.assertNotEquals(myRandomIterator.next(), randomIterator2.next());
        }
    }

    @Test
    public void test_remove()
    {
        while (myRandomIterator.hasNext())
            myRandomIterator.remove();
    }
}
