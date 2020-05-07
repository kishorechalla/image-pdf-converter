package org.jconverter.fileconvert;

import java.io.File;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;

public class App {

	public static void main(String[] args) {
		try {
			String sourceDir = "C:\\Users\\P001369F\\Downloads\\converter-test\\source";
			String destDir = "C:\\Users\\P001369F\\Downloads\\converter-test\\destination";
			boolean finalStatus = readDirectoryAndConvert(sourceDir, destDir, true);
			if (finalStatus) {
				System.out.println("all the files from " + sourceDir + " converted into " + destDir);
			} else {
				System.out.println("failed to convert some of the files in " + sourceDir
						+ ". Please see above logs for more information.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean readDirectoryAndConvert(String sourceDir, String destinationDir, boolean status) throws Exception {
		File source = new File(sourceDir);
		if (source.exists()) {
			if (source.isDirectory()) {
				for (File file : source.listFiles()) {
					String newDest;
					if(file.isDirectory()) {
						newDest = String.format("%s\\%s", destinationDir, file.getName());
						new File(newDest).mkdir();
					} else {
						newDest = destinationDir;
					}
					readDirectoryAndConvert(file.getAbsolutePath(), newDest, status && true);
				}
				return status;
			} else if (source.isFile()) {
				String sourceFileName = source.getName();
				String[] nameSplits = sourceFileName.split("\\.");
				String ext = nameSplits[1].toLowerCase();
				String destFileName = String.format("%s\\%s.pdf", destinationDir, nameSplits[0]);
				boolean isConverted = false;
				switch (ext) {
				case "jpg":
					isConverted = convertGenericImageToPdf(source.getAbsolutePath(), destFileName);
					break;
				case "jpeg":
					isConverted = convertGenericImageToPdf(source.getAbsolutePath(), destFileName);
					break;
				case "png":
					isConverted = convertGenericImageToPdf(source.getAbsolutePath(), destFileName);
					break;
				case "tiff":
					isConverted = convertTiffToPdf(source.getAbsolutePath(), destFileName);
					break;
				case "tif":
					isConverted = convertTiffToPdf(source.getAbsolutePath(), destFileName);
					break;
				default:
					System.out
							.println("the current file format: " + sourceDir + " is not supported. Skipping this file");
					isConverted = false;
					break;
				}
				return isConverted;
			} else {
				System.out.println("is the current file " + sourceDir
						+ " correpted? Something is wrong with this. Skipping this file");
				return false;
			}
		} else {
			System.out.println("the source " + sourceDir + " does not exist!!");
			return false;
		}
	}

	private static boolean convertTiffToPdf(String source, String destination) {
		try {
			RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
			RandomAccessFileOrArray tiffFile = new RandomAccessFileOrArray(factory.createBestSource(source));
			int numberOfPages = TiffImage.getNumberOfPages(tiffFile);
			Image[] images = new Image[numberOfPages];
			Document pdfFile = new Document();
			PdfWriter.getInstance(pdfFile, new FileOutputStream(destination));
			pdfFile.open();
			for (int i = 1; i <= numberOfPages; i++) {
				images[i - 1] = TiffImage.getTiffImage(tiffFile, i);
			}
			return convertToPdf(destination, images);
		} catch (Exception e) {
			System.out.println("error while converting " + source + " to " + destination);
			e.printStackTrace();
			return false;
		}
	}

	private static boolean convertGenericImageToPdf(String source, String destination) {
		try {
			Image image = Image.getInstance(source);
			return convertToPdf(destination, new Image[] { image });
		} catch (Exception e) {
			System.out.println("error while converting " + source + " to " + destination);
			e.printStackTrace();
			return false;
		}
	}

	private static boolean convertToPdf(String destination, Image[] images) throws Exception {
		Document pdfFile = new Document();
		PdfWriter.getInstance(pdfFile, new FileOutputStream(destination));
		pdfFile.open();
		for (Image image : images) {
			image.scaleToFit(PageSize.A4);
			float x = (PageSize.A4.getWidth() - image.getScaledWidth()) / 2;
			float y = (PageSize.A4.getHeight() - image.getScaledHeight()) / 2;
			image.setAbsolutePosition(x, y);
			image.setBorderWidth(0);
			pdfFile.add(image);
			pdfFile.newPage();
		}
		pdfFile.close();
		return true;
	}

}
