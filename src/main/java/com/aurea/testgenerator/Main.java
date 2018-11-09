package com.aurea.testgenerator;

import com.aurea.testgenerator.args.ArgsParser;
import com.aurea.testgenerator.yaml.YAMLParser;
import com.google.common.base.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebClientAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@SpringBootApplication
@EnableAutoConfiguration(
        exclude = {
                JmxAutoConfiguration.class,
                GroovyTemplateAutoConfiguration.class,
                JacksonAutoConfiguration.class,
                ProjectInfoAutoConfiguration.class,
                WebClientAutoConfiguration.class
        }
)
public class Main implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(Main.class.getSimpleName());

    @Autowired
    Pipeline pipeline;

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        Optional<String> parallelConfigPath = new ArgsParser(args).getKey("--parallel");
        if (parallelConfigPath.isPresent()) {
            String path = parallelConfigPath.get();
            new YAMLParser().parse(path)
                    .parallel()
                    .forEach(Main::runApp);
        } else {
            runApp(args);
        }
        logger.info("Executed in {}", stopwatch);
    }

    private static void runApp(String[] argsLine) {
        SpringApplication app = new SpringApplication(Main.class);
        app.setBannerMode(Banner.Mode.OFF);
        ConfigurableApplicationContext context = app.run(argsLine);
        context.close();
    }

    @Override
    public void run(String... args) throws Exception {
        pipeline.start();
    }
}
