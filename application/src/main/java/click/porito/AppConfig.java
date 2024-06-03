package click.porito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("click.porito")
public class AppConfig {
    @Autowired
    TestInterface testInterface;
}
