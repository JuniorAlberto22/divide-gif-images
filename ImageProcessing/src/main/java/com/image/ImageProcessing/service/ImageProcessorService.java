package com.image.ImageProcessing.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.image.ImageProcessing.response.ImagesResponse;

@Service
public class ImageProcessorService {

	final Logger logger = Logger.getLogger(ImageProcessorService.class.getName());
	
	private static final String FILE_TYPE_GIF = "gif";
	private static final String METADATA_GIF_IMAGE = "javax_imageio_gif_image_1.0";
	
	@Autowired
	private Environment env;
	
	
	/**
	 * Process images from one gif image, where were returned the frames transformed into a byte array
	 * @param pathFrom
	 * @return
	 * @throws Exception
	 * @author Alberto
	 */
	public ImagesResponse divideGifImages(String pathFrom) throws Exception {
		String DEFAULT_PATH = env.getProperty("default.path");
		logger.info("Started the separate gif prossecing");
		long timeMiles = System.currentTimeMillis();
		byte[][] imagesBytes;
		
		String absolutePath = new StringBuilder(DEFAULT_PATH).append(pathFrom).toString();
		File file = new File(absolutePath);
		try {
			ImagesResponse response = new ImagesResponse();
			response.setNomeGif(pathFrom);
			response.setCaminhoGif(absolutePath);
			if(file.exists()) {
				
			    String[] imageatt = getImagesProperties();    
			    ImageReader reader = (ImageReader)ImageIO.getImageReadersByFormatName(FILE_TYPE_GIF).next();
			    ImageInputStream ciis = ImageIO.createImageInputStream(file);
			    reader.setInput(ciis, false);
			    int noi = reader.getNumImages(true);
			    imagesBytes = new byte[noi][];
			    BufferedImage master = null;
			    for (int i = 0; i < noi; i++) { 
			        BufferedImage image = reader.read(i);
			        
			        IIOMetadata metadata = reader.getImageMetadata(i);
			        ByteArrayOutputStream out = new ByteArrayOutputStream();
			        Node tree = metadata.getAsTree(METADATA_GIF_IMAGE);
			        NodeList children = tree.getChildNodes();
			        master = OptimizeImages(imageatt, master, i, image, children);
			        CollectBytes(imagesBytes, master, i, out);
			    }
			    response.setImagesBytes(imagesBytes);
			}
			
		    logger.info("Gif processing Waited: " + (new Long(System.currentTimeMillis() - timeMiles) / 1000.0) + " segundos");
		    return response;
		} catch (IOException e) {
		    throw new Exception("Error to processing gif image.");
		}
	}

	/**
	 * Image properties to optimizer
	 * @return
	 */
	private String[] getImagesProperties() {
		String[] imageatt = new String[]{
		        "imageLeftPosition",
		        "imageTopPosition",
		        "imageWidth",
		        "imageHeight"
		};
		return imageatt;
	}

	/**
	 * Optimize images
	 * @param imageatt
	 * @param master
	 * @param i
	 * @param image
	 * @param children
	 * @return
	 * @author Alberto
	 */
	private BufferedImage OptimizeImages(String[] imageatt, BufferedImage master, int i, BufferedImage image,
			NodeList children) {
		for (int j = 0; j < children.getLength(); j++) {
		    Node nodeItem = children.item(j);

		    if(nodeItem.getNodeName().equals("ImageDescriptor")){
		        Map<String, Integer> imageAttr = new HashMap<String, Integer>();

		        for (int k = 0; k < imageatt.length; k++) {
		            NamedNodeMap attr = nodeItem.getAttributes();
		            Node attnode = attr.getNamedItem(imageatt[k]);
		            imageAttr.put(imageatt[k], Integer.valueOf(attnode.getNodeValue()));
		        }
		        if(i==0){
		            master = new BufferedImage(imageAttr.get("imageWidth"), imageAttr.get("imageHeight"), BufferedImage.TYPE_INT_ARGB);
		        }
		        master.getGraphics().drawImage(image, imageAttr.get("imageLeftPosition"), imageAttr.get("imageTopPosition"), null);
		    }
		}
		return master;
	}

	/**
	 * Convert {@link BufferedImage} into one byte array
	 * 
	 * @param imagesBytes
	 * @param master
	 * @param i
	 * @param out
	 * @throws IOException
	 * @author Alberto
	 */
	private void CollectBytes(byte[][] imagesBytes, BufferedImage master, int i, ByteArrayOutputStream out)
			throws IOException {
		ImageIO.write(master, "PNG", out); 
		out.flush();
		imagesBytes[i] = out.toByteArray();
		out.close();
	}
	
	public List<String> listFiles(){
		String DEFAULT_PATH = env.getProperty("default.path");
		List<String> listFiles = new ArrayList<>();
		File file = new File(DEFAULT_PATH);
		File[] files = file.listFiles();
		for(File f: files) {
			listFiles.add(f.getName());
		}
		return listFiles;
	}
}
