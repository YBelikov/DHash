package bsa.java.concurrency.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.IOException;
import javax.imageio.ImageIO;

@Service
public class HashingService {

    private static final Logger logger = LoggerFactory.getLogger(HashingService.class);

    public long computeHashOfImage(MultipartFile file) throws Exception{
        try {
            System.out.println("Main thread");
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            bufferedImage = grayScale(bufferedImage);
            bufferedImage = resizeImage(bufferedImage, 9, 9);
            return dHash(bufferedImage, 9, 9);
        } catch (IOException ex) {
            logger.info(ex.getMessage(), ex);
        }
       throw new Exception("Something went wrong");
    }

    private BufferedImage grayScale(BufferedImage bufferedImage) {
       var grayScale = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
       grayScale.getGraphics().drawImage(bufferedImage, 0, 0, null);
       return grayScale;
    }

    private BufferedImage resizeImage(BufferedImage bufferedImage, int newWidth, int newHeight) {
        Image image = bufferedImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        resizedImage.getGraphics().drawImage(image, 0, 0, null);
        return resizedImage;
    }

    private long dHash(BufferedImage image, int width, int height) {
        long hash = 0;
        for (int y = 1; y < height; ++y) {
            for (int x = 1; x < width; ++x) {
                if (computeColorScore(image.getRGB(x, y)) > computeColorScore(image.getRGB(x - 1, y - 1))) {
                    hash |= 1;
                }
                hash <<= 1;
             }
        }
        return hash;
    }
    private int computeColorScore(int color) {
        return color & 0b11111111;
    }
}


