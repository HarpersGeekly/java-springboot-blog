package com.codeup.blog.springbootblog;

import com.codeup.blog.springbootblog.Models.Post;
import com.codeup.blog.springbootblog.repositories.PostsRepository;
import com.codeup.blog.springbootblog.services.PostService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SpringbootBlogApplication extends SpringBootServletInitializer{

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
