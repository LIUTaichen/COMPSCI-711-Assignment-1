package com.uoa.webcache.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

import com.uoa.webcache.server.Server;

public class FileFragmenter {
	
	private  Logger log = Logger.getLogger(FileFragmenter.class.getName());

	//private Integer prime = 10000169;
	private Integer prime = 1009;
	private Integer windowSize = 48;
	private Integer hashFactor = 256;
	public static void main(String[] Args){
		FileFragmenter fragmenter = new FileFragmenter();
		fragmenter.fragment("src/resources/files/Wildlife.wmv");
	}
	


public List<byte[]> fragment(String fileName) {
		List<byte[]> resultList = new ArrayList<byte[]>();
		int eliminationMultiplier = generateEliminationMultiplier(hashFactor, windowSize, prime);
		
		try {
			Path requestedFile = Paths.get(fileName);
			byte[] data = Files.readAllBytes(requestedFile);
			if(data.length <= 2048){
				resultList.add(data);
				return resultList;
			}
			int rollingHash = 0;
			int boundary = 0;
			int count = 1;
			int lastBoundaryIndex = 0;
			Queue<Integer> window = new LinkedList<Integer>();

			log.info("total bytes : " + data.length);
			for (int i = 0; i < windowSize; i++) {
				int value = data[i];
				window.add(value);
				rollingHash = (rollingHash * hashFactor + value  + prime) % prime;
			}
			
			for (int j = windowSize;  j < data.length; j++)
			{
				int nextValue = data[j];
				int firstValueInWindow = window.poll();
				window.add(nextValue);
				rollingHash = (rollingHash  + prime -  (firstValueInWindow) * (eliminationMultiplier ) % prime) % prime ;
				rollingHash = (rollingHash * hashFactor + nextValue ) % prime;
				if (rollingHash == boundary){
					//log.info("boundary found " + j);
					if( j - lastBoundaryIndex >= 2048){
						resultList.add(Arrays.copyOfRange(data,lastBoundaryIndex, j));
						lastBoundaryIndex = j;
						
						count ++;
					}
					
				}
				
			}
			resultList.add(Arrays.copyOfRange(data,lastBoundaryIndex, data.length));
			log.info("average chunk size " + (data.length / count));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info(resultList.size()+"");
		return resultList;

	}

	private int generateEliminationMultiplier(Integer hashFactor, Integer windowSize, Integer prime) {
		int multiplier =1;
		for (int i = 0; i < windowSize -1; i++){
			multiplier = (multiplier  * hashFactor )% prime;
		}
		return multiplier;
	}
	
}
