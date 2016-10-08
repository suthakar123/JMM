package com.jmm.cra;

import java.io.File;

public class TestClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(File.pathSeparator);
		System.out.println(File.separator);
		String treeFileName="TreeFiles/4_activity_tree_v1.mat";
		String treeFileRootFolderPath=System.getProperty("java.class.path");
		System.out.println(new TestClass().getClass().getClassLoader().getResource(treeFileName));
		System.out.println(treeFileRootFolderPath);
		File f=new File(treeFileName);
		System.out.println(f.getAbsolutePath());
	}

}
