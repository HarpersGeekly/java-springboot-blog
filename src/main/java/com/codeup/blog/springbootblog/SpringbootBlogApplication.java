package com.codeup.blog.springbootblog;


import com.codeup.blog.springbootblog.Models.FormatterUtil;
import com.codeup.blog.springbootblog.Models.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

import java.util.Arrays;
import java.util.List;

//for jsr310 java 8 java.time.*
@EntityScan(
		basePackageClasses = {SpringbootBlogApplication.class, Jsr310JpaConverters.class}
)
@SpringBootApplication
//public class SpringbootBlogApplication extends SpringBootServletInitializer implements CommandLineRunner  {
public class SpringbootBlogApplication extends SpringBootServletInitializer  {

	@Autowired
	@Qualifier("javaMailSender")
	public MailSender mailSender;

	@Bean
	public Java8TimeDialect java8TimeDialect() {
		return new Java8TimeDialect();
	}

	@Bean
	public FormatterUtil formatterUtil() {
		return new FormatterUtil();
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootBlogApplication.class, args);
	}

	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringbootBlogApplication.class);
	}

//	@Override
//	public void run(String... strings) throws Exception {
//
//		String from = "support@demo.com";
//		String to = "harperryanc@gmail.com";
//		String subject = "JavaMailSender";
//		String body = "Just-Testing!";
//
//		mailSender.sendMail(from, to, subject, body);
//	}

	// test repository or service queries
//	@Bean
//	CommandLineRunner initData(PostService service) {
//		return args -> {
//			List<Post> posts = service.searchPostsByKeyword("Fer");
//			System.out.println(Arrays.toString(posts.toArray()));
//		};
//	}
}
