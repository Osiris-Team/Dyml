import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.osiris.dyml.DreamYaml;
import com.osiris.dyml.exceptions.DYReaderException;
import com.osiris.dyml.exceptions.DuplicateKeyException;
import com.osiris.dyml.exceptions.IllegalListException;
import com.osiris.dyml.utils.UtilsTimeStopper;
import org.junit.jupiter.api.Test;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.*;
import java.util.Map;

public class YamlBenchmarks {

    @Test
    void compareAll() throws InterruptedException, IOException, InvalidConfigurationException, DuplicateKeyException, DYReaderException, IllegalListException {
        System.out.println("Performing benchmark... Results:");
        System.out.println("Run | Gson | DreamYaml | SnakeYaml | YamlBeans | EoYaml | SimpleYaml");
        String msGSON;
        String msDY;
        String msSNY;
        String msYB;
        String msEOY;
        String msSMY;
        for (int i = 0; i < 10; i++) {
            File file = new File(System.getProperty("user.dir")+"/benchmark-small-config.yml");
            File json = new File(System.getProperty("user.dir")+"/benchmark-small-config.json");
            UtilsTimeStopper timer = new UtilsTimeStopper();

            // DREAM-YAML
            timer.start();
            new Gson().fromJson(new FileReader(json), JsonObject.class);
            timer.stop();
            msGSON = timer.getFormattedMillis();

            // GSON
            timer.start();
            DreamYaml dreamYaml = new DreamYaml(file);
            dreamYaml.load();
            timer.stop();
            dreamYaml=null;
            msDY = timer.getFormattedMillis();

            // SNAKE-YAML
            timer.start();
            org.yaml.snakeyaml.Yaml snakeYaml = new org.yaml.snakeyaml.Yaml();
            InputStream fileInput = new FileInputStream(file);
            snakeYaml.loadAll(fileInput);
            timer.stop();
            snakeYaml=null;
            msSNY = timer.getFormattedMillis();

            // YAML-BEANS
            timer.start();
            YamlReader yamlBeans = new YamlReader(new FileReader(file));
            Object object = yamlBeans.read();
            Map map = (Map)object;
            timer.stop();
            yamlBeans=null;
            msYB = timer.getFormattedMillis();

            // EO-YAML
            timer.start();
            YamlMapping eolYamlTest = Yaml.createYamlInput(
                    file
            ).readYamlMapping();
            eolYamlTest.values();
            timer.stop();
            eolYamlTest=null;
            msEOY = timer.getFormattedMillis();

            // SIMPLE-YAML
            timer.start();
            YamlFile simpleYamlTest = new YamlFile(file);
            simpleYamlTest.loadWithComments(); // Loads the entire file
            timer.stop();
            simpleYamlTest=null;
            msSMY = timer.getFormattedMillis();

            System.out.println("["+i+"] [GSON:"+msGSON+"ms] [DY: "+msDY+"ms] [SNY: "+msSNY+"ms] [YB: "+msYB+"ms] [EOY: "+msEOY+"ms] [SMY: "+msSMY+"ms]");
            Thread.sleep(500);
        }
    }
}
