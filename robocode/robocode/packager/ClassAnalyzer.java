/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.packager;


import java.io.*;
import java.util.*;
import robocode.util.*;


/**
 * Insert the type's description here.
 * Creation date: (10/8/2001 1:04:11 AM)
 * @author: Administrator
 */
public class ClassAnalyzer {
	private final static byte CONSTANT_Class = 7;
	private final static byte CONSTANT_Fieldref = 9;
	private final static byte CONSTANT_Methodref = 10; 
	private final static byte CONSTANT_InterfaceMethodref = 11;
	private final static byte CONSTANT_String = 8;
	private final static byte CONSTANT_Integer = 3; 
	private final static byte CONSTANT_Float = 4;
	private final static byte CONSTANT_Long = 5;
	private final static byte CONSTANT_Double = 6;
	private final static byte CONSTANT_NameAndType = 12;  
	private final static byte CONSTANT_Utf8 = 1;

	/**
	 * ClassAnalyzer constructor comment.
	 */
	public ClassAnalyzer() {
		super();
	}

	public static Vector getReferencedClasses(byte[] classFile) {

		/*
		 http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html

		 4.1 The ClassFile Structure
		 A class file consists of a single ClassFile structure: 

		 ClassFile {
		 u4 magic;
		 u2 minor_version;
		 u2 major_version;
		 u2 constant_pool_count;
		 cp_info constant_pool[constant_pool_count-1];
		 u2 access_flags;
		 u2 this_class;
		 u2 super_class;
		 u2 interfaces_count;
		 u2 interfaces[interfaces_count];
		 u2 fields_count;
		 field_info fields[fields_count];
		 u2 methods_count;
		 method_info methods[methods_count];
		 u2 attributes_count;
		 attribute_info attributes[attributes_count];
		 }
		 */
		Vector referencedClasses = new Vector();
		String strings[];
		Vector classNameIndexes = new Vector();

		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(classFile));
			long magic = in.readInt();

			if (magic != 0xCAFEBABE) {
				log("Not a class file!");
				return null;
			}
			int minor_version = in.readUnsignedShort();
			int major_version = in.readUnsignedShort();
			int constant_pool_count = in.readUnsignedShort();

			strings = new String[constant_pool_count];

			/*

			 All constant_pool table entries have the following general format:


			 cp_info {
			 u1 tag;
			 u1 info[];
			 }

			 Constant Type  Value  
			 CONSTANT_Class  7  
			 CONSTANT_Fieldref  9  
			 CONSTANT_Methodref  10  
			 CONSTANT_InterfaceMethodref  11  
			 CONSTANT_String  8  
			 CONSTANT_Integer  3  
			 CONSTANT_Float  4  
			 CONSTANT_Long  5  
			 CONSTANT_Double  6  
			 CONSTANT_NameAndType  12  
			 CONSTANT_Utf8  1
			 */
	

			for (int i = 1; i < constant_pool_count; i++) {
				byte tag = in.readByte();

				switch (tag) {

				/*
				 CONSTANT_Class_info {
				 u1 tag;
				 u2 name_index;
				 }
				 */
				case CONSTANT_Class: {
						int name_index = in.readUnsignedShort();

						classNameIndexes.add(new Integer(name_index));
						// log("There is a class at index: " + name_index);
					}
					break;

				/*
				 CONSTANT_Fieldref_info {
				 u1 tag;
				 u2 class_index;
				 u2 name_and_type_index;
				 }
				 CONSTANT_Methodref_info {
				 u1 tag;
				 u2 class_index;
				 u2 name_and_type_index;
				 }
				 CONSTANT_InterfaceMethodref_info {
				 u1 tag;
				 u2 class_index;
				 u2 name_and_type_index;
				 }
				 */
				case CONSTANT_Fieldref:
				case CONSTANT_Methodref:
				case CONSTANT_InterfaceMethodref: {
						int class_index = in.readUnsignedShort();
						int name_and_type_index = in.readUnsignedShort();
					}
					break;

				/*
				 CONSTANT_String_info {
				 u1 tag;
				 u2 string_index;
				 }
				 */
				case CONSTANT_String: {
						int string_index = in.readUnsignedShort();
					}
					break;

				/*
				 CONSTANT_Integer_info {
				 u1 tag;
				 u4 bytes;
				 }
				 CONSTANT_Float_info {
				 u1 tag;
				 u4 bytes;
				 }
				 */
				case CONSTANT_Integer:
				case CONSTANT_Float: {
						int bytes = in.readInt();
					}
					break;

				/*
				 CONSTANT_Long_info {
				 u1 tag;
				 u4 high_bytes;
				 u4 low_bytes;
				 }
				 CONSTANT_Double_info {
				 u1 tag;
				 u4 high_bytes;
				 u4 low_bytes;
				 }
				 All 8-byte constants take up two entries in the constant_pool table of the class file. If a CONSTANT_Long_info or CONSTANT_Double_info structure is the item in the constant_pool table at index n, then the next usable item in the pool is located at index n+2. The constant_pool index n+1 must be valid but is considered unusable.2
				 */

				case CONSTANT_Long:  
				case CONSTANT_Double: {
						int high_bytes = in.readInt();
						int low_bytes = in.readInt();

						i++; // see "all 8-byte..." comment above.
					}
					break;

				/*
				 CONSTANT_NameAndType_info {
				 u1 tag;
				 u2 name_index;
				 u2 descriptor_index;
				 }
				 */
				case CONSTANT_NameAndType: {
						int name_index = in.readUnsignedShort();
						int descriptor_index = in.readUnsignedShort();
					}
					break;

				/*
				 CONSTANT_Utf8_info {
				 u1 tag;
				 u2 length;
				 u1 bytes[length];
				 }
				 */
				case CONSTANT_Utf8: {
						String utf8_string = in.readUTF();

						strings[i] = utf8_string;
						// log("The string at index " + i + " is: " + utf8_string);
					}
					break;
				} // switch
			} // for i
		} catch (IOException e) {
			return null;
		}

		for (int i = 0; i < classNameIndexes.size(); i++) {
			String className = strings[((Integer) classNameIndexes.elementAt(i)).intValue()];

			if (className.indexOf("[") != 0) {
				referencedClasses.add(className);
			} // strings[((Integer)classNameIndexes.elementAt(i)).intValue()]);
			// log("Adding class: " + referencedClasses.elementAt(referencedClasses.size() - 1));
		}
	
		return referencedClasses;
	}              

	/**
	 * Insert the method's description here.
	 * Creation date: (10/23/2001 5:43:37 PM)
	 * @param s java.lang.String
	 */
	private static void log(String s) {
		Utils.log(s);
	}
}
