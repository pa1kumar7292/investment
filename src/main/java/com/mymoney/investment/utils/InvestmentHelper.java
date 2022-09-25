package com.mymoney.investment.utils;

import com.mymoney.investment.enums.Assets;
import com.mymoney.investment.enums.SupportedOperations;
import com.mymoney.investment.service.InvestmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Month;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;

@Service
@Slf4j
public class InvestmentHelper {

    private final InvestmentService investmentService;

    public InvestmentHelper(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }


    public List<String> processInvestment(String file) throws IOException {
        try {
            Stream<String> lines = Files.lines(Paths.get(file));

            List<String> outputs =
                    lines.filter(line -> !isNullOrEmpty(line))
                            .map(this::processLine)
                            .collect(Collectors.toList());

            outputs.stream().filter(Objects::nonNull).forEach(System.out::println);
            return outputs;
        } catch (IOException e) {
            log.error("Invalid file, not able to process");
            throw new IOException("Invalid file, Please check and provide the correct file");
        }
    }

    private String processLine(String line) {
        String output = null;
        int supportedAssets = investmentService.getSupportedAssets();
        String[] commandAndInputs = line.split(" ");
        try {
            SupportedOperations command = SupportedOperations.valueOf(commandAndInputs[0]);
            switch (command) {
                case ALLOCATE:
                    validateInputSize(commandAndInputs, supportedAssets);
                    List<Double> allocations = getDoubleValues(1, supportedAssets, commandAndInputs);
                    investmentService.allocate(allocations);
                    break;
                case SIP:
                    validateInputSize(commandAndInputs, supportedAssets);
                    List<Double> sips = getDoubleValues(1, supportedAssets, commandAndInputs);
                    investmentService.sip(sips);
                    break;
                case CHANGE:
                    validateInputSize(commandAndInputs, supportedAssets + 1);
                    List<Double> rates =
                            Arrays.stream(commandAndInputs)
                                    .skip(1)
                                    .limit(supportedAssets)
                                    .map(str -> Double.parseDouble(str.replace("%", "")))
                                    .collect(Collectors.toList());
                    Month month = Month.valueOf(commandAndInputs[supportedAssets + 1]);
                    investmentService.change(rates, month);
                    break;
                case BALANCE:
                    validateInputSize(commandAndInputs, 1);
                    month = Month.valueOf(commandAndInputs[1]);
                    output = investmentService.balance(month);
                    break;
                case REBALANCE:
                    output = investmentService.reBalance();
                    break;
                default:
                    throw new DataFormatException("Invalid Command " + command + " supplied");
            }
        } catch (Exception e) {
            System.out.println(
                    "Error Occurred while processing " + String.join(" ", commandAndInputs) + e.getMessage());
        }
        return output;
    }

    public static boolean isNullOrEmpty(String inputString) {
        if (inputString == null || inputString. isEmpty())
            return true;
        return false;
    }

    private void validateInputSize(String[] commandAndInputs, int size) {
        if (commandAndInputs.length != size + 1) {
            throw new InputMismatchException(
                    "Please check the command " + String.join(" ", commandAndInputs));
        }
    }

    private List<Double> getDoubleValues(int skip, int limit, String[] commandAndInputs) {
        return Arrays.stream(commandAndInputs)
                .skip(skip)
                .limit(limit)
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }

    public static void validateInputs(Set<Assets> assetOrderForIO, List<Double> allocations)
            throws DataFormatException {
        if (Objects.isNull(allocations) || allocations.size() != assetOrderForIO.size()) {
            throw new DataFormatException("The input is not in the desired format");
        }
    }



}
