package de.intektor.landshut_app_server;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Intektor
 */
public class BuildingInformation {

    private String buildingName;
    private BufferedImage htmlPicture;
    private BufferedImage image;

    public BuildingInformation(String buildingName, BufferedImage htmlPicture, BufferedImage image) {
        this.buildingName = buildingName;
        this.htmlPicture = htmlPicture;
        this.image = image;
    }

    public BuildingInformation() {
    }

    public String getBuildingName() {
        return buildingName;
    }

    public BufferedImage getHtmlPicture() {
        return htmlPicture;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void writeToStream(DataOutputStream out) throws IOException {
        out.writeUTF(buildingName);
        writeImage(out, htmlPicture);
        writeImage(out, image);
    }

    private void writeImage(DataOutputStream out, BufferedImage image) throws IOException {
        out.writeInt(image.getWidth());
        out.writeInt(image.getHeight());
        out.writeInt(image.getType());
        int[] a = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        out.writeInt(a.length);
        for (int i : a) {
            out.writeInt(i);
        }
    }

    public void readFromStream(DataInputStream in) throws IOException {
        buildingName = in.readUTF();
        htmlPicture = readImage(in);
        image = readImage(in);
    }

    public BufferedImage readImage(DataInputStream in) throws IOException {
        int width = in.readInt();
        int height = in.readInt();
        BufferedImage image = new BufferedImage(width, height, in.readInt());
        int length = in.readInt();
        int a[] = new int[length];
        for (int i = 0; i < length; i++) {
            a[i] = in.readInt();
        }
        image.setRGB(0, 0, width, height, a, 0, width);
        return image;
    }

    @Override
    public String toString() {
        return String.format("%s", buildingName);
    }
}
