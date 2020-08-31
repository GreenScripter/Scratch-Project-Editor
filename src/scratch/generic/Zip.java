package scratch.generic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip {
	
	/**
	 * Recursively zip a file.
	 * 
	 * @param file the file to zip
	 * @param destination the location to store the zip
	 * @throws IOException
	 */
	public static void zipFile(File file, File destination) throws IOException {
		FileOutputStream fos = new FileOutputStream(destination.getCanonicalFile());
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		
		zipFile(file.getCanonicalFile(), file.getName(), zipOut);
		zipOut.close();
		fos.close();
	}
	
	/**
	 * Zip the contents of a folder.
	 * 
	 * @param file the source folder.
	 * @param destination location to save the zip on disk
	 * @throws IOException
	 */
	public static void zipFolderContents(File file, File destination) throws IOException {
		FileOutputStream fos = new FileOutputStream(destination.getCanonicalFile());
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		
		zipFolderContents(file.getCanonicalFile(), file.getName(), zipOut);
		zipOut.close();
		fos.close();
	}
	
	/**
	 * Delete a file recursively.
	 * 
	 * @param toDelete the file to delete
	 */
	public static void deleteFile(File toDelete) {
		if (toDelete.isDirectory()) {
			for (File f : toDelete.listFiles()) {
				deleteFile(f);
			}
		}
		toDelete.delete();
	}
	
	/**
	 * Zip a file as bytes.
	 * 
	 * @param content the file data
	 * @param name the name of the file
	 * @return the file as a zip
	 * @throws IOException
	 */
	public static byte[] zipContent(byte[] content, String name) throws IOException {
		ByteArrayOutputStream fos = new ByteArrayOutputStream();
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		
		zipContent(content, name, zipOut);
		zipOut.close();
		return fos.toByteArray();
	}
	
	/**
	 * Zip multiple files worth of content into a single zip.
	 * 
	 * @param content the list of file data
	 * @param names the names of every file
	 * @return the files as a single zip
	 * @throws IOException
	 */
	public static byte[] zipContent(List<byte[]> content, List<String> names) throws IOException {
		ByteArrayOutputStream fos = new ByteArrayOutputStream();
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		
		zipContent(content, names, zipOut);
		zipOut.close();
		return fos.toByteArray();
	}
	
	/**
	 * Zip multiple files worth of content into a single zip.
	 * 
	 * @param content the content to put in the zip
	 * @return the zipped content
	 * @throws IOException
	 */
	public static byte[] zipContent(Map<String, byte[]> content) throws IOException {
		List<byte[]> bytes = new ArrayList<>();
		List<String> names = new ArrayList<>();
		
		for (Entry<String, byte[]> e : content.entrySet()) {
			bytes.add(e.getValue());
			names.add(e.getKey());
		}
		
		return zipContent(bytes, names);
	}
	
	private static void zipContent(byte[] fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		ByteArrayInputStream fis = new ByteArrayInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}
	
	private static void zipContent(List<byte[]> filesToZip, List<String> fileNames, ZipOutputStream zipOut) throws IOException {
		for (int i = 0; i < filesToZip.size(); i++) {
			zipContent(filesToZip.get(i), fileNames.get(i), zipOut);
		}
	}
	
	private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		
		if (fileToZip.isDirectory()) {
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + File.separator + childFile.getName(), zipOut);
			}
			return;
		}
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}
	
	private static void zipFolderContents(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		
		if (fileToZip.isDirectory()) {
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, childFile.getName(), zipOut);
			}
			return;
		}
	}
	
	/**
	 * Unzip a file and store the output in a destination.
	 * 
	 * @param file the file to unzip
	 * @param destination where to put the unzipped file
	 * @throws IOException
	 */
	public static void unzipFile(File file, File destination) throws IOException {
		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(file.getCanonicalFile()));
		ZipEntry zipEntry = zis.getNextEntry();
		
		destination = destination.getCanonicalFile();
		while (zipEntry != null) {
			String fileName = zipEntry.getName();
			File newFile = new File(destination + File.separator + fileName).getCanonicalFile();
			{//breaks if the parent directory is never what it should be.
				File dupe = newFile;
				while (!dupe.equals(destination)) {
					dupe = dupe.getParentFile();
				}
			}
			newFile.getParentFile().mkdirs();
			
			FileOutputStream fos = new FileOutputStream(newFile);
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}
	
	/**
	 * Unzip a zip file as a byte array into a mapping of file names to content.
	 * 
	 * @param file the content of the file
	 * @return the files in the zip
	 * @throws IOException
	 */
	public static Map<String, byte[]> unzipContent(byte[] file) throws IOException {
		Map<String, byte[]> output = new HashMap<>();
		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(file));
		ZipEntry zipEntry = zis.getNextEntry();
		
		while (zipEntry != null) {
			String fileName = zipEntry.getName();
			ByteArrayOutputStream fos = new ByteArrayOutputStream();
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			output.put(fileName, fos.toByteArray());
			zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
		return output;
	}
}
