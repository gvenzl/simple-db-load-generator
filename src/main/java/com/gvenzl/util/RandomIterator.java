/*
 * Since: September, 2012
 * Author: gvenzl
 * Name: RandomIterator.java
 * Description: A random iterator over the commands.
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

package com.gvenzl.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * A iterator over a collection in random order.
 * @author gvenzl
 * @param <E> the type of the element returned by this iterator
 */
public class RandomIterator<E> implements Iterator<E>
{
    private final ArrayList<Integer> index;
    private final List<E> list;
    private final Random random;

    /**
     *  Generates a new RandomIterator over passed on list.
     */
    public RandomIterator(List<E> l)
    {
        list = l;
        random = new Random();
        index = new ArrayList<>();

        // Build HashSet with values
        for (int i=0;i<list.size();i++)
        {
            index.add(i);
        }
    }

    @Override
    public boolean hasNext()
    {
        // as long as there are still values in the indexSet there are still values to iterate over
        return (!index.isEmpty());
    }

    @Override
    public E next() throws NoSuchElementException
    {
        if (!hasNext())
        {
            throw new NoSuchElementException();
        }

        int idx = random.nextInt(index.size());
        E obj = list.get(index.get(idx));
        index.remove(idx);
        return obj;
    }

    @Override
    public void remove()
    {
        index.remove(index.size()-1);
    }
}
