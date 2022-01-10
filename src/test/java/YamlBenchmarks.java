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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YamlBenchmarks {

    @Test
    void compareAll() throws InterruptedException, IOException, InvalidConfigurationException, DuplicateKeyException, DYReaderException, IllegalListException {
        System.out.println("Performing benchmark... Results:");
        System.out.println("Run | Gson | DreamYaml | SnakeYaml | YamlBeans | EoYaml | SimpleYaml");

        List<Double> resultsGSON = new ArrayList<>();
        List<Double> resultsDreamYaml = new ArrayList<>();
        List<Double> resultsSnakeYaml = new ArrayList<>();
        List<Double> resultsYamlBeans = new ArrayList<>();
        List<Double> resultsEOYaml = new ArrayList<>();
        List<Double> resultsSimpleYaml = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            File file = new File(System.getProperty("user.dir")+"/benchmark-small-config.yml");
            File json = new File(System.getProperty("user.dir")+"/benchmark-small-config.json");
            UtilsTimeStopper timer = new UtilsTimeStopper();
            String msGSON;
            String msDY;
            String msSNY;
            String msYB;
            String msEOY;
            String msSMY;

            // DREAM-YAML
            timer.start();
            new Gson().fromJson(new FileReader(json), JsonObject.class);
            timer.stop();
            resultsGSON.add(timer.getMillis());
            msGSON = timer.getFormattedMillis();

            // GSON
            timer.start();
            DreamYaml dreamYaml = new DreamYaml(file);
            dreamYaml.load();
            timer.stop();
            dreamYaml=null;
            msDY = timer.getFormattedMillis();
            resultsDreamYaml.add(timer.getMillis());

            // SNAKE-YAML
            timer.start();
            org.yaml.snakeyaml.Yaml snakeYaml = new org.yaml.snakeyaml.Yaml();
            InputStream fileInput = new FileInputStream(file);
            snakeYaml.loadAll(fileInput);
            timer.stop();
            snakeYaml=null;
            msSNY = timer.getFormattedMillis();
            resultsSnakeYaml.add(timer.getMillis());

            // YAML-BEANS
            timer.start();
            YamlReader yamlBeans = new YamlReader(new FileReader(file));
            Object object = yamlBeans.read();
            Map map = (Map)object;
            timer.stop();
            yamlBeans=null;
            msYB = timer.getFormattedMillis();
            resultsYamlBeans.add(timer.getMillis());

            // EO-YAML
            timer.start();
            YamlMapping eolYamlTest = Yaml.createYamlInput(
                    file
            ).readYamlMapping();
            eolYamlTest.values();
            timer.stop();
            eolYamlTest=null;
            msEOY = timer.getFormattedMillis();
            resultsEOYaml.add(timer.getMillis());

            // SIMPLE-YAML
            timer.start();
            YamlFile simpleYamlTest = new YamlFile(file);
            simpleYamlTest.loadWithComments(); // Loads the entire file
            timer.stop();
            simpleYamlTest=null;
            msSMY = timer.getFormattedMillis();
            resultsSimpleYaml.add(timer.getMillis());

            System.out.println("["+i+"] [GSON:"+msGSON+"ms] [DY: "+msDY+"ms] [SNY: "+msSNY+"ms] [YB: "+msYB+"ms] [EOY: "+msEOY+"ms] [SMY: "+msSMY+"ms]");
            Thread.sleep(500);
        }
        System.out.println("Average read speeds in milliseconds:");
        System.out.println("Gson: "+calcAverage(resultsGSON));
        System.out.println("DreamYaml: "+calcAverage(resultsDreamYaml));
        System.out.println("SnakeYaml: "+calcAverage(resultsSnakeYaml));
        System.out.println("YamlBeans: "+calcAverage(resultsYamlBeans));
        System.out.println("EOYaml: "+calcAverage(resultsEOYaml));
        System.out.println("SimpleYaml: "+calcAverage(resultsSimpleYaml));
    }

    private Double calcAverage(List<Double> values){
        Double total = 0.0;
        for (Double val :
                values) {
            total = total + val;
        }
        return total / values.size();
    }
}
