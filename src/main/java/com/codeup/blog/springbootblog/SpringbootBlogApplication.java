package com.codeup.blog.springbootblog;


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
public class SpringbootBlogApplication extends SpringBootServletInitializer{

	@Bean
	public Java8TimeDialect java8TimeDialect() {
		return new Java8TimeDialect();
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootBlogApplication.class, args);
	}

	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringbootBlogApplication.class);
	}

	// test repository or service queries
//	@Bean
//	CommandLineRunner initData(PostService service) {
//		return args -> {
//			List<Post> posts = service.searchPostsByKeyword("Fer");
//			System.out.println(Arrays.toString(posts.toArray()));
//		};
//	}
}
