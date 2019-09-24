package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        innitControllers();

        System.out.println("Done, go to localhost:8080");
    }

    private static void innitControllers() {
        new GreetingController();
    }

}
