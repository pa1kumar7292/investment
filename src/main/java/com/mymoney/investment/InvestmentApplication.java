package com.mymoney.investment;

import com.mymoney.investment.service.InvestmentHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;

import java.util.InputMismatchException;

@SpringBootApplication
@Order(-1)
@Slf4j
public class InvestmentApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(InvestmentApplication.class, args);
	}

	final InvestmentHelper investmentHelper;

	public InvestmentApplication(InvestmentHelper investmentHelper) {
		this.investmentHelper = investmentHelper;
	}

	@Override
	public void run(String... args) throws Exception {
		if (args.length < 1 || args.length != 1) {
			log.error("input arguments not supplied");
			throw new InputMismatchException(
					"Please specify the input file");
		}
		String input = args[0];
		if ("shell".equalsIgnoreCase(input)) {
			log.info("Entering to Command line Mode");
			return;
		} else {
			investmentHelper.processInvestment(input);
			System.exit(0);
		}

	}
}

