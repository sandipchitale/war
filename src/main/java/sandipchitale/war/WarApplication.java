package sandipchitale.war;

import jakarta.servlet.ServletContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class WarApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(WarApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WarApplication.class);
	}

	@RestController
	public static class IndexController {

		private final ServletContext servletContext;

		public IndexController(ServletContext servletContext) {
			this.servletContext = servletContext;
		}

		@GetMapping("/")
	    public String index() {
			String contextPath = servletContext.getContextPath();
			return "Hello World: " + (contextPath.length() == 0 ? "/" : contextPath);
	    }
	}

	@Bean
	@ConditionalOnWebApplication
	@ConditionalOnNotWarDeployment()
	public CommandLineRunner clrNotInWar() {
	    return (args) -> {
			System.out.println("Not running as war!");
	    };
	}

	static class OnWarDeployment extends NoneNestedConditions {
	 	OnWarDeployment() {
			super(ConfigurationPhase.REGISTER_BEAN);
		}

		@ConditionalOnNotWarDeployment()
		static class OnNotWarDeployment{}
	}

	@Bean
	@ConditionalOnWebApplication
	@Conditional(OnWarDeployment.class)
	public CommandLineRunner clrOnWar() {
		return (args) -> {
			System.out.println("Running as war!");
		};
	}

}
