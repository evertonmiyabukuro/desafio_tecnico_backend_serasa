package com.serasa.DesafioTecnicoBackEnd;

import org.springframework.boot.SpringApplication;

public class TestDesafioTecnicoBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.from(DesafioTecnicoBackEndApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
