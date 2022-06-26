package com.example.IdentityCheckServer;

import com.example.IdentityCheckServer.models.TicketModel;
import com.example.IdentityCheckServer.repos.RepoTicket;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IdentityCheckServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdentityCheckServerApplication.class, args);
	}

/*
	@Bean
	public CommandLineRunner addTicket(RepoTicket repoTicket){
		return args -> {
			TicketModel ticketModel1 = new TicketModel(0L, "username1", "encryptedMsg1");

			repoTicket.save(ticketModel1);
		};
	}

	*/
}
