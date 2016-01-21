package org.kidneyomics.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GraphicsWritingServiceImpl {

	public void writeGraphicsToFile(File file, BufferedImage bufferedImage) throws IOException {
		ImageIO.write(bufferedImage, "PNG", file);
	}
	
}
