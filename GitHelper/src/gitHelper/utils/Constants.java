package gitHelper.utils;

public class Constants
{
	public static String GIT = "git";
	
	public enum Relationship
	{
		MergeClean,
		MergeConflict,
		Error
	};
}