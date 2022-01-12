import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.osiris.dyml.DreamYaml;
import com.osiris.dyml.Dyml;
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
    void test() {
        String s = "hi:#-\nhi";
        try{
            StringReader reader = new StringReader(s);
            int c = 0;
            while((c=reader.read())!=-1){
                System.out.println("int: "+c+" char: "+(char)c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void compareAll() throws InterruptedException, IOException, InvalidConfigurationException, DuplicateKeyException, DYReaderException, IllegalListException {
        File fileDyml = new File(System.getProperty("user.dir") + "/src/test/benchmark-small-config.dyml");
        File fileYml = new File(System.getProperty("user.dir") + "/src/test/benchmark-small-config.yml");
        File fileJson = new File(System.getProperty("user.dir") + "/src/test/benchmark-small-config.json");
        System.out.println("Performing read speed benchmark on files:");
        System.out.println("Size in bytes: "+fileYml.length()+" Path: "+fileYml);
        System.out.println("Size in bytes: "+fileJson.length()+" Path: "+fileJson);
        System.out.println("Run | Dyml | Gson | DreamYaml | SnakeYaml | YamlBeans | EoYaml | SimpleYaml");

        List<Double> resultsDYML = new ArrayList<>();
        List<Double> resultsGSON = new ArrayList<>();
        List<Double> resultsDreamYaml = new ArrayList<>();
        List<Double> resultsSnakeYaml = new ArrayList<>();
        List<Double> resultsYamlBeans = new ArrayList<>();
        List<Double> resultsEOYaml = new ArrayList<>();
        List<Double> resultsSimpleYaml = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            fileDyml = new File(System.getProperty("user.dir") + "/src/test/benchmark-small-config.dyml");
            fileYml = new File(System.getProperty("user.dir") + "/src/test/benchmark-small-config.yml");
            fileJson = new File(System.getProperty("user.dir") + "/src/test/benchmark-small-config.json");
            UtilsTimeStopper timer = new UtilsTimeStopper();
            String msDYML;
            String msGSON;
            String msDY;
            String msSNY;
            String msYB;
            String msEOY;
            String msSMY;

            // DREAM-YAML DYML
            timer.start();
            Dyml.from(fileDyml);
            timer.stop();
            resultsDYML.add(timer.getMillis());
            msDYML = timer.getFormattedMillis();

            // DREAM-YAML
            timer.start();
            new Gson().fromJson(new FileReader(fileJson), JsonObject.class);
            timer.stop();
            resultsGSON.add(timer.getMillis());
            msGSON = timer.getFormattedMillis();

            // GSON
            timer.start();
            DreamYaml dreamYaml = new DreamYaml(fileYml);
            dreamYaml.load();
            timer.stop();
            dreamYaml = null;
            msDY = timer.getFormattedMillis();
            resultsDreamYaml.add(timer.getMillis());

            // SNAKE-YAML
            timer.start();
            org.yaml.snakeyaml.Yaml snakeYaml = new org.yaml.snakeyaml.Yaml();
            InputStream fileInput = new FileInputStream(fileYml);
            snakeYaml.loadAll(fileInput);
            timer.stop();
            snakeYaml = null;
            msSNY = timer.getFormattedMillis();
            resultsSnakeYaml.add(timer.getMillis());

            // YAML-BEANS
            timer.start();
            YamlReader yamlBeans = new YamlReader(new FileReader(fileYml));
            Object object = yamlBeans.read();
            Map map = (Map) object;
            timer.stop();
            yamlBeans = null;
            msYB = timer.getFormattedMillis();
            resultsYamlBeans.add(timer.getMillis());

            // EO-YAML
            timer.start();
            YamlMapping eolYamlTest = Yaml.createYamlInput(
                    fileYml
            ).readYamlMapping();
            eolYamlTest.values();
            timer.stop();
            eolYamlTest = null;
            msEOY = timer.getFormattedMillis();
            resultsEOYaml.add(timer.getMillis());

            // SIMPLE-YAML
            timer.start();
            YamlFile simpleYamlTest = new YamlFile(fileYml);
            simpleYamlTest.loadWithComments(); // Loads the entire file
            timer.stop();
            simpleYamlTest = null;
            msSMY = timer.getFormattedMillis();
            resultsSimpleYaml.add(timer.getMillis());

            System.out.println("[" + i + "] [DYML:" + msDYML + "ms] [GSON:" + msGSON + "ms] [DY: " + msDY + "ms] [SNY: " + msSNY + "ms] [YB: " + msYB + "ms] [EOY: " + msEOY + "ms] [SMY: " + msSMY + "ms]");
            Thread.sleep(500);
        }
        System.out.println("Average read speeds in milliseconds (first run was excluded):");
        System.out.println("Dyml: " + calcAverage(resultsDYML));
        System.out.println("Gson: " + calcAverage(resultsGSON));
        System.out.println("DreamYaml: " + calcAverage(resultsDreamYaml));
        System.out.println("SnakeYaml: " + calcAverage(resultsSnakeYaml));
        System.out.println("YamlBeans: " + calcAverage(resultsYamlBeans));
        System.out.println("EOYaml: " + calcAverage(resultsEOYaml));
        System.out.println("SimpleYaml: " + calcAverage(resultsSimpleYaml));
    }

    private Double calcAverage(List<Double> values) {
        Double total = 0.0;
        values.remove(0);
        for (Double val :
                values) {
            total = total + val;
        }
        return total / values.size();
    }
}
