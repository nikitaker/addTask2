package nikitaker;

import net.coobird.thumbnailator.geometry.Positions;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import net.coobird.thumbnailator.filters.Watermark;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Mojo(name = "run")
public class WatermarkMojo extends AbstractMojo {
    @Parameter(property = "path")
    private String pathToFiles;

    @Parameter(property = "watermark", defaultValue = "src/main/watermark/E1r9X.png")
    private String pathToWatermark;

    public void execute() {
        Log logger = getLog();

        try {

            logger.info(String.format("Try to watermark `%s` on Image in path `%s`", pathToWatermark, pathToFiles));

            Path path = Paths.get(pathToFiles);

            if (!path.toFile().exists()) {
                logger.warn(String.format("`%s` does not exist", pathToFiles));
                return;
            }

            if (!path.toFile().canExecute() && !path.toFile().isFile()) {
                logger.warn(String.format("Can not execute `%s`", pathToFiles));
                return;
            }


            if (path.toFile().isFile()) {
                addWatermark(path);
                return;
            }

            parseFiles(path);
        } catch (IOException e) {
            getLog().error(e);
        }
    }

    private void parseFiles(Path path) {
        for (File file : path.toFile().listFiles()) {
            if (file.isFile()) {
                try {
                    String type = Files.probeContentType(file.toPath());
                    if(type.split("/")[0].equals("image")){
                        addWatermark(Paths.get(file.getPath()));
                    }
                    else {
                        getLog().warn(file.toPath()+" is not an image");
                    }
                } catch (IOException e) {
                    getLog().error(e);
                }
            } else if (file.isDirectory())
                parseFiles(Paths.get(file.getPath()));
        }
    }

    private void addWatermark(Path path) throws IOException {


        if (!path.toFile().canRead() || !path.toFile().canWrite()) {
            getLog().warn(String.format("Insufficient permissions on the `%s` file", path.toFile().getPath()));
            return;
        }

        BufferedImage originalImage = ImageIO.read(path.toFile());
        BufferedImage watermarkImage = ImageIO.read(new File(pathToWatermark));


        Watermark filter = new Watermark(Positions.CENTER, watermarkImage, 0.5f);
        BufferedImage watermarkedImage = filter.apply(originalImage);

        ImageIO.write(watermarkedImage, "jpg", path.toFile());

    }
}
