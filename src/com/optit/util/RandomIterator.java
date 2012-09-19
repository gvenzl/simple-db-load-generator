package com.optit.util;

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
	private ArrayList<Integer> index; 
	private List<E> list;
	private Random random;
	
	/**
	 *  Generates a new RandomIterator over passed on list.
	 */
	public RandomIterator(List<E> l)
	{
		list = l;
		random = new Random();
		index = new ArrayList<Integer>();
		
		// Build HashSet with values
		for (int i=0;i<list.size();i++)
		{
			index.add(Integer.valueOf(i));
		}
	}

	@Override
	public boolean hasNext()
	{
		// as long as there are still values in the indexSet there are still values to iterate over
		return (index.size() > 0 ? true : false);
	}

	@Override
	public E next() throws NoSuchElementException
	{
		if (!hasNext())
		{
			throw new NoSuchElementException();
		}
		
		Integer idx = random.nextInt(index.size());
		E obj = list.get(index.get(idx.intValue()));
		index.remove(idx.intValue());
		return obj;
	}

	@Override
	public void remove()
	{
		index.remove(index.size()-1);
	}
}
