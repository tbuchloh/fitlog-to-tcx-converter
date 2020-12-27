package de.tbuchloh.fitlogtotcxconverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FitlogToTcxConverterApplication {

    public static void main(final String[] args) {
	System.exit(SpringApplication.exit(SpringApplication.run(FitlogToTcxConverterApplication.class, args)));
    }

}
