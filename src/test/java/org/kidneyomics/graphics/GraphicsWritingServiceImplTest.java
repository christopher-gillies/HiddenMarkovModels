package org.kidneyomics.graphics;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class GraphicsWritingServiceImplTest {

	@Test
	public void test() throws IOException {
	 Logger logger = Logger.getAnonymousLogger();
	 int width = 200, height = 200;

      // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed
      // into integer pixels
      BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

      Graphics2D g2 = bi.createGraphics();
      
      GradientPaint redtowhite = new GradientPaint(0,0,Color.BLACK,100, 0,Color.BLUE);
      g2.setPaint(redtowhite);
      
      g2.fill(new Ellipse2D.Double(50, 50, 20, 20));
      //top left corner is 50,50
      //diameter should be 20
      //middle of circle 50 + 10, 50 + 10
      
      g2.draw(new Line2D.Double(60, 60, 100, 100));
      
      Font font = new Font("Serif", Font.PLAIN, 10);
      g2.setFont(font);
      
      g2.setColor(Color.WHITE);
      
      g2.drawString("H", 57, 62);
      
      GraphicsWritingServiceImpl gs = new GraphicsWritingServiceImpl();
      File out = new File(FileUtils.getTempDirectoryPath() + "/out.png");
      if(out.exists()) {
    	  out.delete();
      }
      
      gs.writeGraphicsToFile(out, bi);
      logger.info("Writing to file " + out.getAbsolutePath());
      
      assertTrue(out.exists());
	}

}
