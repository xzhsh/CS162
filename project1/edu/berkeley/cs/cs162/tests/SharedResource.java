package edu.berkeley.cs.cs162.tests;

/**
 * Utility shared resource class.
 * 
 * Basically a mutable wrapper around an object
 * 
 * @author xshi
 *
 * @param <E>
 */
class SharedResource<E>
{
	private E resource;
	public SharedResource(E initial)
	{
		resource = initial;
	}
	
	public E getResource()
	{
		return resource;
	}
	
	public void setResource(E newVal)
	{
		resource = newVal;
	}
}