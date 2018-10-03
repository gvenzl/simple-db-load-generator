package com.gvenzl.test;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.gvenzl.util.RandomIterator;

public class RandomIteratorTest extends TestCase
{
	private ArrayList<Integer> myTestList;
	private RandomIterator<Integer> myRandomIterator;
	
	@Before
	public void setUp() throws Exception
	{
		myTestList = new ArrayList<Integer>();
		
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
		
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		
		for(int i=0;i<10000;i++)
		{
			arrayList.add(i);
		}
		
		RandomIterator<Integer> randomIterator2 = new RandomIterator<Integer>(arrayList);
		
		for (int i=0;i<myTestList.size();i++)
		{
			assertFalse(myRandomIterator.next().equals(randomIterator2.next()));
		}
	}
	
	@Test
	public void test_remove()
	{
		while (myRandomIterator.hasNext())
			myRandomIterator.remove();
	}
}
